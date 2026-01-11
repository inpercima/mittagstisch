package net.inpercima.mittagstisch.service;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Slf4j
@Service
public class BistroAmKanal extends Mittagstisch {

    protected BistroAmKanal(String bistroJson) {
        super(bistroJson);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = Utils.readBistroConfig(bistroJson, "bistroAmKanal");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Bistro am Kanal".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        // sometimes the lunch report is not a pdf
        String content = mainContent;
        if (bistro.isDocument() && !mainContent.endsWith("pdf")) {
            content = htmlPage.querySelector(bistro.getCssLunchSelector()).getAttributes()
                    .getNamedItem("src")
                    .getNodeValue();
            log.debug("prepare lunch for: '{}' with alternative image link: '{}'", bistro.getName(), content);
        }
        return content;
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek) {
        // always true b/c of pdf
        return true;
    }
}
