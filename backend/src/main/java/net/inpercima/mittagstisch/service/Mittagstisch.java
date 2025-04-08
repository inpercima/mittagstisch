package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.BrowserVersion;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.WebClientOptions;
import org.htmlunit.WebRequest;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Data
@Slf4j
abstract class Mittagstisch {

    protected final String bistroJson;

    protected Mittagstisch(String bistroJson) {
        this.bistroJson = bistroJson;
    }

    /**
     * Checks if the dates in the determined week are up-to-date.
     *
     * @param bistro
     * @param weekText
     * @param checkForNextWeek
     * @return boolean True if up-to-date otherwise false
     * @throws Exception
     */
    abstract boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek)
            throws Exception;

    /**
     * @param bistro
     * @param htmlPage
     * @param mainContent
     * @return String
     */
    abstract String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent);

    protected Lunch crawlLunch(final Bistro bistro) {
        String content = "";
        State state = new State();
        if (bistro.isDisabled()) {
            log.debug("preparing lunch for '{}' is disabled", bistro.getName());
            state = MittagstischUtils.setDisabledState(bistro.getName());
        } else {
            HtmlPage htmlPage;
            try {
                htmlPage = determineHtmlPage((bistro.getUrl()));
                if (bistro.isDocument()) {
                    content = determineDocumentLink(htmlPage, bistro);
                    state = MittagstischUtils.setSuccessState(bistro.getName());
                    content = crawlSpecificData(bistro, htmlPage, content);
                } else {
                    boolean findWeekText = true;
                    // for biomare and other with special weekText detection
                    if (bistro.getCssWeekSelector().contains("%d")) {
                        final String day = MittagstischUtils.getDay(bistro.getDays());
                        System.out.println(day);
                        if ("Samstag".equals(day) || "Sonntag".equals(day)) {
                            findWeekText = false;
                            state = MittagstischUtils.setOutdatedState(bistro.getName());
                        } else {
                            bistro.setCssWeekSelector(String.format(bistro.getCssWeekSelector(), bistro.getDays() + 1));
                        }
                    }
                    if (findWeekText) {
                        final String weekText = determineWeekText(htmlPage, bistro);
                        state = checkWeekAndGetState(bistro, weekText);
                        if (StringUtils.isBlank(state.getStatusText())) {
                            content = crawlSpecificData(bistro, htmlPage, content);
                        }
                    }
                }
            } catch (Exception e) {
                state = MittagstischUtils.setErrorState(bistro.getName());
                if (e instanceof MalformedURLException) {
                    log.error("URL could not be parsed.", e);
                } else {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return buildLunch(bistro, state, content);
    }

    /**
     * Init webclient with firefox browser and some options and requests the bistro
     * url to get the htmlpage.
     *
     * @param url
     * @return HtmlPage
     * @throws URISyntaxException
     * @throws IOException
     * @throws FailingHttpStatusCodeException
     */
    public static HtmlPage determineHtmlPage(final String url)
            throws FailingHttpStatusCodeException, URISyntaxException, IOException {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        final WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled(false);
        options.setUseInsecureSSL(true);
        options.setThrowExceptionOnScriptError(true);
        options.setThrowExceptionOnFailingStatusCode(true);
        final WebRequest request = new WebRequest(new URI(url).toURL());
        request.setCharset(StandardCharsets.UTF_8);
        return webClient.getPage(request);
    }

    /**
     * Determine the information of the week.
     *
     * @return String The content including week information
     */
    private String determineWeekText(HtmlPage htmlPage, Bistro bistro) {
        String weekText = StringUtils.EMPTY;
        String originalWeekText = htmlPage.querySelectorAll(bistro.getCssWeekSelector()).stream()
                .filter(node -> StringUtils.isNotBlank(MittagstischUtils.filterSpecialChars(node))).findFirst()
                .map(node -> node.asNormalizedText()).get();
        if (StringUtils.isBlank(originalWeekText)) {
            originalWeekText = ((DomNode) htmlPage
                    .getFirstByXPath(bistro.getXpathWeekSelector()))
                    .asNormalizedText();
        }
        weekText = originalWeekText.replace(" ", StringUtils.EMPTY).trim();
        log.debug("prepare lunch for: '{}' with weektext: '{}'", bistro.getName(), weekText);
        return weekText;
    }

    private String determineDocumentLink(HtmlPage htmlPage, Bistro bistro)
            throws MalformedURLException, URISyntaxException {
        String documentLink = htmlPage.querySelector(bistro.getCssWeekSelector()).getAttributes()
                .getNamedItem("href")
                .getNodeValue();
        if (!bistro.isDocumentFullPath()) {
            final URL url = new URI(bistro.getUrl()).toURL();
            final String host = url.getProtocol() + "://" + url.getHost();
            documentLink = host + documentLink;
        }
        log.debug("prepare lunch for: '{}' with document link: '{}'", bistro.getName(), documentLink);
        return documentLink;
    }

    private State checkWeekAndGetState(final Bistro bistro, final String weekText) throws Exception {
        final int days = bistro.getDays();
        State state = new State();
        if ((days == 0 || days == 1) && isWithinRange(bistro, weekText, false)) {
            state = MittagstischUtils.setSuccessState(bistro.getName());
        } else if ((days == 0 || days == 1) && !isWithinRange(bistro, weekText, false)
                && isWithinRange(bistro, weekText, true)) {
            state = MittagstischUtils.setNextWeekState(bistro.getName());
        } else {
            state = MittagstischUtils.setOutdatedState(bistro.getName());
        }
        return state;
    }

    /**
     * Build a lunch with all collected data.
     *
     * @param bistro
     * @param content
     * @param state
     * @return Lunch
     */
    public Lunch buildLunch(final Bistro bistro, final State state, final String content) {
        final Lunch lunch = new Lunch();
        lunch.setBistroName(bistro.getName());
        lunch.setUrl(bistro.getUrl());
        lunch.setContent(StringUtils.isNotBlank(state.getStatusText()) ? state.getStatusText() : content);
        lunch.setStatus(state.getStatus());
        return lunch;
    }

    public static boolean isWithinRange(final boolean checkForNextWeek, final String weekText, final int days,
            final String regex, final DateTimeFormatter dateFormat, final String suffix1, final String suffix2)
            throws Exception {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(weekText);
        LocalDate firstDate = null;
        LocalDate lastDate = null;
        while (matcher.find()) {
            if (firstDate == null) {
                firstDate = getDateForRangeCheck(dateFormat, suffix1, matcher, firstDate, "firstDate");
            } else {
                lastDate = getDateForRangeCheck(dateFormat, suffix2, matcher, lastDate, "lastDate");
            }
        }
        final boolean isWithinRange = MittagstischUtils.isWithinRange(firstDate, lastDate, checkForNextWeek,
                days);
        log.debug("is in week: '{}'", isWithinRange);
        return isWithinRange;
    }

    private static LocalDate getDateForRangeCheck(final DateTimeFormatter dateFormat, final String suffix,
            Matcher matcher,
            LocalDate date, String marker) {
        try {
            date = LocalDate.parse(matcher.group(1) + suffix, dateFormat);
            log.debug("extracted {}: '{}'", marker, date);
        } catch (DateTimeParseException e) {
            if (matcher.group(1).length() == 5) {
                date = LocalDate.parse(matcher.group(1) + "." + LocalDate.now().getYear() + suffix,
                        MittagstischUtils.ddMMYYYY);
                log.debug("second time extracted {}: '{}'", marker, date);
            }
            if (matcher.group(1).length() == 10) {
                date = LocalDate.parse(matcher.group(1), MittagstischUtils.ddMMYYYY);
                log.debug("second time extracted {}: '{}'", marker, date);
            }
        }
        return date;
    }
}
