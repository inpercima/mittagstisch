package net.inpercima.mittagstisch.service;

import java.io.IOException;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischKaiserbad extends Mittagstisch {

    public MittagstischKaiserbad(final int days) {
        this.setLunchSelector("div[id='wochenkarte'] + div div div p");
        this.setUrl("http://kaiserbad-leipzig.de/#wochenkarte");
        this.setWeekSelector("div[id='wochenkarte'] + div h1");
        this.setName("Kaiserbad");
        this.setDaily(true);
        this.setDissabled(true);
        this.setDays(days);
    }

    /**
     * Parses and returns the output for the lunch in "Kaiserbad".
     *
     * @param state
     * @throws IOException
     */
    public Lunch parse(final State state) {
        return buildLunch(state, "");
    }

}
