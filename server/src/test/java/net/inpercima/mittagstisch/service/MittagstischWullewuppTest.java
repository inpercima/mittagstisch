package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischWullewuppTest {

    private MittagstischWullewupp mw;

    @BeforeEach
    public void create() {
        mw = new MittagstischWullewupp(0);
    }

    @Test
    public void wullewupp() throws IOException {
        final HtmlPage page = MittagstischWullewupp.getHtmlPage(mw.getUrl());
        assertThat(page.getTitleText()).isEqualTo("Speiseplan | suppenbar");
        assertThat(MittagstischWullewupp.getWeek(mw.getWeekSelector(), mw.getUrl())).contains("Wochenplan");
    }

    @Test
    public void shouldPrepare() throws IOException {
        final Lunch lunch = mw.prepare();
        assertThat(lunch).isNotNull();
        assertThat(lunch.getFood()).isNotEmpty();
    }

}
