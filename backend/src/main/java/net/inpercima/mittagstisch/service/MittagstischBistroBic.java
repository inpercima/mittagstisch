package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischBistroBic extends Mittagstisch {

    public MittagstischBistroBic(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setLunchSelector("div.content_main_dho");
        this.setName("Bistro BIC");
        this.setUrl("http://www.bistro-bic.de/Speiseplan");
        this.setWeekSelector("div.content_main_dho p");
        this.setWeekSelectorXPath("/html/body/div[4]/div[3]/div/div[1]//*[contains(text(),'Speiseplan')]");
    }

    /**
     * Parses and returns the output for the lunch in "Bistro BIC".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        String food = StringUtils.EMPTY;
        // if (StringUtils.isBlank(state.getStatusText())) {
        //     // details are in paragraphs per day
        //     HtmlParagraph p = getHtmlPage()
        //             .querySelector(String.format(getLunchSelector(), getLocalizedDate(getDays()).get(dayOfWeek())));
        //     food = p.getTextContent();
        // }
        return buildLunch(state, food);
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
