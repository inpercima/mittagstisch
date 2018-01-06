package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischCrawler {

    protected static final String URL_KANTINE_3 = "http://www.tapetenwerk.de/aktuelles/speiseplan-kantine/";

    protected static final String URL_LEBENSMITTEL_SEIDEL = "https://lebensmittel-imbiss-seidel.de/imbiss.htm";

    protected static final String URL_WULLEWUPP = "https://www.wullewupp.de/bar";

    protected static final String XPATH_LUNCH_KANTINE_3 = "//main/section/div/div/p";

    protected static final String XPATH_LUNCH_LEBENSMITTEL_SEIDEL = "";

    protected static final String XPATH_LUNCH_WULLEWUPP = "";

    protected static final String XPATH_WEEK_KANTINE_3 = "//main/section/div/div/h1";

    protected static final String XPATH_WEEK_LEBENSMITTEL_SEIDEL = "//div[@id='xr_td']/div/div/span";

    protected static final String XPATH_WEEK_WULLEWUPP = "//div[@id='SITE_PAGES']/div/div/div/h2/span";

    private static final String OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein.";

    protected static final Logger LOGGER = LoggerFactory.getLogger(MittagstischCrawler.class);

    private MittagstischCrawler() {
        // not used
    }

    /**
     * Creates the output for the lunch in "Kantine 3 (im Tapetenwerk)".
     *
     * @return Lunch Information about the lunch
     */
    public static Lunch lunchInKantine3() throws IOException {
        Lunch lunch = new Lunch("Kantine 3 (im Tapetenwerk)");
        final HtmlPage page = getHtmlPage(URL_KANTINE_3);
        if (isCorrectWeek(getWeek(XPATH_WEEK_KANTINE_3, page))) {
            parseKantine3Data(page, lunch);
        } else {
            lunch.setFood(OUTDATED);
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
        // details are in paragraphs
        List<HtmlParagraph> list = page.getByXPath(XPATH_LUNCH_KANTINE_3);

        boolean found = false;
        for (HtmlParagraph p : list) {
            if (p.getTextContent().startsWith(getDay(0, true))) {
                found = true;
            } else if (p.getTextContent().startsWith(getDay(1, true)) || p.getTextContent().startsWith("TÄGLICH")) {
                break;
            }
            if (found) {
                final String text = p.getTextContent();
                if (!text.trim().isEmpty()) {
                    lunch.setFood(lunch.getFood() == null ? text : lunch.getFood() + "<br><br>" + text);
                }
            }
        }
    }

    public static Lunch lunchInLebensmittelSeidel() throws IOException {
        Lunch lunch = new Lunch("Lebensmittel Seidel Imbiss");
        final HtmlPage page = getHtmlPage(URL_LEBENSMITTEL_SEIDEL);
        if (isCorrectWeek(getWeek(XPATH_WEEK_LEBENSMITTEL_SEIDEL, page))) {
            // TODO: implement parser
        } else {
            lunch.setFood(OUTDATED);
        }
        return lunch;
    }

    public static Lunch lunchInWullewupp() throws IOException {
        Lunch lunch = new Lunch("Wullewupp");
        final HtmlPage page = getHtmlPage(URL_WULLEWUPP);
        if (isCorrectWeek(getWeek(XPATH_WEEK_WULLEWUPP, page))) {
            // TODO: implement parser
        } else {
            lunch.setFood(OUTDATED);
        }
        return lunch;
    }

    /**
     * Determine the page to get information of the lunch.
     *
     * @param url The URL of the page which should be parsed
     * @return HtmlPage The page which should be parsed
     */
    public static HtmlPage getHtmlPage(final String url) throws IOException {
        final WebRequest request = new WebRequest(new URL(url));
        request.setCharset(StandardCharsets.UTF_8);
        return initWebClient().getPage(request);
    }

    /**
     * Init webclient with firefox browser and some options.
     *
     * @return WebClient The initialized client
     */
    private static WebClient initWebClient() {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
        final WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled(true);
        options.setUseInsecureSSL(true);
        options.setThrowExceptionOnScriptError(true);
        options.setThrowExceptionOnFailingStatusCode(true);
        return webClient;
    }

    /**
     * Determine the information of the week.
     * 
     * @param xpath The path to the inforamtion of week
     * @param page The page to be parsed
     * @return String The content including week information
     */
    public static String getWeek(final String xpath, final HtmlPage page) {
        return xpath == XPATH_WEEK_KANTINE_3 ? ((HtmlHeading1) page.getByXPath(xpath).get(0)).getTextContent()
                : ((HtmlSpan) page.getByXPath(xpath).get(0)).getTextContent();
    }

    /**
     * Checks if the dates in the determined week are up-to-date.
     * 
     * @param weekText The determined information of week.
     * @return boolean True if up-to-date otherwise false
     */
    private static boolean isCorrectWeek(final String weekText) {
        LocalDate now = LocalDate.now();
        final TemporalField field = WeekFields.of(Locale.GERMAN).dayOfWeek();
        final LocalDate firstDay = now.with(field, 1);
        final LocalDate lastDay = now.with(field, 5);

        return (weekText.contains(firstDay.format(DateTimeFormatter.ofPattern("dd.MM")))
                || weekText.contains(firstDay.format(DateTimeFormatter.ofPattern("dd.MMMM"))))
                && (weekText.contains(lastDay.format(DateTimeFormatter.ofPattern("dd.MM")))
                        || weekText.contains(lastDay.format(DateTimeFormatter.ofPattern("dd.MMMM"))));
    }

    /**
     * Determine the day name for checks.
     * 
     * @param value The number of days added to the current day
     * @param toUppercase True if the result should be in uppercase otherwise false
     * @return String
     */
    private static String getDay(final int value, final boolean toUppercase) {
        String day = LocalDate.now().plusDays(value).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
        return toUppercase ? day.toUpperCase() : day;
    }

}
