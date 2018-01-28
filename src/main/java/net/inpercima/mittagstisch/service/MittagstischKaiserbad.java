package net.inpercima.mittagstisch.service;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKaiserbad {

    protected static final String LUNCH = "div[id='wochenkarte'] + div div div p";

    protected static final String URL = "http://kaiserbad-leipzig.de/#wochenkarte";

    protected static final String WEEK = "div[id='wochenkarte'] + div h1";

    private MittagstischKaiserbad() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Kaiserbad".
     *
     * @param daily True if the lunch is per day otherwise false
     * @param days The number of days added to the current day
     * @return Lunch Information about the lunch
     * @throws IOException
     */
    public static Lunch prepare(final boolean daily, final int days) throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "Kaiserbad", WEEK, URL, daily, days);
        if (lunch.getFood() == null) {
            parse(page, lunch);
            lunch.setStatus(MittagstischUtil.STATUS_SUCCESS);
        }
        return lunch;
    }

    /**
     * Parses the given page to get information about the lunch.
     *
     * @param page The page which should be parsed
     * @param lunch The lunch which should be used
     */
    private static void parse(final HtmlPage page, final Lunch lunch) {
        // details are in paragraphs for all days in week
        for (DomNode p : page.querySelectorAll(LUNCH)) {
            final String content = p.getTextContent();
            if (!content.trim().isEmpty()) {
                lunch.setFood(lunch.getFood() == null ? content : lunch.getFood() + "<br><br>" + content);
            }
        }
    }

}
