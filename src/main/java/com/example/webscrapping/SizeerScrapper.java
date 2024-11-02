package com.example.webscrapping;

import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


public class SizeerScrapper implements Scrapper{
    private List<Product> products = new ArrayList<>();
    WebDriver driver;
    private boolean startSearch = false;

    @Override
    public void initialize(WebDriver driver){
        this.driver = driver;
        try{
            driver.get("https://sizeer.ro/barbati");
            handleCookies(driver);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> scrape(String modelPapuci, String numarModel) {
        try {
            performSearch(driver, modelPapuci);
            selectSize(numarModel);
            geImageAndPrice(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;

    }

    @Override
    public void cleanup(){
        if(driver != null){
            driver.quit();
            driver = null;
            System.out.println("Sizeer driver closed");
        }
        products.clear();
    }



    private static void scrollToElement(WebDriver driver, WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void geImageAndPrice(WebDriver driver) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int currentIndex = 0;
        while (true) {
            List<WebElement> imageElements = driver.findElements(By.xpath("//div[@class='s-item']//picture//img"));
            List<WebElement> priceElements = driver.findElements(By.xpath("//div[@class='s-item']//span[contains(@class, 's-item-regular') or contains(@class, 's-item-after-sale')]"));
            List<WebElement> linkElements = driver.findElements(By.xpath("//div[@class='s-item']//picture//img/ancestor::a")); // Find the parent anchor tag

            if (currentIndex >= imageElements.size()) {
                break;
            }
            WebElement currentImage = imageElements.get(currentIndex);
            WebElement currentPrice = priceElements.get(currentIndex);
            WebElement currentLink = linkElements.get(currentIndex);

            String link = currentLink.getAttribute("href");
            String src = currentImage.getAttribute("src");
            String pret = currentPrice.getText();

            if (src != null && !src.isEmpty()) {
                products.add(new Product(src,pret,link));
                scrollToElement(driver, currentImage);
                System.out.println("Imaginea " + (currentIndex + 1) + " : " + src + " pret " + pret + " link " + link);
            }

            currentIndex++;
        }
    }

    private void handleCookies(WebDriver driver) {
        try{
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            WebElement acceptCookiesButton = driver.findElement(By.xpath("//button[@id='onetrust-accept-btn-handler']"));
            acceptCookiesButton.click();
            System.out.println("SizerScrapper initialized");
            startSearch = true;
        } catch (NoSuchElementException e) {
            System.out.println("Cookie consent button not found, continuing without accepting cookies.");
        }
    }

    private void performSearch(WebDriver driver, String modelPapuci) {
        try{
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement searchField = driver.findElement(By.xpath("//input[@id='snrs_searchInput']"));
        searchField.click();
        searchField.sendKeys(modelPapuci);
        searchField.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(10000);
            takeScreenshot("resources/ScreenShots/dupa10secunde.png");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(22000);
            takeScreenshot("resources/ScreenShots/dupa25secunde..png");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectSize(String numarMarime) {
        try {
            WebElement afisareLista = driver.findElement(By.xpath("//p[contains(text(),'Mărime')]"));
            afisareLista.click();
            try{
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            };

            WebElement marime = driver.findElement(By.xpath("//label[@for='"+numarMarime+"']//p[@class='s-facet']"));
            marime.click();
        } catch (Exception e) {
            System.out.println("An error occurred while selecting the size: " + e.getMessage());
        }
    }

    private void takeScreenshot(String filePath) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            File destination = new File(filePath);
            destination.getParentFile().mkdirs(); // Create directories if they don't exist
            Files.copy(screenshot.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot saved: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    @Override
    public boolean isReady() {
        return startSearch;
    }
}

