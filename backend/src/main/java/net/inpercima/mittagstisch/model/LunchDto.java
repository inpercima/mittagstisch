package net.inpercima.mittagstisch.model;

import java.time.LocalDate;
import java.util.List;

public record LunchDto(
        BistroDto bistro,
        List<DishDto> dishes,
        StatusEnum status,
        LocalDate importDate) {
}
