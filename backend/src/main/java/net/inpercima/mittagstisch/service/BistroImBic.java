package net.inpercima.mittagstisch.service;

import java.io.File;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
public class BistroImBic extends Mittagstisch {

    protected BistroImBic(File bistroConfigFile) {
        super(bistroConfigFile);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = MittagstischUtils.readBistroConfig(bistroConfigFile, "bistroImBic");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Bistro im BIC".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        String content = StringUtils.EMPTY;
        // details are in paragraphs per day
        content = content
                .replaceFirst(MittagstischUtils.getDay(bistro.getDays()), "").trim()
                .replace("\n", "<br><br>");

        final String extractedText = htmlPage.querySelectorAll(bistro.getCssLunchSelector()).stream()
                .map(node -> node.asNormalizedText()).collect(Collectors.joining(" "));
        final String currentDay = MittagstischUtils.getDay(bistro.getDays());
        final String lastString = "Freitag".equals(currentDay) ? "Bei Fragen"
                : MittagstischUtils.getDay(bistro.getDays() + 1);
        return extractedText.substring(extractedText.indexOf(currentDay) +
                currentDay.length(),
                extractedText.indexOf(lastString)).trim().replace("\n", "<br><br>");
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek)
            throws Exception {
                System.out.println(weekText);
        return isWithinRange(checkForNextWeek, weekText, bistro.getDays(),
                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2])\\.?[0-9]{0,4})", MittagstischUtils.ddMMYY, "", "");
    }
}
