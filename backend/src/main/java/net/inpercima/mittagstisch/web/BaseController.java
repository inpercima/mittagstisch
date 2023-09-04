package net.inpercima.mittagstisch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.service.BistroAmKanal;
import net.inpercima.mittagstisch.service.BistroImBic;
import net.inpercima.mittagstisch.service.CafeteriaM9;
import net.inpercima.mittagstisch.service.GeschmackssacheLeipzig;
import net.inpercima.mittagstisch.service.Kantine3;
import net.inpercima.mittagstisch.service.LebensmittelimbissSeidel;
import net.inpercima.mittagstisch.service.PanLokal;

public class BaseController {

    public List<Lunch> list(final int days) throws IOException {
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

        CafeteriaM9 cafeteriaM9 = new CafeteriaM9(days);
        cafeteriaM9.prepare();
        lunch.add(cafeteriaM9.parse());

        LebensmittelimbissSeidel lebensmittelimbissSeidel = new LebensmittelimbissSeidel(days);
        lebensmittelimbissSeidel.prepare();
        lunch.add(lebensmittelimbissSeidel.parse());

        Kantine3 kantine3 = new Kantine3(days);
        kantine3.prepare();
        lunch.add(kantine3.parse());

        PanLokal panLokal = new PanLokal(days);
        panLokal.prepare();
        lunch.add(panLokal.parse());
        return lunch;
    }
}
