package net.inpercima.mittagstisch.service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

public class MittagstischPanLokalTest {

    @Test
    @Disabled("page does not exist")
    public void panLokal() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischPanLokal.URL);
        assertThat(page.getTitleText()).isEqualTo("Mittagessen Archives - Pan");
        final String week = MittagstischUtil.getWeek(MittagstischPanLokal.WEEK, page);
        assertThat(week).is(
                anyOf(new Condition<>(week::contains, "Wochenkarte"), new Condition<>(week::contains, "Mittagskarte")));
    }

    @Test
    @Disabled("page does not exist")
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischPanLokal.prepare(0);
        assertThat(lunch).isNotNull();
    }

}
