package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.stream.Stream;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Data
@Slf4j
public class Mittagstisch {

    private static final int IN_NEXT_WEEK = 7;

    private static final String NEXT_WEEK = "Der Speiseplan scheint schon für nächste Woche vorgegeben. Bitte unter 'nächste Woche' schauen.";

    private static final String OUTDATED = "Der Speiseplan scheint nicht mehr aktuell zu sein. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String TECHNICAL = "Derzeit kann aufgrund einer technischen Besonderheit keine Information zur Karte eingeholt werden. Bitte prüfe manuell: <a href='%s' target='_blank'>%s</>";

    private static final String STATUS_ERROR = "status-error";

    private static final String STATUS_SUCCESS = "status-success";

    private static final String STATUS_WARNING = "status-warning";

    private String lunchSelector;

    private String url;

    private String weekSelector;

    private String name;

    private boolean daily;

    private int days;

    private String detailSelector;

    private boolean dissabled;

    /**
     * Prepares a State with some predefined content if needed.
     *
     * @return State
     * @throws IOException
     */
    public State prepare() throws IOException {
        final State state = new State();
        if (isDissabled()) {
            log.debug("prepare lunch for '{}' is dissabeld", getName());
            state.setStatusText(String.format(TECHNICAL, getUrl(), getUrl()));
            state.setStatus(STATUS_ERROR);
        } else {
            final String weekText = MittagstischUtil.getWeek(getWeekSelector(), getUrl());
            log.debug("prepare lunch for '{}' with weektext '{}'", getName(), weekText);
            state.setStatus(STATUS_SUCCESS);
            if (!MittagstischUtil.isInWeek(weekText, getDays()) && !MittagstischUtil.isInWeek(weekText, IN_NEXT_WEEK)) {
                state.setStatusText(String.format(OUTDATED, getUrl(), getUrl()));
                state.setStatus(STATUS_ERROR);
            } else if (MittagstischUtil.isInWeek(weekText, IN_NEXT_WEEK) && isDaily() && getDays() == 0) {
                state.setStatusText(NEXT_WEEK);
                state.setStatus(STATUS_WARNING);
            }
        }
        return state;
    }

    public Stream<DomNode> filter(final String filter) throws IOException {
        final HtmlPage htmlPage = MittagstischUtil.getHtmlPage(getUrl());
         return htmlPage.querySelectorAll(getLunchSelector()).stream()
        .filter(span -> MittagstischUtil.filterNodes(span, getDays(), filter, false));

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

}
