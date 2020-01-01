package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.stream.Stream;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Data
@Slf4j
abstract class Mittagstisch {

    private static final int IN_NEXT_WEEK = 7;

    private static final String STATUS_ERROR = "Derzeit kann aufgrund einer technischen Besonderheit keine Information zur Karte eingeholt werden. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String STATUS_NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Bitte unter 'nächste Woche' schauen.";

    private static final String STATUS_OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String DATE_FORMAT = "dd.MM.YYYY";

    private static final DateTimeFormatter LOGGER_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT);

    // global b/c of itteration for valid sections
    private static boolean found = false;

    private String lunchSelector;

    private String url;

    private String weekSelector;

    private String name;

    private boolean daily;

    private int days;

    private String detailSelector;

    private boolean dissabled;

    private HtmlPage htmlPage;

    /**
     * Prepares a Lunch with some predefined content if needed.
     *
     * @return State
     * @throws IOException
     */
    public Lunch prepare() throws IOException {
        final State state = new State();
        if (isDissabled()) {
            log.debug("prepare lunch for '{}' is dissabeld", getName());
            state.setStatusText(String.format(STATUS_ERROR, getUrl(), getUrl()));
            state.setStatus("status-error");
        } else {
            final String weekText = getWeek(getWeekSelector(), getUrl());
            log.debug("prepare lunch for '{}' with weektext '{}'", getName(), weekText);
            state.setStatus("status-success");
            if (!isInWeek(weekText, getDays()) && !isInWeek(weekText, IN_NEXT_WEEK)) {
                state.setStatusText(String.format(STATUS_OUTDATED, getUrl(), getUrl()));
                state.setStatus("status-outdated");
            } else if (isInWeek(weekText, IN_NEXT_WEEK) && isDaily() && getDays() == 0) {
                state.setStatusText(STATUS_NEXT_WEEK);
                state.setStatus("status-next-week");
            }
        }
        return parse(state);
    }

    /**
     * Filter a page
     *
     * @return Stream<DomNode>
     * @throws IOException
     */
    protected Stream<DomNode> filter(final String filter) throws IOException {
        getHtmlPage(getUrl());
        return getHtmlPage().querySelectorAll(getLunchSelector()).stream()
                .filter(span -> filterNodes(span, getDays(), filter, false));

    }

    /**
     * Prepares a Lunch with some predefined content if needed.
     *
     * @param state
     * @param food
     * @return Lunch
     * @throws IOException
     */
    public Lunch buildLunch(final State state, final String food) {
        final Lunch lunch = new Lunch();
        lunch.setName(getName());
        lunch.setFood(!state.getStatusText().isBlank() ? state.getStatusText() : food);
        lunch.setStatus(state.getStatus());
        return lunch;
    }

    abstract Lunch parse(final State state) throws IOException;

    /**
     * Determine the page to get information of the lunch.
     *
     * @param url The URL of the page which should be parsed
     * @return HtmlPage The page which should be parsed
     */
    protected void getHtmlPage(final String url) throws IOException {
        final WebRequest request = new WebRequest(new URL(url));
        request.setCharset(StandardCharsets.UTF_8);
        setHtmlPage(initWebClient().getPage(request));
    }

    /**
     * Init webclient with firefox browser and some options.
     *
     * @return WebClient The initialized client
     */
    private static WebClient initWebClient() {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
        final WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled(false);
        options.setUseInsecureSSL(true);
        options.setThrowExceptionOnScriptError(true);
        options.setThrowExceptionOnFailingStatusCode(true);
        return webClient;
    }

    /**
     * Determine the information of the week.
     *
     * @param selector The selector to the inforamtion of week
     * @param url      The url of the page to be parsed
     * @return String The content including week information
     * @throws IOException
     */
    protected String getWeek(final String selector, final String url) throws IOException {
        getHtmlPage(url);
        return getHtmlPage().querySelectorAll(selector).stream().filter(node -> !node.getTextContent().isEmpty())
                .findFirst().map(node -> node.getTextContent()).get();
    }

    /**
     * Checks if the dates in the determined week are up-to-date.
     *
     * @param weekText The determined information of week.
     * @param days     Days added to this day.
     * @return boolean True if up-to-date otherwise false
     */
    private static boolean isInWeek(final String weekText, final int days) {
        final LocalDate now = getLocalizedDate(days);
        final LocalDate firstDay = now.with(dayOfWeek(), 1);
        final LocalDate lastDay = now.with(dayOfWeek(), 5);

        final DateTimeFormatter d = DateTimeFormatter.ofPattern("dd.", Locale.GERMANY);
        final DateTimeFormatter dMM = DateTimeFormatter.ofPattern("d.MM", Locale.GERMANY);
        final DateTimeFormatter ddMMYYYY = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.GERMANY);
        final DateTimeFormatter dMMMM = DateTimeFormatter.ofPattern("d.MMMM", Locale.GERMANY);
        final DateTimeFormatter dMMMMYYYY = DateTimeFormatter.ofPattern("dd. MMMM YYYY", Locale.GERMANY);

        final int weekNumber = now.get(WeekFields.of(Locale.GERMANY).weekOfYear());

        final String formatFirsDay = firstDay.format(LOGGER_FORMAT);
        log.debug("first day in week '{}'", formatFirsDay);
        final String formatLastDay = lastDay.format(LOGGER_FORMAT);
        log.debug("last day in week '{}'", formatLastDay);

        final boolean kaiserbad = weekText.contains(firstDay.format(d)) && weekText.contains(lastDay.format(dMMMMYYYY));
        final boolean kantine3 = weekText.contains(firstDay.format(dMMMM).toUpperCase())
                && weekText.contains(lastDay.format(dMMMM).toUpperCase());
        final boolean lebensmittelSeidel = weekText.contains(firstDay.format(ddMMYYYY))
                && weekText.contains(lastDay.format(ddMMYYYY));
        final boolean pan = weekText.contains(String.valueOf(weekNumber))
                && (weekText.contains("KW") || weekText.contains("KARTE"));
        final boolean wullewupp = weekText.contains(firstDay.format(dMM)) && weekText.contains(lastDay.format(dMM));
        final boolean isInweek = lastDay.isAfter(now)
                && (kaiserbad || kantine3 || lebensmittelSeidel || pan || wullewupp);
        log.debug("is in week: '{}'", isInweek);

        return isInweek;
    }

    /**
     * Gets the TemporalField for day of the week.
     *
     * @return TemporalField
     */
    private static TemporalField dayOfWeek() {
        return WeekFields.of(Locale.GERMANY).dayOfWeek();
    }

    /**
     * Gets the current date with specified days added.
     *
     * @param days Days added to this day.
     * @return LocalDate
     */
    private static LocalDate getLocalizedDate(final int days) {
        final LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
        String format = now.format(LOGGER_FORMAT);
        log.debug("current date: '{}'", format);
        final LocalDate date = now.plusDays(days);
        format = date.format(LOGGER_FORMAT);
        log.debug("used date for weekcheck: '{}'", format);
        return date;
    }

    /**
     * Determine the day name for checks.
     *
     * @param toUppercase True if the result should be in uppercase otherwise false
     * @param days        The number of days added to the current day
     * @return String
     */
    protected static String getDay(final boolean toUppercase, final int days) {
        String day = getLocalizedDate(days).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
        return toUppercase ? day.toUpperCase() : day;
    }

    private static boolean filterNodes(final DomNode node, final int days, final String endText,
            final boolean uppercase) {

        // use regex '\u00A0' to match No-Break space (&nbsp;)
        final String content = node.getTextContent().replace('\u00A0', ' ').trim();
        if (startsWith(content, uppercase, days)) {
            found = true;
        } else if (startsWith(content, uppercase, days + 1) || content.startsWith(endText)) {
            found = false;
        }
        return found && !content.trim().equals(getDay(uppercase, days));
    }

    private static boolean startsWith(final String content, final boolean uppercase, final int days) {
        return content.startsWith(getDay(uppercase, days));
    }

}
