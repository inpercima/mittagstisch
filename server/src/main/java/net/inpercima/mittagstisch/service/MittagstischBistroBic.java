package net.inpercima.mittagstisch.service;

import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischBistroBic extends Mittagstisch {

    public MittagstischBistroBic(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setDissabled(true);
        this.setLunchSelector("div.content_main_dho");
        this.setName("Bistro BIC");
        this.setUrl("http://www.bistro-bic.de/Speiseplan");
        this.setWeekSelector("div.content_main_dho p:first-of-type");
    }

    /**
     * Parses and returns the output for the lunch in "Bistro BIC".
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

    public boolean isWithinWeek(final int days) {
        final boolean isWithinWeek = isWithinRange(days) && weekTextContains(days, ddMMYYYY);
        log.debug("is in week: '{}'", isWithinWeek);
        return isWithinWeek;
    }
}
