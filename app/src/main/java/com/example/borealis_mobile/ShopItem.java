package com.example.borealis_mobile;

import com.example.borealis_mobile.model.BaseElement;

public class ShopItem {
    private String id;
    private BaseElement baseElement;
    private double price;
    private String imageURL;
    private boolean isPremium;

    public ShopItem() {
    }

    public ShopItem(String id, BaseElement baseElement, double price, String imageURL, boolean isPremium) {
        this.id = id;
        this.baseElement = baseElement;
        this.price = price;
        this.imageURL = imageURL;
        this.isPremium = isPremium;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseElement getBaseElement() {
        return baseElement;
    }

    public void setBaseElement(BaseElement baseElement) {
        this.baseElement = baseElement;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
