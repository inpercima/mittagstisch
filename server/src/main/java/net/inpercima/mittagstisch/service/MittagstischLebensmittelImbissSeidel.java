package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.DomNode;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischLebensmittelImbissSeidel extends Mittagstisch {

    public MittagstischLebensmittelImbissSeidel(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setLunchSelector("body div div div div div.xr_s20 span");
        this.setName("Lebensmittel Imbiss Seidel");
        this.setUrl("https://lebensmittel-imbiss-seidel.de/imbiss.htm");
        this.setWeekSelector("body div div div div div.xr_s20 span:nth-of-type(1)");
    }

    /**
     * Parses and returns the output for the lunch in "Lebensmittel Imbiss Seidel".
     *
     * @param state
     * @throws IOException
     */
    public Lunch parse(final State state) throws IOException {
        String food = StringUtils.EMPTY;
        if (StringUtils.isBlank(state.getStatusText())) {
            // details are in spans per day after span with dayname
            food = filter("Änderungen").map(DomNode::getTextContent).collect(Collectors.joining(" "));
        }
        return buildLunch(state, food);
    }

    public boolean isInWeek(final int days) {
        final boolean isInweek = isWithinRange(days) && weekContains(days, ddMMYYYY);
        log.debug("is in week: '{}'", isInweek);
        return isInweek;
    }

}
