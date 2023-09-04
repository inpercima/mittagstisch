package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.stream.Collectors;

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
     * Parses and returns the output for the lunch in "Lebensmittel Imbiss Seidel".
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        if (StringUtils.isBlank(getState().getStatusText())) {
            // details are in spans per day
            final String extractedText = getHtmlPage().querySelectorAll(getBistro().getLunchSelector()).stream()
                    .map(node -> node.asNormalizedText()).collect(Collectors.joining(" "));
            final String currentDay = MittagstischUtils.getDay(this.getBistro().getDays());
            final String lastString = currentDay == "Freitag" ? "Wir liefern Ihnen"
                    : MittagstischUtils.getDay(this.getBistro().getDays() + 1);

            mealWithDayAndPrice = extractedText.substring(extractedText.indexOf(currentDay) + currentDay.length(),
                    extractedText.indexOf(lastString)).trim();
        }
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        final LocalDate now = MittagstischUtils.getLocalizedDate(false, this.getBistro().getDays());
        final String suffix = String.valueOf(now.getYear());
        return MittagstischUtils.isWithinWeek(checkForNextWeek, getWeekText(), getBistro().getDays(),
                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{0,4})", MittagstischUtils.ddMMYYYY, suffix, "");
    }

}
