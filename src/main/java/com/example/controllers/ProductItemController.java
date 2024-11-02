package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.net.URI;


public class ProductItemController {

    @FXML
    private ImageView imaginePapuci;

    @FXML
    private Label pret;

    public void setProductData(String imageUrl, String price, String shoeURL){
        imaginePapuci.setImage(new Image(imageUrl));
        pret.setText(price);
        imaginePapuci.setOnMouseClicked(event -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(shoeURL));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public double getProductPrice(){
        String primulCuvant = pret.getText().split(" ")[0].replace(",",".");
        return Double.parseDouble(primulCuvant);
    }

}
