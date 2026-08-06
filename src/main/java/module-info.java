module br.com.dev.lukas.pomodorotimer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires com.google.gson;
    requires java.net.http;
    requires java.sql;
    requires org.junit.jupiter.api;


    opens br.com.dev.lukas.pomodorotimer to javafx.fxml;
    exports br.com.dev.lukas.pomodorotimer.model;
    exports br.com.dev.lukas.pomodorotimer.controller;
    opens br.com.dev.lukas.pomodorotimer.controller to javafx.fxml, org.junit.platform.commons;
    opens br.com.dev.lukas.pomodorotimer.model to com.google.gson;
}