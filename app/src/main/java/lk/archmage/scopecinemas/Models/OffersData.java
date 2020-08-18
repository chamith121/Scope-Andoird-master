package lk.archmage.scopecinemas.Models;

import java.io.Serializable;

public class OffersData implements Serializable {

    private String offerId;
    private String name;
    private String subTitle;
    private String description;
    private String imageOne;
    private String imageTwo;

    public OffersData(String offerId, String name, String subTitle, String description, String imageOne, String imageTwo) {
        this.offerId = offerId;
        this.name = name;
        this.subTitle = subTitle;
        this.description = description;
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageOne() {
        return imageOne;
    }

    public void setImageOne(String imageOne) {
        this.imageOne = imageOne;
    }

    public String getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(String imageTwo) {
        this.imageTwo = imageTwo;
    }
}
