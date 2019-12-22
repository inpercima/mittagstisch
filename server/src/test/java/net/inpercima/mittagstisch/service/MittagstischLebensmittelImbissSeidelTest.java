package net.inpercima.mittagstisch.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischLebensmittelImbissSeidelTest {

    @Test
    public void lebensmittelSeidel() throws Exception {
        final HtmlPage page = MittagstischUtil.getHtmlPage(MittagstischLebensmittelImbissSeidel.URL);
        assertThat(page.getTitleText(), is("Lebensmittel & Imbiss Seidel"));
        assertThat(MittagstischUtil.getWeek(MittagstischLebensmittelImbissSeidel.WEEK, page), containsString("Woche"));
    }

    @Test
    public void shouldPrepare() throws NoSuchAlgorithmException, IOException {
        final Lunch lunch = MittagstischLebensmittelImbissSeidel.prepare(0);
        assertNotNull(lunch);
        assertThat(lunch.getFood(), not(isEmptyString()));
    }

}
