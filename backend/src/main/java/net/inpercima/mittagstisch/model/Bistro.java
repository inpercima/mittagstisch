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

    private int days;

    private boolean daily = true;

    private boolean pdf = false;

    private boolean pdfFullPath = false;

    private boolean disabled = false;
}
