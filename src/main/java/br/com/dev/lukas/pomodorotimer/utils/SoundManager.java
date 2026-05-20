package br.com.dev.lukas.pomodorotimer.utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {
    private MediaPlayer mediaPlayer;

    public static void play(String path, float volume) {
        try {
            // Busca o arquivo dentro da pasta resources
            URL soundUrl = SoundManager.class.getResource(path);

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Obtém o controle de ganho (volume)
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // O volume aqui é em decibéis.
                // 0.0f é o volume original. Valores negativos diminuem o som.
                gainControl.setValue(volume);
            }

            clip.start();


        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void  playPokemonSound(int idPokemon) {
        try {
            String soundUrl = "https://pokemoncries.com/cries/" + idPokemon + ".mp3";
            Media media = new Media(soundUrl);
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(()-> {
                mediaPlayer.setVolume(0.05);
                mediaPlayer.play();
                System.out.println("O som rodou");
            });

            mediaPlayer.setOnError(() -> {
                System.out.println("Erro no MediaPlayer" + mediaPlayer.getError().getMessage());
            });


        }catch (Exception e){
            System.out.println("Erro ao tocar o alarme" + e.getMessage());
        }
    }


}
