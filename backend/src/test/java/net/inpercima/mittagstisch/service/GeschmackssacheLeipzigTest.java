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
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("Wochenkarte"), "type Wochenkarte");
        Condition<String> kalenderwoche = new Condition<>(s -> s.contains("Kalenderwoche"), "type Kalenderwoche");
        assertThat(geschmackssacheLeipzig.getWeekText()).is(anyOf(wochenkarte, kalenderwoche));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = geschmackssacheLeipzig.parse();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getMeal()).isNotEmpty();
    }
}
