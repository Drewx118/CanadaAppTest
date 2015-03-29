package com.example.chriscorscadden1.canadaapptest;

public class Fact {

    // title stores the title for the fact
    private String title;
    // description store the description for the fact
    private String description;
    // imageHref stores the image URL for the fact
    private String imageHref;

    public Fact(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageHref() {
        return imageHref;
    }

    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }

}
