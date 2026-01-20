package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.inpercima.mittagstisch.entity.BistroEntity;
import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.BistroDto;
import net.inpercima.mittagstisch.model.Day;
import net.inpercima.mittagstisch.model.LunchDto;
import net.inpercima.mittagstisch.repository.LunchRepository;

@Service
@AllArgsConstructor
public class LunchService {

    private final LunchRepository lunchRepository;
    private final BistroService bistroService;
    private final ContentService contentService;
    private final AiService aiService;

    public List<LunchDto> getDataByDay(Day day) {
        return lunchRepository.findByImportDateAndDay(LocalDate.now(), day)
                .stream()
                .map(lunch -> new LunchDto(
                        new BistroDto(
                                lunch.getBistro().getName(),
                                lunch.getBistro().getUrl()),
                        lunch.getLunches()))
                .toList();
    }

    public void importLunches() {
        for (Day day : Day.values()) {
            for (BistroEntity bistro : bistroService.findAll()) {
                String text = contentService.extractText(bistro.getUrl(), bistro.getSelector());
                String lunches = aiService.extractLunches(text,
                        day == Day.TODAY ? LocalDate.now() : LocalDate.now().plusDays(1),
                        bistro);

                LunchEntity lunchEntity = new LunchEntity();
                lunchEntity.setBistro(bistro);
                lunchEntity.setLunches(lunches);
                lunchEntity.setImportDate(LocalDate.now());
                lunchEntity.setDay(day);
                lunchRepository.save(lunchEntity);
            }
        }
    }
}
