package net.inpercima.mittagstisch.model;

import java.time.LocalDate;

public record LunchDto(
        BistroDto bistro,
        String lunches,
        Status status,
        LocalDate importDate) {
}
