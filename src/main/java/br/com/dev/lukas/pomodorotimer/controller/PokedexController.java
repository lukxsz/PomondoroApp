package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.PokemonDetails;
import br.com.dev.lukas.pomodorotimer.model.User;
import br.com.dev.lukas.pomodorotimer.utils.PokeApiService;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Cursor;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PokedexController {
    @FXML private StackPane principalScreen;
    @FXML private Pagination pokedexPagination;
    private final int ITEM_PER_PAGE = 18;
    private final int POKEMON_TOTAL = 151;
    private HelloController centralController;
    private final PokeApiService apiService = new PokeApiService();


    public void setCentralController(HelloController centralController) {
        this.centralController = centralController;
    }

    public void initialize(){

        int pagestotal = (int) Math.ceil((double) POKEMON_TOTAL / ITEM_PER_PAGE);
        pokedexPagination.setPageCount(pagestotal); //calcula o total de paginas. quantidade de pokemons/itens por
        // pagina

        pokedexPagination.setPageFactory(paginationIndice -> createPage(paginationIndice));
    }

    private Node createPage(int paginationIndex){
        TilePane grid = new TilePane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        grid.prefTileWidthProperty().bind(principalScreen.widthProperty().divide(5).subtract(47));

        //grid.setStyle("-fx-border-color: lime; -fx-border-width: 3px;");
        buscarPokemonsNaApi(paginationIndex, grid);

        return grid;
    }

    private void buscarPokemonsNaApi(int paginationIndex, TilePane grid) {
        int offset = paginationIndex * ITEM_PER_PAGE;

        // O Controller pede os dados para o Service e diz o que fazer quando eles chegarem
        apiService.buscarPagina(offset, ITEM_PER_PAGE, (resultadosDaApi) -> {

            // Quando a API responder, o JavaFX assume daqui!
            Platform.runLater(() -> {
                for (int i = 0; i < resultadosDaApi.size(); i++) {

                    JsonObject pokemon = resultadosDaApi.get(i).getAsJsonObject();
                    String nomeApi = pokemon.get("name").getAsString();
                    String nomeCapitalizado = nomeApi.substring(0, 1).toUpperCase() + nomeApi.substring(1);

                    String url = pokemon.get("url").getAsString();
                    String[] partesDaUrl = url.split("/");
                    int id = Integer.parseInt(partesDaUrl[partesDaUrl.length - 1]);

                    // Usa o metodo que desenha o visual
                    VBox card = createCard(id, nomeCapitalizado);
                    grid.getChildren().add(card);
                }
            });
        });
    }

    private VBox createCard(int id, String nome) {
        VBox card = new VBox();
        //card.setStyle("-fx-border-color: rgba(0, 0, 255, 0.3);");
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card-pokemon");


        // Baixa a imagem diretamente pelo ID usando o truque mágico
        String urlImagem = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
        Image imagem = new Image(urlImagem, 200, 200, true, true, true);

        ImageView moldura = new ImageView(imagem);
        moldura.setPreserveRatio(true);
        moldura.setSmooth(true);
        moldura.fitWidthProperty().bind(pokedexPagination.widthProperty().divide(5).multiply(0.5));

        boolean isCaptured = User.getInstance().verifyPokemon(id);
        String exhibitionName;

        if (!isCaptured) {
            exhibitionName = nome;

            card.getStyleClass().add("card-unlocked");
        }else {
            exhibitionName = "???";

            ColorAdjust filtroSilhueta = new ColorAdjust();
            filtroSilhueta.setBrightness(-1.0);
            moldura.setEffect(filtroSilhueta);

            card.getStyleClass().add("card-locked");
        }

        String numeroFormatado = String.format("#%03d", id);
        Label lblNumero = new Label(numeroFormatado);
        Label lblNome = new Label(exhibitionName);

        card.getChildren().addAll(moldura, lblNumero, lblNome);


        card.setCursor(Cursor.HAND); //maozinha
        card.setOnMouseClicked(event -> {
            if (!isCaptured) {
                System.out.println("Clicou no card do pokemon: "+ id);
                openPokemonModal(id);
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pokemon Desconhecido");
                alert.setHeaderText(null);
                alert.setContentText("Você precisa concluir mais ciclos de Pomodoro para descobrir informações sobre este Pokemon.");

                alert.getDialogPane().getStylesheets().add(getClass().getResource("/br/com/dev/lukas/pomodorotimer/css/style.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("pokemon-alert");

                alert.showAndWait();
            }
        });

        return card;

    }

    private void openPokemonModal(int idPokemon){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/dev/lukas/pomodorotimer/pokemon-data-view.fxml"));
            Parent root = loader.load();

            PokeApiService service = new  PokeApiService();
            PokemonDetails details = service.getPokemonDetails(idPokemon);

            PokemonDataController controller = loader.getController();

            if (details != null){
                controller.dataOnScreen(idPokemon, details);
            }else{
                System.out.println("Erro ao buscar pokemon do Pokemon");
                return;
            }

            Stage modalStage = new Stage();
            modalStage.setTitle("Dados do Pokemon");
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);
            modalStage.initModality(Modality.APPLICATION_MODAL);

            modalStage.showAndWait();

        }catch (Exception e){
            System.out.println("Erro ao tentar abrir o FXML "+e.getMessage());
            e.printStackTrace();
        }
    }

}
