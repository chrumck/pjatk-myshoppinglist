package com.chrumck.pjatk.myshoppinglist;

public class Product {
    public String id;
    public String name;
    public double price;
    public long quantity;
    public boolean bought;

    public Product() {
    }

    public Product(String id, String name, double price, long quantity, boolean bought) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.bought = bought;
    }
}