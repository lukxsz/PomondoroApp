package br.com.dev.lukas.pomodorotimer.controller;

import br.com.dev.lukas.pomodorotimer.model.*;
import br.com.dev.lukas.pomodorotimer.utils.SaveManager;
import br.com.dev.lukas.pomodorotimer.utils.SoundManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;


public class HelloController {

    public Label labelTitulo;
    public TitledPane onGoingPane;
    public TitledPane toDoPane;
    public TitledPane finishedPane;
    @FXML private HBox buttonsHbox;
    @FXML private Label labelTimer;
    @FXML public Button buttonTimer;
    @FXML private Timeline timeline;
    @FXML private Timeline breaktimeline;
    @FXML private Timeline longbreaktimeline;
    @FXML private ListView<Task> taskView;
    @FXML private TextField inputTask;
    @FXML private Pomodoro pomodoroTimer = new Pomodoro(25);
    @FXML private BreakTimer breakTimer =  new BreakTimer(5);
    @FXML private BreakTimer longbreakTimer =  new BreakTimer(15);
    //@FXML private ObservableList<Task> visibleTasks = FXCollections.observableArrayList();
    @FXML private BorderPane mainPane;
    @FXML private VBox timerVBox;
    @FXML private VBox rightPane;
    @FXML private VBox leftPane;
    @FXML private Button timerButton;
    @FXML private Button pokedexButton;
    @FXML private Button configurationButton;
    @FXML private Button skipStageButton;
    @FXML private ImageView imgMoita;
    @FXML private  ImageView cenarioView;
    @FXML private StackPane battleContainer;
    @FXML private VBox onGoingList;
    @FXML private VBox toDoList;
    @FXML private VBox finishedList;
    private Timeline animationBushTimiline;
    @FXML private VBox vBoxConfig;
    @FXML private HBox hBoxCiclo;
    @FXML private HBox hBoxPLonga;
    @FXML private HBox hBoxFoco;
    @FXML private HBox hBoxPausa;
    @FXML private VBox vBoxButtons;
    @FXML private HBox hBoxLogo;
    @FXML private Label textLabel;
    @FXML private VBox textVBox;
    @FXML private Label labelConfigFocoTitle;
    @FXML private Label labelConfigFocoDesc;
    @FXML private Label labelConfigPausaTitle;
    @FXML private Label labelConfigPausaDesc;
    @FXML private Label labelConfigPausaLTitle;
    @FXML private Label labelConfigPausaLDesc;
    @FXML private Label labelConfigCicloTitle;
    @FXML private Label labelConfigCicloDesc;
    @FXML private Label subLabelSpiner;

    // ── ETAPA 4: Controle de estado da tela ativa ──
    // Essa variável rastreia qual view está sendo exibida no centro.
    // Os métodos openTimer(), onConfiguration() e onPokedex() atualizam ela.
    // O metodo atualizarLayout() usa ela para decidir se mostra o rightPane.
    // Valores possíveis: "timer", "config", "pokedex"
    private String telaAtiva = "timer";

    // ── ETAPA 5: Breakpoints de responsividade ──
    // Altere esses valores para mudar em qual largura cada componente aparece/desaparece.
    // BREAKPOINT_WIDGET: abaixo disso, só o relógio aparece (sem botões, sem painéis)
    // BREAKPOINT_SIDEBAR: abaixo disso, a sidebar esquerda é oculta
    // BREAKPOINT_RIGHTPANE: abaixo disso, o painel direito (tarefas) é oculto
    private static final double BREAKPOINT_WIDGET = 380;
    private static final double BREAKPOINT_SIDEBAR = 500;
    private static final double BREAKPOINT_RIGHTPANE = 750;

    @FXML
    private void initialize() {
        UserSettings settings = User.getInstance().getSettings();

        pomodoroTimer = new Pomodoro(settings.getFocusMinutes());
        breakTimer = new BreakTimer(settings.getShortBreakMinutes());
        longbreakTimer = new BreakTimer(settings.getLongBreakMinutes());

        labelTimer.setText(pomodoroTimer.formatedTime());
        animationTallGrass();
        pomodoroTimer();

        // ── ETAPA 2: Font-size dinâmico ──
        // O font-size do app inteiro é proporcional à largura da janela.
        // Isso faz todas as medidas em "em" no CSS escalarem automaticamente.
        // Fórmula: largura ÷ 60. Ex: 1200px → 20px, 600px → 10px, 300px → 5px.
        // Para fontes maiores/menores, altere o divisor (menor = fonte maior).
        mainPane.styleProperty().bind(
                Bindings.concat(
                        "-fx-font-size: ",
                        mainPane.widthProperty().divide(60).asString("%.1f"),
                        "px;"
                )
        );

        // ── ETAPA 3: Listener único de responsividade ──
        // Sempre que a largura da janela mudar, chama atualizarLayout().
        // Esse é o ÚNICO lugar que escuta mudanças de tamanho.
        mainPane.widthProperty().addListener((obs, oldVal, newVal) -> atualizarLayout());

        cenarioView.fitWidthProperty().bind(battleContainer.prefWidthProperty().subtract(6));
        cenarioView.fitHeightProperty().bind(battleContainer.prefHeightProperty().subtract(6));

        imgMoita.fitWidthProperty().bind(battleContainer.prefWidthProperty().divide(3.0));

        labelTimer.styleProperty().bind(
                Bindings.concat(
                        "-fx-font-size: ",
                        battleContainer.prefWidthProperty().multiply(0.32).asString(),
                        "px;"
                )
        );
        timerVBox.widthProperty().addListener((obs,oldVal,newVal)-> redimensionarArea());
        timerVBox.heightProperty().addListener((obs,oldVal,newVal)-> redimensionarArea());

        battleContainer.maxWidthProperty().bind(battleContainer.prefWidthProperty());
        battleContainer.maxHeightProperty().bind(battleContainer.prefHeightProperty());

        battleContainer.setMinWidth(0);
        battleContainer.setMinHeight(0);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            pomodoroTimer.startTimer(); animationBushTimiline.play();
            labelTimer.setText(pomodoroTimer.formatedTime());
            if (pomodoroTimer.isFinished()){
                stopAllTimers();
                animationBushTimiline.stop();
                setPlayIcon();
                breakTime();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        breaktimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            breakTimer.startTimer();
            labelTimer.setText(breakTimer.formatedTime());
            if (breakTimer.isFinished()){
                stopAllTimers();
                setPlayIcon();
                pomodoroTimer();
            }
        }));
        breaktimeline.setCycleCount(Timeline.INDEFINITE);

        longbreaktimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            longbreakTimer.startTimer();
            labelTimer.setText(longbreakTimer.formatedTime());
            if (longbreakTimer.isFinished()){
                stopAllTimers();
                setPlayIcon();
                pomodoroTimer();
            }
        }));
        longbreaktimeline.setCycleCount(Timeline.INDEFINITE);

        // Expande o cabeçalho (BorderPane) para a largura total do título, fixando a seta na extrema direita
        configurarCabecalhoTitledPane(onGoingPane);
        configurarCabecalhoTitledPane(toDoPane);
        configurarCabecalhoTitledPane(finishedPane);

        atualizarBotoesSidebar();
    }

    private void configurarCabecalhoTitledPane(TitledPane pane) {
        if (pane.getGraphic() instanceof Region region) {
            region.prefWidthProperty().bind(pane.widthProperty().subtract(35));
        }
    }

    @FXML
    private void pomodoroTimer() {
        System.out.println("Hora de focar");
        stopAllTimers();
        breakTimer.setStatus(false); longbreakTimer.setStatus(false);
        pomodoroTimer.setStatus(true);
        pomodoroTimer.reStartTimer();
        labelTimer.setText(pomodoroTimer.formatedTime());
        pomodoroTimer.increaseCycleCounter();
        if(pomodoroTimer.cycleCheck()){
            isTimeToCatch();
        }
    }

    @FXML
    protected void buttonClicked(ActionEvent event) {
        if(pomodoroTimer.isStatus()){
            if (timeline.getStatus() == Animation.Status.STOPPED) {
                timeline.play();
                soundChooser(2);
                setPauseIcon();
            }else  if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
                setPlayIcon();
            }
        }else if(breakTimer.isStatus()){
            if (breaktimeline.getStatus() == Animation.Status.STOPPED) {
                breaktimeline.play();
                soundChooser(2);
                setPauseIcon();
            }else  if (breaktimeline.getStatus() == Animation.Status.RUNNING) {
                breaktimeline.stop();
                setPlayIcon();
            }
        } else if (longbreakTimer.isStatus()) {
            if (longbreaktimeline.getStatus() == Animation.Status.STOPPED) {
                longbreaktimeline.play();
                soundChooser(2);
                setPauseIcon();
            }else  if (longbreaktimeline.getStatus() == Animation.Status.RUNNING) {
                longbreaktimeline.stop();
                setPlayIcon();
            }

        }
    }

    @FXML
    private void onAddTask(ActionEvent event) {
        String text = inputTask.getText();

        if (text != null && !text.trim().isEmpty()) {
            CheckBox newTask = new CheckBox(text);
            Button deleteTask = new Button();
            deleteTask.getStyleClass().add("buttonDeleteTask");

            HBox taskItem = new HBox(10);
            taskItem.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(newTask, Priority.ALWAYS);
            newTask.setMaxWidth(Double.MAX_VALUE);

            taskItem.getChildren().addAll(newTask, deleteTask);

            int onGoingLimit = 1;

            newTask.setOnAction(e -> {
                if (newTask.isSelected()) {
                    if(newTask.lookup(".text") instanceof
                            javafx.scene.text.Text textNode){
                        textNode.setStrikethrough(true);
                    }
                    onGoingList.getChildren().remove(taskItem);
                    toDoList.getChildren().remove(taskItem);
                    finishedList.getChildren().add(taskItem);

                    newTask.setDisable(true);

                    promoteNextTask();
                }
            });

            deleteTask.setOnAction(e -> {
                if (taskItem.getParent() instanceof Pane parentPane) {
                    parentPane.getChildren().remove(taskItem);
                }
                promoteNextTask();
            });

            if (onGoingList.getChildren().size() < onGoingLimit) {
                onGoingList.getChildren().add(taskItem);
            } else {
                toDoList.getChildren().add(taskItem);
            }

            inputTask.clear();
        }
    }

    private void promoteNextTask() {
        if (onGoingList.getChildren().isEmpty() && !toDoList.getChildren().isEmpty()) {
            javafx.scene.Node proximaTarefa = toDoList.getChildren().get(0);
            toDoList.getChildren().remove(proximaTarefa);
            onGoingList.getChildren().add(proximaTarefa);
        }
    }

    @FXML
    public void onConfiguration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/dev/lukas/pomodorotimer/settings-view.fxml"));
            Parent configScreen = loader.load();

            SettingsController gerenteDaTela = loader.getController();

            gerenteDaTela.setCentralController(this);

            mainPane.setCenter(configScreen);
            telaAtiva = "config";
            atualizarLayout(); // Deixa o método central decidir o que mostrar/esconder

        } catch (IOException e) {
            System.out.println("Erro ao carregar configurações" + e.getMessage());
        }
    }

    @FXML
    public void onPokedex(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/dev/lukas/pomodorotimer/pokedex-view.fxml"));
            Parent pokedexScreen = loader.load();

            PokedexController gerenteDaTela = loader.getController();

            gerenteDaTela.setCentralController(this);

            mainPane.setCenter(pokedexScreen);
            telaAtiva = "pokedex";
            atualizarLayout(); // Deixa o método central decidir o que mostrar/esconder
        }catch (IOException e) {
            System.out.println("Erro ao carregar pokedex" +e.getMessage());
        }
    }

    @FXML
    public void openTimer() {
        mainPane.setCenter(timerVBox);
        telaAtiva = "timer";
        atualizarLayout(); // Deixa o método central decidir o que mostrar/esconder
    }

    @FXML
    public void updateTimer(int newTime, int newBreakTime, int howMuchToLongBreak, int longBreakDuration) {
        stopAllTimers();
        pomodoroTimer.setTimer(newTime);
        breakTimer.setTimer(newBreakTime);
        longbreakTimer.setTimer(longBreakDuration);
        breakTimer.setWhenLongBreak(howMuchToLongBreak);
        labelTimer.setText(pomodoroTimer.formatedTime());
        pomodoroTimer.resetCycleCounter();
        breakTimer.resetCycleCounter();
        setPlayIcon();

    }

    @FXML
    public void breakTime() {
        int whenLong = breakTimer.getWhenLongBreak();
        int cycleCounter = breakTimer.getCycleCounter();
        stopAllTimers();

        if (cycleCounter < whenLong) {
            soundChooser(1);
            pomodoroTimer.setStatus(false); longbreakTimer.setStatus(false);
            breakTimer.setStatus(true);
            breakTimer.reStartTimer();
            labelTimer.setText(breakTimer.formatedTime());
            breakTimer.increaseCycleCounter();

        } else if (cycleCounter == whenLong) {
            soundChooser( 0);
            pomodoroTimer.setStatus(false); breakTimer.setStatus(false);
            longbreakTimer.setStatus(true);
            longbreakTimer.reStartTimer();
            labelTimer.setText(longbreakTimer.formatedTime());
            breakTimer.resetCycleCounter();
        }

    }

    @FXML
    public void onSkipStageButtonClicked(ActionEvent event) {

        if (pomodoroTimer.isStatus()) {
            timeline.stop();
            setPlayIcon();
            breakTime();
        } else if (breakTimer.isStatus()) {
            breaktimeline.stop();
            setPlayIcon();
            pomodoroTimer();
        } else if (longbreakTimer.isStatus()) {
            longbreaktimeline.stop();
            setPlayIcon();
            pomodoroTimer();
        }
    }

    @FXML
    public void soundChooser(int option) {
        switch (option) {
            case 0:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/PokemonCenterRecoverwav.wav",-20.0f);
                break;
            case 1:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/PokemonLevelUp.wav",-20.0f);
                break;
            case 2:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/ButtonPressed.wav",-15.0f);
                break;
            case 3:
                SoundManager.play("/br/com/dev/lukas/pomodorotimer/sounds/GrassSound.wav",-50.0f);
                break;
        }
    }

    public void isTimeToCatch(){
        int pokemonCaptured = pokemonIdGenerator();
        User.getInstance().addPokemon(pokemonCaptured);
        SaveManager.saveData();
        SoundManager.playPokemonSound(pokemonCaptured);
        showCaptureAlert(pokemonCaptured);
    }

    public Integer pokemonIdGenerator(){
        Set<Integer> catched = User.getInstance().getUserPokemons();

        if(catched.size() >= 150){
            showCongratulations();
            return null;
        }

        List<Integer> missing = new ArrayList<>();
        for(int i = 1; i <=150; i++){
            if(!catched.contains(i)){
                missing.add(i);
            }
        }

        Random random = new Random();
        return missing.get(random.nextInt(missing.size()));
    }

    private void showCongratulations(){
        soundChooser(0);
        Alert alert = createCongratulationsAlert();
        alert.show();
    }

    Alert createCongratulationsAlert(){ //Todo mudar o alert para stage scene
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Parabéns, Mestre Pokémon!");
        alert.setHeaderText(null);

        // Mew (#151) como recompensa secreta por completar a Pokédex
        String urlImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official"
                + "-artwork/" + 151 + ".png";

        Image pokemonImage = new Image(urlImage, 200, 200, true, true);
        ImageView imageView = new ImageView(pokemonImage);

        alert.setGraphic(imageView);

        alert.setContentText(
                "Você completou a Pokédex!\n"
              + "Todos os 150 Pokémon foram capturados!\n\n"
              + "Como recompensa, Mew apareceu para você!\n"
              + "Você é um verdadeiro Mestre Pokémon!"
        );

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/br/com/dev/lukas/pomodorotimer/css/style.css").toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("pokemon-alert");

        return alert;
    }

    private void showCaptureAlert(int idPokemon) {   //TODO mudar o Alert para scene
        animationBushTimiline.stop();
        Popup popup = new Popup();
        popup.setAutoHide(true);

        String urlImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official" +
                "-artwork/" + idPokemon + ".png";
        Image pokemonImage = new Image(urlImage, 150, 150, true, true);
        ImageView imageView = new ImageView(pokemonImage);

        Label captureLabel = new Label("Parabéns! Um novo Pokémon!");
        captureLabel.getStyleClass().add("captureLabel");

        Label contextLabel = new Label("Você encontrou um novo Pokémon!\nEle foi adicionado a sua coleção!\n" +
                "Para saber mais sobre ele consulte a Pokédex.");
        contextLabel.getStyleClass().add("contextLabel");

        VBox textContainer = new VBox(5, captureLabel, contextLabel);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        // 3. Layout Raiz com CSS
        HBox root = new HBox(imageView, textContainer);
        root.getStyleClass().add("pokemonRoot");

        String cssPath = getClass().getResource("/br/com/dev/lukas/pomodorotimer/css/style.css").toExternalForm();
        root.getStylesheets().add(cssPath);

        popup.getContent().add(root);

        javafx.stage.Window window = mainPane.
                getScene().getWindow();
        if (window != null) {
            popup.show(window);
            popup.setX(window.getX() + (window.
                    getWidth() - root.prefWidth(-1)) / 2);
            popup.setY(window.getY() + (window.
                    getHeight() - root.prefHeight(-1)) / 2);
        }
    }


    public void animationTallGrass(){
        Random random = new Random();

        RotateTransition shake = new RotateTransition(Duration.millis(80), imgMoita);
        shake.setByAngle(30);
        shake.setCycleCount(3);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> imgMoita.setRotate(0));

        animationBushTimiline = new Timeline(new KeyFrame(Duration.seconds(5), event ->{
            if (random.nextInt(100) < 30){
                shake.play();
                soundChooser(3);
            }
        }));

        animationBushTimiline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setPlayIcon (){
        Image playImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/br/com/dev/lukas" +
                "/pomodorotimer/images/Play.png")));
        ImageView playView = new ImageView(playImg);
        playView.setFitHeight(24);
        playView.setFitWidth(20);
        buttonTimer.setGraphic(playView);
    }

    public void setPauseIcon (){
        Image pauseImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/br/com/dev/lukas" +
                "/pomodorotimer/images/Pause.png")));
        ImageView pauseView = new ImageView(pauseImg);
        pauseView.setFitHeight(24);
        pauseView.setFitWidth(20);
        buttonTimer.setGraphic(pauseView);
    }

    // ══════════════════════════════════════════════════════════════
    //  ETAPA 6: Método central de responsividade — atualizarLayout()
    // ══════════════════════════════════════════════════════════════
    //
    //  COMO FUNCIONA:
    //  Este é o ÚNICO método que controla a visibilidade dos painéis.
    //  Ele é chamado em 2 situações:
    //    1. Quando a largura da janela muda (pelo listener na ETAPA 3)
    //    2. Quando o usuário troca de tela (openTimer, onConfiguration, onPokedex)
    //
    //  PARA MANUTENÇÃO:
    //  - Para mudar os breakpoints, altere as constantes na ETAPA 5
    //  - Para adicionar uma nova tela, crie o método (ex: onNovaView()),
    //    defina telaAtiva = "novaView" e chame atualizarLayout()
    //  - Para adicionar um novo painel responsivo, adicione a lógica aqui
    //
    //  FLUXO DE DECISÃO:
    //  ┌─────────────────────────────────────────────────────────┐
    //  │ largura < 380  → Widget mode (só relógio)              │
    //  │ largura < 500  → Sem sidebar, sem painel direito       │
    //  │ largura < 750  → Com sidebar, sem painel direito       │
    //  │ largura >= 750 → Tudo visível (se tela = "timer")      │
    //  └─────────────────────────────────────────────────────────┘
    //
    private void atualizarLayout() {
        double largura = mainPane.getWidth();

        boolean mostrarSidebar = largura >= BREAKPOINT_SIDEBAR;
        boolean mostrarRightPane = largura >= BREAKPOINT_RIGHTPANE && "timer".equals(telaAtiva);
        boolean widgetMode = largura < BREAKPOINT_WIDGET;

        // Sidebar esquerda: visível apenas se tiver espaço suficiente
        leftPane.setVisible(mostrarSidebar);
        leftPane.setManaged(mostrarSidebar);

        // Painel direito (tarefas): só aparece na tela do timer E se tiver espaço
        if (mostrarRightPane) {
            mainPane.setRight(rightPane);
            rightPane.setPrefWidth(largura * 0.20); // 20% da largura total
        } else {
            mainPane.setRight(null); // Remove completamente do layout
        }

        // Botões de play/skip: ocultos no widget mode
        buttonsHbox.setVisible(!widgetMode);
        buttonsHbox.setManaged(!widgetMode);

        // Widget mode: força a tela do timer (não faz sentido ver config/pokedex tão pequeno)
        if (widgetMode && !"timer".equals(telaAtiva)) {
            telaAtiva = "timer";
            mainPane.setCenter(timerVBox);
        }

        atualizarBotoesSidebar();
    }

    public void redimensionarArea(){
        boolean isWidgetMode = mainPane.getWidth() < BREAKPOINT_WIDGET;

        double margemLargura = isWidgetMode ? 0 : 40;
        double margemAltura = isWidgetMode ? 0 : 120;

        double larguraDisponivel = timerVBox.getWidth() - margemLargura;
        double alturaDisponivel = timerVBox.getHeight() - margemAltura;


        if (larguraDisponivel <= 0 || alturaDisponivel <= 0) return;

        double proporcaoIdeal = 300.0 / 250.0 ;

        double novaLargura;
        double novaAltura;

        if(larguraDisponivel / alturaDisponivel > proporcaoIdeal){
            novaAltura = alturaDisponivel;
            novaLargura = alturaDisponivel * proporcaoIdeal;
        }else{
            novaLargura = larguraDisponivel;
            novaAltura = larguraDisponivel / proporcaoIdeal;
        }

        battleContainer.setPrefWidth(novaLargura);
        battleContainer.setPrefHeight(novaAltura);
    }

    public void stopAllTimers(){
        if (timeline != null) timeline.stop();
        if (breaktimeline != null) breaktimeline.stop();
        if (longbreaktimeline != null) longbreaktimeline.stop();
        if (animationBushTimiline != null) animationBushTimiline.stop();
    }

    public void  atualizarBotoesSidebar() {
        if (timerButton == null || pokedexButton == null || configurationButton == null) {
            return;
        }

        timerButton.getStyleClass().remove("active");
        pokedexButton.getStyleClass().remove("active");
        configurationButton.getStyleClass().remove("active");

        switch (telaAtiva) {
            case "timer" -> {
                if (!timerButton.getStyleClass().contains("active")) {
                    timerButton.getStyleClass().add("active");
                }
            }
            case "pokedex" -> {
                if (!pokedexButton.getStyleClass().contains("active")) {
                    pokedexButton.getStyleClass().add("active");
                }
            }
            case "config" -> {
                if (!configurationButton.getStyleClass().contains("active")) {
                    configurationButton.getStyleClass().add("active");
                }
            }
        }
    }
}
