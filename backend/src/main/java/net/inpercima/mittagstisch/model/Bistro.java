package net.inpercima.mittagstisch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bistro {

    private String name;

    private String url;

    private String cssWeekSelector;

    private String xpathWeekSelector;

    private String cssLunchSelector;

    private String selector;

    private int days;

    private boolean daily = true;

    private boolean document = false;

    private boolean documentFullPath = false;

    private boolean disabled = false;
}
