package net.inpercima.mittagstisch.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.entity.BistroEntity;
import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.BistroDto;
import net.inpercima.mittagstisch.model.Day;
import net.inpercima.mittagstisch.model.LunchDto;
import net.inpercima.mittagstisch.model.Status;
import net.inpercima.mittagstisch.repository.LunchRepository;

@Slf4j
@Service
@AllArgsConstructor
public class LunchService {

    private final LunchRepository lunchRepository;
    private final BistroService bistroService;
    private final ContentService contentService;
    private final AiService aiService;

    public List<LunchDto> getDataByDay(Day day) {
        Pageable top = PageRequest.of(0, (int) bistroService.count(), Sort.by("id").descending());
        System.out.println("Fetching lunches with pageable: " + top);
        return lunchRepository.findByImportDateAndDay(LocalDate.now(), day, top)
                .stream()
                .map(lunch -> new LunchDto(
                        new BistroDto(
                                lunch.getBistro().getName(),
                                lunch.getBistro().getUrl()),
                        lunch.getLunches(),
                        lunch.getStatus(),
                        lunch.getImportDate()))
                .toList();
    }

    public void importLunches() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        LocalDate weekStartDate = today.with(DayOfWeek.MONDAY);
        LocalDate weekEndDate = today.with(DayOfWeek.FRIDAY);

        for (BistroEntity bistro : bistroService.findAll()) {
            String content = contentService.extractText(bistro.getUrl(), bistro.getSelector());
            String lunches = aiService.extractLunches(content,
                    weekStartDate, weekEndDate, today, tomorrow);
            List<LunchEntity> lunchEntities = new ArrayList<>();
            try {
                lunchEntities = contentService.prepareLunchEntities(lunches);
            } catch (Exception e) {
                log.error("Error preparing lunch entity for bistro {}: {}", bistro.getName(), e.getMessage());
                LunchEntity lunchEntity = new LunchEntity();
                lunchEntity.setStatus(Status.NO_DATA);
                lunchEntity.setDay(Day.TODAY);
                lunchEntities.add(lunchEntity);

                lunchEntity = new LunchEntity();
                lunchEntity.setStatus(Status.NO_DATA);
                lunchEntity.setDay(Day.TOMORROW);
                lunchEntities.add(lunchEntity);
            }
            for (LunchEntity lunchEntity : lunchEntities) {
                if (lunchEntity.getStatus() == Status.NEXT_WEEK) {
                    lunchEntity.setLunches("Die Speisekarte ist bereits für die nächste Woche verfügbar.");
                }
                if (lunchEntity.getStatus() == Status.OUTDATED) {
                    lunchEntity.setLunches("Die Speisekarte ist noch von letzter Woche.");
                }
                if (lunchEntity.getStatus() == Status.NO_DATA) {
                    lunchEntity.setLunches("Für heute liegen leider keine Informationen vor.");
                }
                lunchEntity.setBistro(bistro);
                lunchEntity.setImportDate(LocalDate.now());
                lunchRepository.save(lunchEntity);
            }
        }
    }
}
