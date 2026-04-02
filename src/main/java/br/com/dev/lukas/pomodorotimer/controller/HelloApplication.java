package br.com.dev.lukas.pomodorotimer.controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {        //Stage > Scene > SceneGraph
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/br/com/dev/lukas/pomodorotimer" +
                "/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setResizable(false); // Disable resizable window
        stage.setTitle("Pomondoro");
        //stage.setFullScreen(true);
        //stage.setFullScreenExitHint("isi nairi isi nairou ele é o jefo jefoou");

        stage.setScene(scene);
        stage.show();
    }
}
