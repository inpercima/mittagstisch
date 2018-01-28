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

public class MittagstischKantine3Test {

    @Test
    public void kantine3() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischKantine3.URL);
        assertThat(page.getTitleText(), is("Speiseplan Kantine / Tapetenwerk"));
        assertThat(MittagstischUtil.getWeek(MittagstischKantine3.WEEK, page), containsString("WOCHENKARTE"));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = MittagstischKantine3.prepare(0);
        assertNotNull(lunch);
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

}
