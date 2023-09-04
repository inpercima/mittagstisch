package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class BistroAmKanal extends Mittagstisch {

    public BistroAmKanal(final int days) {
        Bistro bistro = new Bistro();
        bistro.setPdf(true);
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("");
        bistro.setName("Bistro am Kanal");
        bistro.setUrl("https://buschmannle.wixsite.com/meinewebsite");
        bistro.setWeekSelector("main + div a[class~='wixui-button']");
        bistro.setWeekSelectorXPath("/html/body/div/div/div[4]/div/div/div/div/section/div[2]/div/div[2]/div/div[4]/a");
        setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Bistro am Kanal".
     *
     * @param state
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        // b/c of pdf this is always true
        return true;
    }
}
