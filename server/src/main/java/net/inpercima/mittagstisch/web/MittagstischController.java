package net.inpercima.mittagstisch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.MittagstischBistroBic;
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
        MittagstischLebensmittelImbissSeidel mlis = new MittagstischLebensmittelImbissSeidel(0);
        MittagstischKantine3 mk3 = new MittagstischKantine3(0);
        MittagstischWullewupp mw = new MittagstischWullewupp(0);
        MittagstischKaiserbad mk = new MittagstischKaiserbad(0);
        MittagstischPanLokal mpl = new MittagstischPanLokal(0);
        MittagstischBistroBic mbb = new MittagstischBistroBic(0);

        lunch.add(mlis.prepare());
        lunch.add(mk3.prepare());
        lunch.add(mw.prepare());
        lunch.add(mk.prepare());
        lunch.add(mpl.prepare());
        lunch.add(mbb.prepare());
        return lunch;
    }

    @GetMapping(value = "/tomorrow")
    public List<Lunch> listTomorrow() throws IOException {
        final List<Lunch> lunch = new ArrayList<>();
        MittagstischLebensmittelImbissSeidel mlis = new MittagstischLebensmittelImbissSeidel(1);
        MittagstischKantine3 mk3 = new MittagstischKantine3(1);
        MittagstischWullewupp mw = new MittagstischWullewupp(1);
        MittagstischKaiserbad mk = new MittagstischKaiserbad(1);
        MittagstischPanLokal mpl = new MittagstischPanLokal(1);
        MittagstischBistroBic mbb = new MittagstischBistroBic(1);

        lunch.add(mlis.prepare());
        lunch.add(mk3.prepare());
        lunch.add(mw.prepare());
        lunch.add(mk.prepare());
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
