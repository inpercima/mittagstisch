package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.State;

public class MittagstischKantine3Test {

    private MittagstischKantine3 mk3;

    @BeforeEach
    public void create() {
        mk3 = new MittagstischKantine3(0);
    }

    @Test
    public void kantine3() throws IOException {
        final HtmlPage htmlPage = MittagstischUtil.getHtmlPage(mk3.getUrl());
        assertThat(htmlPage.getTitleText()).isEqualTo("Speiseplan Kantine / Tapetenwerk");
        assertThat(MittagstischUtil.getWeek(mk3.getWeekSelector(), mk3.getUrl())).contains("WOCHENKARTE");
    }

    @Test
    public void shouldPrepare() throws IOException {
        final State state = mk3.prepare();
        assertThat(state).isNotNull();
        assertThat(state.getStatusText()).isNotEmpty();
    }

    @Test
    public void shouldUpdate() {
        String updated = MittagstischKantine3.update("Rote Beete Risotto, grüne Erbsen und Salat7,-");
        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo("Rote Beete Risotto, grüne Erbsen und Salat 7,-");

        updated = MittagstischKantine3.update("Rote Beete Risotto, grüne Erbsen und Salat10,-");
        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo("Rote Beete Risotto, grüne Erbsen und Salat 10,-");

        updated = MittagstischKantine3.update("Zitronenhähnchen8,50");
        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo("Zitronenhähnchen 8,50");

        updated = MittagstischKantine3.update("Zitronenhähnchen10,50");
        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo("Zitronenhähnchen 10,50");
    }

}
