package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.time.format.TextStyle;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKantine3 {

    protected static final String LUNCH = "main section div div p";

    protected static final String URL = "http://www.tapetenwerk.de/aktuelles/speiseplan-kantine/";

    protected static final String WEEK = "main section div div h1";

    private MittagstischKantine3() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Kantine 3 (im Tapetenwerk)".
     *
     * @param days The number of days added to the current day
     * @return Lunch Information about the lunch
     */
    public static Lunch prepare(final int days) throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "Kantine 3 (im Tapetenwerk)", WEEK, URL, true, days);
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
        boolean found = false;
        for (DomNode p : page.querySelectorAll(LUNCH)) {
            final String content = p.getTextContent();
            if (content.startsWith(getDay(true, days))) {
                found = true;
            } else if (content.startsWith(getDay(true, days + 1)) || content.startsWith("TÄGLICH")) {
                break;
            }
            if (found && !content.trim().isEmpty()) {
                lunch.setFood(lunch.getFood() == null ? content : lunch.getFood() + "<br><br>" + content);
            }
        }
    }

    /**
     * Determine the day name for checks.
     * 
     * @param toUppercase True if the result should be in uppercase otherwise false
     * @param days The number of days added to the current day
     * @return String
     */
    private static String getDay(final boolean toUppercase, final int days) {
        String day = MittagstischUtil.getLocalizedDate(days).getDayOfWeek().getDisplayName(TextStyle.FULL,
                Locale.GERMANY);
        return toUppercase ? day.toUpperCase() : day;
    }

}
