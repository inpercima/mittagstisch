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

    private static void parseKantine3Data(final HtmlPage page, final Lunch lunch) {
        List<HtmlParagraph> list = page.getByXPath(XPATH_LUNCH_KANTINE_3);
        boolean foundDay = false;

        for (HtmlParagraph p : list) {
            if (foundDay) {
                if (p.getTextContent().startsWith(getDay(1, true)) || p.getTextContent().startsWith("TÄGLICH")) {
                    foundDay = false;
                } else {
                    final String text = p.getTextContent();
                    if (!text.trim().isEmpty()) {
                        lunch.setFood(lunch.getFood() == null ? text : lunch.getFood() + "<br><br>" + text);
                    }
                }
            }
            if (p.getTextContent().startsWith(getDay(0, true))) {
                foundDay = true;
            }
        }
    }

    public static Lunch lunchInLebensmittelSeidel() throws IOException {
        Lunch lunch = new Lunch("Lebensmittel Seidel Imbiss 3");
        final HtmlPage page = getHtmlPage(URL_LEBENSMITTEL_SEIDEL);
        if (isCorrectWeek(getWeek(XPATH_WEEK_LEBENSMITTEL_SEIDEL, page))) {
            // parseLunchData(XPATH_LUNCH_LEBENSMITTEL_SEIDEL, page, lunch);
        } else {
            lunch.setFood(OUTDATED);
        }
        return lunch;
    }

    public static Lunch lunchInWullewupp() throws IOException {
        Lunch lunch = new Lunch("Wullewupp");
        final HtmlPage page = getHtmlPage(URL_WULLEWUPP);
        if (isCorrectWeek(getWeek(XPATH_WEEK_WULLEWUPP, page))) {
            // parseLunchData(XPATH_LUNCH_WULLEWUPP, page, lunch);
        } else {
            lunch.setFood(OUTDATED);
        }
        return lunch;
    }

    public static HtmlPage getHtmlPage(final String url) throws IOException {
        final WebRequest request = new WebRequest(new URL(url));
        request.setCharset(StandardCharsets.UTF_8);
        return initWebClient().getPage(request);
    }

    /**
     * Init webclient with Firefox browser.
     *
     * @return WebClient
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

    public static String getWeek(final String xpath, final HtmlPage page) {
        String weekText = "";
        if (xpath == XPATH_WEEK_KANTINE_3) {
            final HtmlHeading1 h1 = (HtmlHeading1) page.getByXPath(xpath).get(0);
            weekText = h1.getTextContent();
        } else {
            final HtmlSpan span = (HtmlSpan) page.getByXPath(xpath).get(0);
            weekText = span.getTextContent();
        }
        return weekText;
    }

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

    private static String getDay(final int value, final boolean toUppercase) {
        String day = LocalDate.now().plusDays(value).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
        return toUppercase ? day.toUpperCase() : day;
    }

}
