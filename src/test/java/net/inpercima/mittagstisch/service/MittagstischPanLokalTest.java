package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischPanLokalTest {

    @Test
    public void panLokal() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischPanLokal.URL);
        assertThat(page.getTitleText(), is("Mittagessen Archives - Pan"));
        assertThat(MittagstischUtil.getWeek(MittagstischPanLokal.WEEK, page), containsString("Wochenkarte"));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischPanLokal.prepare(0);
        assertNotNull(lunch);
    }

}
