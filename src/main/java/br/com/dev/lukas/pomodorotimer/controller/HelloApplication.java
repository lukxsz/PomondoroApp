package br.com.dev.lukas.pomodorotimer.controller;
import br.com.dev.lukas.pomodorotimer.utils.SaveManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {//Stage > Scene > SceneGraph

        SaveManager.loadData();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/br/com/dev/lukas/pomodorotimer" +
                "/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 720);
//            stage.setMinHeight(600);
//            stage.setMinWidth(400);
            stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/br/com" +
                    "/dev/lukas/pomodorotimer/images/clipart1908920.png"))));
            //stage.setResizable(false); // Disable resizable window
            stage.setTitle(" Pomondoro");
            //stage.setFullScreen(true);

            stage.setScene(scene);
            stage.show();
    }
}
