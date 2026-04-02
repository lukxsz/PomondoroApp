package br.com.dev.lukas.pomodorotimer.model;

public interface Timer {

    void startTimer();
    void setTimer(int timeInput);
    String formatedTime();
    boolean isFinished();
    boolean isRunning();



}
