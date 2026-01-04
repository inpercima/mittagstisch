package net.inpercima.mittagstisch.service;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import net.inpercima.mittagstisch.Utils;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Component
@AllArgsConstructor
public class RockyMaria {

    private final String bistroJson;
    private final ContentService contentService;

    public Lunch getLunch(final int days) {
        final Bistro bistro = Utils.readBistroConfig(bistroJson, "rockyMaria");
        return contentService.generateContent(bistro, days);
    }
}
