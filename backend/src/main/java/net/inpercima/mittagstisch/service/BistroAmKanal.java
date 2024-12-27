package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Slf4j
public class BistroAmKanal extends Mittagstisch {

    public BistroAmKanal(final int days) {
        Bistro bistro = new Bistro();
        bistro.setPdf(true);
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("main wow-image img");
        bistro.setName("Bistro am Kanal");
        bistro.setUrl("https://buschmannle.wixsite.com/meinewebsite/wochenkarte");
        bistro.setWeekSelector("main + div a[class~='wixui-button']");
        bistro.setWeekSelectorXPath("/html/body/div/div/div[4]/div/div/div/div/section/div[2]/div/div[2]/div/div[4]/a");
        setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Bistro am Kanal".
     *
     * @param state
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        Lunch lunch = buildLunch(mealWithDayAndPrice);
        if (getBistro().isPdf() && !lunch.getPdfLink().endsWith("pdf")) {
            final String linkedImage = getHtmlPage().querySelector(getBistro().getLunchSelector()).getAttributes()
                    .getNamedItem("src")
                    .getNodeValue();
            log.debug("prepare lunch for: '{}' with alternative image link: '{}'", getBistro().getName(), linkedImage);
            lunch.setPdfLink(linkedImage);
        }
        return lunch;
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) throws Exception {
        // b/c of pdf this is always true
        return true;
    }
}
