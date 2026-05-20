package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.PokemonDetails;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

public class PokemonDataController {
    @FXML private Label labelPokemonName;
    @FXML private ImageView imagePokemon;
    @FXML private Label labelType1;
    @FXML private Label labelType2;
    @FXML private Label labelPokedexEntry;

    public void dataOnScreen(int idPokemon, PokemonDetails pokemonDetails) {

        String urlGif = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation" +
                "-v/black-white/animated/" + idPokemon + ".gif";

        Image gif = new Image(urlGif, 150, 150, true, true, true);
        imagePokemon.setImage(gif);

        labelPokedexEntry.setText(pokemonDetails.pokedexEntry);
        labelPokedexEntry.setWrapText(true);
        labelPokedexEntry.setTextAlignment(TextAlignment.CENTER);
        labelPokedexEntry.setMaxWidth(250);

        labelType1.setText(pokemonDetails.type1.toUpperCase());
        labelType1.setVisible(true);
        labelType1.setManaged(true);
       // labelType1.setStyle("-fx-border-color: #00ff00; -fx-border-width: 3px; -fx-text-fill: black; -fx-font-size:
        // 24px; -fx-background-color: white;");
        labelType1.setMinSize(50,25);


        if (pokemonDetails.type2 != null && !pokemonDetails.type2.isEmpty()) {
            labelType2.setText(pokemonDetails.type2.toUpperCase());
            labelType2.setVisible(true);
            labelType2.setManaged(true);
            //labelType2.setStyle("-fx-border-color: #00ff00; -fx-border-width: 3px; -fx-text-fill: black; " +
                   // "-fx-font-size: 24px; -fx-background-color: white;");
            labelType2.setMinSize(50,25);

        }else{
            labelType2.setVisible(false);
            labelType2.setManaged(false);
        }

        System.out.println("CHEGOU NA TELA:");
        System.out.println("Descricao: " + pokemonDetails.pokedexEntry);
        System.out.println("Tipo 1: " + pokemonDetails.type1.toUpperCase());
        System.out.println("Tipo 2: " + pokemonDetails.type2.toUpperCase());
    }

}
