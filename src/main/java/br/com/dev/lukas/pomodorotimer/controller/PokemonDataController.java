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
        labelPokemonName.setText(pokemonDetails.name);

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
        String corTipo1 = obterCorDoTipo(pokemonDetails.type1);
        labelType1.setStyle("-fx-background-color: " + corTipo1 + "; -fx-text-fill: #ffffff;");
       // labelType1.setStyle("-fx-border-color: #00ff00; -fx-border-width: 3px; -fx-text-fill: black; -fx-font-size:
        // 24px; -fx-background-color: white;");
        labelType1.setMinSize(50,25);


        if (pokemonDetails.type2 != null && !pokemonDetails.type2.isEmpty()) {
            labelType2.setText(pokemonDetails.type2.toUpperCase());
            labelType2.setVisible(true);
            labelType2.setManaged(true);
            String corTipo2 = obterCorDoTipo(pokemonDetails.type2);
            labelType2.setStyle("-fx-background-color: " + corTipo2 + "; -fx-text-fill: #ffffff;");
            labelType2.setMinSize(50,25);

        }else{
            labelType2.setVisible(false);
            labelType2.setManaged(false);
        }

    }

    private String obterCorDoTipo(String tipo) {
        if (tipo == null) return "#777777"; // Cor padrão caso seja nulo

        switch (tipo.toLowerCase()) {
            case "fire": return "#E3350D";     // Vermelho
            case "water": return "#318EFF";    // Azul
            case "grass": return "#55C844";    // Verde
            case "electric": return "#F7D02C"; // Amarelo
            case "bug": return "#A6B91A";      // Verde-inseto
            case "normal": return "#A8A77A";   // Cinza claro
            case "poison": return "#A33EA1";   // Roxo
            case "ground": return "#E2BF65";   // Marrom/Terra
            case "flying": return "#A98FF3";   // Azul claro
            case "psychic": return "#F95587";  // Rosa
            case "rock": return "#B6A136";     // Pedra
            case "ice": return "#96D9D6";      // Gelo
            case "ghost": return "#705898";     // Fantasma
            case "dragon": return "#6F35FC";    // Dragão
            case "dark": return "#705746";      // Sombrio
            case "steel": return "#B7B7CE";     // Aço
            case "fairy": return "#D685AD";     // Fada
            default: return "#777777";          // Cor padrão
        }
    }

}
