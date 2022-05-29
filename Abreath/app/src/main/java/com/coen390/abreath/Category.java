package com.coen390.abreath;

public class Category {
    int image;
    String category;

//This class has been implemented using the following video https://www.youtube.com/watch?v=zS8jYzLKirM&ab_channel=PhucVR
//The code is adapted to this project but solely belongs to the owner.

    public Category(int image, String category) {
        this.image = image;
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }
}
