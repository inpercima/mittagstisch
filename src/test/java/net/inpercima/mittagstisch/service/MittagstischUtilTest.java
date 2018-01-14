package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MittagstischUtilTest {

    @Test
    public void kaiserbad() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.KAISERBAD_URL);
        assertThat(page.getTitleText(), is("Home - kaiserbad-leipzig.de"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.KAISERBAD_CSS_WEEK, page),
                containsString("Wochenkarte"));
    }

    @Test
    public void kaiserbadInWeek() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.KAISERBAD_URL);
        assertThat(page.getTitleText(), is("Home - kaiserbad-leipzig.de"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.KAISERBAD_CSS_WEEK, page),
                containsString("Wochenkarte"));
    }

    @Test
    public void kantine3() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.KANTINE_3_URL);
        assertThat(page.getTitleText(), is("Speiseplan Kantine / Tapetenwerk"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.KANTINE_3_CSS_WEEK, page),
                containsString("WOCHENKARTE"));
    }

    @Test
    @Ignore("javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure")
    public void lebensmittelSeidel() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.LEBENSMITTEL_SEIDEL_URL);
        assertThat(page.getTitleText(), is("Lebensmittel & Imbiss Seidel"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.LEBENSMITTEL_SEIDEL_CSS_WEEK, page),
                containsString("Woche"));
    }

    @Test
    public void panLokal() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.PAN_LOKAL_URL);
        assertThat(page.getTitleText(), is("Mittagessen Archives - Pan"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.PAN_LOKAL_CSS_WEEK, page),
                containsString("Wochenkarte"));
    }

    @Test
    @Ignore("com.gargoylesoftware.htmlunit.ScriptException: illegal character (https://static.parastorage.com/polyfill/v2/polyfill.min.js?rum=0#1")
    public void wullewupp() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.WULLEWUPP_URL);
        assertThat(page.getTitleText(), is("suppenbar | Speiseplan"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.WULLEWUPP_CSS_WEEK, page),
                containsString("Wochenplan"));
    }

}
