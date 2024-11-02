package com.example.controllers;

import com.example.webscrapping.Scrapper;
import com.example.webscrapping.WebDriverFactory;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class CreateDriveTask extends Task<Void> {
    private final CheckBox checkBox;
    private final Scrapper scrapper;
    private final WebDriver driver;
    private final Map<CheckBox, WebDriver> webDrivers;

    public CreateDriveTask(CheckBox checkBox, Scrapper scrapper, WebDriver driver, Map<CheckBox, WebDriver> webDrivers) {
        this.checkBox = checkBox;
        this.scrapper = scrapper;
        this.driver = driver;
        this.webDrivers = webDrivers;
    }

    @Override
    protected Void call() throws Exception {
        // Initialize the scrapper with the WebDriver
        if (scrapper != null) {
            webDrivers.put(checkBox, driver);
            scrapper.initialize(driver);
        }
        return null;
    }

    public void cleanup() {
        if (scrapper != null) {
            scrapper.cleanup();
            WebDriver driver = webDrivers.remove(checkBox);
            if (driver != null) {
                driver.quit();
            }
        }
    }
}

