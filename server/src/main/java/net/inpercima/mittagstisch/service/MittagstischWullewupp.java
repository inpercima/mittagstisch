package net.inpercima.mittagstisch.service;

import com.gargoylesoftware.htmlunit.html.HtmlDivision;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischWullewupp extends Mittagstisch {

    public MittagstischWullewupp(final int days) {
        this.setDaily(false);
        this.setDays(days);
        this.setLunchSelector("div[id='PAGES_CONTAINERinlineContent'] div div div div div div div div div");
        this.setName("Wullewupp");
        this.setUrl("https://www.wullewupp.de/bar");
        this.setWeekSelector("div[id='PAGES_CONTAINERinlineContent'] div div div div div div div div div div h2");
    }

    /**
     * Parses and returns the output for the lunch in "Wullewupp".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        String food = StringUtils.EMPTY;
        if (StringUtils.isBlank(state.getStatusText())) {
            HtmlDivision div = getHtmlPage().querySelector(getLunchSelector());
            food = div.getTextContent();
        }
        return buildLunch(state, food);
    }

    public boolean isInWeek(final int days) {
        final boolean isInweek = isWithinRange(days) && weekContains(days, ddMMYYYY);
        log.debug("is in week: '{}'", isInweek);
        return isInweek;
    }

}
