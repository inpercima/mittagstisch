package net.inpercima.mittagstisch.web;

import java.util.ArrayList;
import java.util.List;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.Biomare;
import net.inpercima.mittagstisch.service.BistroAmKanal;
import net.inpercima.mittagstisch.service.BistroImBic;
import net.inpercima.mittagstisch.service.CafeteriaM9;
import net.inpercima.mittagstisch.service.GeschmackssacheLeipzig;
import net.inpercima.mittagstisch.service.Kantine3;

public class BaseController {

    public List<Lunch> list(final int days) {
        final List<Lunch> lunch = new ArrayList<>();

        GeschmackssacheLeipzig geschmackssacheLeipizg = new GeschmackssacheLeipzig(days);
        geschmackssacheLeipizg.prepare();
        lunch.add(geschmackssacheLeipizg.parse());

        BistroImBic bistroImBic = new BistroImBic(days);
        bistroImBic.prepare();
        lunch.add(bistroImBic.parse());

        BistroAmKanal bistroAmKanal = new BistroAmKanal(days);
        bistroAmKanal.prepare();
        lunch.add(bistroAmKanal.parse());

        Biomare biomare = new Biomare(days);
        biomare.prepare();
        lunch.add(biomare.parse());

        CafeteriaM9 cafeteriaM9 = new CafeteriaM9(days);
        cafeteriaM9.prepare();
        lunch.add(cafeteriaM9.parse());

        Kantine3 kantine3 = new Kantine3(days);
        kantine3.prepare();
        lunch.add(kantine3.parse());

        return lunch;
    }
}
