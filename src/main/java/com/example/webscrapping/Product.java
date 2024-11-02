package com.example.webscrapping;

public class Product {
    private String imageUrl;
    private String price;
    private String productLink;

    public Product(String imageUrl, String price, String productLink){
        this.imageUrl = imageUrl;
        this.price = price;
        this.productLink = productLink;
    }
    public String getImageUrl(){
        return imageUrl;
    }

    public String getPrice(){
        return price;
    }
    public String getProductLink(){
        return productLink;
    }
}
