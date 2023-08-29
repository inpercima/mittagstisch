package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;

@UtilityClass
@Slf4j
public class MittagstischUtils {

    // monday - friday used so just 5 days
    private static final int IN_NEXT_WEEK = 5;

    private static final String DATE_FORMAT = "dd.MM.yyyy";

    protected static final DateTimeFormatter ddMMYY = new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("dd.MM.yy")
            .toFormatter(Locale.GERMANY);

    protected static final DateTimeFormatter dMMMMYYYY = new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d.MMMMyyyy")
            .toFormatter(Locale.GERMANY);

    protected static final DateTimeFormatter ddMMYYYY = new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern(DATE_FORMAT)
            .toFormatter(Locale.GERMANY);

    /**
     * Init webclient with firefox browser and some options.
     *
     * @return WebClient The initialized client
     */
    public WebClient initWebClient() {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        final WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled(false);
        options.setUseInsecureSSL(true);
        options.setThrowExceptionOnScriptError(true);
        options.setThrowExceptionOnFailingStatusCode(true);
        return webClient;
    }

    /**
     * Determine the page to get information of the lunch.
     *
     * @param url
     * @return HtmlPage The page which should be parsed
     */
    public HtmlPage determineHtmlPage(final String url) throws IOException {
        final WebRequest request = new WebRequest(new URL(url));
        request.setCharset(StandardCharsets.UTF_8);
        return initWebClient().getPage(request);
    }

    /**
     * Determine the information of the week.
     *
     * @param HtmlPage
     * @param Bistro
     * @return String The content including week information
     * @throws IOException
     */
    public String determineWeekText(final HtmlPage htmlPage, final Bistro bistro) throws IOException {
        String weekText = StringUtils.EMPTY;
        if (bistro.isPdf()) {
            weekText = htmlPage.querySelector(bistro.getWeekSelector()).getAttributes().getNamedItem("href").getNodeValue();
            log.debug("prepare lunch for: '{}' with pdf link: '{}'", bistro.getName(), weekText);
        } else {
            String originalWeekText = htmlPage.querySelectorAll(bistro.getWeekSelector()).stream()
                    .filter(node -> StringUtils.isNotBlank(MittagstischUtils.filterSpecialChars(node))).findFirst()
                    .map(node -> node.getTextContent()).get();
            if (StringUtils.isBlank(originalWeekText)) {
                originalWeekText = ((DomNode) htmlPage
                        .getFirstByXPath(bistro.getWeekSelectorXPath()))
                        .asNormalizedText();
            }
            weekText = originalWeekText.replace(" ", StringUtils.EMPTY).trim();
            log.debug("prepare lunch for: '{}' with weektext: '{}'", bistro.getName(), weekText);
        }
        return weekText;
    }

    /**
     * Filter out special chars from given node.
     *
     * @param node The element to filter
     * @return String
     */
    public String filterSpecialChars(final DomNode node) {
        // use regex '\u00A0' to match no-break space (&nbsp;)
        return node.getTextContent().replace("\u00A0", " ").trim().toUpperCase();
    }

    /**
     * Gets the current date with specified days added to simulate today (0 days),
     * tomorrow (1 day) and next week (5 days - mo-fr).
     *
     * @return LocalDate
     */
    public LocalDate getLocalizedDate(final boolean checkForNextWeek, final int days) {
        final LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
        final LocalDate date = now.plusDays(checkForNextWeek ? IN_NEXT_WEEK : days);
        return date;
    }

    /**
     * Determine the specified day name.
     *
     * @param days The number of days added to the current day
     * @return String
     */
    public String getDay(final int days) {
        return getLocalizedDate(false, days).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek, final String weekText, final int days,
            final String regex, final DateTimeFormatter dateFormat, final String suffix1, final String suffix2) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(weekText);
        LocalDate firstDate = null;
        LocalDate lastDate = null;
        while (matcher.find()) {
            if (firstDate == null) {
                firstDate = LocalDate.parse(matcher.group(1) + suffix1, dateFormat);
                log.debug("extracted firstDate: '{}'", firstDate);
            } else {
                lastDate = LocalDate.parse(matcher.group(1) + suffix2, dateFormat);
                log.debug("extracted lastDate: '{}'", lastDate);
            }
        }
        final boolean isWithinWeek = isWithinRange(firstDate, lastDate, checkForNextWeek,
                days);
        log.debug("is in week: '{}'", isWithinWeek);
        return isWithinWeek;
    }

    /**
     * Checks if the selected day is within the week.
     * 
     * @return boolean True if within week otherwise false
     */
    public boolean isWithinRange(final LocalDate firstDate, final LocalDate lastDate,
            final boolean checkForNextWeek, final int days) {
        final LocalDate now = getLocalizedDate(checkForNextWeek, days);
        log.debug("used day: '{}'", now);
        return now.isEqual(firstDate) || now.isEqual(lastDate) || (now.isAfter(firstDate) && now.isBefore(lastDate));
    }
}
