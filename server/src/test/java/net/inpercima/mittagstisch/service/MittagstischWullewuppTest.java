package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischWullewuppTest {

    @Test
    @Ignore("com.gargoylesoftware.htmlunit.ScriptException: illegal character (https://static.parastorage.com/polyfill/v2/polyfill.min.js?rum=0#1")
    public void wullewupp() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischWullewupp.URL);
        assertThat(page.getTitleText(), is("suppenbar | Speiseplan"));
        assertThat(MittagstischUtil.getWeek(MittagstischWullewupp.WEEK, page), containsString("Wochenplan"));
    }

    @Test
    @Ignore("page not parsable")
    public void shouldPrepare() {
        final Lunch lunch = MittagstischWullewupp.prepare();
        assertNotNull(lunch);
    }

}
