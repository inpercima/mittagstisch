package net.inpercima.mittagstisch.service;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKaiserbadTest {

    @Test
    @Disabled("Wochenkarte doesn't exist anymore")
    public void kaiserbad() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischKaiserbad.URL);
        assertThat(page.getTitleText()).isEqualTo("Home - kaiserbad-leipzig.de");
        assertThat(MittagstischUtil.getWeek(MittagstischKaiserbad.WEEK, page)).contains("Wochenkarte");
    }

    @Test
    @Disabled("Wochenkarte doesn't exist anymore")
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischKaiserbad.prepare(true, 0);
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isEmpty();
    }

}
