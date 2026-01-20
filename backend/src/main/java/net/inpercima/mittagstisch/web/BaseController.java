package net.inpercima.mittagstisch.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.inpercima.mittagstisch.model.Day;
import net.inpercima.mittagstisch.model.LunchDto;
import net.inpercima.mittagstisch.service.LunchService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BaseController {

    private final LunchService lunchService;

    @GetMapping("/today")
    public List<LunchDto> getToday() {
        return lunchService.getDataByDay(Day.TODAY);
    }

    @GetMapping("/tomorrow")
    public List<LunchDto> getTomorrow() {
        return lunchService.getDataByDay(Day.TOMORROW);
    }

    @GetMapping(value = "/data")
    public void loadData() {
        lunchService.importLunches();
    }
}
