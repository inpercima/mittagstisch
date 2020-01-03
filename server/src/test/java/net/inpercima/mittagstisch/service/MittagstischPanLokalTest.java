package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischPanLokalTest {

    private MittagstischPanLokal mpl;

    @BeforeEach
    public void create() {
        mpl = new MittagstischPanLokal(0);
    }

    @Test
    public void panLokal() throws IOException {
        mpl.getHtmlPage(mpl.getUrl());
        assertThat(mpl.getHtmlPage().getTitleText()).isEqualTo("Mittagessen Archives - Pan Lokal");
        final String week = mpl.getWeek(mpl.getWeekSelector());
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("Wochenkarte"), "type Wochenkarte");
        Condition<String> mittagskarte = new Condition<>(s -> s.contains("Mittagskarte"), "type Mittagskarte");
        assertThat(week).is(anyOf(wochenkarte, mittagskarte));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mpl.prepare();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }

}
