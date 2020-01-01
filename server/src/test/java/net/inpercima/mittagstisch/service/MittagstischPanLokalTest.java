package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischPanLokalTest {

    private MittagstischPanLokal mpl;

    @BeforeEach
    public void create() {
        mpl = new MittagstischPanLokal(0);
    }

    @Test
    @Disabled("page does not exist")
    public void panLokal() throws IOException {
        mpl.getHtmlPage(mpl.getUrl());
        assertThat(mpl.getHtmlPage().getTitleText()).isEqualTo("Mittagessen Archives - Pan");
        final String week = mpl.getWeek(mpl.getWeekSelector(), mpl.getUrl());
        assertThat(week).is(
                anyOf(new Condition<>(week::contains, "Wochenkarte"), new Condition<>(week::contains, "Mittagskarte")));
    }

    @Test
    @Disabled("page does not exist")
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mpl.prepare();
        assertThat(lunch).isNotNull();
    }

}
