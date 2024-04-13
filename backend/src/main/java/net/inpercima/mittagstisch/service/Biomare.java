package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class Biomare extends Mittagstisch {

    public Biomare(final int days) {
        Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("section#mittagstisch div.grid div");
        bistro.setName("Biomare");
        bistro.setUrl("https://www.bio-mare.com/");
        bistro.setWeekSelector("");
        bistro.setWeekSelectorXPath("");
        this.setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Biomare".
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        mealWithDayAndPrice = getHtmlPage()
                .querySelector(this.getBistro().getLunchSelector() + ":nth-child("
                        + (this.getBistro().getDays() + 1) + ") article div")
                .asNormalizedText().trim().replace("\n", "<br>");
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        Pattern pattern = Pattern.compile("((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{4})");
        Matcher matcher = pattern.matcher(this.getWeekText());
        LocalDate firstDate = null;
        while (matcher.find()) {
            if (firstDate == null) {
                try {
                    firstDate = LocalDate.parse(matcher.group(1), MittagstischUtils.ddMMYYYY);
                } catch (DateTimeParseException e) {
                }
            }
        }
        final boolean isWithinWeek = MittagstischUtils.isWithinRange(firstDate, firstDate, checkForNextWeek,
                this.getBistro().getDays());
        return isWithinWeek;
    }
}
