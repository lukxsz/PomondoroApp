package br.com.dev.lukas.pomodorotimer.model;

public class BreakTimer implements Timer {
    int remainingSeconds;
    int firstTime;
    boolean status;
    private int cycleCounter = 0;
    private int whenLongBreak = 4;

    public BreakTimer(int timeInput) {
        remainingSeconds = timeInput*60;
        firstTime = remainingSeconds;
    }

    @Override
    public void startTimer() {
        if(remainingSeconds > 0){
            remainingSeconds--;
        }
    }

    public void reStartTimer() {
        remainingSeconds = firstTime;
    }

    @Override
    public String formatedTime() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        String timeString = String.format("%d:%02d", minutes, seconds);
        return timeString;
    }

    @Override
    public void setTimer(int timeInput) {
        remainingSeconds = timeInput;
        firstTime = remainingSeconds;
    }

    @Override
    public boolean isFinished() {
        return remainingSeconds <= 0;
    }

    @Override
    public boolean isRunning() {
        return status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void increaseCycleCounter(){
            this.cycleCounter++;
        System.out.println("Pause Current Cycle Counter: " + cycleCounter);
    }

    public void resetCycleCounter(){
        this.cycleCounter = 0;
    }

    public int getCycleCounter() {
        return cycleCounter;
    }

    public int getWhenLongBreak(){
        return whenLongBreak;
    }

    public void setWhenLongBreak(int whenLongBreak) {
        this.whenLongBreak = whenLongBreak;

    }
}
