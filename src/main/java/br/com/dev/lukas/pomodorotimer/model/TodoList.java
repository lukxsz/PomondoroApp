package br.com.dev.lukas.pomodorotimer.model;

import java.util.ArrayList;

public class TodoList {
    ArrayList<Task> taskList = new ArrayList<Task>();

    public  void add(Task task){
        taskList.add(task);
    }

    public  void remove(Task task){
        taskList.remove(task);
    }

    public  void clear(){
        taskList.clear();
    }

    public int size(){
        return taskList.size();
    }

}
