package com.example.webscrapping;

import org.openqa.selenium.WebDriver;

public class TestLogic {

    public static void main(String[] args) {
        WebDriver driver = WebDriverFactory.createWebDriver();
        FootShopScrapper zaalandoScrapper = new FootShopScrapper();
        try {
        zaalandoScrapper.initialize(driver);
        zaalandoScrapper.scrape("Nike Air Force 1","42");

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }



    }
}
