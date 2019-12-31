package net.inpercima.mittagstisch.service;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischWullewupp extends Mittagstisch {

    public MittagstischWullewupp(final int days) {
        this.setLunchSelector("");
        this.setUrl("https://www.wullewupp.de/bar");
        this.setWeekSelector("div[id='PAGES_CONTAINERinlineContent'] div div div div div div div div div div h2");
        this.setName("Wullewupp");
        this.setDays(days);
        this.setDaily(true);
        this.setDissabled(true);
    }

    /**
     * Parses and returns the output for the lunch in "Lebensmittel Imbiss Seidel".
     *
     * @param state
     * @throws IOException
     */
    public Lunch parse(final State state) {
        return buildLunch(state, "");
    }

}
