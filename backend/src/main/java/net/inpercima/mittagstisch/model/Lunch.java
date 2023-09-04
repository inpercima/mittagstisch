package net.inpercima.mittagstisch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lunch {

    private String bistroName;

    private String meal;

    private String price;

    private String status;

    private String pdfLink;
}
