package br.com.dev.lukas.pomodorotimer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoListTest {
    private TodoList todoList;
    @BeforeEach
    void setUp(){
        todoList = new TodoList();
    }

    @Test
    @DisplayName("Deve adicionar uma task a lista de tarefas")
    void add() {
        todoList.add(new Task("task1"));
        int tamanhoLista = todoList.size();

        assertEquals(1, tamanhoLista);
    }

    @Test
    @DisplayName("Deve retornar a lista com menos um elementos")
    void remove() {
        Task tarefa = new Task("Aprender Testes");
        todoList.add(tarefa);
        todoList.remove(tarefa);
        int tamanhoLista = todoList.size();

        assertEquals(0,tamanhoLista);
    }

    @Test
    @DisplayName("Deve remover todos os itens da lista")
    void clear() {
        todoList.add(new Task("task"));
        todoList.add(new Task("task"));
        todoList.clear();
        int tamanhoLista = todoList.size();

        assertEquals(0 , tamanhoLista);
    }

    @Test
    @DisplayName("Deve exibir as tarefas que estão na lista")
    void printList() {
    }
}