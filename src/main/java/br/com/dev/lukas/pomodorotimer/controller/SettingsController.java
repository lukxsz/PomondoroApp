package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.User;
import br.com.dev.lukas.pomodorotimer.model.UserSettings;
import br.com.dev.lukas.pomodorotimer.utils.SaveManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SettingsController {
    @FXML private Label labelFocusValue;
    @FXML private Label labelBreakValue;
    @FXML private Label labelLBreakValue;
    @FXML private Label labelCicloValue;

    @FXML private Button btnMinusFocus;
    @FXML private Button btnPlusFocus;
    @FXML private Button btnMinusBreak;
    @FXML private Button btnPlusBreak;
    @FXML private Button btnMinusLBreak;
    @FXML private Button btnPlusLBreak;
    @FXML private Button btnMinusCiclo;
    @FXML private Button btnPlusCiclo;

    @FXML private Button saveChangesButton;
    private HelloController centralController;

    private int focusMinutes = 25;
    private int breakMinutes = 5;
    private int lBreakMinutes = 15;
    private int cycles = 5;

    public void setCentralController(HelloController centralController) {
        this.centralController = centralController;
    }

    public void initialize() {
        UserSettings settings = User.getInstance().getSettings();
        focusMinutes = settings.getFocusMinutes();
        breakMinutes = settings.getShortBreakMinutes();
        lBreakMinutes = settings.getLongBreakMinutes();
        cycles = settings.getCyclesBeforeLongBreak();
        updateLabels();
    }

    private void updateLabels() {
        if (labelFocusValue != null) labelFocusValue.setText(String.valueOf(focusMinutes));
        if (labelBreakValue != null) labelBreakValue.setText(String.valueOf(breakMinutes));
        if (labelLBreakValue != null) labelLBreakValue.setText(String.valueOf(lBreakMinutes));
        if (labelCicloValue != null) labelCicloValue.setText(String.valueOf(cycles));
    }

    @FXML
    public void onDecrementFocus() {
        if (focusMinutes > 1) {
            focusMinutes--;
            labelFocusValue.setText(String.valueOf(focusMinutes));
        }
    }

    @FXML
    public void onIncrementFocus() {
        if (focusMinutes < 99) {
            focusMinutes++;
            labelFocusValue.setText(String.valueOf(focusMinutes));
        }
    }

    @FXML
    public void onDecrementBreak() {
        if (breakMinutes > 1) {
            breakMinutes--;
            labelBreakValue.setText(String.valueOf(breakMinutes));
        }
    }

    @FXML
    public void onIncrementBreak() {
        if (breakMinutes < 15) {
            breakMinutes++;
            labelBreakValue.setText(String.valueOf(breakMinutes));
        }
    }

    @FXML
    public void onDecrementLBreak() {
        if (lBreakMinutes > 1) {
            lBreakMinutes--;
            labelLBreakValue.setText(String.valueOf(lBreakMinutes));
        }
    }

    @FXML
    public void onIncrementLBreak() {
        if (lBreakMinutes < 30) {
            lBreakMinutes++;
            labelLBreakValue.setText(String.valueOf(lBreakMinutes));
        }
    }

    @FXML
    public void onDecrementCiclo() {
        if (cycles > 1) {
            cycles--;
            labelCicloValue.setText(String.valueOf(cycles));
        }
    }

    @FXML
    public void onIncrementCiclo() {
        if (cycles < 9) {
            cycles++;
            labelCicloValue.setText(String.valueOf(cycles));
        }
    }

    @FXML
    public void onSavedChanges() {
        try {
             // Persistir no UserSettings
            UserSettings settings = User.getInstance().getSettings();
            settings.setFocusMinutes(focusMinutes);
            settings.setShortBreakMinutes(breakMinutes);
            settings.setLongBreakMinutes(lBreakMinutes);
            settings.setCyclesBeforeLongBreak(cycles);

            // Salvar em disco
            SaveManager.saveData();

            // Aplicar no timer ativo
            centralController.updateTimer(
                    focusMinutes * 60,
                    breakMinutes * 60,
                    cycles,
                    lBreakMinutes * 60
            );
            centralController.openTimer();

        } catch (Exception e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }
}
