package net.inpercima.mittagstisch.service;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischWullewupp {

    protected static final String LUNCH = "";

    protected static final String URL = "https://www.wullewupp.de/bar";

    protected static final String WEEK = "div[id='PAGES_CONTAINERinlineContent'] div div div div div div div div div div h2";

    private MittagstischWullewupp() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Wullewupp".
     */
    public static Lunch prepare() {
        final Lunch lunch = new Lunch("Wullewupp");
        lunch.setFood(String.format(MittagstischUtil.TECHNICAL, URL, URL));
        lunch.setStatus(MittagstischUtil.STATUS_ERROR);
        return lunch;
    }

}
