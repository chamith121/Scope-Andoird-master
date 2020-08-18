package lk.archmage.scopecinemas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ConcessionData implements Serializable {

    private String name;
    private String itemId;
    private String price;
    private String categoryId;
    private String category;
    private int count;
    private String image;
    private int status = 0;

    public ConcessionData(String name, String itemId, String price, String categoryId, String category, int count, String image) {
        this.name = name;
        this.itemId = itemId;
        this.price = price;
        this.categoryId = categoryId;
        this.category = category;
        this.count = count;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
