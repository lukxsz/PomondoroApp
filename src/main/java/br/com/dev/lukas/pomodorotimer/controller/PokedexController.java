package br.com.dev.lukas.pomodorotimer.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class PokedexController {

    @FXML private Pagination pokedexPagination;
    private final int ITEM_PER_PAGE = 15;
    private final int POKEMON_TOTAL = 151;
    private HelloController centralController;

    public void setCentralController(HelloController centralController) {
        this.centralController = centralController;
    }

    public void initialize(){
        int totalpages = (int) Math.ceil((double) POKEMON_TOTAL / ITEM_PER_PAGE);
        pokedexPagination.setPageCount(totalpages); //calcula o total de paginas total de pokemons / itens por pagina

        pokedexPagination.setPageFactory(paginationIndice -> createPage(paginationIndice));
    }

    private Node createPage(int paginationIndice){
        TilePane grid = new TilePane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));

        int begin = (paginationIndice * ITEM_PER_PAGE) +1;
        int end = Math.min(begin + ITEM_PER_PAGE -1, POKEMON_TOTAL);

        for(int i = begin; i <= end; i++){

            VBox card = new VBox(5);
            card.setAlignment(Pos.CENTER);
            card.setPrefSize(120,150);

            card.getStyleClass().add("card-pokemon");

            String formatedNumber = String.format("#%03d", i);
            Label lblNumber = new Label(formatedNumber);
            Label lblName = new Label("Pokemon " +i);

            card.getChildren().addAll(lblNumber,lblName);

            grid.getChildren().add(card);
        }
        return grid;
    }
    public void pokedexPagination(){

    }
}
