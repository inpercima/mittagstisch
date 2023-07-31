package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Lunch;
import net.inpercima.mittagstisch.model.State;

@Slf4j
public class MittagstischPanLokal extends Mittagstisch {

    public MittagstischPanLokal(final int days) {
        this.setDaily(true);
        this.setDays(days);
        this.setLunchSelector("div[id='widgetbar_page_1'] div[class='wdn-pricelist-priceList']");
        this.setName("PAN Lokal");
        this.setUrl("https://www.pan-leipzig.de/SPEISEKARTEN/Mittag");
        this.setWeekSelector("h2:first-of-type");
        this.setWeekSelectorXPath("/html/body/div[4]/div[2]/div[2]/div/div[1]/div//*[contains(text(),'MITTAGSKARTE')]");
    }

    /**
     * Parses and returns the output for the lunch in "PAN Lokal".
     *
     * @param state
     */
    public Lunch parse(final State state) {
        String food = StringUtils.EMPTY;
        // if (StringUtils.isBlank(state.getStatusText())) {
        // // details are in paragraphs per day
        // HtmlParagraph p = getHtmlPage()
        // .querySelector(String.format(getLunchSelector(),
        // getLocalizedDate(getDays()).get(dayOfWeek())));
        // food = p.getTextContent();
        // }
        return buildLunch(state, food);
    }

    public boolean isWithinWeek(final boolean checkForNextWeek) {
        Pattern pattern = Pattern.compile("((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2]).[0-9]{2})");
        Matcher matcher = pattern.matcher(getWeekText());
        LocalDate firstDate = null;
        LocalDate lastDate = null;
        while (matcher.find()) {
            if (firstDate == null) {
                firstDate = LocalDate.parse(matcher.group(1), ddMMYY);
                log.debug("extracted firstDate: '{}'", firstDate);
            } else {
                lastDate = LocalDate.parse(matcher.group(1), ddMMYY);
                log.debug("extracted lastDate: '{}'", lastDate);
            }
        }
        final boolean isWithinWeek = isWithinRange(firstDate, lastDate, checkForNextWeek);
        log.debug("is in week: '{}'", isWithinWeek);
        return isWithinWeek;
    }
}
