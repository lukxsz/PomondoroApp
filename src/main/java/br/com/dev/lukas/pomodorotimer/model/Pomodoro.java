package br.com.dev.lukas.pomodorotimer.model;

public class Pomodoro implements Timer {
    int remainingSeconds;
    int firstTime;
    boolean status;
    int cycleCounter;

    public Pomodoro(int timeInput) {
        remainingSeconds = timeInput*60;
        firstTime = remainingSeconds;
    }

    public void startTimer(){
        if(remainingSeconds > 0){
            remainingSeconds--;
        }
    }
    public void reStartTimer() {
        remainingSeconds = firstTime;
    }
    public String formatedTime(){
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);
        return timeString;
    }
    public boolean isFinished(){
        return remainingSeconds <= 0;
    }
    public void setTimer(int timeInput) {
        remainingSeconds = timeInput;
        firstTime = remainingSeconds;
    }
    public boolean isRunning(){
        return status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean cycleCheck(){
        if (cycleCounter == 4){
            cycleCounter = 0;
            return true;
        }
        return false;
    }

    public void resetCycleCounter(){
        cycleCounter = 0;
    }

    public void increaseCycleCounter(){
        cycleCounter++;
        System.out.println("Current Cycle Counter: " + cycleCounter);
    }

}
