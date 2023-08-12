package net.inpercima.mittagstisch.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

public class Kantine3 extends Mittagstisch {

    public Kantine3(final int days) {
        Bistro bistro = new Bistro();
        bistro.setDaily(true);
        bistro.setDays(days);
        bistro.setLunchSelector("main section div div p");
        bistro.setName("Kantine 3 (im Tapetenwerk)");
        bistro.setUrl("http://www.tapetenwerk.de/aktuelles/speiseplan-kantine/");
        bistro.setWeekSelector("main section div div h1");
        bistro.setWeekSelectorXPath("");
        this.setBistro(bistro);
    }

    /**
     * Parses and returns the output for the lunch in "Geschmackssache Leipzig".
     */
    public Lunch parse() {
        String mealWithDayAndPrice = StringUtils.EMPTY;
        return buildLunch(mealWithDayAndPrice);
    }

    /**
     * Updates the content if there is no space between food and price.
     *
     * @param content The text in paragraph.
     * @return
     */
    public static String update(final String content) {
        String result = content;
        final String[] pattern = { "\\d+,-", "\\d+,\\d+" };
        for (int i = 0; i < pattern.length; i++) {
            final Matcher matcher = Pattern.compile(pattern[i]).matcher(content);
            if (matcher.find()) {
                result = content.substring(0, matcher.start()).concat(" ")
                        .concat(content.substring(matcher.start(), matcher.end()));
                break;
            }
        }
        return result;
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        // final boolean isWithinWeek = isWithinRange(days)
        // && (weekTextContains(days, dMMMM) || weekTextContains(days, d, dMMMM));
        // log.debug("is in week: '{}'", isWithinWeek);
        // return isWithinWeek;
        return false;
    }

}
