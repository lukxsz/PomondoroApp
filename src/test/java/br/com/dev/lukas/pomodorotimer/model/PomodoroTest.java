package br.com.dev.lukas.pomodorotimer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PomodoroTest {
    private Pomodoro pomodoro;
    @BeforeEach
    void setUp(){
        pomodoro = new Pomodoro(25);
    }
    @Test
    @DisplayName("Deve diminuir o tempo segundo por segundo")
    void startTimer() {
        String tempoCerto = "24:59";
        pomodoro.startTimer();
        String tempoAtual = pomodoro.formatedTime();
        assertEquals(tempoCerto, tempoAtual);
    }

    @Test
    @DisplayName("Deve reiniciar o tempo")
    void reStartTimer() {
        String primeiroTempo = "25:00";
        pomodoro.startTimer();
        pomodoro.reStartTimer();
        String tempoAtual = pomodoro.formatedTime();
        assertEquals(primeiroTempo, tempoAtual);

    }

    @Test
    @DisplayName("Deve mostrar o tempo formatado no padrão MM:SS")
    void formatedTime() {
        String tempoCorreto = "25:00";
        String tempoFormatado = pomodoro.formatedTime();
        assertEquals(tempoCorreto, tempoFormatado);

    }

    @Test
    @DisplayName("Deve retornar true e reiniciar contador no 4° ciclo")
    void cycleCheck() {
        for(int i=0; i<3; i++){
            pomodoro.increaseCycleCounter();
        }
        boolean pausaLonga = pomodoro.cycleCheck();
        assertFalse(pausaLonga, "Deve retornar false com menos de 4 ciclos");

        pomodoro.increaseCycleCounter();
        pausaLonga = pomodoro.cycleCheck();
        assertTrue(pausaLonga, "Deve retornar true com 4 ciclos");
        assertFalse(pomodoro.cycleCheck(), "Deve ter zerado o ciclo após disparar a pausa longa");
    }

}