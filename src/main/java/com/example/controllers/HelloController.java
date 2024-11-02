package com.example.controllers;

import com.example.webscrapping.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.*;

public class HelloController {
    @FXML
    private ScrollPane scrollPaneParent;

    @FXML
    private Button scrapeButton;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane myGridPane;

    @FXML
    private HBox HBoxItems;

    @FXML
    private VBox VBoxItems;

    @FXML
    private CheckBox footShopCheck;

    @FXML
    private CheckBox sizeerCheck;

    @FXML
    private CheckBox zaalandoCheck;

    @FXML
    private Label numberOfThreads;

    @FXML
    private ChoiceBox <String> ordonareList ;

    @FXML
    private ChoiceBox<String> marimeList;

    private VBox animation;

    private int currentRow = 0;
    private int currentCol = 0;
    private int activeTask = 0;
    private Timeline timeline;
    private boolean firstDecrement = false;

    private Map<CheckBox, Scrapper> scrappers = new HashMap<>();
    private Map<CheckBox, WebDriver> webDrivers = new HashMap<>();

    @FXML
    public void initialize() throws IOException {
        scrappers.put(footShopCheck, new FootShopScrapper());
        scrappers.put(sizeerCheck, new SizeerScrapper());
        scrappers.put(zaalandoCheck, new ZaalandoScrapper());


        setupCheckBoxAction(footShopCheck);
        setupCheckBoxAction(sizeerCheck);
        setupCheckBoxAction(zaalandoCheck);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/shoefinder/animation.fxml"));
        animation = fxmlLoader.load();
        scrollPaneParent.setFitToWidth(true);
        scrollPaneParent.setFitToHeight(true);

        HBoxItems.setHgrow(scrollPaneParent, Priority.ALWAYS);
        VBox.setVgrow(HBoxItems,Priority.ALWAYS);
        VBox.setVgrow(scrollPaneParent, Priority.ALWAYS);

        ordonareList.setValue("Ordonare dupa pret");
        ordonareList.getItems().addAll("Crescator","Descrescator");
        adaugareFunctionalitateChoiceBox(ordonareList);
        ordonareList.setVisible(false);
        ordonareList.setManaged(false);

        marimeList.setValue("None");
        marimeList.getItems().addAll("39","40","41","42","43","44","45");

        searchField.setPromptText("Enter the model here");

    }

    private void setupCheckBoxAction(CheckBox checkBox){
        checkBox.setOnAction(event ->{
            if (checkBox.isSelected()){
                Task<Void> initTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        WebDriver driver = WebDriverFactory.createWebDriver();
                        Scrapper scrapper = scrappers.get(checkBox);
                        scrapper.initialize(driver);
                        if (scrapper != null) {
                            webDrivers.put(checkBox, driver);
                        }
                        return null;
                    }
                };
                new Thread(initTask).start();
            }else{
               removeAfterSeach(checkBox);
            }
        });
    }
    @FXML
    protected void showResults() {
        numberOfThreads.setText("");
        currentCol = 0;
        currentRow = 0;
        boolean anySelected = scrappers.keySet().stream().anyMatch(CheckBox::isSelected);
        boolean allReady = scrappers.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())  // Only selected checkboxes
                .map(Map.Entry::getValue)                      // Get the Scrapper instances
                .allMatch(Scrapper::isReady);

        if(marimeList.getValue().equals("None")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No size selected");
            alert.setContentText("Please select a size before searching.");
            alert.showAndWait();
            return;
        }

        if (!anySelected) {
            // If no checkbox is selected, show an alert and exit
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No site Selected");
            alert.setContentText("Please select at least one site to start scraping.");
            alert.showAndWait();
            return;
        }

        if (!allReady) {
            // If some selected scrappers are not ready, show an alert and exit
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not All Scrappers Ready");
            alert.setHeaderText("Please wait");
            alert.setContentText("Please wait until all selected scrappers are ready to start.");
            alert.showAndWait();
            return;
        }
        createTimeLine();
        scrollPaneParent.setContent(animation);
        myGridPane.getChildren().clear();

        for(Map.Entry<CheckBox, Scrapper> entry : scrappers.entrySet()){
            CheckBox checkBox = entry.getKey();
            Scrapper scrapper = entry.getValue();

            if(checkBox.isSelected()){
                Task<List<Product>> scrapeTask = new Task<>() {

                    @Override
                    protected List<Product> call() {
                        return scrapper.scrape(searchField.getText(), marimeList.getValue());
                    }

                    @Override
                    protected void succeeded() {
                        scrollPaneParent.setContent(myGridPane);
                        List<Product> products = getValue();
                        for (Product product : products) {
                            try {
                                addProductToGridPane(product);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        checkBox.setSelected(false);
                        removeAfterSeach(checkBox);
                        System.out.println("Unchecked box done");
                        activeTask--;
                        firstDecrement = true;
                    }

                    @Override
                    protected void failed() {
                        showAlert("Error", "Scrapping Failed");
                        getException().printStackTrace();
                        activeTask--;
                        firstDecrement = true;
                    }
                };
                activeTask++;
                new Thread(scrapeTask).start();
            }
        }
    }

    private VBox createProductItem(Product product) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/shoefinder/shoeView.fxml"));
        VBox productItem = fxmlLoader.load();

        ProductItemController controller = fxmlLoader.getController();
        controller.setProductData(product.getImageUrl(), product.getPrice(), product.getProductLink());

        productItem.setUserData(controller);

        return productItem;
    }

    private void addProductToGridPane(Product product) throws IOException {
        VBox productItem = createProductItem(product);

        if (currentCol == 4) {
            currentRow++;
            currentCol = 0;
        }

        myGridPane.add(productItem, currentCol, currentRow);

        currentCol++;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private  void removeAfterSeach(CheckBox checkBox) {
        Scrapper scrapper = scrappers.get(checkBox);
        if (scrapper != null) {
            scrapper.cleanup();
            WebDriver driver = webDrivers.remove(checkBox);
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void createTimeLine() {
        // Base text and text variations for the animation
        String baseText = "Searching still happening in background";
        String[] textStates = {baseText, baseText + ".", baseText + "..", baseText + "..."};
        int[] stateIndex = {0}; // To keep track of the current animation state

        // Initialize the Timeline with a repeating KeyFrame
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    if (firstDecrement) {
                        // Update the label with the current state of the animation
                        numberOfThreads.setText(textStates[stateIndex[0]]);
                        numberOfThreads.setTextFill(Color.RED);
                        // Update the index to loop through the text states
                        stateIndex[0] = (stateIndex[0] + 1) % textStates.length;
                    }
                    if(activeTask == 0){
                        ordonareList.setVisible(true);
                        ordonareList.setManaged(true);
                        marimeList.setValue("None");
                        timeline.stop();
                        timeline = null;
                        numberOfThreads.setText("Searching finished.");
                        numberOfThreads.setTextFill(Color.GREEN);
                        System.out.println("Timeline Stopped");
                        firstDecrement = false;
                        }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void adaugareFunctionalitateChoiceBox(ChoiceBox<String> choiceBox){
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable , oldValue, newValue) -> {
            if(newValue != null) {
                switch (newValue){
                    case "Ordonează după pret":
                        System.out.println("Mesaj informativ: Vă rugăm să alegeți o opțiune de sortare.");
                        break;
                    case "Crescator":
                        System.out.println("A fost selectată ordonarea crescătoare.");
                        ordoneazaCrescator();
                        scrollPaneParent.setVvalue(0);
                        break;
                    case "Descrescator":
                        System.out.println("A fost selectată ordonarea descrescătoare.");
                        // Adaugă aici logica pentru ordonare descrescătoare
                        ordoneazaDescrescator();
                        scrollPaneParent.setVvalue(0);
                        break;
                    default:
                        System.out.println("Opțiune necunoscută");
                        break;
                }
            }
        });
    }

    private void ordoneazaDescrescator() {
        {
            List<VBox> productItems = new ArrayList<>();

            for(int i = 0; i < myGridPane.getChildren().size(); i++){
                VBox productItem = (VBox) myGridPane.getChildren().get(i);
                productItems.add(productItem);
            }

            productItems.sort((vbox1, vbox2) -> {
                ProductItemController controller1 = (ProductItemController) vbox1.getUserData();
                ProductItemController controller2 = (ProductItemController) vbox2.getUserData();

                // Obține prețurile
                double price1 = controller1.getProductPrice();
                double price2 = controller2.getProductPrice();

                // Compară descrescător
                return Double.compare(price2, price1); // Comparație descrescătoare
            });

            myGridPane.getChildren().clear();
            int col = 0;
            int row = 0;

            for(VBox productItem : productItems){
                myGridPane.add(productItem,col,row);
                col++;
                if (col == 4){
                    col = 0;
                    row++;
                }
            }
        }
    }

    private void ordoneazaCrescator() {
        List<VBox> productItems = new ArrayList<>();

        for(int i = 0; i < myGridPane.getChildren().size(); i++){
            VBox productItem = (VBox) myGridPane.getChildren().get(i);
            productItems.add(productItem);
        }

        productItems.sort(Comparator.comparingDouble(vbox -> {
            ProductItemController controller = (ProductItemController) vbox.getUserData();
            return (controller.getProductPrice());
        }));

        myGridPane.getChildren().clear();
        int col = 0;
        int row = 0;

        for(VBox productItem : productItems){
            myGridPane.add(productItem,col,row);
            col++;
            if (col == 4){
                col = 0;
                row++;
            }
        }
    }


}