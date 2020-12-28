package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischBistroBicTest {

    private MittagstischBistroBic mbb;

    @BeforeEach
    public void create() {
        mbb = new MittagstischBistroBic(0);
    }

    @Test
    public void bistroBic() throws IOException {
        mbb.getHtmlPage(mbb.getUrl());
        assertThat(mbb.getHtmlPage().getTitleText()).isEqualTo("Bistro im Bussines & Innovation Centre Leipzig - Speiseplan");
        // Condition<String> wochenkarte = new Condition<>(s -> s.contains("Wochenkarte"), "type Wochenkarte");
        // Condition<String> mittagskarte = new Condition<>(s -> s.contains("Mittagskarte"), "type Mittagskarte");
        // assertThat(mbb.getWeekText()).is(anyOf(wochenkarte, mittagskarte));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mbb.prepare();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }
}
