module br.com.dev.lukas.pomodorotimer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens br.com.dev.lukas.pomodorotimer to javafx.fxml;
    exports br.com.dev.lukas.pomodorotimer.model;
    exports br.com.dev.lukas.pomodorotimer.controller;
    opens br.com.dev.lukas.pomodorotimer.controller to javafx.fxml;
}