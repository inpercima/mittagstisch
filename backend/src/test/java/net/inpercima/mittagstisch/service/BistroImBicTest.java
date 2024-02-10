package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class BistroImBicTest {

    private BistroImBic bistroImBic;

    @BeforeEach
    public void create() throws IOException {
        bistroImBic = new BistroImBic(0);
        bistroImBic.prepare();
    }

    @Test
    public void bistroBic() throws IOException {
        assertThat(bistroImBic.getHtmlPage().getTitleText())
                .isEqualTo("Bistro im Bussines & Innovation Centre Leipzig - Speiseplan");
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("Wochenkarte"), "type Wochenkarte");
        Condition<String> speiseplan = new Condition<>(s -> s.contains("Speiseplan"), "type Speiseplan");
        assertThat(bistroImBic.getWeekText()).is(anyOf(wochenkarte, speiseplan));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = bistroImBic.parse();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getMeal()).isNotEmpty();
    }
}
