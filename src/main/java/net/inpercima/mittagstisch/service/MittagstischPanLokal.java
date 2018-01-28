package net.inpercima.mittagstisch.service;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischPanLokal {

    protected static final String LUNCH = "main article:first-of-type div p:nth-of-type(%s)";

    protected static final String URL = "http://pan-leipzig.de/menu/mittagessen";

    protected static final String WEEK = "main article header h2 a";

    private MittagstischPanLokal() {
        // not used
    }

    /**
     * Returns the output for the lunch in "PAN Lokal".
     *
     * @param days The number of days added to the current day
     * @return Lunch Information about the lunch
     * @throws IOException
     */
    public static Lunch prepare(final int days) throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "PAN Lokal", WEEK, URL, true, days);
        if (lunch.getFood() == null) {
            parse(page, lunch, days);
            lunch.setStatus(MittagstischUtil.STATUS_SUCCESS);
        }
        return lunch;
    }

    /**
     * Parses the given page to get information about the lunch.
     *
     * @param page The page which should be parsed
     * @param lunch The lunch which should be used
     * @param days The number of days added to the current day
     */
    private static void parse(final HtmlPage page, final Lunch lunch, final int days) {
        // details are in paragraphs per day
        HtmlParagraph p = page.querySelector(
                String.format(LUNCH, MittagstischUtil.getLocalizedDate(days).get(MittagstischUtil.dayOfWeek())));
        lunch.setFood(p.getTextContent());
    }

}
