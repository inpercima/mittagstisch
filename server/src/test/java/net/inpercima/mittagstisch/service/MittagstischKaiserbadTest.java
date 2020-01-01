package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKaiserbadTest {

    private MittagstischKaiserbad mk;

    @BeforeEach
    public void create() {
        mk = new MittagstischKaiserbad(0);
    }

    @Test
    @Disabled("Wochenkarte doesn't exist anymore")
    public void kaiserbad() throws IOException {
        mk.getHtmlPage(mk.getUrl());
        assertThat(mk.getHtmlPage().getTitleText()).isEqualTo("Home - kaiserbad-leipzig.de");
        assertThat(mk.getWeek(mk.getWeekSelector(), mk.getUrl())).contains("Wochenkarte");
    }

    @Test
    @Disabled("Wochenkarte doesn't exist anymore")
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mk.prepare();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }

}
