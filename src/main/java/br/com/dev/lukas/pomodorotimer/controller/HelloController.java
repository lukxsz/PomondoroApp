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
import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;


public class HelloController {

    @FXML private HBox buttonsHbox;
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
    @FXML private VBox leftPane;
    @FXML private Button skipStageButton;
    @FXML private ImageView imgMoita;
    @FXML private  ImageView cenarioView;
    @FXML private StackPane battleContainer;
    private Timeline animationBushTimiline;

    @FXML
    private void initialize() {
        labelTimer.setText(pomodoroTimer.formatedTime());
        taskView.setItems(visibleTasks);
        animationTallGrass();
        pomodoroTimer();

        mainPane.styleProperty().bind(
                Bindings.concat(
                        "-fx-font-size: ",
                        mainPane.widthProperty().divide(60).asString("%.1f"),
                        "px;"
                )
        );

        // Listener de responsividade: oculta os painéis laterais em telas pequenas
        mainPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            boolean isLargeEnoughLeft = newValue.doubleValue() >= 400;
            boolean isLargeEnoughRight = newValue.doubleValue() >= 600;
            boolean isWidgetMode = newValue.doubleValue() < 380;
            
            leftPane.setVisible(isLargeEnoughLeft);
            leftPane.setManaged(isLargeEnoughLeft);
            
            rightPane.setVisible(isLargeEnoughRight);
            rightPane.setManaged(isLargeEnoughRight);

            buttonsHbox.setVisible(!isWidgetMode);
            buttonsHbox.setManaged(!isWidgetMode);

            if (isWidgetMode && mainPane.getCenter() != timerVBox) {
                openTimer();
            }
        });

        cenarioView.fitWidthProperty().bind(battleContainer.prefWidthProperty().subtract(6));
        cenarioView.fitHeightProperty().bind(battleContainer.prefHeightProperty().subtract(6));

        imgMoita.fitWidthProperty().bind(battleContainer.prefWidthProperty().divide(3.0));

        labelTimer.styleProperty().bind(
                Bindings.concat(
                        "-fx-font-size: ",
                        battleContainer.prefWidthProperty().multiply(0.32).asString(),
                        "px;"
                )
        );
        timerVBox.widthProperty().addListener((obs,oldVal,newVal)-> redimensionarArea());
        timerVBox.heightProperty().addListener((obs,oldVal,newVal)-> redimensionarArea());

        battleContainer.maxWidthProperty().bind(battleContainer.prefWidthProperty());
        battleContainer.maxHeightProperty().bind(battleContainer.prefHeightProperty());

        battleContainer.setMinWidth(0);
        battleContainer.setMinHeight(0);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            pomodoroTimer.startTimer(); animationBushTimiline.play();
            labelTimer.setText(pomodoroTimer.formatedTime());
            if (pomodoroTimer.isFinished()){
                stopAllTimers();
                animationBushTimiline.stop();
                setPlayIcon();
                breakTime();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        breaktimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            breakTimer.startTimer();
            labelTimer.setText(breakTimer.formatedTime());
            if (breakTimer.isFinished()){
                stopAllTimers();
                setPlayIcon();
                pomodoroTimer();
            }
        }));
        breaktimeline.setCycleCount(Timeline.INDEFINITE);

        longbreaktimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            longbreakTimer.startTimer();
            labelTimer.setText(longbreakTimer.formatedTime());
            if (longbreakTimer.isFinished()){
                stopAllTimers();
                setPlayIcon();
                pomodoroTimer();
            }
        }));
        longbreaktimeline.setCycleCount(Timeline.INDEFINITE);

    }

    @FXML
    private void pomodoroTimer() {
        System.out.println("Hora de focar");
        stopAllTimers();
        breakTimer.setStatus(false); longbreakTimer.setStatus(false);
        pomodoroTimer.setStatus(true);
        pomodoroTimer.reStartTimer();
        labelTimer.setText(pomodoroTimer.formatedTime());
        pomodoroTimer.increaseCycleCounter();
        if(pomodoroTimer.cycleCheck()){
            isTimeToCatch();
        }
    }

    @FXML
    protected void buttonClicked(ActionEvent event) {
        if(pomodoroTimer.isStatus()){
            if (timeline.getStatus() == Animation.Status.STOPPED) {
                timeline.play();
                soundChooser(2);
                setPauseIcon();
            }else  if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
                setPlayIcon();
            }
        }else if(breakTimer.isStatus()){
            if (breaktimeline.getStatus() == Animation.Status.STOPPED) {
                breaktimeline.play();
                soundChooser(2);
                setPauseIcon();
            }else  if (breaktimeline.getStatus() == Animation.Status.RUNNING) {
                breaktimeline.stop();
                setPlayIcon();
            }
        } else if (longbreakTimer.isStatus()) {
            if (longbreaktimeline.getStatus() == Animation.Status.STOPPED) {
                longbreaktimeline.play();
                soundChooser(2);
                setPauseIcon();
            }else  if (longbreaktimeline.getStatus() == Animation.Status.RUNNING) {
                longbreaktimeline.stop();
                setPlayIcon();
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
        stopAllTimers();
        pomodoroTimer.setTimer(newTime);
        breakTimer.setTimer(newBreakTime);
        longbreakTimer.setTimer(longBreakDuration);
        breakTimer.setWhenLongBreak(howMuchToLongBreak);
        labelTimer.setText(pomodoroTimer.formatedTime());
        pomodoroTimer.resetCycleCounter();
        breakTimer.resetCycleCounter();
        setPlayIcon();

    }

    @FXML
    public void breakTime() {
        int whenLong = breakTimer.getWhenLongBreak();
        int cycleCounter = breakTimer.getCycleCounter();
        stopAllTimers();

        if (cycleCounter < whenLong) {
            soundChooser(1);
            pomodoroTimer.setStatus(false); longbreakTimer.setStatus(false);
            breakTimer.setStatus(true);
            breakTimer.reStartTimer();
            labelTimer.setText(breakTimer.formatedTime());
            breakTimer.increaseCycleCounter();

        } else if (cycleCounter == whenLong) {
            soundChooser( 0);
            pomodoroTimer.setStatus(false); breakTimer.setStatus(false);
            longbreakTimer.setStatus(true);
            longbreakTimer.reStartTimer();
            labelTimer.setText(longbreakTimer.formatedTime());
            breakTimer.resetCycleCounter();
        }

    }

    @FXML
    public void onSkipStageButtonClicked(ActionEvent event) {

        if (pomodoroTimer.isStatus()) {
            timeline.stop();
            setPlayIcon();
            breakTime();
        } else if (breakTimer.isStatus()) {
            breaktimeline.stop();
            setPlayIcon();
            pomodoroTimer();
        } else if (longbreakTimer.isStatus()) {
            longbreaktimeline.stop();
            setPlayIcon();
            pomodoroTimer();
        }
    }

    @FXML
    public void soundChooser(int option) {
        switch (option) {
            case 0:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/PokemonCenterRecoverwav.wav",-20.0f);
                break;
            case 1:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/PokemonLevelUp.wav",-20.0f);
                break;
            case 2:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/ButtonPressed.wav",-15.0f);
                break;
            case 3:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/GrassSound.wav",-50.0f);
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
        Set<Integer> catched = User.getInstance().getUserPokemons();

        if(catched.size() >= 150){
            showCongratulations();
            return null;
        }

        List<Integer> missing = new ArrayList<>();
        for(int i = 1; i <=150; i++){
            if(!catched.contains(i)){
                missing.add(i);
            }
        }

        Random random = new Random();
        return missing.get(random.nextInt(missing.size()));
    }

    private void showCongratulations(){
        soundChooser(0);
        Alert alert = createCongratulationsAlert();
        alert.show();
    }

    Alert createCongratulationsAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Parabéns, Mestre Pokémon!");
        alert.setHeaderText(null);

        // Mew (#151) como recompensa secreta por completar a Pokédex
        String urlImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official"
                + "-artwork/" + 151 + ".png";

        Image pokemonImage = new Image(urlImage, 200, 200, true, true);
        ImageView imageView = new ImageView(pokemonImage);

        alert.setGraphic(imageView);

        alert.setContentText(
                "Você completou a Pokédex!\n"
              + "Todos os 150 Pokémon foram capturados!\n\n"
              + "Como recompensa, Mew apareceu para você!\n"
              + "Você é um verdadeiro Mestre Pokémon!"
        );

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/br/com/dev/lukas/pomodorotimer/css/style.css").toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("pokemon-alert");

        return alert;
    }

    private void showCaptureAlert(int idPokemon) {
        animationBushTimiline.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Um pokemon selvagem apareceu!");
        alert.setHeaderText(null);

        String urlImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official" +
                "-artwork/" + idPokemon + ".png";

        Image pokemonImage = new Image(urlImage, 150, 150, true, true);
        ImageView imageView = new ImageView(pokemonImage);

        alert.setGraphic(imageView);

        alert.setContentText("Você encontrou um novo pokemon!\nQue tal checar sua pokédex para saber quem é!");

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/br/com/dev/lukas/pomodorotimer/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("pokemon-alert");

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

    public void setPlayIcon (){
        Image playImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/br/com/dev/lukas" +
                "/pomodorotimer/images/right-arrow.png")));
        ImageView playView = new ImageView(playImg);
        playView.setFitHeight(30);
        playView.setFitWidth(30);
        buttonTimer.setGraphic(playView);
    }

    public void setPauseIcon (){
        Image pauseImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/br/com/dev/lukas" +
                "/pomodorotimer/images/pause-button.png")));
        ImageView pauseView = new ImageView(pauseImg);
        pauseView.setFitHeight(30);
        pauseView.setFitWidth(30);
        buttonTimer.setGraphic(pauseView);
    }

    public void redimensionarArea(){
        boolean isWidgetMode = mainPane.getWidth() < 380;

        double margemLargura = isWidgetMode ? 0 : 40;
        double margemAltura = isWidgetMode ? 0 : 120;

        double larguraDisponivel = timerVBox.getWidth() - margemLargura;
        double alturaDisponivel = timerVBox.getHeight() - margemAltura;


        if (larguraDisponivel <= 0 || alturaDisponivel <= 0) return;

        double proporcaoIdeal = 300.0 / 250.0 ;

        double novaLargura;
        double novaAltura;

        if(larguraDisponivel / alturaDisponivel > proporcaoIdeal){
            novaAltura = alturaDisponivel;
            novaLargura = alturaDisponivel * proporcaoIdeal;
        }else{
            novaLargura = larguraDisponivel;
            novaAltura = larguraDisponivel / proporcaoIdeal;
        }

        battleContainer.setPrefWidth(novaLargura);
        battleContainer.setPrefHeight(novaAltura);
    }

    public void stopAllTimers(){
        if (timeline != null) timeline.stop();
        if (breaktimeline != null) breaktimeline.stop();
        if (longbreaktimeline != null) longbreaktimeline.stop();
        if (animationBushTimiline != null) animationBushTimiline.stop();
    }
}
