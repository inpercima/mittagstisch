package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class CafeteriaM9 extends Mittagstisch {

    public CafeteriaM9(final int days) {
        Bistro bistro = new Bistro();
        bistro.setPdf(true);
        bistro.setPdfFullPath(false);
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("");
        bistro.setName("Cafeteria M9");
        bistro.setUrl("https://www.philippus-leipzig.de/catering/cafeteria-m9/");
        bistro.setWeekSelector("div[id='content'] div[class='cs-row'] a[class='button']");
        bistro.setWeekSelectorXPath("/html/body/div[1]/div[2]/div/div[2]/div[1]/div[1]/div[2]/div/div/p/a");
        setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Cafeteria M9".
     *
     * @param state
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) throws Exception {
        // b/c of pdf this is always true
        return true;
    }
}
