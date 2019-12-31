package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.inpercima.mittagstisch.model.State;

public class MittagstischLebensmittelImbissSeidelTest {

    private MittagstischLebensmittelImbissSeidel mlis;

    @BeforeEach
    public void create() {
        mlis = new MittagstischLebensmittelImbissSeidel(0);
    }

    @Test
    public void lebensmittelSeidel() throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(mlis.getUrl());
        assertThat(page.getTitleText()).isEqualTo("Lebensmittel & Imbiss Seidel");
        assertThat(MittagstischUtil.getWeek(mlis.getWeekSelector(), mlis.getUrl()).contains("Woche"));
    }

    @Test
    public void shouldPrepare() throws IOException {
        final State state = mlis.prepare();
        assertThat(state).isNotNull();
        assertThat(state.getStatus()).isNotEmpty();
    }

}
