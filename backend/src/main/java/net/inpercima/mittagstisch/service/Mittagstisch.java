package net.inpercima.mittagstisch.service;

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

    private static final String STATUS_NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String STATUS_OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private Bistro bistro;

    private HtmlPage htmlPage;

    private String weekText;

    private State state;

    /**
     * Checks if the dates in the determined week are up-to-date.
     *
     * @param checkForNextWeek
     * @return boolean True if up-to-date otherwise false
     * @throws Exception 
     */
    abstract boolean isWithinWeek(final boolean checkForNextWeek) throws Exception;

    /**
     * Prepares a lunch with some predefined content if needed.
     */
    public void prepare() {
        final String url = this.getBistro().getUrl();
        final String bistro = this.getBistro().getName();
        final State state = new State();
        if (this.getBistro().isDisabled()) {
            log.debug("preparing lunch for '{}' is disabled", bistro);
            MittagstischUtils.setErrorState(bistro, state, url);
        } else {
            try {
                setHtmlPage(MittagstischUtils.determineHtmlPage(url));
                // special for biomare
                if (this.getBistro().getWeekSelector().isBlank()) {
                    weekText = htmlPage
                            .querySelector(this.getBistro().getLunchSelector() + ":nth-child("
                                    + (this.getBistro().getDays() + 1) + ") article h3")
                            .asNormalizedText();
                    setWeekText(weekText);
                } else {
                    setWeekText(MittagstischUtils.determineWeekText(getHtmlPage(), this.getBistro()));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MittagstischUtils.setErrorState(bistro, state, url);
            }
            final int days = this.getBistro().getDays();

            try {
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
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MittagstischUtils.setErrorState(bistro, state, url);
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
