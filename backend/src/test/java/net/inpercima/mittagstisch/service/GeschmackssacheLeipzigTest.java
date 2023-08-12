package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class GeschmackssacheLeipzigTest {

    private GeschmackssacheLeipzig geschmackssacheLeipzig;

    @BeforeEach
    public void create() throws IOException {
        geschmackssacheLeipzig = new GeschmackssacheLeipzig(0);
        geschmackssacheLeipzig.prepare();
    }

    @Test
    public void bistroBic() {
        assertThat(geschmackssacheLeipzig.getHtmlPage().getTitleText())
                .isEqualTo("");
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("WOCHENKARTE"), "type Wochenkarte");
        Condition<String> mittagskarte = new Condition<>(s -> s.contains("SPEISEPLAN"), "type Mittagskarte");
        assertThat(geschmackssacheLeipzig.getWeekText()).is(anyOf(wochenkarte, mittagskarte));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = geschmackssacheLeipzig.parse();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getMeal()).isNotEmpty();
    }
}
