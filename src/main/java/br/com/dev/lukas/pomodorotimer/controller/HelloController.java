package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.BreakTimer;
import br.com.dev.lukas.pomodorotimer.model.Pomodoro;
import br.com.dev.lukas.pomodorotimer.model.Task;
import br.com.dev.lukas.pomodorotimer.model.User;
import br.com.dev.lukas.pomodorotimer.utils.SaveManager;
import br.com.dev.lukas.pomodorotimer.utils.SoundManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;


public class HelloController {

    @FXML private Label labelTimer;
    @FXML public Button buttonTimer;
    @FXML private Timeline timeline;
    @FXML private Timeline breaktimeline;
    @FXML private Timeline longbreaktimeline;
    @FXML private ListView<Task> taskView;
    @FXML private TextField inputTask;
    @FXML private Pomodoro pomodoroTimer = new Pomodoro(25);
    @FXML private BreakTimer breakTimer =  new BreakTimer(5);
    @FXML private BreakTimer longbreakTimer =  new BreakTimer(15);
    @FXML private ObservableList<Task> visibleTasks = FXCollections.observableArrayList();
    @FXML private BorderPane mainPane;
    @FXML private VBox timerVBox;
    @FXML private VBox rightPane;
    @FXML private Button skipStageButton;
    @FXML private ImageView imgMoita;


    private Timeline animationBushTimiline;




    @FXML
    private void initialize() {
        labelTimer.setText(pomodoroTimer.formatedTime());
        taskView.setItems(visibleTasks);
        animationTallGrass();
        pomodoroTimer();

    };

    @FXML
    private void pomodoroTimer() {
        System.out.println("Hora de focar");
        breakTimer.setStatus(false); longbreakTimer.setStatus(false);
        pomodoroTimer.setStatus(true);
        pomodoroTimer.reStartTimer();
        labelTimer.setText(pomodoroTimer.formatedTime());
        pomodoroTimer.increaseCycleCounter();
        if(pomodoroTimer.cycleCheck()){
            isTimeToCatch();
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->{ //define o tempo do tick
            pomodoroTimer.startTimer(); animationBushTimiline.play();
            labelTimer.setText(pomodoroTimer.formatedTime());
            if (pomodoroTimer.isFinished()){
                timeline.stop(); animationBushTimiline.stop();
                buttonTimer.setText("Retomar");
                breakTime();


            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    protected void buttonClicked(ActionEvent event) {
        if(pomodoroTimer.isStatus()){
            if (timeline.getStatus() == Animation.Status.STOPPED) {
                timeline.play();
                soundChooser(2);
                buttonTimer.setText("Pausar");
            }else  if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
                buttonTimer.setText("Retomar");
            }
        }else if(breakTimer.isStatus()){
            if (breaktimeline.getStatus() == Animation.Status.STOPPED) {
                breaktimeline.play();
                soundChooser(2);
                buttonTimer.setText("Pausar");
            }else  if (breaktimeline.getStatus() == Animation.Status.RUNNING) {
                breaktimeline.stop();
                buttonTimer.setText("Retomar");
            }
        } else if (longbreakTimer.isStatus()) {
            if (longbreaktimeline.getStatus() == Animation.Status.STOPPED) {
                longbreaktimeline.play();
                soundChooser(2);
                buttonTimer.setText("Pausar");
            }else  if (longbreaktimeline.getStatus() == Animation.Status.RUNNING) {
                longbreaktimeline.stop();
                buttonTimer.setText("Retomar");
            }

        }
    }

    @FXML
    protected void onAddTask() {
        String text = inputTask.getText();
         if (text != null && !text.isEmpty()) {
             Task newTask = new Task(text);

             visibleTasks.add(newTask);

             inputTask.clear();
         }
    }

    @FXML
    public void onMarkAsDone() {
        Task selected = taskView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.isDone() == false)  {
            selected.setDone(true);

            taskView.refresh(); // Força a interface a ler o toString() denovo
        } else if (selected != null && selected.isDone()) {
            int selectedIndex = taskView.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0) {
                visibleTasks.remove(taskView.getSelectionModel().getSelectedIndex());
            }
        }



    }

    @FXML
    public void onConfiguration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/dev/lukas/pomodorotimer/settings-view.fxml"));
            Parent configScreen = loader.load();

            SettingsController gerenteDaTela = loader.getController();

            gerenteDaTela.setCentralController(this);


            mainPane.setCenter(configScreen);
            mainPane.setRight(null);

        } catch (IOException e) {
            System.out.println("Erro ao carregar configurações" + e.getMessage());
        }
    }

    @FXML
    public void onPokedex(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/dev/lukas/pomodorotimer/pokedex-view.fxml"));
            Parent pokedexScreen = loader.load();

            PokedexController gerenteDaTela = loader.getController();

            gerenteDaTela.setCentralController(this);

            mainPane.setCenter(pokedexScreen);
            mainPane.setRight(null);
        }catch (IOException e) {
            System.out.println("Erro ao carregar pokedex" +e.getMessage());
        }
    }

    @FXML
    public void openTimer() {
        mainPane.setCenter(timerVBox);
        mainPane.setRight(rightPane);
    }

    @FXML
    public void updateTimer(int newTime, int newBreakTime, int howMuchToLongBreak, int longBreakDuration) {
        pomodoroTimer.setTimer(newTime);
        breakTimer.setTimer(newBreakTime);
        longbreakTimer.setTimer(longBreakDuration);
        breakTimer.setWhenLongBreak(howMuchToLongBreak);
        labelTimer.setText(pomodoroTimer.formatedTime());
        pomodoroTimer.resetCycleCounter();
        breakTimer.resetCycleCounter();

    }

    @FXML
    public void breakTime() {
        int whenLong = breakTimer.getWhenLongBreak();
        int cycleCounter = breakTimer.getCycleCounter();
        animationBushTimiline.stop();
        if (cycleCounter < whenLong) {
            soundChooser(1);
            pomodoroTimer.setStatus(false); longbreakTimer.setStatus(false);
            breakTimer.setStatus(true);
            breakTimer.reStartTimer();
            labelTimer.setText(breakTimer.formatedTime());
            breakTimer.increaseCycleCounter();
            breaktimeline = new Timeline(new KeyFrame(Duration.seconds(1), _ ->{ //define o tempo do tick
                breakTimer.startTimer();
                labelTimer.setText(breakTimer.formatedTime());
                if (breakTimer.isFinished()){
                    breaktimeline.stop();
                    pomodoroTimer();
                    buttonTimer.setText("Retomar");
                }
            }));
            breaktimeline.setCycleCount(Timeline.INDEFINITE);
        } else if (cycleCounter == whenLong) {
            soundChooser( 0);
            pomodoroTimer.setStatus(false); breakTimer.setStatus(false);
            longbreakTimer.setStatus(true);
            longbreakTimer.reStartTimer();
            labelTimer.setText(longbreakTimer.formatedTime());
            longbreaktimeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->{ //define o tempo do tick
                longbreakTimer.startTimer();
                labelTimer.setText(longbreakTimer.formatedTime());
                if (longbreakTimer.isFinished()){
                    longbreaktimeline.stop();
                    pomodoroTimer();
                    buttonTimer.setText("Retomar");
                }
            }));
            breakTimer.resetCycleCounter();
            longbreaktimeline.setCycleCount(Timeline.INDEFINITE);
        }

    }

    @FXML
    public void onSkipStageButtonClicked(ActionEvent event) {

        if (pomodoroTimer.isStatus()) {
            timeline.stop();
            buttonTimer.setText("Retomar");
            breakTime();
        } else if (breakTimer.isStatus()) {
            breaktimeline.stop();
            buttonTimer.setText("Retomar");
            pomodoroTimer();
        } else if (longbreakTimer.isStatus()) {
            longbreaktimeline.stop();
            buttonTimer.setText("Retomar");
            pomodoroTimer();
        }
    }

    @FXML
    public void soundChooser(int option) {
        switch (option) {
            case 0:
                SoundManager.play("/sounds/PokemonCenterRecoverwav.wav",-20.0f);
                break;
            case 1:
                SoundManager.play("/sounds/PokemonLevelUp.wav",-20.0f);
                break;
            case 2:
                SoundManager.play("/sounds/ButtonPressed.wav",-15.0f);
                break;
            case 3:
                SoundManager.play("/sounds/GrassSound.wav",-50.0f);
                break;
        }
    }

    public void isTimeToCatch(){
        int pokemonCaptured = pokemonIdGenerator();
        User.getInstance().addPokemon(pokemonCaptured);
        SaveManager.saveData();
        SoundManager.playPokemonSound(pokemonCaptured);
        showCaptureAlert(pokemonCaptured);
    }

    public Integer pokemonIdGenerator(){
       Random random = new Random();

        int idPokemon = random.nextInt(150 - 1)+1;
        if(!User.getInstance().verifyPokemon(idPokemon)){
            pokemonIdGenerator();
        }
        return idPokemon;
    }

    private void showCaptureAlert(int idPokemon) {
        animationBushTimiline.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Um pokemon selvagem apareceu!");
        alert.setHeaderText("");

        String urlImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official" +
                "-artwork/" + idPokemon + ".png";

        Image pokemonImage = new Image(urlImage, 150, 150, true, true);
        ImageView imageView = new ImageView(pokemonImage);

        alert.setGraphic(imageView);

        alert.setContentText("Você encontrou um novo pokemon! \nQue tal checar sua pokédex para saber quem é!");

        alert.getDialogPane().setStyle("-fx-background-color: #fff");

        alert.showAndWait();
    }

    public void animationTallGrass(){
        Random random = new Random();

        RotateTransition shake = new RotateTransition(Duration.millis(80), imgMoita);
        shake.setByAngle(30);
        shake.setCycleCount(3);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> imgMoita.setRotate(0));

        animationBushTimiline = new Timeline(new KeyFrame(Duration.seconds(5), event ->{
            if (random.nextInt(100) < 30){
                shake.play();
                soundChooser(3);
            }
        }));

        animationBushTimiline.setCycleCount(Timeline.INDEFINITE);
    }

}
