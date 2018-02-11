package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.inpercima.mittagstisch.model.Lunch;

public class MittagstischKantine3 {

    protected static final String LUNCH = "main section div div p";

    protected static final String URL = "http://www.tapetenwerk.de/aktuelles/speiseplan-kantine/";

    protected static final String WEEK = "main section div div h1";

    private MittagstischKantine3() {
        // not used
    }

    /**
     * Returns the output for the lunch in "Kantine 3 (im Tapetenwerk)".
     *
     * @param days The number of days added to the current day
     * @return Lunch Information about the lunch
     * @throws IOException
     */
    public static Lunch prepare(final int days) throws IOException {
        final HtmlPage page = MittagstischUtil.getHtmlPage(URL);
        final Lunch lunch = MittagstischUtil.prepareLunch(page, "Kantine 3 (im Tapetenwerk)", WEEK, URL, true, days);
        if (lunch.getFood() == null) {
            parse(page, lunch, days);
            lunch.setStatus(MittagstischUtil.STATUS_SUCCESS);
        }
        return lunch;
    }

    /**
     * Parses the given page to get information about the lunch.
     *
     * @param page The page which should be parsed
     * @param lunch The lunch which should be used
     * @param days The number of days added to the current day
     */
    private static void parse(final HtmlPage page, final Lunch lunch, final int days) {
        String food = page.querySelectorAll(LUNCH).stream()
                .filter(p -> MittagstischUtil.filterNodes(p, days, "TÄGLICH", true))
                .map(p -> update(p.getTextContent())).collect(Collectors.joining("<br>"));
        // Replacement necessary because name of day can be in the paragraph
        lunch.setFood(food.replace(MittagstischUtil.getDay(true, days), ""));

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

}
