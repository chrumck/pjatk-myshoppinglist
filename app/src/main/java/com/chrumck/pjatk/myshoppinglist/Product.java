package com.chrumck.pjatk.myshoppinglist;

public class Product {
    public int id;
    public String name;
    public double price;
    public int quantity;
    public boolean bought;

    public Product(int id, String name, double price, int quantity, boolean bought) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.bought = bought;
    }
}