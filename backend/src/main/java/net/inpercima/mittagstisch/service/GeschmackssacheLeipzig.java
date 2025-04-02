package net.inpercima.mittagstisch.service;

import java.io.File;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
public class GeschmackssacheLeipzig extends Mittagstisch {

    protected GeschmackssacheLeipzig(File bistroConfigFile) {
        super(bistroConfigFile);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = MittagstischUtils.readBistroConfig(bistroConfigFile, "geschmackssache");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Geschmackssache Leipzig".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        // details are in table rows per day
        final String dayAbbr = MittagstischUtils.getDay(bistro.getDays()).substring(0, 2);
        final String dayLowerCaseAbbr = dayAbbr.toLowerCase();

        String content = htmlPage.querySelectorAll(bistro.getCssLunchSelector()).stream()
                .filter(node -> node.asNormalizedText().toLowerCase().startsWith(dayLowerCaseAbbr)).findFirst()
                .get()
                .asNormalizedText();
        // remove day b/c the selected day is clear, and replace tab by single html
        // whitespace
        return content.replaceFirst(dayAbbr,
                "").trim().replace("\t", "&nbsp;");
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek)
            throws Exception {
        return isWithinRange(checkForNextWeek, weekText, bistro.getDays(),
                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{4})", MittagstischUtils.ddMMYYYY, "", "");
    }
}
