package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class GeschmackssacheLeipzig extends Mittagstisch {

    public GeschmackssacheLeipzig(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setLunchSelector("table:first-of-type tr");
        this.setName("Geschmackssache Leipzig");
        this.setUrl("https://geschmackssache-leipzig.de");
        this.setWeekSelector("div[id='main'] div h2");
        this.setWeekSelectorXPath("/html/body/div/div/div/div[1]/h2//*[contains(text(),'Wochenkarte')]");
    }

    /**
     * Parses and returns the output for the lunch in "Geschmackssache Leipzig".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        if (StringUtils.isBlank(state.getStatusText())) {
            // details are in rows per day
            final String dayAbbr = getDay().substring(0, 2);
            final String dayLowerCaseAbbr = dayAbbr.toLowerCase();

            mealWithDayAndPrice = getHtmlPage().querySelectorAll(getLunchSelector()).stream()
                    .filter(node -> node.asNormalizedText().toLowerCase().startsWith(dayLowerCaseAbbr)).findFirst().get()
                    .asNormalizedText();
            mealWithDayAndPrice = mealWithDayAndPrice.replaceFirst(dayAbbr, "").trim();
        }
        return buildLunch(state, mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        Pattern pattern = Pattern.compile("((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{4})");
        Matcher matcher = pattern.matcher(getWeekText());
        LocalDate firstDate = null;
        LocalDate lastDate = null;
        while (matcher.find()) {
            if (firstDate == null) {
                firstDate = LocalDate.parse(matcher.group(1), ddMMYYYY);
                log.debug("extracted firstDate: '{}'", firstDate);
            } else {
                lastDate = LocalDate.parse(matcher.group(1), ddMMYYYY);
                log.debug("extracted lastDate: '{}'", lastDate);
            }
        }
        final boolean isWithinWeek = isWithinRange(firstDate, lastDate, checkForNextWeek);
        log.debug("is in week: '{}'", isWithinWeek);
        return isWithinWeek;
    }
}
