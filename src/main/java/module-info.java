module com.example.shoefinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.support;
    requires dev.failsafe.core;
    requires com.google.auto.service;
    requires org.checkerframework.checker.qual;


    opens com.example.shoefinder to javafx.fxml;
    exports com.example.shoefinder;
    exports com.example.controllers;
    opens com.example.controllers to javafx.fxml;
}
