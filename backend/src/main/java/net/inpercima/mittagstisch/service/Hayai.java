package net.inpercima.mittagstisch.service;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Component
@AllArgsConstructor
public class Hayai {

    private final String bistroJson;
    private final ContentService contentService;

    // public Lunch getLunch(final int days) {
    //     final Bistro bistro = Utils.readBistroConfigById(bistroJson, "hayai");
    //     return contentService.generateContent(bistro, days);
    // }
}
