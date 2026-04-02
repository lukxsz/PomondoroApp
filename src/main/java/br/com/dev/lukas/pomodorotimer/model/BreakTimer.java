package br.com.dev.lukas.pomodorotimer.model;

public class BreakTimer implements Timer {
    int remainingSeconds;
    int firstTime;
    boolean status;

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
        remainingSeconds = timeInput*60;
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

}
