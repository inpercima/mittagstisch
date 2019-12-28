package net.inpercima.mittagstisch.service;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischWullewuppTest {

    @Test
    public void wullewupp() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischWullewupp.URL);
        assertThat(page.getTitleText()).isEqualTo("Speiseplan | suppenbar");
        assertThat(MittagstischUtil.getWeek(MittagstischWullewupp.WEEK, page)).contains("Wochenplan");
    }

    @Test
    public void shouldPrepare() {
        final Lunch lunch = MittagstischWullewupp.prepare();
        assertThat(lunch).isNotNull();
    }

}
