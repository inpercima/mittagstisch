package net.inpercima.mittagstisch.service;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischCrawlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MittagstischCrawlerTest.class);

    @Test
    public void lunchInKaiserbad() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInKaiserbad(true);
        assertNotNull(lunch);
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

    @Test
    public void lunchInKantine3() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInKantine3();
        assertNotNull(lunch);
        LOGGER.debug(lunch.getFood());
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

    @Test
    @Ignore
    public void lunchInLebensmittelSeidel() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInLebensmittelSeidel();
        assertNotNull(lunch);
    }

    @Test
    public void lunchInPanLokal() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInPanLokal();
        assertNotNull(lunch);
    }

    @Test
    @Ignore
    public void lunchInWullewup() throws IOException {
        final Lunch lunch = MittagstischCrawler.lunchInWullewupp();
        assertNotNull(lunch);
    }

}
