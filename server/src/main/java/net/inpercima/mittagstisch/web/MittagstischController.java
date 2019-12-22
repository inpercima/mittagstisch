package net.inpercima.mittagstisch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.MittagstischKaiserbad;
import net.inpercima.mittagstisch.service.MittagstischKantine3;
import net.inpercima.mittagstisch.service.MittagstischLebensmittelImbissSeidel;
import net.inpercima.mittagstisch.service.MittagstischPanLokal;
import net.inpercima.mittagstisch.service.MittagstischWullewupp;

@RestController
public class MittagstischController {

    @GetMapping(value = "/today")
    public List<Lunch> listToday() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        lunch.add(MittagstischLebensmittelImbissSeidel.prepare(0));
        lunch.add(MittagstischKantine3.prepare(0));
        lunch.add(MittagstischKaiserbad.prepare(true, 0));
        lunch.add(MittagstischPanLokal.prepare(0));
        lunch.add(MittagstischWullewupp.prepare());
        return lunch;
    }

    @GetMapping(value = "/tomorrow")
    public List<Lunch> listTomorrow() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        lunch.add(MittagstischLebensmittelImbissSeidel.prepare(1));
        lunch.add(MittagstischKantine3.prepare(1));
        lunch.add(MittagstischKaiserbad.prepare(true, 1));
        lunch.add(MittagstischPanLokal.prepare(1));
        lunch.add(MittagstischWullewupp.prepare());
        return lunch;
    }

    @GetMapping(value = "/next-week")
    public List<Lunch> listNextWeek() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        lunch.add(MittagstischKaiserbad.prepare(false, 0));
        return lunch;
    }

}
