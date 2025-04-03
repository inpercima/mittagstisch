package net.inpercima.mittagstisch.service;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
@Slf4j
public class Kaiserbad extends Mittagstisch {

    protected Kaiserbad(String bistroJson) {
        super(bistroJson);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = MittagstischUtils.readBistroConfig(bistroJson, "kaiserbad");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Kaiserbad".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        return mainContent;
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek) {
        // always true b/c of pdf
        return true;
    }
}
