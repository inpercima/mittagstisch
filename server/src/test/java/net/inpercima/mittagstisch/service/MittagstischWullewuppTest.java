package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.State;

public class MittagstischWullewuppTest {

    private MittagstischWullewupp mw;

    @BeforeEach
    public void create() {
        mw = new MittagstischWullewupp(0);
    }

    @Test
    public void wullewupp() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(mw.getUrl());
        assertThat(page.getTitleText()).isEqualTo("Speiseplan | suppenbar");
        assertThat(MittagstischUtil.getWeek(mw.getWeekSelector(), mw.getUrl())).contains("Wochenplan");
    }

    @Test
    public void shouldPrepare() throws IOException {
        final State state = mw.prepare();
        assertThat(state).isNotNull();
        assertThat(state.getStatusText()).isNotEmpty();
    }

}
