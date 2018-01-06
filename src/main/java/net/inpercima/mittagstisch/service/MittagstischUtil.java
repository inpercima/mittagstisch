package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischUtil {

    protected static final String OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    protected static final String NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Bitte unter 'nächste Woche' schauen.";

    private MittagstischUtil() {
        // not used
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
     * @param selector The selector to the inforamtion of week
     * @param page The page to be parsed
     * @return String The content including week information
     */
    public static String getWeek(final String selector, final HtmlPage page) {
        return page.querySelector(selector).getTextContent();
    }

    /**
     * Checks if the dates in the determined week are up-to-date.
     * 
     * @param weekText The determined information of week.
     * @return boolean True if up-to-date otherwise false
     */
    protected static boolean isInWeek(final String weekText, final int value) {
        final LocalDate now = LocalDate.now().plusDays(value);
        final TemporalField field = WeekFields.of(Locale.GERMAN).dayOfWeek();
        final LocalDate firstDay = now.with(field, 1);
        final LocalDate lastDay = now.with(field, 5);

        final DateTimeFormatter dd = DateTimeFormatter.ofPattern("dd.");
        final DateTimeFormatter ddMM = DateTimeFormatter.ofPattern("dd.MM");
        final DateTimeFormatter ddMMMM = DateTimeFormatter.ofPattern("dd.MMMM");
        final DateTimeFormatter ddSpaceMMMM = DateTimeFormatter.ofPattern("dd. MMMM YYYY");

        return
        // Kaiserbad
        (weekText.contains(firstDay.format(dd)) && weekText.contains(lastDay.format(ddSpaceMMMM)))
                // Kantine 3
                || (weekText.contains(firstDay.format(ddMMMM)) && weekText.contains(lastDay.format(ddMMMM)))
                // Wullewupp
                || (weekText.contains(firstDay.format(ddMM)) && weekText.contains(lastDay.format(ddMM)));
    }

    /**
     * Determine the day name for checks.
     * 
     * @param value The number of days added to the current day
     * @param toUppercase True if the result should be in uppercase otherwise false
     * @return String
     */
    protected static String getDay(final int value, final boolean toUppercase) {
        String day = LocalDate.now().plusDays(value).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
        return toUppercase ? day.toUpperCase() : day;
    }

    protected static Lunch prepareLunch(final HtmlPage page, final String name, final String selectorWeek,
            final String url, final boolean daily) {
        final Lunch lunch = new Lunch(name);
        final String weekText = MittagstischUtil.getWeek(selectorWeek, page);
        if (!MittagstischUtil.isInWeek(weekText, 0) && !MittagstischUtil.isInWeek(weekText, 7)) {
            lunch.setFood(String.format(OUTDATED, url, url));
        } else if (MittagstischUtil.isInWeek(weekText, 7) && daily) {
            lunch.setFood(NEXT_WEEK);
        }
        return lunch;
    }

}
