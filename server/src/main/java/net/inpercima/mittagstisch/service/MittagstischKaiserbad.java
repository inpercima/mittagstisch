package net.inpercima.mittagstisch.service;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischKaiserbad extends Mittagstisch {

    public MittagstischKaiserbad(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setDissabled(true);
        this.setLunchSelector("div[id='wochenkarte'] + div div div p");
        this.setName("Kaiserbad");
        this.setUrl("http://kaiserbad-leipzig.de/#wochenkarte");
        this.setWeekSelector("div[id='wochenkarte'] + div h1");
    }

    /**
     * Parses and returns the output for the lunch in "Kaiserbad".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        return buildLunch(state, "");
    }

    public boolean isInWeek(final String weekText, final int days) {
        return false;
    }

}
