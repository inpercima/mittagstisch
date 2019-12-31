package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.DomNode;

import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

public class MittagstischLebensmittelImbissSeidel extends Mittagstisch {

    public MittagstischLebensmittelImbissSeidel(final int days) {
        this.setLunchSelector("body div div div div div.xr_s19 span");
        this.setUrl("https://lebensmittel-imbiss-seidel.de/imbiss.htm");
        this.setWeekSelector("body div div div div div.xr_s19 span:nth-of-type(1)");
        this.setName("Lebensmittel Imbiss Seidel");
        this.setDaily(true);
        this.setDays(days);
    }

    /**
     * Parses and returns the output for the lunch in "Lebensmittel Imbiss Seidel".
     *
     * @param state
     * @throws IOException
     */
    public Lunch parse(final State state) throws IOException {
        // details are in spans per day after span with dayname
        final String food = filter("Änderungen").map(DomNode::getTextContent).collect(Collectors.joining(" "));
        return buildLunch(state, food);
    }

}
