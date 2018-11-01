package com.chrumck.pjatk.myshoppinglist;

public class Product {
    public String name;
    public double price;
    public int quantity;
    public boolean bought;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.bought = false;
    }
}