package net.inpercima.mittagstisch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.MittagstischCrawler;

@RestController
public class MittagstischController {

    @GetMapping(value = "/today")
    public List<Lunch> listToday() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        lunch.add(MittagstischCrawler.lunchInKaiserbad(true));
        lunch.add(MittagstischCrawler.lunchInKantine3());
        return lunch;
    }

    @GetMapping(value = "/next-week")
    public List<Lunch> listNextWeek() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        lunch.add(MittagstischCrawler.lunchInKaiserbad(false));
        return lunch;
    }

}
