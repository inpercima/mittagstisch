package net.inpercima.mittagstisch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lunch {

    private String bistroName;

    private String url;

    private String content;

    private String status;
}
