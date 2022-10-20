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

import org.apache.commons.lang3.StringUtils;

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

    protected static final String DATE_FORMAT = "dd.MM.yyyy";

    protected static final DateTimeFormatter LOGGER_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT);

    protected static final DateTimeFormatter d = DateTimeFormatter.ofPattern("d.", Locale.GERMANY);

    protected static final DateTimeFormatter dM = DateTimeFormatter.ofPattern("d.M", Locale.GERMANY);

    protected static final DateTimeFormatter dMM = DateTimeFormatter.ofPattern("d.MM", Locale.GERMANY);

    protected static final DateTimeFormatter ddMM = DateTimeFormatter.ofPattern("dd.MM", Locale.GERMANY);

    protected static final DateTimeFormatter dMMMM = DateTimeFormatter.ofPattern("d.MMMM", Locale.GERMANY);

    protected static final DateTimeFormatter ddMMYYYY = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.GERMANY);

    private boolean daily;

    private int days;

    private boolean dissabled;

    // global b/c of itteration for valid sections
    private static boolean found = false;

    private HtmlPage htmlPage;

    private String lunchSelector;

    private String name;

    private String url;

    private String weekSelector;

    private String originalWeekText;

    private String weekText;

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
            getHtmlPage(getUrl());
            determineWeekText(getWeekSelector());
            log.debug("prepare lunch for '{}' with original weektext '{}' and modified weektext '{}'", getName(),
                    getOriginalWeekText(), getWeekText());
            state.setStatus("status-success");
            if (!isWithinWeek(getDays()) && !isWithinWeek(IN_NEXT_WEEK)) {
                state.setStatusText(String.format(STATUS_OUTDATED, getUrl(), getUrl()));
                state.setStatus("status-outdated");
            } else if (isWithinWeek(IN_NEXT_WEEK) && isDaily() && getDays() == 0) {
                state.setStatusText(STATUS_NEXT_WEEK);
                state.setStatus("status-next-week");
            }
        }
        return parse(state);
    }

    /**
     * Filter a page.
     *
     * @return Stream<DomNode>
     * @throws IOException
     */
    protected Stream<DomNode> filter(final String filter) throws IOException {
        return getHtmlPage().querySelectorAll(getLunchSelector()).stream()
                .filter(node -> filterNodes(node, getDays(), filter));

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
        lunch.setFood(StringUtils.isNotBlank(state.getStatusText()) ? state.getStatusText() : food);
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
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
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
     * @param selector The selector to the information of week
     * @return String The content including week information
     * @throws IOException
     */
    protected void determineWeekText(final String selector) throws IOException {
        final String weekText = getHtmlPage().querySelectorAll(selector).stream()
                .filter(node -> !node.getTextContent().isEmpty()).findFirst().map(node -> node.getTextContent()).get();
        setOriginalWeekText(weekText);
        setWeekText(weekText.replace(" ", StringUtils.EMPTY).toUpperCase());
    }

    /**
     * Checks if the dates in the determined week are up-to-date.
     *
     * @param days Days added to this day.
     * @return boolean True if up-to-date otherwise false
     */
    abstract boolean isWithinWeek(final int days);

    /**
     * Gets the TemporalField for day of the week.
     *
     * @return TemporalField
     */
    protected static TemporalField dayOfWeek() {
        return WeekFields.of(Locale.GERMANY).dayOfWeek();
    }

    /**
     * Gets the current date with specified days added.
     *
     * @param days Days added to this day.
     * @return LocalDate
     */
    protected static LocalDate getLocalizedDate(final int days) {
        final LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
        final LocalDate date = now.plusDays(days);
        return date;
    }

    protected static LocalDate firstDay(final int days) {
        final LocalDate now = getLocalizedDate(days);
        final LocalDate firstDay = now.with(dayOfWeek(), 1);
        log.debug("first day in week '{}'", firstDay.format(LOGGER_FORMAT));
        return firstDay;
    }

    protected static LocalDate lastDay(final int days) {
        final LocalDate now = getLocalizedDate(days);
        final LocalDate lastDay = now.with(dayOfWeek(), 5);
        log.debug("last day in week '{}'", lastDay.format(LOGGER_FORMAT));
        return lastDay;
    }

    /**
     * Determine the day name for checks.
     *
     * @param days        The number of days added to the current day
     * @return String
     */
    protected static String getDay(final int days) {
        String day = getLocalizedDate(days).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
        return day.toUpperCase();
    }

    /**
     * Determine the day name for checks.
     *
     * @param node      The element to filter
     * @param days      The number of days added to the current day
     * @param endText   The text at the end of the lunch section
     * @return boolean
     */
    private static boolean filterNodes(final DomNode node, final int days, final String endText) {
        // use regex '\u00A0' to match No-Break space (&nbsp;)
        final String content = node.getTextContent().replace("\u00A0", " ").trim().toUpperCase();
        if (startsWithDayname(content, days)) {
            found = true;
        } else if (startsWithDayname(content, days + 1) || content.startsWith(endText)) {
            found = false;
        }
        // just return true if content not dayname
        return found && !content.equals(getDay(days));
    }

    private static boolean startsWithDayname(final String content, final int days) {
        return content.startsWith(getDay(days));
    }

    protected boolean isWithinRange(final int days) {
        final LocalDate now = getLocalizedDate(days);
        final LocalDate firstDay = firstDay(days);
        final LocalDate lastDay = lastDay(days);
        return now.isEqual(firstDay) || now.isEqual(lastDay) || (now.isAfter(firstDay) && now.isBefore(lastDay));
    }

    protected boolean weekTextContains(final int days, final DateTimeFormatter formatter) {
        return weekTextContains(firstDay(days).format(formatter)) && weekTextContains(lastDay(days).format(formatter));
    }

    protected boolean weekTextContains(final int days, final DateTimeFormatter formatterA,
            final DateTimeFormatter formatterB) {
        return weekTextContains(firstDay(days).format(formatterA))
                && weekTextContains(lastDay(days).format(formatterB));
    }

    protected boolean weekTextContains(final String sequenceA, final String sequenceB) {
        return weekTextContains(sequenceA) && weekTextContains(sequenceB);
    }

    private boolean weekTextContains(final String seqeunce) {
        return getWeekText().contains(seqeunce.toUpperCase());
    }
}
