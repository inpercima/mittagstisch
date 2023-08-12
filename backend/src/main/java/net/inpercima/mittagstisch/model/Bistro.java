package net.inpercima.mittagstisch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bistro {

    private Integer id;

    private String name;

    private String lunchSelector;

    private String url;

    private String weekSelector;

    private String weekSelectorXPath;

    private boolean daily;

    private boolean disabled;

    private int days;
}
