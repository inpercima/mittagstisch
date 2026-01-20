package net.inpercima.mittagstisch.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
public class KirowKantine extends Mittagstisch {

    protected KirowKantine(String bistroJson) {
        super(bistroJson);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = Utils.readBistroConfigById(bistroJson, "kirowKantine");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "KirowKantine im alten Kesselhaus".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        String content = htmlPage.querySelector(bistro.getCssLunchSelector()).asNormalizedText().trim();
        final String currentDay = MittagstischUtils.getDay(bistro.getDays());
        Pattern pattern = Pattern.compile("(?m)^" + currentDay + ": .*");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            content = matcher.group();
        }
        return content;
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek) {
        final String currentDay = MittagstischUtils.getDay(bistro.getDays());
        return weekText.contains(currentDay + ":");
    }
}
