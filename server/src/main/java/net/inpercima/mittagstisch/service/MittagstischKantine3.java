package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischKantine3 extends Mittagstisch {

    public MittagstischKantine3(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setLunchSelector("main section div div p");
        this.setName("Kantine 3 (im Tapetenwerk)");
        this.setUrl("http://www.tapetenwerk.de/aktuelles/speiseplan-kantine/");
        this.setWeekSelector("main section div div h1");
    }

    /**
     * Parses and returns the output for the lunch in "Kantine 3 (im Tapetenwerk)".
     *
     * @param state
     * @throws IOException
     */
    public Lunch parse(final State state) throws IOException {
        String food = StringUtils.EMPTY;
        if (StringUtils.isBlank(state.getStatusText())) {
            // details are in spans per day after span with dayname
            filter("TÄGLICH").map(p -> update(p.getTextContent())).collect(Collectors.joining("<br>"));
        }
        // Replacement necessary because name of day can be in the paragraph
        return buildLunch(state, food.replace(getDay(true, getDays()), ""));
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

    public boolean isInWeek(final int days) {
        final boolean isInweek = isWithinRange(days) && ((weekContains(days, dMMMM)))
                || (weekTextContains(days, d, dMMMM));
        log.debug("is in week: '{}'", isInweek);
        return isInweek;
    }

}
