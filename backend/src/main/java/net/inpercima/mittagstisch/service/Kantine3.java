package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class Kantine3 extends Mittagstisch {

    public Kantine3(final int days) {
        Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("main section div div p");
        bistro.setName("Rocky Maria - Kantine Tapetenwerk");
        bistro.setUrl("https://www.tapetenwerk.de/aktuelles/speiseplan-kantine/");
        bistro.setWeekSelector("main section div div h1");
        bistro.setWeekSelectorXPath("");
        this.setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Kantine 3 (im Tapetenwerk)".
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        if (StringUtils.isBlank(getState().getStatusText())) {
            // details are in spans per day
            final String extractedText = getHtmlPage().querySelectorAll(getBistro().getLunchSelector()).stream()
                    .map(node -> node.asNormalizedText()).collect(Collectors.joining(" "));
            final String currentDay = MittagstischUtils.getDay(this.getBistro().getDays()).toUpperCase();
            final String lastString = currentDay.equals("FREITAG") ? "PREISE"
                    : MittagstischUtils.getDay(this.getBistro().getDays() + 1).toUpperCase();
            mealWithDayAndPrice = extractedText.substring(extractedText.indexOf(currentDay) + currentDay.length(),
                    extractedText.indexOf(lastString)).trim();
        }
        return buildLunch(mealWithDayAndPrice);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        final LocalDate now = MittagstischUtils.getLocalizedDate(false, this.getBistro().getDays());
        final String suffix = String.valueOf(now.getYear());
        return MittagstischUtils.isWithinWeek(checkForNextWeek, getWeekText(), getBistro().getDays(),
                "((?:[0-2][0-9]|3[01]|[1-9]).(?:(?:JAN)(?:UAR)?|FEB(?:RUAR)?|MÄR(?:Z)?|APR(?:IL)?|MAI|JUN(?:I)?|JUL(?:I)?|AUG(?:UST)?|SEP(?:TEMBER)?|OKT(?:OBER)?|NOV(?:EMBER)?|DEZ(?:EMBER)?))",
                MittagstischUtils.dMMMMYYYY, suffix, suffix);
    }
}
