package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKaiserbadTest {

    @Test
    public void kaiserbad() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischKaiserbad.URL);
        assertThat(page.getTitleText(), is("Home - kaiserbad-leipzig.de"));
        assertThat(MittagstischUtil.getWeek(MittagstischKaiserbad.WEEK, page), containsString("Wochenkarte"));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischKaiserbad.prepare(true, 0);
        assertNotNull(lunch);
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

}
