package net.inpercima.mittagstisch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LunchDto {

    private BistroDto bistro;

    private String meals;
}
