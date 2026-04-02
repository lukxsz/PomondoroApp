package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.BreakTimer;
import br.com.dev.lukas.pomodorotimer.model.Pomodoro;
import br.com.dev.lukas.pomodorotimer.model.Task;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class HelloController {

    @FXML private Label labelTimer;
    @FXML public Button buttonTimer;
    @FXML private Timeline timeline;
    @FXML private Timeline breaktimeline;
    @FXML private ListView<Task> taskView;
    @FXML private TextField inputTask;
    @FXML private Pomodoro pomodoroTimer = new Pomodoro(25);
    @FXML private BreakTimer breakTimer =  new BreakTimer(5);
    @FXML private ObservableList<Task> visibleTasks = FXCollections.observableArrayList();
    @FXML  public Button buttonTask;
    @FXML private BorderPane mainPane;
    @FXML private VBox timerVBox;
    @FXML private Button skipStageButton;



    @FXML
    private void initialize() {
        labelTimer.setText(pomodoroTimer.formatedTime());
        taskView.setItems(visibleTasks);
        pomodoroTimer();

    };

    @FXML
    private void pomodoroTimer() {
        System.out.println("Hora de focar");
        breakTimer.setStatus(false); pomodoroTimer.setStatus(true);
        pomodoroTimer.reStartTimer();
        labelTimer.setText(pomodoroTimer.formatedTime());
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->{ //define o tempo do tick
            pomodoroTimer.startTimer();
            labelTimer.setText(pomodoroTimer.formatedTime());
//            timerVBox = mainPane.getCenter();
            if (pomodoroTimer.isFinished()){
                timeline.stop();
                breakTime();
                buttonTimer.setText("Retomar");
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    protected void buttonClicked(ActionEvent event) {
        if(pomodoroTimer.isStatus()){
            if (timeline.getStatus() == Animation.Status.STOPPED) {
                timeline.play();
                buttonTimer.setText("Pausar");
            }else  if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
                buttonTimer.setText("Retomar");
            }
        }else if(breakTimer.isStatus()){
            if (breaktimeline.getStatus() == Animation.Status.STOPPED) {
                breaktimeline.play();
                buttonTimer.setText("Pausar");
            }else  if (breaktimeline.getStatus() == Animation.Status.RUNNING) {
                breaktimeline.stop();
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

            taskView.refresh(); // Força a interface a ler o toString() de novo
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



        } catch (IOException e) {
            System.out.println("Erro ao carregar configurações" + e.getMessage());
        }
    }

    @FXML
    public void openTimer() {
        // coloca no centro o Vbox do timer
        mainPane.setCenter(timerVBox);
    }

    @FXML
    public void updateTimer(int newTime, int newBreakTime) {
        pomodoroTimer.setTimer(newTime);
        breakTimer.setTimer(newBreakTime);
        labelTimer.setText(pomodoroTimer.formatedTime());
    }

    @FXML
    public void breakTime() {
        System.out.println("a pausa comecou");
        pomodoroTimer.setStatus(false); breakTimer.setStatus(true);
        breakTimer.reStartTimer();
        labelTimer.setText(breakTimer.formatedTime());
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
        }
    }

}
