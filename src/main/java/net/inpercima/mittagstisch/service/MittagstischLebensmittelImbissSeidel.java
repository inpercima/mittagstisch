package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischLebensmittelImbissSeidel {

    protected static final String LUNCH = "body div div div div div.xr_s19 span";

    protected static final String URL = "https://lebensmittel-imbiss-seidel.de/imbiss.htm";

    protected static final String WEEK = "body div div div div div.xr_s19 span:nth-of-type(1)";

    private static boolean found = false;

    private MittagstischLebensmittelImbissSeidel() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Lebensmittel Imbiss Seidel".
     *
     * @param days The number of days added to the current day
     * @return Lunch Information about the lunch
     * @throws IOException
     */
    public static Lunch prepare(final int days) throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "Lebensmittel Imbiss Seidel", WEEK, URL, true, days);
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
        // details are in spans per day after span with dayname
        String food = page.querySelectorAll(LUNCH).stream().filter(span -> filterSpan(span, days))
                .map(DomNode::getTextContent).collect(Collectors.joining(" "));
        lunch.setFood(food.replace(MittagstischUtil.getDay(false, days), ""));
    }

    private static boolean filterSpan(final DomNode span, final int days) {
        final String content = span.getTextContent();
        if (content.startsWith(MittagstischUtil.getDay(false, days))) {
            found = true;
        } else if (content.startsWith(MittagstischUtil.getDay(false, days + 1)) || content.startsWith("Änderungen")) {
            found = false;
        }
        return found;
    }

}
