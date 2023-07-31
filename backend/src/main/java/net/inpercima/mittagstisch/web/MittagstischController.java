package net.inpercima.mittagstisch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.MittagstischBistroBic;
import net.inpercima.mittagstisch.service.MittagstischPanLokal;

@RestController
public class MittagstischController {

    @GetMapping(value = "/today")
    public List<Lunch> listToday() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        //MittagstischPanLokal mpl = new MittagstischPanLokal(0);
        MittagstischBistroBic mbb = new MittagstischBistroBic(0);

        //lunch.add(mpl.prepare());
        lunch.add(mbb.prepare());
        return lunch;
    }

    @GetMapping(value = "/tomorrow")
    public List<Lunch> listTomorrow() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        MittagstischPanLokal mpl = new MittagstischPanLokal(1);
        MittagstischBistroBic mbb = new MittagstischBistroBic(1);

        lunch.add(mpl.prepare());
        lunch.add(mbb.prepare());
        return lunch;
    }

    @GetMapping(value = "/next-week")
    public List<Lunch> listNextWeek() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        return lunch;
    }
}
