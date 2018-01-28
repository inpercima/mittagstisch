package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischLebensmittelImbissSeidelTest {

    @Test
    @Ignore("javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure")
    public void lebensmittelSeidel() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischLebensmittelImbissSeidel.URL);
        assertThat(page.getTitleText(), is("Lebensmittel & Imbiss Seidel"));
        assertThat(MittagstischUtil.getWeek(MittagstischLebensmittelImbissSeidel.WEEK, page), containsString("Woche"));
    }

    @Test
    @Ignore
    public void shouldPrepare() {
        final Lunch lunch = MittagstischLebensmittelImbissSeidel.prepare();
        assertNotNull(lunch);
    }

}
