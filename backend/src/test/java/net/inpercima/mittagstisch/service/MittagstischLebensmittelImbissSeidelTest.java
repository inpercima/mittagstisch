package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischLebensmittelImbissSeidelTest {

    private MittagstischLebensmittelImbissSeidel mlis;

    @BeforeEach
    public void create() {
        mlis = new MittagstischLebensmittelImbissSeidel(0);
    }

    @Test
    public void lebensmittelSeidel() throws IOException {
        mlis.determineHtmlPage();
        assertThat(mlis.getHtmlPage().getTitleText()).isEqualTo("Lebensmittel & Imbiss Seidel");
        assertThat(mlis.getWeekText().contains("Woche"));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mlis.prepare();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }

}
