package net.inpercima.mittagstisch.model;

import lombok.Data;

@Data
public class Lunch {

    private String page;

    private String food;

    private String price;
    
    private String status;

    public Lunch(final String page) {
        this.page = page;
    }

}
