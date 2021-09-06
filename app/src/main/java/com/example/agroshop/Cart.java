package com.example.agroshop;

public class Cart {
    String Pid;
    String date;
    String image;
    String name;
    String price;
    String quantity;
    public Cart(){}

    public Cart(String pid, String date, String image, String name, String price, String quantity) {
        Pid = pid;
        this.date = date;
        this.image = image;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPid() {
        return Pid;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
