package com.example.webscrapping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {

    public static ChromeOptions getChromeOptions(){
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");

        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--log-level=3");
        options.addArguments("--silent");

        return options;
    }

    public static WebDriver createWebDriver() {
        try {
            return new ChromeDriver(getChromeOptions());
        } catch (Exception e) {
            System.err.println("Failed to create WebDriver: " + e.getMessage());
            return null;  // Handle as appropriate for your application
        }
    }
}
