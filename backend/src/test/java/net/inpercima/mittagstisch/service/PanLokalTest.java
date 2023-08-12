package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class PanLokalTest {

    private PanLokal panLokal;

    @BeforeEach
    public void create() throws IOException {
        panLokal = new PanLokal(0);
        panLokal.prepare();
    }

    @Test
    public void panLokal() throws IOException {
        assertThat(panLokal.getHtmlPage().getTitleText()).isEqualTo("PAN Lokal - Mittag");
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("Wochenkarte"), "type Wochenkarte");
        Condition<String> mittagskarte = new Condition<>(s -> s.contains("MITTAGSKARTE"), "type Mittagskarte");
        assertThat(panLokal.getWeekText()).is(anyOf(wochenkarte, mittagskarte));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = panLokal.parse();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getMeal()).isNotEmpty();
    }

}
