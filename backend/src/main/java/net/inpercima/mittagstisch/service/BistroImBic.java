package net.inpercima.mittagstisch.service;

import java.util.stream.Collectors;

import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Service;

import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
public class BistroImBic extends Mittagstisch {

        protected BistroImBic(String bistroJson) {
                super(bistroJson);
        }

        public Lunch getLunch(final int days) {
                final Bistro bistro = MittagstischUtils.readBistroConfig(bistroJson, "bistroImBic");
                bistro.setDays(days);
                return crawlLunch(bistro);
        }

        /**
         * Gets specific data of the lunch for "Bistro im BIC".
         */
        protected String crawlSpecificData(final Bistro bistro, final HtmlPage htmlPage, final String mainContent) {
                String content = htmlPage.querySelectorAll(bistro.getCssLunchSelector()).stream()
                                .map(node -> node.asNormalizedText()).collect(Collectors.joining(" "));
                final String currentDay = MittagstischUtils.getDay(bistro.getDays());
                final String nextDay = MittagstischUtils.getDay(bistro.getDays() + 1);
                final int endIndex = "Freitag".equals(currentDay) ? content.length()
                                : content.indexOf(nextDay);

                return content.substring(content.indexOf(currentDay) +
                                currentDay.length(),
                                endIndex).trim().replace("\n", "<br><br>");
        }

        protected boolean isWithinRange(final Bistro bistro, final String weekText, final boolean checkForNextWeek)
                        throws Exception {
                return isWithinRange(checkForNextWeek, weekText, bistro.getDays(),
                                "((?:[0-2][0-9]|3[01]).(?:0[0-9]|1[0-2])\\.?[0-9]{0,4})", MittagstischUtils.ddMMYY, "",
                                "");
        }
}
