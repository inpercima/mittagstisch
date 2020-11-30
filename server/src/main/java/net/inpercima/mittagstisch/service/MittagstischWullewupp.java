package net.inpercima.mittagstisch.service;

import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.DomNode;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischWullewupp extends Mittagstisch {

    public MittagstischWullewupp(final int days) {
        this.setDaily(false);
        this.setDays(days);
        this.setDissabled(true);
        this.setLunchSelector("div[id='comp-j41q3qb7inlineContent-gridContainer'] > div");
        this.setName("Wullewupp");
        this.setUrl("https://www.wullewupp.de/bar");
        this.setWeekSelector("div[id='comp-j41q3qb7inlineContent-gridContainer'] > div > h2");
    }

    /**
     * Parses and returns the output for the lunch in "Wullewupp".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        String food = StringUtils.EMPTY;
        if (StringUtils.isBlank(state.getStatusText())) {
            // details are in divs per lunch after div with weekname
            food = getHtmlPage().querySelectorAll(getLunchSelector()).stream()
                    .filter(node -> !node.asText().contains(getOriginalWeekText())).map(DomNode::asText)
                    .collect(Collectors.joining("<br><br>"));
        }
        return buildLunch(state, food);
    }

    public boolean isWithinWeek(final int days) {
        final boolean isWithinWeek = isWithinRange(days) && weekTextContains(days, dM);
        log.debug("is in week: '{}'", isWithinWeek);
        return isWithinWeek;
    }

}
