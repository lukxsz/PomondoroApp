package br.com.dev.lukas.pomodorotimer.controller;
import br.com.dev.lukas.pomodorotimer.utils.SaveManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Objects;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {//Stage > Scene > SceneGraph

        SaveManager.loadData();
            //docker run -d -p 5000:5000 libretranslate/libretranslate para o tradutor.
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/br/com/dev/lukas/pomodorotimer" +
                "/hello-view.fxml"));
            Parent root = fxmlLoader.load();
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double width = screenBounds.getWidth() * 0.75;
            double height = screenBounds.getHeight() * 0.75;
            System.out.println(width+" "+height);
            Scene scene = new Scene(root, width, height);

            width = width / 200;
            height = height / 5.9;

            System.out.println("width "+width+" height "+height );

            stage.setMinHeight(height);
            stage.setMinWidth(width);

            stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/br/com" +
                    "/dev/lukas/pomodorotimer/images/clipart1908920.png"))));

            //stage.setResizable(false); // Disable resizable window
            stage.setTitle("Pomondoro");

            stage.setScene(scene);
            stage.show();
    }
}
