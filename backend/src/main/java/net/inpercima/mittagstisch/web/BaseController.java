package net.inpercima.mittagstisch.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.Biomare;
import net.inpercima.mittagstisch.service.BistroAmKanal;
import net.inpercima.mittagstisch.service.BistroImBic;
import net.inpercima.mittagstisch.service.CafeteriaM9;
import net.inpercima.mittagstisch.service.GeschmackssacheLeipzig;
import net.inpercima.mittagstisch.service.Kaiserbad;
import net.inpercima.mittagstisch.service.Kantine3;
import net.inpercima.mittagstisch.service.KirowKantine;

@RestController
@RequiredArgsConstructor
public class BaseController {

    private final GeschmackssacheLeipzig geschmackssacheLeipzig;
    private final BistroImBic bistroImBic;
    private final CafeteriaM9 cafeteriaM9;
    private final BistroAmKanal bistroAmKanal;
    private final Biomare biomare;
    private final Kantine3 kantine3;
    private final Kaiserbad kaiserbad;
    private final KirowKantine kirowKantine;

    @GetMapping(value = "/api/today")
    public List<Lunch> listToday() {
        return list(0);
    }

    @GetMapping(value = "/api/tomorrow")
    public List<Lunch> listTomorrow() {
        return list(1);
    }

    private List<Lunch> list(final int days) {
        final List<Lunch> lunch = new ArrayList<>();
        lunch.add(geschmackssacheLeipzig.getLunch(days));
        lunch.add(bistroImBic.getLunch(days));
        lunch.add(cafeteriaM9.getLunch(days));
        lunch.add(bistroAmKanal.getLunch(days));
        lunch.add(biomare.getLunch(days));
        lunch.add(kantine3.getLunch(days));
        lunch.add(kaiserbad.getLunch(days));
        lunch.add(kirowKantine.getLunch(days));
        return lunch;
    }
}
