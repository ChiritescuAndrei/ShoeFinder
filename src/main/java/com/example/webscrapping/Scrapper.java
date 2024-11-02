package com.example.webscrapping;

import org.openqa.selenium.WebDriver;


import java.util.List;

public interface Scrapper {
    void initialize(WebDriver driver);
    List<Product> scrape(String model, String numarModel);
    void cleanup();
    boolean isReady();
}
