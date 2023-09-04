package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class Kantine3Test {

    private Kantine3 kantine3;

    @BeforeEach
    public void create() throws IOException {
        kantine3 = new Kantine3(0);
        kantine3.prepare();
    }

    @Test
    public void bistroBic() {
        assertThat(kantine3.getHtmlPage().getTitleText())
                .isEqualTo("Speiseplan Kantine / Tapetenwerk");
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("-"), "type Wochenkarte");
        Condition<String> mittagskarte = new Condition<>(s -> s.contains("Mittagskarte"), "type Mittagskarte");
        assertThat(kantine3.getWeekText()).is(anyOf(wochenkarte, mittagskarte));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = kantine3.parse();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getMeal()).isNotEmpty();
    }
}
