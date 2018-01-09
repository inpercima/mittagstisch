package net.inpercima.mittagstisch.service;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischCrawler {

    protected static final String KAISERBAD_CSS_LUNCH = "div[id='wochenkarte'] + div div div p";

    protected static final String KAISERBAD_CSS_WEEK = "div[id='wochenkarte'] + div h1";

    protected static final String KAISERBAD_URL = "http://kaiserbad-leipzig.de/";

    protected static final String KANTINE_3_CSS_LUNCH = "main section div div p";

    protected static final String KANTINE_3_CSS_WEEK = "main section div div h1";

    protected static final String KANTINE_3_URL = "http://www.tapetenwerk.de/aktuelles/speiseplan-kantine/";

    protected static final String LEBENSMITTEL_SEIDEL_URL = "https://lebensmittel-imbiss-seidel.de/imbiss.htm";

    protected static final String LEBENSMITTEL_SEIDEL_CSS_LUNCH = "";

    protected static final String LEBENSMITTEL_SEIDEL_CSS_WEEK = "";

    protected static final String WULLEWUPP_CSS_LUNCH = "";

    protected static final String WULLEWUPP_CSS_WEEK = "";

    protected static final String WULLEWUPP_URL = "https://www.wullewupp.de/bar";

    private MittagstischCrawler() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Kaiserbad".
     *
     * @return Lunch Information about the lunch
     * @throws IOException
     */
    public static Lunch lunchInKaiserbad(final boolean daily) throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(KAISERBAD_URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "Kaiserbad", KAISERBAD_CSS_WEEK, KAISERBAD_URL, daily);
        if (lunch.getFood() == null) {
            parseKaiserbadData(page, lunch);
        }
        return lunch;
    }

    /**
     * Parses the given page to get information about the lunch.
     *
     * @param page The page which should be parsed
     * @param lunch The lunch which should be used
     */
    private static void parseKaiserbadData(final HtmlPage page, final Lunch lunch) {
        // details are in paragraphs for all days in week
        for (DomNode p : page.querySelectorAll(KAISERBAD_CSS_LUNCH)) {
            final String content = p.getTextContent();
            if (!content.trim().isEmpty()) {
                lunch.setFood(lunch.getFood() == null ? content : lunch.getFood() + "<br><br>" + content);
            }
        }
    }

    /**
     * Returns the output for the lunch in "Kantine 3 (im Tapetenwerk)".
     *
     * @return Lunch Information about the lunch
     */
    public static Lunch lunchInKantine3() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(KANTINE_3_URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "Kantine 3 (im Tapetenwerk)", KANTINE_3_CSS_WEEK,
                KANTINE_3_URL, true);
        if (lunch.getFood() == null) {
            parseKantine3Data(page, lunch);
        }
        return lunch;
    }

    /**
     * Parses the given page to get information about the lunch.
     *
     * @param page The page which should be parsed
     * @param lunch The lunch which should be used
     */
    private static void parseKantine3Data(final HtmlPage page, final Lunch lunch) {
        // details are in paragraphs per day
        boolean found = false;
        for (DomNode p : page.querySelectorAll(KANTINE_3_CSS_LUNCH)) {
            final String content = p.getTextContent();
            if (content.startsWith(MittagstischUtil.getDay(0, true))) {
                found = true;
            } else if (content.startsWith(MittagstischUtil.getDay(1, true)) || content.startsWith("TÄGLICH")) {
                break;
            }
            if (found && !content.trim().isEmpty()) {
                lunch.setFood(lunch.getFood() == null ? content : lunch.getFood() + "<br><br>" + content);
            }
        }
    }

    public static Lunch lunchInLebensmittelSeidel() {
        final Lunch lunch = new Lunch("Lebensmittel Seidel Imbiss");
        lunch.setFood(String.format(MittagstischUtil.TECHNICAL, LEBENSMITTEL_SEIDEL_URL, LEBENSMITTEL_SEIDEL_URL));
        return lunch;
    }

    public static Lunch lunchInWullewupp() {
        final Lunch lunch = new Lunch("Wullewupp");
        lunch.setFood(String.format(MittagstischUtil.TECHNICAL, WULLEWUPP_URL, WULLEWUPP_URL));
        return lunch;
    }

}
