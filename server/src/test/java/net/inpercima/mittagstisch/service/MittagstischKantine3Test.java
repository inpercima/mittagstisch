package net.inpercima.mittagstisch.service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKantine3Test {

    @Test
    public void kantine3() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischKantine3.URL);
        assertThat(page.getTitleText()).isEqualTo("Speiseplan Kantine / Tapetenwerk");
        assertThat(MittagstischUtil.getWeek(MittagstischKantine3.WEEK, page)).contains("WOCHENKARTE");
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischKantine3.prepare(0);
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
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
