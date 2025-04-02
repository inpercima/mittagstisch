package net.inpercima.mittagstisch.service;

import java.io.File;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Slf4j
@Service
public class Kantine3 extends Mittagstisch {

    protected Kantine3(File bistroConfigFile) {
        super(bistroConfigFile);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = MittagstischUtils.readBistroConfig(bistroConfigFile, "kantine3");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Rocky Maria - Kantine Tapetenwerk".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        // details are in spans per day
        final String extractedText = htmlPage.querySelectorAll(bistro.getCssLunchSelector()).stream()
                .map(node -> node.asNormalizedText()).collect(Collectors.joining(" "));
        final String currentDay = MittagstischUtils.getDay(bistro.getDays()).toUpperCase();
        final String lastString = currentDay.equals("FREITAG") ? "PREISE"
                : MittagstischUtils.getDay(bistro.getDays() + 1).toUpperCase();
        return extractedText.substring(extractedText.indexOf(currentDay) + currentDay.length(),
                extractedText.indexOf(lastString)).trim();
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek)
            throws Exception {
        final LocalDate now = MittagstischUtils.getLocalizedDate(false, bistro.getDays());
        final String suffix = String.valueOf(now.getYear());
        return isWithinRange(checkForNextWeek, weekText, bistro.getDays(),
                "((?:[0-2][0-9]|3[01]|[1-9]).(?:(?:JAN)(?:UAR)?|FEB(?:RUAR)?|MÄR(?:Z)?|APR(?:IL)?|MAI|JUN(?:I)?|JUL(?:I)?|AUG(?:UST)?|SEP(?:TEMBER)?|OKT(?:OBER)?|NOV(?:EMBER)?|DEZ(?:EMBER)?))",
                MittagstischUtils.dMMMMYYYY, suffix, suffix);
    }
}
