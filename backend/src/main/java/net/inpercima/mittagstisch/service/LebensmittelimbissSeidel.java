package net.inpercima.mittagstisch.service;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class LebensmittelimbissSeidel extends Mittagstisch {

    public LebensmittelimbissSeidel(final int days) {
        Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("body div div div div div.xr_s20 span");
        bistro.setName("Lebensmittel Imbiss Seidel");
        bistro.setUrl("https://lebensmittel-imbiss-seidel.de/imbiss.htm");
        bistro.setWeekSelector("body div div div div div.xr_s20 span:nth-of-type(1)");
        this.setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Geschmackssache Leipzig".
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        //if (StringUtils.isBlank(state.getStatusText())) {
            // details are in spans per day after span with dayname
            //food = filter("Änderungen").map(DomNode::asNormalizedText).collect(Collectors.joining(" "));
        //}
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        // final boolean isWithinWeek = isWithinRange(days)
        //         && (weekTextContains(days, ddMMYYYY) || weekTextContains(days, ddMM, ddMMYYYY));
        // log.debug("is in week: '{}'", isWithinWeek);
        // return isWithinWeek;
        return false;
    }

}
