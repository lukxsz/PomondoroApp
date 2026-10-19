package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.Pokemon;
import br.com.dev.lukas.pomodorotimer.model.PokemonDetails;
import br.com.dev.lukas.pomodorotimer.model.User;
import br.com.dev.lukas.pomodorotimer.utils.PokeApiService;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Cursor;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PokedexController {
    @FXML private TextField pokedexTextField;
    @FXML private StackPane pokedexStackPane;
    @FXML private VBox pokedexVbox;
    @FXML private Pagination pokedexPagination;
    @FXML private HBox HboxPrincipal;
    @FXML private HBox HboxPesquisa;
    @FXML private VBox VboxEncontrados;
    @FXML private Label labelEncontrados;
    @FXML private VBox VboxDisponiveis;
    @FXML private Label labelTodos;
    @FXML private Label labelE;
    @FXML private Label labelD;
    @FXML private Label labelTituloPokedex;
    @FXML private Label labelDescPokedex;
    @FXML private Label labelPesquisaPokedex;
    // A barra do Pagination ocupa 2 linhas: botões (~28px) + indicador "x/y" (~24px) + folgas
    private static final double CONTROL_BAR_HEIGHT = 72.0;
    private static final double CARD_MIN_WIDTH = 130.0;
    private static final double CARD_MAX_WIDTH = 180.0;
    private static final double CARD_MIN_HEIGHT = 170.0;
    private static final double GAP = 16.0;
    private int colunasAtuais = 5;
    private int linhasAtuais = 3;
    private int itensPorPagina = 15;
    private double larguraCardAtual = 0;
    private double alturaCardAtual = 0;
    private final int POKEMON_TOTAL = 151;
    private HelloController centralController;
    private ArrayList<Pokemon> todosOsPokemons;
    private ArrayList<Pokemon> nomesPesquisado;
    private ArrayList<Pokemon> numeroPesquisado;
    private final PokeApiService apiService = new PokeApiService();


    public void setCentralController(HelloController centralController) {
        this.centralController = centralController;
    }

    public void initialize(){
        todosOsPokemons = new ArrayList<Pokemon>();
        nomesPesquisado = new ArrayList<Pokemon>();
        numeroPesquisado = new ArrayList<Pokemon>();

        BorderPane.setMargin(pokedexVbox, new Insets(24,48,24,48));
        quantidadePokemon();
        buscarPokemonsNaApi();
        pokedexTextField.textProperty().addListener(((
                observable, oldValue, newValue
        ) -> {
            filterText(newValue);
        }));

        // Mede o espaço pelo StackPane pai (estável), nunca pelo Pagination,
        // cuja altura muda quando o conteúdo muda — isso causava loop de re-layout.
        // O recálculo é ADIADO para o próximo pulso: ler os heights dos cabeçalhos
        // no meio do layout retorna valores intermediários que oscilam e recriam
        // o grid sem parar (loop observado: chrome 242→255→260→242...).
        pokedexStackPane.widthProperty().addListener((obs, oldVal, newVal) -> agendarRecalculo());
        pokedexStackPane.heightProperty().addListener((obs, oldVal, newVal) -> agendarRecalculo());

        atualizarPaginacao();
    }

    // Coalescente: vários listeners no mesmo pulso viram um único recálculo,
    // executado DEPOIS do layout assentar (leituras estáveis)
    private boolean recalculoAgendado = false;

    private void agendarRecalculo() {
        if (recalculoAgendado) return;
        recalculoAgendado = true;
        Platform.runLater(() -> {
            recalculoAgendado = false;
            double w = pokedexStackPane.getWidth();
            double h = pokedexStackPane.getHeight();
            if (w > 0 && h > 0) {
                recalcularLayout(w, h);
            }
        });
    }

    private void recalcularLayout(double larguraDisponivel, double alturaDisponivel) {
        double larguraUtil = Math.max(0, larguraDisponivel - 32);

        // A altura do StackPane não é confiável: o grid com maxSize MAX infla o
        // Pagination, que infla o StackPane, num loop via pref size (stackH foi
        // observado crescendo até 18000px). Limita pela altura da Scene (janela),
        // descontando o chrome REAL medido dos cabeçalhos — valor fixo falha
        // porque a descrição quebra linha conforme a largura da janela.
        double chrome = 48  // padding do pokedexVbox (24 topo + 24 base)
                      + 48  // margem do BorderPane aplicada no initialize (24 + 24)
                      + 48; // margens internas: desc(16+16) + pesquisa(8) + hbox(8)
        // Usar prefHeight, NÃO getHeight: em janela apertada o VBox espremer os
        // labels abaixo do pref (corta linhas do desc) e o getHeight retorna o
        // valor espremido — isso realimenta o cálculo e o grid oscila sem parar
        // (loop observado: desc 35→30→17 com largura e scene constantes).
        // prefHeight(largura) é determinístico e quebra o ciclo.
        double larguraDesc = labelDescPokedex.getWidth();
        chrome += labelTituloPokedex.prefHeight(-1)
                + labelDescPokedex.prefHeight(larguraDesc > 0 ? larguraDesc : Region.USE_COMPUTED_SIZE)
                + labelPesquisaPokedex.prefHeight(-1)
                + HboxPrincipal.prefHeight(-1);

        // NÃO usar min(stackH, scene-chrome): quando stackH < scene-chrome ele vira
        // a referência, mas o stackH é influenciado pelo pref size do grid recém-criado
        // — o cálculo se realimenta e oscila (observado loop 521↔467 recriando o
        // grid sem parar). Derivar SEMPRE da janela; stackH só como fallback sem scene.
        double alturaRef = alturaDisponivel;
        if (pokedexStackPane.getScene() != null) {
            alturaRef = pokedexStackPane.getScene().getHeight() - chrome;
        }
        // Reserva a barra de controle do Pagination no rodapé
        double alturaUtil = Math.max(0, alturaRef - CONTROL_BAR_HEIGHT - 16);

        int novasColunas = Math.max(2, (int) (larguraUtil / (CARD_MIN_WIDTH + GAP)));
        int novasLinhas = Math.max(1, (int) (alturaUtil / (CARD_MIN_HEIGHT + GAP)));

        double novaLarguraCard = calcularLarguraCard(larguraDisponivel, novasColunas);
        double novaAlturaCard = calcularAlturaCard(alturaRef, novasLinhas);

        // Recria também quando o TAMANHO do card muda, mesmo sem mudar colunas/linhas —
        // antes, resize sem mudança de contagem deixava as páginas com tamanho antigo
        boolean mudouGrade = novasColunas != colunasAtuais || novasLinhas != linhasAtuais;
        boolean mudouTamanho = Math.abs(novaLarguraCard - larguraCardAtual) > 4
                            || Math.abs(novaAlturaCard - alturaCardAtual) > 4;

        if (mudouGrade || mudouTamanho) {
            colunasAtuais = novasColunas;
            linhasAtuais = novasLinhas;
            larguraCardAtual = novaLarguraCard;
            alturaCardAtual = novaAlturaCard;
            itensPorPagina = colunasAtuais * linhasAtuais;
            atualizarPaginacao();
        }
    }

    private double calcularLarguraCard(double larguraDisponivel, int colunas) {
        double largura = (larguraDisponivel - 16 - (colunas - 1) * GAP) / colunas;
        return Math.min(CARD_MAX_WIDTH, Math.max(CARD_MIN_WIDTH - 20, largura));
    }

    private double calcularAlturaCard(double alturaDisponivel, int linhas) {
        double altura = (alturaDisponivel - CONTROL_BAR_HEIGHT - 16 - (linhas - 1) * GAP) / linhas;
        return Math.max(120, altura);
    }

    private void atualizarPaginacao() {
        int totalItens = (nomesPesquisado != null) ? nomesPesquisado.size() : 0;
        int pagesTotal = Math.max(1, (int) Math.ceil((double) totalItens / itensPorPagina));

        pokedexPagination.setPageCount(pagesTotal);
        int paginaAtual = Math.min(pokedexPagination.getCurrentPageIndex(), pagesTotal - 1);
        pokedexPagination.setCurrentPageIndex(Math.max(0, paginaAtual));
        // Nova instância de factory a cada atualização força o Pagination
        // a reconstruir a página atual com os tamanhos novos
        pokedexPagination.setPageFactory(pageIndex -> createPage(pageIndex));
    }

    private Node createPage(int paginationIndex){
        GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP);
        grid.setPadding(new Insets(8));
        grid.setAlignment(Pos.CENTER);
        // Permite o grid preencher toda a área da página (default é max = pref)
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.getStyleClass().add("gridPokedex");

        // Usa os tamanhos já calculados em recalcularLayout — garante que todas
        // as páginas tenham o mesmo tamanho, mesmo ao folhear rápido
        double larguraCard = larguraCardAtual > 0 ? larguraCardAtual
                : calcularLarguraCard(pokedexStackPane.getWidth() > 0 ? pokedexStackPane.getWidth() : 800, colunasAtuais);
        double alturaCard = alturaCardAtual > 0 ? alturaCardAtual
                : calcularAlturaCard(pokedexStackPane.getHeight() > 0 ? pokedexStackPane.getHeight() : 600, linhasAtuais);

        for (int c = 0; c < colunasAtuais; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / colunasAtuais);
            cc.setHalignment(HPos.CENTER);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int r = 0; r < linhasAtuais; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / linhasAtuais);
            rc.setValignment(VPos.CENTER);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }

        buscarPokemonsNaMemoria(paginationIndex, grid, larguraCard, alturaCard);

        return grid;
    }

    private void buscarPokemonsNaMemoria(int paginationIndex, GridPane grid, double larguraCard, double alturaCard){
        int inicio = paginationIndex * itensPorPagina;
        int fim = Math.min(inicio + itensPorPagina, nomesPesquisado.size());

        for(int i=inicio; i<fim; i++){
            Pokemon p = nomesPesquisado.get(i);
            VBox card = createCard(p.id, p.nome, larguraCard, alturaCard);
            int indiceNaPagina = i - inicio;
            int coluna = indiceNaPagina % colunasAtuais;
            int linha = indiceNaPagina / colunasAtuais;
            grid.add(card, coluna, linha);
        }
    }

    private void buscarPokemonsNaApi() {

        // O Controller pede os dados para o Service e diz o que fazer quando eles chegarem
        apiService.buscarPagina(0,
                POKEMON_TOTAL,
                (resultadosDaApi) -> {
                    todosOsPokemons.clear();
                    for(int i = 0; i<resultadosDaApi.size(); i++){
                        JsonObject pokemon = resultadosDaApi.get(i).getAsJsonObject();
                        String nomeApi = pokemon.get("name").getAsString();
                        String nomeCapitalizado = nomeApi.substring(0,1).toUpperCase() + nomeApi.substring(1);

                        String url = pokemon.get("url").getAsString();
                        String[] partesDaUrl = url.split("/");
                        int id = Integer.parseInt(partesDaUrl[partesDaUrl.length - 1]);

                        Pokemon p = new Pokemon();
                        p.id = id;
                        p.nome = nomeCapitalizado;
                        todosOsPokemons.add(p);
                    }
                    nomesPesquisado.clear();
                    nomesPesquisado.addAll(todosOsPokemons);

                    pokedexStackPane.getChildren().setAll(pokedexPagination);
                    atualizarPaginacao();
                },
                (mensagemErro) -> {
                    exibirFeedbackSemInternet(mensagemErro);
                }

        );
    }

    private VBox createCard(int id, String nome, double larguraCard, double alturaCard) {
        VBox card = new VBox();
//        card.setStyle("-fx-border-color: rgba(0, 0, 255, 0.3);");
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("cardPokemon");
        card.setPrefSize(larguraCard, alturaCard);
        card.setMaxSize(larguraCard, alturaCard);
        card.setMinWidth(CARD_MIN_WIDTH - 20);


        // Baixa a imagem diretamente pelo ID usando o truque mágico
        String urlImagem = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
        Image imagem = new Image(urlImagem, 200, 200, true, true, true);

        ImageView moldura = new ImageView(imagem);
        moldura.setPreserveRatio(true);
        moldura.setSmooth(true);
        // Tamanhos fixos calculados — sem bindings circulares
        moldura.setFitWidth(larguraCard * 0.65);
        moldura.setFitHeight(Math.max(60, alturaCard - 48));

        boolean isCaptured = User.getInstance().verifyPokemon(id);
        String exhibitionName;

        if (!isCaptured) {
            exhibitionName = nome;

            card.getStyleClass().add("card-unlocked");
        }else {
            exhibitionName = "???";

            ColorAdjust filtroSilhueta = new ColorAdjust();
            filtroSilhueta.setBrightness(-0.9);
            moldura.setEffect(filtroSilhueta);

            card.getStyleClass().add("card-locked");
        }

        String numeroFormatado = String.format("#%03d", id);
        Label lblNumero = new Label(numeroFormatado);
        lblNumero.setAlignment(Pos.CENTER);
        Label lblNome = new Label(exhibitionName);
        lblNome.setAlignment(Pos.CENTER);

        lblNome.getStyleClass().add("labelNome");
        lblNumero.getStyleClass().add("labelNumero");

        card.getChildren().addAll(moldura, lblNumero, lblNome);


        card.setCursor(Cursor.HAND);
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
            PokemonDataController controller = loader.getController();

            PokeApiService service = new  PokeApiService();
            service.getPokemonDetails(idPokemon, details -> {

                if (details != null){
                    controller.dataOnScreen(idPokemon, details);

                    Stage modalStage = new Stage();
                    modalStage.setTitle("Dados do Pokemon");
                    modalStage.setScene(new Scene(root));
                    modalStage.setResizable(false);
                    modalStage.initModality(Modality.APPLICATION_MODAL);
                    modalStage.showAndWait();
                }else{
                    System.out.println("Erro ao buscar dados do Pokemon");
                }
            });

        }catch (Exception e){
            System.out.println("Erro ao tentar abrir o FXML "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void filterText(String newValue){
        nomesPesquisado.clear();

        if (newValue == null || newValue.trim().isEmpty()){
            nomesPesquisado.addAll(todosOsPokemons);
        } else {
            String pokemonBuscado = newValue.toLowerCase().trim();
            for(Pokemon p : todosOsPokemons){
                if(p.nome.toLowerCase().contains(pokemonBuscado) ||
                String.valueOf(p.id).contains(pokemonBuscado)){
                    nomesPesquisado.add(p);
                }
            }
        }
        atualizarPaginacao();
    }

    private void quantidadePokemon(){
        String pokemonsPegos = String.valueOf(User.getInstance().verifyListSize());
        String totalPokemons = String.valueOf(POKEMON_TOTAL);

        labelEncontrados.setText(pokemonsPegos);
        labelTodos.setText(totalPokemons);
    }

    private void exibirFeedbackSemInternet(String mensagem){
        VBox containerErro = new VBox(14);
        containerErro.setAlignment(Pos.CENTER);
        containerErro.setPadding(new Insets(24));

        Image wifiImg = new Image(Objects.
                requireNonNull(getClass().getResourceAsStream(
                        "/br/com/dev/lukas/pomodorotimer/images/wifi.png")));
        ImageView wifi = new ImageView(wifiImg);

        Label descricao = new Label(mensagem != null ? mensagem :
                "Não foi possível carregar a Pokédex. Verifique sua conexão.");
        descricao.getStyleClass().add("Descricao");
        containerErro.getChildren().addAll(wifi, descricao);
        pokedexStackPane.getChildren().setAll(containerErro);

    }
}
