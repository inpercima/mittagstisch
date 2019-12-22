package net.inpercima.mittagstisch.service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischLebensmittelImbissSeidelTest {

    @Test
    public void lebensmittelSeidel() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischLebensmittelImbissSeidel.URL);
        assertThat(page.getTitleText()).isEqualTo("Lebensmittel & Imbiss Seidel");
        assertThat(MittagstischUtil.getWeek(MittagstischLebensmittelImbissSeidel.WEEK, page).contains("Woche"));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischLebensmittelImbissSeidel.prepare(0);
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }

}
