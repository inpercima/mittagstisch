package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

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
        final HtmlPage htmlPage = MittagstischKaiserbad.getHtmlPage(mk.getUrl());
        assertThat(htmlPage.getTitleText()).isEqualTo("Home - kaiserbad-leipzig.de");
        assertThat(MittagstischKaiserbad.getWeek(mk.getWeekSelector(), mk.getUrl())).contains("Wochenkarte");
    }

    @Test
    @Disabled("Wochenkarte doesn't exist anymore")
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mk.prepare();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }

}
