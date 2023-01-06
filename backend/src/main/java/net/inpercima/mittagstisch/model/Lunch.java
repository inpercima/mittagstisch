package net.inpercima.mittagstisch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lunch {

    private String name;

    private String food;

    private String price;

    private String status;


}
