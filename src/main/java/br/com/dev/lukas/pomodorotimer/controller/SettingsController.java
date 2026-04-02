package br.com.dev.lukas.pomodorotimer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML private Spinner<Integer> numberBreakSpinner;
    @FXML private TextField inputFocusDuration;
    @FXML private TextField inputBreakDuration;
    @FXML private Button saveChangesButton;
    private HelloController centralController;

    public void setCentralController(HelloController centralController) {
        this.centralController = centralController;
    }


    @FXML
    public void onSavedChanges(){
        try{
            String duration = inputFocusDuration.getText();
            String breakDuration = inputBreakDuration.getText();

            int durationInt = Integer.parseInt(duration);
            int breakDurationInt = Integer.parseInt(breakDuration);

            centralController.updateTimer(durationInt,breakDurationInt);
            centralController.openTimer();

        }catch(NumberFormatException e){
            System.out.println("Ei, digite apenas números!");
        }
    }

    //TODO: Configurar o Spinner para a quantidade de pausas.



}
