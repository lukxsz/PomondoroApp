package br.com.dev.lukas.pomodorotimer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML private Spinner<Integer> customCicleSpinner;
    @FXML private Spinner<Integer> spinnerMinutesFocus;
    @FXML private Spinner<Integer> spinnerSecondsFocus;
    @FXML private Spinner<Integer> spinnerMinutesBreak;
    @FXML private Spinner<Integer> spinnerSecondsBreak;
    @FXML private Spinner<Integer> spinnerMinutesLBreak;
    @FXML private Spinner<Integer> spinnerSecondsLBreak;
    @FXML private Button saveChangesButton;
    private HelloController centralController;

    public void setCentralController(HelloController centralController) {
        this.centralController = centralController;
    }

    public void initialize() {

        SpinnerValueFactory<Integer> minutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 25);
        spinnerMinutesFocus.setValueFactory(minutes);
        SpinnerValueFactory<Integer> seconds = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        spinnerSecondsFocus.setValueFactory(seconds);

        SpinnerValueFactory<Integer> minutesBreak = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 5);
        spinnerMinutesBreak.setValueFactory(minutesBreak);
        SpinnerValueFactory<Integer> secondsBreak = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        spinnerSecondsBreak.setValueFactory(secondsBreak);

        SpinnerValueFactory<Integer> minutesLBreak = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 15);
        spinnerMinutesLBreak.setValueFactory(minutesLBreak);
        SpinnerValueFactory<Integer> secondsLBreak = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        spinnerSecondsLBreak.setValueFactory(secondsLBreak);



        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,9, 5);
        customCicleSpinner.setValueFactory(valueFactory);
    }


    @FXML
    public void onSavedChanges(){
        try{
            //Tempo de foco
            int minutesFocus = spinnerMinutesFocus.getValue();
            int secondsFocus = spinnerSecondsFocus.getValue();
            int durationFocus = (minutesFocus*60) + secondsFocus;

            System.out.println("Novo tempo: " + durationFocus);

            //Tempo de pausa
            int minutesBreak =  spinnerMinutesBreak.getValue();
            int secondsBreak = spinnerSecondsBreak.getValue();
            int durationBreak = (minutesBreak*60) + secondsBreak;
            System.out.println("Novo tempo: " + durationBreak);

            //Tempo de pausa longa
            int minutesLBreak =  spinnerMinutesLBreak.getValue();
            int secondsLBreak = spinnerSecondsLBreak.getValue();
            int durationLBreak = (minutesLBreak*60) + secondsLBreak;
            System.out.println("Novo tempo: " + durationLBreak);

            //Ciclos ate a pasua longa
            int howMuchToLongBreak = customCicleSpinner.getValue();


            centralController.updateTimer(durationFocus,durationBreak,howMuchToLongBreak,durationLBreak);
            centralController.openTimer();

        }catch(NumberFormatException e){
            System.out.println("Ei, digite apenas números!");
        }
    }



}
