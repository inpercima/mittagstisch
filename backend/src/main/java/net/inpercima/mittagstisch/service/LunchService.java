package net.inpercima.mittagstisch.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        bistroService.findAll()
                .forEach(bistro -> importBistroLunches(bistro, today, tomorrow, weekStartDate, weekEndDate));
    }

    private void importBistroLunches(BistroEntity bistro, LocalDate today, LocalDate tomorrow, LocalDate weekStart,
            LocalDate weekEnd) {
        String dishes;
        if (bistro.getDocumentSelector() != null && !bistro.getDocumentSelector().isBlank()) {
            String pdfUrl = contentService.extractPdfUrlFromWebsite(bistro.getUrl(), bistro.getDocumentSelector());
            dishes = aiService.extractDishesFromDocument(pdfUrl, weekStart, weekEnd, today, tomorrow);
        } else {
            String lunch = contentService.extractLunchFromWebsite(bistro.getUrl(), bistro.getSelector());
            dishes = aiService.extractDishes(lunch, weekStart, weekEnd, today, tomorrow);
        }
        List<LunchEntity> lunches = new ArrayList<>();

        try {
            lunches = contentService.prepareLunchEntities(dishes);
        } catch (Exception e) {
            log.error("Error preparing lunch entity for bistro {}: {}", bistro.getName(), e.getMessage());
            lunches = createNoDataEntities();
        }

        lunches.forEach(entity -> persistLunchEntity(entity, bistro, today, tomorrow));
    }

    private static List<LunchEntity> createNoDataEntities() {
        List<LunchEntity> entities = new ArrayList<>();
        for (DayEnum day : DayEnum.values()) {
            LunchEntity entity = new LunchEntity();
            entity.setStatus(StatusEnum.NO_DATA);
            entity.setDay(day);
            entities.add(entity);
        }
        return entities;
    }

    private void persistLunchEntity(LunchEntity entity, BistroEntity bistro, LocalDate today, LocalDate tomorrow) {
        String message = buildStatusMessage(entity.getStatus(), entity.getDay(), today, tomorrow);
        if (message != null) {
            entity.setDishes(message);
        }
        entity.setBistro(bistro);
        entity.setImportDate(LocalDate.now());
        lunchRepository.save(entity);
    }

    String buildStatusMessage(StatusEnum status, DayEnum day, LocalDate today, LocalDate tomorrow) {
        return switch (status) {
            case NEXT_WEEK -> "Die Speisekarte ist bereits für die nächste Woche verfügbar ("
                    + formatDateRange(today.plusWeeks(1).with(DayOfWeek.MONDAY),
                            today.plusWeeks(1).with(DayOfWeek.FRIDAY))
                    + ").";
            case OUTDATED -> "Die Speisekarte ist noch von letzter Woche ("
                    + formatDateRange(today.minusWeeks(1).with(DayOfWeek.MONDAY),
                            today.minusWeeks(1).with(DayOfWeek.FRIDAY))
                    + ").";
            case NO_DATA -> "Für den " + formatDate(day == DayEnum.TOMORROW ? tomorrow : today)
                    + " liegen leider keine Informationen vor.";
            default -> null;
        };
    }

    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    private String formatDateRange(LocalDate start, LocalDate end) {
        return formatDate(start) + " bis " + formatDate(end);
    }
}
