package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class LebensmittelimbissSeidelTest {

    private LebensmittelimbissSeidel lebensmittelimbissSeidel;

    @BeforeEach
    public void create() throws IOException {
        lebensmittelimbissSeidel = new LebensmittelimbissSeidel(0);
        lebensmittelimbissSeidel.prepare();
    }

    @Test
    public void bistroBic() {
        assertThat(lebensmittelimbissSeidel.getHtmlPage().getTitleText())
                .isEqualTo("Lebensmittel & Imbiss Seidel");
        Condition<String> wochenkarte = new Condition<>(s -> s.contains("WOCHE"), "type Wochenkarte");
        Condition<String> mittagskarte = new Condition<>(s -> s.contains("Mittagskarte"), "type Mittagskarte");
        assertThat(lebensmittelimbissSeidel.getWeekText()).is(anyOf(wochenkarte, mittagskarte));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = lebensmittelimbissSeidel.parse();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getMeal()).isNotEmpty();
    }
}
