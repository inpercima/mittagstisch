package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

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

    // monday - friday used so just 5 days
    private static final int IN_NEXT_WEEK = 5;

    private static final String STATUS_ERROR = "Oops, wir können leider keine Informationen zu '%s' einholen. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String STATUS_NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Schau bitte unter 'nächste Woche'";

    private static final String STATUS_OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    protected static final String DATE_FORMAT = "dd.MM.yyyy";

    protected static final DateTimeFormatter d = DateTimeFormatter.ofPattern("d.", Locale.GERMANY);

    protected static final DateTimeFormatter dM = DateTimeFormatter.ofPattern("d.M", Locale.GERMANY);

    protected static final DateTimeFormatter dMM = DateTimeFormatter.ofPattern("d.MM", Locale.GERMANY);

    protected static final DateTimeFormatter ddMM = DateTimeFormatter.ofPattern("dd.MM", Locale.GERMANY);

    protected static final DateTimeFormatter dMMMM = DateTimeFormatter.ofPattern("d.MMMM", Locale.GERMANY);

    protected static final DateTimeFormatter ddMMYY = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.GERMANY);

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

    private String weekSelectorXPath;

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
            state.setStatusText(String.format(STATUS_ERROR, getName(), getUrl(), getUrl()));
            state.setStatus("status-error");
        } else {
            determineHtmlPage();
            determineWeekText();

            state.setStatusText(String.format(STATUS_OUTDATED, getUrl(), getUrl()));
            state.setStatus("status-outdated");
            if ((getDays() == 0 || getDays() == 1) && isWithinWeek(false)) {
                state.setStatusText("");
                state.setStatus("status-success");
            } else if (getDays() == 7 && isWithinWeek(false)) {
                state.setStatusText("");
                state.setStatus("status-success");
            } else if ((getDays() == 0 || getDays() == 1) && !isWithinWeek(false) && isWithinWeek(true)) {
                state.setStatusText(STATUS_NEXT_WEEK);
                state.setStatus("status-next-week");
            }
        }
        return parse(state);
    }

    /**
     * Determine the page to get information of the lunch.
     *
     * @return HtmlPage The page which should be parsed
     */
    protected void determineHtmlPage() throws IOException {
        final WebRequest request = new WebRequest(new URL(getUrl()));
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
     * @return String The content including week information
     * @throws IOException
     */
    protected void determineWeekText() throws IOException {
        String weekText = getHtmlPage().querySelectorAll(getWeekSelector()).stream()
                .filter(node -> StringUtils.isNotBlank(filterSpecialChars(node))).findFirst()
                .map(node -> node.getTextContent()).get();
        if (StringUtils.isBlank(weekText)) {
            weekText = ((DomNode) getHtmlPage()
                    .getFirstByXPath(getWeekSelectorXPath()))
                    .asNormalizedText();
        }
        setWeekText(weekText.replace(" ", StringUtils.EMPTY).trim().toUpperCase());
        log.debug("prepare lunch for: '{}' with weektext: '{}'", getName(),
                getWeekText());
    }

    /**
     * Filter out special chars from given node.
     *
     * @param node The element to filter
     * @return String
     */
    private static String filterSpecialChars(final DomNode node) {
        // use regex '\u00A0' to match no-break space (&nbsp;)
        return node.getTextContent().replace("\u00A0", " ").trim().toUpperCase();
    }

    /**
     * Checks if the dates in the determined week are up-to-date.
     *
     * @param checkForNextWeek
     * @return boolean True if up-to-date otherwise false
     */
    abstract boolean isWithinWeek(final boolean checkForNextWeek);

    /**
     * Checks if the selected day is within the week.
     * 
     * @return boolean True if within week otherwise false
     */
    protected boolean isWithinRange(final LocalDate firstDate, final LocalDate lastDate,
            final boolean checkForNextWeek) {
        final LocalDate now = getLocalizedDate(checkForNextWeek);
        log.debug("used day: '{}'", now);
        return now.isEqual(firstDate) || now.isEqual(lastDate) || (now.isAfter(firstDate) && now.isBefore(lastDate));
    }

    /**
     * Gets the current date with specified days added to simulate today (0 days),
     * tomorrow (1 day) and next week (7 days).
     *
     * @return LocalDate
     */
    protected LocalDate getLocalizedDate(final boolean checkForNextWeek) {
        final LocalDate now = LocalDate.now(ZoneId.of("Europe/Berlin"));
        final LocalDate date = now.plusDays(checkForNextWeek ? IN_NEXT_WEEK : getDays());
        return date;
    }

    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    protected LocalDate getWeekDate(final String date) {
        final LocalDate now = getLocalizedDate(false);
        final LocalDate firstDay = now.with(WeekFields.of(Locale.GERMANY).dayOfWeek(), 1);
        log.debug(date + " day in week '{}'", firstDay.format(ddMMYYYY));
        return firstDay;
    }

    /**
     * Checks if the selected day is within the week.
     * 
     * @param days
     * @return boolean True if within week otherwise false
     */
    protected boolean isWithinRange(final int days) {
        final LocalDate now = getLocalizedDate(false);
        final LocalDate firstDay = getWeekDate("first");
        final LocalDate lastDay = getWeekDate("last");
        return now.isEqual(firstDay) || now.isEqual(lastDay) || (now.isAfter(firstDay) && now.isBefore(lastDay));
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
     * Determine the day name for checks.
     *
     * @param days The number of days added to the current day
     * @return String
     */
    protected String getDay(final int days) {
        String day = getLocalizedDate(false).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
        return day.toUpperCase();
    }

    /**
     * Determine the day name for checks.
     *
     * @param node    The element to filter
     * @param days    The number of days added to the current day
     * @param endText The text at the end of the lunch section
     * @return boolean
     */
    private boolean filterNodes(final DomNode node, final int days, final String endText) {
        // use regex '\u00A0' to match no-break space (&nbsp;)
        final String content = node.getTextContent().replace("\u00A0", " ").trim().toUpperCase();
        if (startsWithDayname(content, days)) {
            found = true;
        } else if (startsWithDayname(content, days + 1) || content.startsWith(endText)) {
            found = false;
        }
        // just return true if content not dayname
        return found && !content.equals(getDay(days));
    }

    private boolean startsWithDayname(final String content, final int days) {
        return content.startsWith(getDay(days));
    }

    // protected boolean weekTextContains(final int days, final DateTimeFormatter
    // formatter) {
    // return weekTextContains(firstDay(days).format(formatter)) &&
    // weekTextContains(lastDay(days).format(formatter));
    // }

    // protected boolean weekTextContains(final int days, final DateTimeFormatter
    // formatterA,
    // final DateTimeFormatter formatterB) {
    // return weekTextContains(firstDay(days).format(formatterA))
    // && weekTextContains(lastDay(days).format(formatterB));
    // }

    // protected boolean weekTextContains(final String sequenceA, final String
    // sequenceB) {
    // return weekTextContains(sequenceA) && weekTextContains(sequenceB);
    // }

    // private boolean weekTextContains(final String seqeunce) {
    // return getWeekText().contains(seqeunce.toUpperCase());
    // }
}
