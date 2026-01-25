package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
public class Biomare extends Mittagstisch {

    protected Biomare(String bistroJson) {
        super(bistroJson);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = Utils.readBistroConfigById(bistroJson, "biomare");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Biomare".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        String content = htmlPage
                .querySelector(bistro.getCssLunchSelector() + ":nth-child("
                        + (bistro.getDays() + 1) + ") article div")
                .asNormalizedText().trim().replace("\n", "<br>");
        return content;
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek)
            throws Exception {
        Pattern pattern = Pattern.compile("((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{4})");
        Matcher matcher = pattern.matcher(weekText);
        LocalDate firstDate = null;
        while (matcher.find()) {
            if (firstDate == null) {
                try {
                    firstDate = LocalDate.parse(matcher.group(1), MittagstischUtils.ddMMYYYY);
                } catch (DateTimeParseException e) {
                }
            }
        }
        return MittagstischUtils.isWithinRange(firstDate, firstDate, checkForNextWeek,
                bistro.getDays());
    }
}
