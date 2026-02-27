package net.inpercima.mittagstisch.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.entity.BistroEntity;
import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.BistroDto;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.DishDto;
import net.inpercima.mittagstisch.model.LunchDto;
import net.inpercima.mittagstisch.model.StatusEnum;
import net.inpercima.mittagstisch.repository.LunchRepository;

@Slf4j
@Service
@AllArgsConstructor
public class LunchService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final LunchRepository lunchRepository;
    private final BistroService bistroService;
    private final ContentService contentService;
    private final AiService aiService;

    public List<LunchDto> getDataByDay(DayEnum day) {
        final Pageable top = PageRequest.of(0, (int) bistroService.count(), Sort.by("id").ascending());
        System.out.println("Fetching lunches with pageable: " + top);
        return lunchRepository.findByImportDateAndDay(LocalDate.now(), day, top)
                .stream()
                .map(lunch -> new LunchDto(
                        new BistroDto(
                                lunch.getBistro().getName(),
                                lunch.getBistro().getUrl()),
                        parseDishes(lunch.getDishes()),
                        lunch.getStatus(),
                        lunch.getImportDate()))
                .toList();
    }

    private List<DishDto> parseDishes(String dishes) {
        final ObjectMapper mapper = new ObjectMapper();
        if (dishes == null || dishes.isBlank()) {
            return List.of();
        }

        try {
            return mapper.readValue(dishes, new TypeReference<List<DishDto>>() {
            });
        } catch (JsonProcessingException e) {
            return List.of(new DishDto(dishes, ""));
        }
    }

    public void importLunches() {
        final LocalDate today = LocalDate.now();
        final LocalDate tomorrow = today.plusDays(1);

        final LocalDate weekStartDate = today.with(DayOfWeek.MONDAY);
        final LocalDate weekEndDate = today.with(DayOfWeek.FRIDAY);

        for (BistroEntity bistro : bistroService.findAll()) {
            String lunch = contentService.extractLunchFromWebsite(bistro.getUrl(), bistro.getSelector());
            String dishes = aiService.extractDishes(lunch,
                    weekStartDate, weekEndDate, today, tomorrow);
            List<LunchEntity> lunchEntities = new ArrayList<>();
            try {
                lunchEntities = contentService.prepareLunchEntities(dishes);
            } catch (Exception e) {
                log.error("Error preparing lunch entity for bistro {}: {}", bistro.getName(), e.getMessage());
                LunchEntity lunchEntity = new LunchEntity();
                lunchEntity.setStatus(StatusEnum.NO_DATA);
                lunchEntity.setDay(DayEnum.TODAY);
                lunchEntities.add(lunchEntity);

                lunchEntity = new LunchEntity();
                lunchEntity.setStatus(StatusEnum.NO_DATA);
                lunchEntity.setDay(DayEnum.TOMORROW);
                lunchEntities.add(lunchEntity);
            }
            for (LunchEntity lunchEntity : lunchEntities) {
                if (lunchEntity.getStatus() == StatusEnum.NEXT_WEEK) {
                    LocalDate nextMonday = today.plusWeeks(1).with(DayOfWeek.MONDAY);
                    LocalDate nextFriday = today.plusWeeks(1).with(DayOfWeek.FRIDAY);
                    lunchEntity.setDishes("Die Speisekarte ist bereits für die nächste Woche verfügbar (" + formatDateRange(nextMonday, nextFriday) + ").");
                }
                if (lunchEntity.getStatus() == StatusEnum.OUTDATED) {
                    LocalDate prevMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
                    LocalDate prevFriday = today.minusWeeks(1).with(DayOfWeek.FRIDAY);
                    lunchEntity.setDishes("Die Speisekarte ist noch von letzter Woche (" + formatDateRange(prevMonday, prevFriday) + ").");
                }
                if (lunchEntity.getStatus() == StatusEnum.NO_DATA) {
                    LocalDate date = resolveDate(lunchEntity.getDay(), today, tomorrow);
                    lunchEntity.setDishes("Für den " + formatDate(date) + " liegen leider keine Informationen vor.");
                }
                lunchEntity.setBistro(bistro);
                lunchEntity.setImportDate(LocalDate.now());
                lunchRepository.save(lunchEntity);
            }
        }
    }

    private LocalDate resolveDate(DayEnum day, LocalDate today, LocalDate tomorrow) {
        return day == DayEnum.TOMORROW ? tomorrow : today;
    }

    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    private String formatDateRange(LocalDate start, LocalDate end) {
        return formatDate(start) + " bis " + formatDate(end);
    }
}
