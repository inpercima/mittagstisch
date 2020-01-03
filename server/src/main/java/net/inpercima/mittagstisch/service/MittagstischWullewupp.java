package net.inpercima.mittagstisch.service;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischWullewupp extends Mittagstisch {

    public MittagstischWullewupp(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setDissabled(true);
        this.setLunchSelector("");
        this.setName("Wullewupp");
        this.setUrl("https://www.wullewupp.de/bar");
        this.setWeekSelector("div[id='PAGES_CONTAINERinlineContent'] div div div div div div div div div div h2");
    }

    /**
     * Parses and returns the output for the lunch in "Wullewupp".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        return buildLunch(state, "");
    }

    public boolean isInWeek(final int days) {
        return false;
    }

}
