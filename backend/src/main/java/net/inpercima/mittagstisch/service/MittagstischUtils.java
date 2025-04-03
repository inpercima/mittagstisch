package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Locale;

import org.htmlunit.html.DomNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.State;

@UtilityClass
@Slf4j
public class MittagstischUtils {

    // monday - friday used so just 5 days
    private static final int IN_NEXT_WEEK = 5;

    private static final String DATE_FORMAT = "dd.MM.yyyy";

    private static final String STATUS_DISABLED = "Das Prüfen von '%s' ist deaktiviert.";

    private static final String STATUS_ERROR = "Oops, wir können leider keine Informationen zu '%s' einholen. Bitte prüfe manuell.";

    private static final String STATUS_NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Bitte prüfe manuell.";

    private static final String STATUS_OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell.";

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
     * Filter out special chars from given node.
     *
     * @param node The element to filter
     * @return String
     */
    public static String filterSpecialChars(final DomNode node) {
        // use regex '\u00A0' to match no-break space (&nbsp;)
        return node.getTextContent().replace("\u00A0", " ").trim().toUpperCase();
    }

    /**
     * Gets the current date with specified days added to simulate today (0 days),
     * tomorrow (1 day) and next week (5 days - mo-fr).
     *
     * @return LocalDate
     */
    public static LocalDate getLocalizedDate(final boolean checkForNextWeek, final int days) {
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
    public static String getDay(final int days) {
        return getLocalizedDate(false, days).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
    }

    /**
     * Checks if the selected day is within the week.
     * 
     * @return boolean True if within week otherwise false
     */
    public static boolean isWithinRange(final LocalDate firstDate, final LocalDate lastDate,
            final boolean checkForNextWeek, final int days) throws Exception {
        final LocalDate now = getLocalizedDate(checkForNextWeek, days);
        log.debug("used day: '{}'", now);
        return now.isEqual(firstDate) || now.isEqual(lastDate) || (now.isAfter(firstDate) && now.isBefore(lastDate));
    }

    public State setSuccessState(final String bistroName) {
        final State state = new State();
        state.setStatusText("");
        state.setStatus("status-success");
        return state;
    }

    public State setNextWeekState(final String bistroName) {
        return setState(bistroName, STATUS_NEXT_WEEK, "next-week");
    }

    public State setOutdatedState(final String bistroName) {
        return setState(bistroName, STATUS_OUTDATED, "outdated");
    }

    public State setDisabledState(final String bistroName) {
        return setState(bistroName, STATUS_DISABLED, "warning");
    }

    public State setErrorState(final String bistroName) {
        return setState(bistroName, STATUS_ERROR, "error");
    }

    private State setState(final String bistroName, final String status, final String statusType) {
        final State state = new State();
        state.setStatusText(String.format(status, bistroName));
        state.setStatus("status-" + statusType);
        return state;
    }

    public static Bistro readBistroConfig(final String bistroJson, final String bistroId) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        Bistro bistro = new Bistro();
        try {
            jsonNode = mapper.readTree(bistroJson).get(bistroId);
            bistro = mapper.convertValue(jsonNode, Bistro.class);
        } catch (IOException e) {
            log.error("Reading configuration file 'bistro.json' failed.");
        }
        return bistro;
    }
}
