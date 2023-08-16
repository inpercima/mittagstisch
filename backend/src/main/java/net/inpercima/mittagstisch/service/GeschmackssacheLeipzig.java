package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class GeschmackssacheLeipzig extends Mittagstisch {

    public GeschmackssacheLeipzig(final int days) {
        Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("table:first-of-type tr");
        bistro.setName("Geschmackssache Leipzig");
        bistro.setUrl("https://geschmackssache-leipzig.de");
        bistro.setWeekSelector("div[id='main'] div h2");
        bistro.setWeekSelectorXPath("/html/body/div/div/div/div[1]/h2//*[contains(text(),'Wochenkarte')]");
        setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Geschmackssache Leipzig".
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        if (StringUtils.isBlank(getState().getStatusText())) {
            // details are in table rows per day
            final String dayAbbr = MittagstischUtils.getDay(getBistro().getDays()).substring(0, 2);
            final String dayLowerCaseAbbr = dayAbbr.toLowerCase();

            mealWithDayAndPrice = getHtmlPage().querySelectorAll(getBistro().getLunchSelector()).stream()
                    .filter(node -> node.asNormalizedText().toLowerCase().startsWith(dayLowerCaseAbbr)).findFirst()
                    .get()
                    .asNormalizedText();
            // remove day b/c the selected day is clear, and replace tab by single html whitespace
            mealWithDayAndPrice = mealWithDayAndPrice.replaceFirst(dayAbbr, "").trim().replace("\t", "&nbsp;");
        }
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        return MittagstischUtils.isWithinWeek(checkForNextWeek, getWeekText(), getBistro().getDays(),
                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{4})", MittagstischUtils.ddMMYYYY, "", "");
    }
}
