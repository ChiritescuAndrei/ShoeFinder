package com.example.controllers;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class AnimationController {

    @FXML
    private Circle circle1;

    @FXML
    private Circle circle2;

    @FXML
    private AnchorPane anchorPaneAnimation;

    @FXML
    public void initialize(){
        setRotate(circle1,true,360,10);
        setRotate(circle2, true, 180,10);
    }

    public void setRotate(Circle c, boolean reverse, int angle, int duration){

        RotateTransition rt = new RotateTransition(Duration.seconds(duration),c);

        rt.setAutoReverse(reverse);

        rt.setByAngle(angle);
        rt.setDelay(Duration.seconds(0));
        rt.setRate(3);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();

    }


}
