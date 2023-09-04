package net.inpercima.mittagstisch.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Data
@Slf4j
abstract class Mittagstisch {

    private static final String STATUS_ERROR = "Oops, wir können leider keine Informationen zu '%s' einholen. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String STATUS_NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String STATUS_OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private Bistro bistro;

    private HtmlPage htmlPage;

    private String weekText;

    private State state;

    /**
     * Checks if the dates in the determined week are up-to-date.
     *
     * @param checkForNextWeek[contains(text(),'Speiseplan'
     * @return boolean True if up-to-date otherwise false
     */
    abstract boolean isWithinWeek(final boolean checkForNextWeek);

    /**
     * Prepares a lunch with some predefined content if needed.
     *
     * @throws IOException
     */
    public void prepare() throws IOException {
        final String url = this.getBistro().getUrl();
        final State state = new State();
        if (this.getBistro().isDisabled()) {
            log.debug("prepare lunch for '{}' is dissabeld", this.getBistro().getName());
            state.setStatusText(String.format(STATUS_ERROR, this.getBistro().getName(), url,
                    url));
            state.setStatus("status-error");
        } else {
            setHtmlPage(MittagstischUtils.determineHtmlPage(url));
            setWeekText(MittagstischUtils.determineWeekText(getHtmlPage(), this.getBistro()));
            final int days = this.getBistro().getDays();

            if ((days == 0 || days == 1) && isWithinWeek(false)) {
                state.setStatusText("");
                state.setStatus("status-success");
            } else if ((days == 0 || days == 1) && !isWithinWeek(false) && isWithinWeek(true)) {
                state.setStatusText(String.format(STATUS_NEXT_WEEK, url, url));
                state.setStatus("status-next-week");
            } else {
                state.setStatusText(String.format(STATUS_OUTDATED, url, url));
                state.setStatus("status-outdated");
            }
        }
        this.setState(state);
    }

    /**
     * Build a lunch with all collected data.
     *
     * @param meal
     * @return Lunch
     */
    public Lunch buildLunch(final String meal) {
        final Lunch lunch = new Lunch();
        lunch.setBistroName(this.getBistro().getName());
        lunch.setMeal(StringUtils.isNotBlank(state.getStatusText()) ? state.getStatusText() : meal);
        lunch.setStatus(this.getState().getStatus());
        if (getBistro().isPdf()) {
            lunch.setPdfLink(getWeekText());
        }
        return lunch;
    }
}
