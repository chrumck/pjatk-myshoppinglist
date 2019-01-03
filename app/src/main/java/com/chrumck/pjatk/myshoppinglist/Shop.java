package com.chrumck.pjatk.myshoppinglist;

public class Shop {
    public String id;
    public String name;
    public String description;
    public ShopLocation location;

    public Shop() {
    }

    public Shop(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static class ShopLocation {
        public double latitude;
        public double longitude;
        public float radius;

        public  ShopLocation(){

        }

        public ShopLocation(double latitude, double longitude, float radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }
    }
}