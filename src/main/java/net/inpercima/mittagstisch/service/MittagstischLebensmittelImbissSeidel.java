package net.inpercima.mittagstisch.service;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischLebensmittelImbissSeidel {

    protected static final String LUNCH = "";

    protected static final String URL = "https://lebensmittel-imbiss-seidel.de/imbiss.htm";

    protected static final String WEEK = "";

    private MittagstischLebensmittelImbissSeidel() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Lebensmittel Imbiss Seidel".
     */
    public static Lunch prepare() {
        final Lunch lunch = new Lunch("Lebensmittel Imbiss Seidel");
        lunch.setFood(String.format(MittagstischUtil.TECHNICAL, URL, URL));
        lunch.setStatus(MittagstischUtil.STATUS_ERROR);
        return lunch;
    }

}
