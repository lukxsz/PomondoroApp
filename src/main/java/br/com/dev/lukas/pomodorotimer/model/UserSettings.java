package br.com.dev.lukas.pomodorotimer.model;

public class UserSettings {
    private int focusMinutes;
    private int shortBreakMinutes;
    private int longBreakMinutes;
    private int cyclesBeforeLongBreak;

    public UserSettings(){
        this.focusMinutes = 25;
        this.shortBreakMinutes = 5;
        this.longBreakMinutes = 15;
        this.cyclesBeforeLongBreak = 5;
    }

    public int getFocusMinutes() {
        return focusMinutes;
    }
    public void setFocusMinutes(int focusMinutes) {
        this.focusMinutes = focusMinutes;
    }

    public int getShortBreakMinutes() {
        return shortBreakMinutes;
    }
    public void setShortBreakMinutes(int shortBreakMinutes) {
        this.shortBreakMinutes = shortBreakMinutes;
    }

    public int getLongBreakMinutes() {
        return longBreakMinutes;
    }
    public void setLongBreakMinutes(int longBreakMinutes) {
        this.longBreakMinutes = longBreakMinutes;
    }

    public int getCyclesBeforeLongBreak() {
        return cyclesBeforeLongBreak;
    }
    public void setCyclesBeforeLongBreak(int cyclesBeforeLongBreak) {
        this.cyclesBeforeLongBreak = cyclesBeforeLongBreak;
    }
}
