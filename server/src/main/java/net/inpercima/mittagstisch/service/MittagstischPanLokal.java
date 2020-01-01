package net.inpercima.mittagstisch.service;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischPanLokal extends Mittagstisch {

    public MittagstischPanLokal(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setDissabled(true);
        this.setLunchSelector("main article:first-of-type div p:nth-of-type(%s)");
        this.setName("PAN Lokal");
        this.setUrl("http://pan-leipzig.de/menu/mittagessen");
        this.setWeekSelector("main article header h2 a");
    }

    /**
     * Parses and returns the output for the lunch in "PAN Lokal".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        return buildLunch(state, "");
    }

}
