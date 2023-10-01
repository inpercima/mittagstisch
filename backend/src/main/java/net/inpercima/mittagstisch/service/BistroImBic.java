package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Slf4j
public class BistroImBic extends Mittagstisch {

    public BistroImBic(final int days) {
        Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("div.content_main_dho p");
        bistro.setName("Bistro im BIC");
        bistro.setUrl("http://www.bistro-bic.de/Speiseplan");
        bistro.setWeekSelector("div.content_main_dho p");
        bistro.setWeekSelectorXPath("/html/body/div[4]/div[3]/div/div[1]//*[contains(text(),'Speiseplan')]");
        setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Bistro im BIC".
     *
     * @param state
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        if (StringUtils.isBlank(getState().getStatusText())) {
            try {
                // details are in paragraphs per day
                mealWithDayAndPrice = getHtmlPage().querySelectorAll(getBistro().getLunchSelector()).stream()
                        .filter(node -> node.asNormalizedText().toLowerCase()
                                .contains(MittagstischUtils.getDay(getBistro().getDays()).toLowerCase()))
                        .findFirst().get()
                        .asNormalizedText();
                // remove day b/c the selected day is clear, and replace line break by html line
                // breaks
                mealWithDayAndPrice = mealWithDayAndPrice
                        .replaceFirst(MittagstischUtils.getDay(getBistro().getDays()), "").trim()
                        .replace("\n", "<br><br>");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MittagstischUtils.setErrorState(getBistro().getName(), getState(), getBistro().getUrl());
            }
        }
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        return MittagstischUtils.isWithinWeek(checkForNextWeek, getWeekText(), getBistro().getDays(),
                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{4})", MittagstischUtils.ddMMYYYY, "", "");
    }
}
