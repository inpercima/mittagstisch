package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class PanLokal extends Mittagstisch {

    public PanLokal(final int days) {
        final Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("div[id='widgetbar_page_1'] div[class='wdn-pricelist-priceList']");
        bistro.setName("PAN Lokal");
        bistro.setUrl("https://www.pan-leipzig.de/SPEISEKARTEN/Mittag");
        bistro.setWeekSelector("h2:first-of-type");
        bistro.setWeekSelectorXPath(
                "/html/body/div[4]/div[2]/div[2]/div/div[1]/div//*[contains(text(),'MITTAGSKARTE')]");
        setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "PAN Lokal".
     */
    public Lunch parse() {
        String food = StringUtils.EMPTY;
        return buildLunch(food);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        return MittagstischUtils.isWithinWeek(checkForNextWeek, getWeekText(), getBistro().getDays(),
                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{2})", ddMMYY);
    }
}
