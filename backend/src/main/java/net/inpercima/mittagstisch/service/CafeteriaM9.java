package net.inpercima.mittagstisch.service;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import net.inpercima.mittagstisch.Utils;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
public class CafeteriaM9 extends Mittagstisch {

    protected CafeteriaM9(String bistroJson) {
        super(bistroJson);
    }

    public Lunch getLunch(final int days) {
        final Bistro bistro = Utils.readBistroConfig(bistroJson, "cafeteriaM9");
        bistro.setDays(days);
        return crawlLunch(bistro);
    }

    /**
     * Gets specific data of the lunch for "Cafeteria M9".
     */
    protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
        return mainContent;
    }

    protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek) {
        // always true b/c of pdf
        return true;
    }
}
