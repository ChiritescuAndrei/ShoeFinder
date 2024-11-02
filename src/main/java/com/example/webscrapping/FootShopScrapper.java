package com.example.webscrapping;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class FootShopScrapper implements Scrapper {
     private List<Product> products = new ArrayList<>();
     WebDriver driver;
     private boolean startSearch = false;

    @Override
    public void initialize(WebDriver driver){
        this.driver = driver;
        try{
            driver.get("https://www.footshop.ro/ro/barbati");
            handleCookies(driver);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> scrape(String modelPapuci, String numarMarime) {
        try {
            performSearch(driver, modelPapuci);
            selectSize(driver, numarMarime);
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
            System.out.println("FootShop driver closed");
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
        try{
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int currentIndex = 0;
        while (true) {
            List<WebElement> imageElements = driver.findElements(By.xpath("//div[contains(@class,'Product_image')]//img"));
            List<WebElement> priceElements = driver.findElements(By.xpath("//div[contains(@class,'ProductPrice')]//strong"));
            List<WebElement> linkElements = driver.findElements(By.xpath("//div[contains(@class,'Product_image')]/ancestor::a")); // Find the parent anchor tag
            int numElements = Math.min(Math.min(imageElements.size(), priceElements.size()), linkElements.size());
            if (currentIndex >= numElements) {
                break;
            }
            WebElement currentImage = imageElements.get(currentIndex);
            WebElement currentPrice = priceElements.get(currentIndex);
            WebElement currentLink = linkElements.get(currentIndex);

            scrollToElement(driver,currentPrice);

            String link = currentLink.getAttribute("href");
            String src = currentImage.getAttribute("src");
            String pret = currentPrice.getText();

            if (src != null && !src.isEmpty()) {
                products.add(new Product(src,pret,link));
                System.out.println("Imaginea " + (currentIndex + 1) + " : " + src + " pret " + pret + " link " + link);
            }

            currentIndex++;
        }
    }

    private void handleCookies(WebDriver driver) {
        try{
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            WebElement acceptCookiesButton = driver.findElement(By.xpath("//button[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']"));
            acceptCookiesButton.click();
            System.out.println("FootShopScrapper initialized");
            startSearch = true;
        } catch (NoSuchElementException e) {
            System.out.println("Cookie consent button not found, continuing without accepting cookies.");
        }
    }

    private void performSearch(WebDriver driver, String modelPapuci) {
        WebElement searchField = driver.findElement(By.xpath("//input[@placeholder='Caută branduri, categorii sau produse']"));
        searchField.sendKeys(modelPapuci);
        searchField.sendKeys(Keys.ENTER);

    }

    private void selectSize(WebDriver driver, String numarMarime) {
        try {
            WebElement afisareLista = driver.findElement(By.xpath("(//span[@class='ShowAll_button_1tOXq'][normalize-space()='Vezi toate'])[1]"));
            scrollToElement(driver, afisareLista);
            afisareLista.click();

            List<WebElement> marimi = driver.findElements(By.xpath("//a[@class='FilterCollectionItem_checkbox_1NkuU']//span[normalize-space(text()) = '" +numarMarime+"' or normalize-space(text()) = '"+numarMarime+".5']"));
            for(WebElement marime: marimi){
                marime.click();
            }


            Thread.sleep(400);
        } catch (Exception e) {
            System.out.println("An error occurred while selecting the size: " + e.getMessage());
        }
    }

    @Override
    public boolean isReady() {
        return startSearch;
    }


}


