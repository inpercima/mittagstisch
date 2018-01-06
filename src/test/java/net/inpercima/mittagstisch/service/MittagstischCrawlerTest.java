package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischCrawlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MittagstischCrawlerTest.class);

    @Test
    public void kaiserbad() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.KAISERBAD_URL);
        assertThat(page.getTitleText(), is("Home - kaiserbad-leipzig.de"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.KAISERBAD_CSS_WEEK, page),
                containsString("Wochenkarte"));
    }

    @Test
    public void lunchInKaiserbad() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInKaiserbad(true);
        assertNotNull(lunch);
        LOGGER.debug(lunch.getFood());
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

    @Test
    public void kantine3() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.KANTINE_3_URL);
        assertThat(page.getTitleText(), is("Speiseplan Kantine / Tapetenwerk"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.KANTINE_3_CSS_WEEK, page),
                containsString("WOCHENKARTE"));
    }

    @Test
    public void lunchInKantine3() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInKantine3();
        assertNotNull(lunch);
        LOGGER.debug(lunch.getFood());
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

    @Test
    @Ignore("javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure")
    public void lebensmittelSeidel() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.URL_LEBENSMITTEL_SEIDEL);
        assertThat(page.getTitleText(), is("Lebensmittel & Imbiss Seidel"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.XPATH_WEEK_LEBENSMITTEL_SEIDEL, page),
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
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischCrawler.URL_WULLEWUPP);
        assertThat(page.getTitleText(), is("suppenbar | Speiseplan"));
        assertThat(MittagstischUtil.getWeek(MittagstischCrawler.XPATH_WEEK_WULLEWUPP, page),
                containsString("Wochenplan"));
    }

    @Test
    @Ignore
    public void lunchInWullewup() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInWullewupp();
        assertNotNull(lunch);
    }

}
