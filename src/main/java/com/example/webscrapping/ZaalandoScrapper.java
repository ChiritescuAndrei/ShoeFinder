package com.example.webscrapping;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ZaalandoScrapper implements Scrapper{
    private List<Product> products = new ArrayList<>();
    WebDriver driver;
    private boolean startSearch = false;

    @Override
    public void initialize(WebDriver driver) {
        this.driver = driver;
        try{
            driver.get("https://www.zalando.ro/barbati-pantofi/");
            handleCookies(driver);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> scrape(String modelPapuci, String numarModel) {
        try {
            performSearch(modelPapuci);
            selectSize(numarModel);
            geImageAndPrice(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void cleanup() {
        if(driver != null){
            driver.quit();
            driver = null;
            System.out.println("Zaalando driver closed");
        }
        products.clear();
    }

    private void handleCookies(WebDriver driver){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement shadowHost = driver.findElement(By.cssSelector("#usercentrics-root"));

        WebElement shadowButton = new WebDriverWait(driver, Duration.ofSeconds(10)).until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver input) {
                return (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].shadowRoot.querySelector('.sc-dcJsrY.kXOIji')", shadowHost);
            }
        });
        shadowButton.click();
        startSearch = true;
        System.out.println("ZaalandoScrapper initialized");
    }


    private void performSearch(String modelPapuci){
        WebElement searchField = driver.findElement(By.xpath("//input[@placeholder='Căutare']"));
        searchField.sendKeys(modelPapuci);
        searchField.sendKeys(Keys.ENTER);
    }

    private void selectSize(String numarMarime) {
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            WebElement afisareLista = driver.findElement(By.xpath("//div[text()='Mărime']"));
            afisareLista.click();

            List<WebElement> marimi = driver.findElements(By.xpath("//label[contains(@for,'"+numarMarime+"')]"));
            for(WebElement marime : marimi){
                marime.click();
            }

            WebElement saveButtonMarime = driver.findElement(By.xpath("//span[contains(text(),'Salvează')]"));
            saveButtonMarime.click();

            Thread.sleep(400);
        } catch (Exception e) {
            System.out.println("An error occurred while selecting the size: " + e.getMessage());
        }
    }

    private void geImageAndPrice(WebDriver driver) {
        int currentIndex = 0;
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            List<WebElement> imageElements = driver.findElements(By.xpath("//div[@class='KVKCn3 u-C3dd jDGwVr mo6ZnF KLaowZ']//img[contains(@class, 'sDq_FX lystZ1 FxZV-M _2Pvyxl JT3_zV EKabf7 mo6ZnF _1RurXL mo6ZnF _7ZONEy') and @sizes]"));
            List<WebElement> priceElements = driver.findElements(By.xpath("//section[@class='_0xLoFW _78xIQ-']//span[1][contains(text(), 'lei')]"));
            List<WebElement> linkElements = driver.findElements(By.xpath("//div[@class='KVKCn3 u-C3dd jDGwVr mo6ZnF KLaowZ']//img[@sizes]/ancestor::a")); // Find the parent anchor tag

            int numElements = Math.min(Math.min(imageElements.size(), priceElements.size()), linkElements.size());
            if (currentIndex >= numElements) {
                break;
            }

            WebElement currentImage = imageElements.get(currentIndex);
            WebElement currentPrice = priceElements.get(currentIndex);
            WebElement currentLink = linkElements.get(currentIndex);

            scrollToElement(driver, currentPrice);

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

    private static void scrollToElement(WebDriver driver, WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
    public boolean isReady(){
        return startSearch;
    }
}
