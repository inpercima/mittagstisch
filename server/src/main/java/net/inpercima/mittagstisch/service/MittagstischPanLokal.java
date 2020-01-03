package net.inpercima.mittagstisch.service;

import java.time.temporal.WeekFields;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischPanLokal extends Mittagstisch {

    public MittagstischPanLokal(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setLunchSelector("main article:first-of-type div p:nth-of-type(%s)");
        this.setName("PAN Lokal");
        this.setUrl("http://pan-leipzig.de/menu/mittagessen");
        this.setWeekSelector("main article header h2 a");
    }

    /**
     * Parses and returns the output for the lunch in "PAN Lokal".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        String food = StringUtils.EMPTY;
        if (StringUtils.isBlank(state.getStatusText())) {
            // details are in paragraphs per day
            HtmlParagraph p = getHtmlPage()
                    .querySelector(String.format(getLunchSelector(), getLocalizedDate(getDays()).get(dayOfWeek())));
            food = p.getTextContent();
        }
        return buildLunch(state, food);
    }

    public boolean isInWeek(final int days) {
        final int weekNumber = getLocalizedDate(days).get(WeekFields.of(Locale.GERMANY).weekOfYear());
        final boolean isInweek = isWithinRange() && ((weekTextContains("KW", String.valueOf(weekNumber))
                || (weekTextContains("AB", firstDay(days).format(dMM)))));
        log.debug("is in week: '{}'", isInweek);
        return isInweek;
    }

}
