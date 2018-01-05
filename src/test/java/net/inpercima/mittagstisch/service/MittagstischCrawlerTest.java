package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischCrawlerTest {

    @Test
    public void kantine3() throws IOException {
        final HtmlPage page = MittagstischCrawler.getHtmlPage(MittagstischCrawler.URL_KANTINE_3);
        assertThat(page.getTitleText(), is("Speiseplan Kantine / Tapetenwerk"));
        assertThat(MittagstischCrawler.getWeek(MittagstischCrawler.XPATH_WEEK_KANTINE_3, page),
                containsString("WOCHENKARTE"));
    }

    @Test
    public void lunchInKantine3() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInKantine3();
        assertNotNull(lunch);
    }

    @Test
    @Ignore("javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure")
    public void lebensmittelSeidel() throws Exception {
        final HtmlPage page = MittagstischCrawler.getHtmlPage(MittagstischCrawler.URL_LEBENSMITTEL_SEIDEL);
        assertThat(page.getTitleText(), is("Lebensmittel & Imbiss Seidel"));
        assertThat(MittagstischCrawler.getWeek(MittagstischCrawler.XPATH_WEEK_LEBENSMITTEL_SEIDEL, page),
                containsString("Woche"));
    }

    @Test
    @Ignore
    public void lunchInLebensmittelSeidel() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInLebensmittelSeidel();
        assertNotNull(lunch);
    }

    @Test
    @Ignore("com.gargoylesoftware.htmlunit.ScriptException: illegal character (https://static.parastorage.com/polyfill/v2/polyfill.min.js?rum=0#1")
    public void wullewupp() throws Exception {
        final HtmlPage page = MittagstischCrawler.getHtmlPage(MittagstischCrawler.URL_WULLEWUPP);
        assertThat(page.getTitleText(), is("suppenbar | Speiseplan"));
        assertThat(MittagstischCrawler.getWeek(MittagstischCrawler.XPATH_WEEK_WULLEWUPP, page),
                containsString("Wochenplan"));
    }

    @Test
    @Ignore
    public void lunchInWullewup() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInWullewupp();
        assertNotNull(lunch);
    }

}
