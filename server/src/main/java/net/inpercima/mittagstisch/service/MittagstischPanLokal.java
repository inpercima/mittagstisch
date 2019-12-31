package net.inpercima.mittagstisch.service;

import java.io.IOException;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischPanLokal extends Mittagstisch {

    public MittagstischPanLokal(final int days) {
        this.setLunchSelector("main article:first-of-type div p:nth-of-type(%s)");
        this.setUrl("http://pan-leipzig.de/menu/mittagessen");
        this.setWeekSelector("main article header h2 a");
        this.setName("PAN Lokal");
        this.setDissabled(true);
        this.setDays(days);
        this.setDaily(true);
    }

    /**
     * Parses and returns the output for the lunch in "PAN Lokal".
     *
     * @param state
     * @throws IOException
     */
    public Lunch parse(final State state) {
        return buildLunch(state, "");
    }

}
