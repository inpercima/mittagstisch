package net.inpercima.mittagstisch.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.assertj.core.api.Assertions.*;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischWullewuppTest {

    @Test
    @Disabled("com.gargoylesoftware.css.parser.CSSException: Invalid selectors")
    public void wullewupp() throws Exception {
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
