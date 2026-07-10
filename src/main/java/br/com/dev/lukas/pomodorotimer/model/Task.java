package br.com.dev.lukas.pomodorotimer.model;

public class Task {
    String taskName;
    boolean done;

    public Task(String taskName) {

        if (taskName == null) {
            this.taskName = "Tarefa sem nome";
        } else {
            this.taskName = taskName;
        }
        this.done = false;
    }

    public String toString() {
        return (done ? "[OK] " : "[  ] ") + taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDone(boolean b) {
        this.done = b;
    }

    public boolean isDone() {
        return done;
    }
}


