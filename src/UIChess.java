import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Miquel de Domingo i Giralt
 * @file UIChess.java
 * @class UIChess
 * @brief Class that controls the game played in a graphic interface
 */
public class UIChess extends Application {
    /// CONSTANTS
    private static final String TITLE = "ESCACS";
    private static final String MENU = "MENU";
    private static final double MAX_BTN_WIDTH = 300.0;

    private static final String DEF_GAME_LOCATION = "./data/default_game.json";
    private static final String DEF_IMG_LOCATION = "./data/img/";
    private static final int IMG_PIXELS = 60;
    private static final double SPACER_PIXELS = 50.0;

    private static final String SELECTED_CSS = "selected";
    private static final String UNSELECTED_CSS = "unselected";

    /// Game Control Options
    private static Stage window;
    private static String choosenConfigFile = null;
    private static int cpuDifficulty = 0;
    private static List<String> knowledgeFiles = null;
    private static GameState lastGameState = GameState.GAME_INIT;

    /// @brief Defines the type of the match currently playing
    private static enum GameType {
        PLAYER_PLAYER, CPU_PLAYER, CPU_CPU
    }

    /// @brief Defines the current applicaton state
    private static enum GameState {
        GAME_INIT, GAME_MODE, GAME_TYPE
    }

    /// @brief Launches the application
    /// @pre ---
    /// @post Starts the GUI application
    public static void main(String[] args) {
        launch(args);
    }

    /// @brief Sets the scene title
    /// @pre ---
    /// @post Changes the scene title to the given one only if it is not null and
    /// not empty
    private static void setSceneTitle(String title) {
        if (!(title == null || title.isEmpty())) {
            window.setTitle(title);
        }
    }

    /// @brief Buils the main scene displaying a menu
    /// @pre ---
    /// @post Displays the 4 buttons of the menu
    private static void buildMainScene() {
        window.setTitle(MENU + " - " + TITLE);

        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("ESCACS"));
        list.addAll(buildMenuButtons());
        VBox body = ItemBuilder.buildVBox(16.0, list, true);

        window.setScene(ItemBuilder.buildScene(body));
        window.show();
    }

    /// @brief Adds a go back button to a collection
    /// @pre ---
    /// @post Adds a go back button to the last position of the collection and a
    /// spacer
    /// of @param SPACER_PIXELS as height above it
    private static void addGoBackButton(Collection<Node> list) {
        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS));

        Button goBackButton = new Button();
        ItemBuilder.buildButton(goBackButton, "TORNAR", MAX_BTN_WIDTH, ItemBuilder.BtnType.EXIT);
        goBackButton.setOnAction(e -> {
            System.out.println(lastGameState.toString());
            switch (lastGameState) {
                case GAME_INIT:
                    buildMainScene();
                    break;
                case GAME_MODE:
                    lastGameState = GameState.GAME_INIT;
                    gameOptions();
                    break;
                case GAME_TYPE:
                    break;
            }

        });
        list.add(goBackButton);
    }

    /// @brief Function that builds the buttons used in the menu
    /// @pre ---
    /// @post Returns a list containing the buttons with the options
    /// of the main menu
    private static Collection<Node> buildMenuButtons() {
        Collection<Node> list = new ArrayList<>();
        Button defaultGameButton = new Button();
        ItemBuilder.buildButton(defaultGameButton, "INICIAR PARTIDA", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        defaultGameButton.setOnAction(e -> {
            setSceneTitle("INICIA PARTIDA NORMAL");
            gameOptions();
        });
        list.add(defaultGameButton);

        Button configuredGameButton = new Button();
        ItemBuilder.buildButton(configuredGameButton, "PRECONFIGURA UNA PARTIDA", MAX_BTN_WIDTH,
                ItemBuilder.BtnType.PRIMARY);
        configuredGameButton.setOnAction(e -> {
            setSceneTitle("PARTIDA PRE-CONFIGURADA");
            preconfiguredGame();
        });
        list.add(configuredGameButton);

        Button loadGameButton = new Button();
        ItemBuilder.buildButton(loadGameButton, "CARREGA UNA PARTIDA", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        loadGameButton.setOnAction(e -> {
            setSceneTitle("PARTIDA CARREGADA");
        });
        list.add(loadGameButton);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS));

        Button exitGameButton = new Button();
        ItemBuilder.buildButton(exitGameButton, "SURT", MAX_BTN_WIDTH, ItemBuilder.BtnType.EXIT);
        exitGameButton.setOnAction(e -> {
            window.close();
        });
        list.add(exitGameButton);

        return list;
    }

    /// @brief Function to build the option game menus
    /// @pre ---
    /// @post Builds the game option buttons
    private static Collection<Node> buildOptionButtons() {
        Collection<Node> list = new ArrayList<>();

        Button playerVsPlayer = new Button();
        ItemBuilder.buildButton(playerVsPlayer, "JUGADOR VS JUGADOR", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        playerVsPlayer.setOnAction(e -> {
            setGameUp(GameType.PLAYER_PLAYER);
        });
        list.add(playerVsPlayer);

        Button cpuVsPlayer = new Button();
        ItemBuilder.buildButton(cpuVsPlayer, "CPU VS JUGADOR", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        cpuVsPlayer.setOnAction(e -> {
            cpuConfiguration(GameType.CPU_PLAYER);
        });
        list.add(cpuVsPlayer);

        Button cpuVsCpu = new Button();
        ItemBuilder.buildButton(cpuVsCpu, "CPU vs CPU", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        cpuVsCpu.setOnAction(e -> {
            cpuConfiguration(GameType.CPU_CPU);
        });
        list.add(cpuVsCpu);

        addGoBackButton(list);

        return list;
    }

    /// @brief Builds the buttons for the preconfigurated game menu
    /// @pre ---
    /// @post Returns a collection with the buttons for the preconfigurated
    /// game menu
    private static Collection<Node> buildPreconfiguredGameButtons() {
        Collection<Node> list = new ArrayList<>();

        Button enterFileBtn = new Button();
        ItemBuilder.buildButton(enterFileBtn, "AFEGIR FITXER", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        enterFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File selected = fileSelector();

                if (selected != null) {
                    String file = selected.getPath();
                    System.out.println(file);
                    choosenConfigFile = file;
                    String res;
                    try {
                        System.out.println("HENLO");
                        res = FromJSONParserHelper.buildChess(choosenConfigFile).toString();
                        gameOptions();
                    } catch (FileNotFoundException e) {
                        res = "Error on openening the file";
                        ItemBuilder
                                .buildPopUp("ERROR",
                                        "Hi ha hagut un error en l'obertura del fitxer. Torna-ho a intentar", true)
                                .show();
                        ;
                    } catch (JSONParseFormatException e) {
                        System.out.println(e.getMessage());
                        ItemBuilder.buildPopUp(e.getType(),
                                "El format del fitxer d'entrada no és el correcte.\nRevisa'l i torna-ho a intentar",
                                true).show();
                        ;
                    }
                }
            }
        });
        list.add(enterFileBtn);

        addGoBackButton(list);

        return list;
    }

    /// @brief Builds the buttons to let the user configure the cpu difficulty and
    /// (if wanted) knowledge
    /// @pre ---
    /// @post Builds the buttons to allow the user to configure the cpu. If they
    /// want, knowledge can be adde (1 to n files). Before adding, all saved
    /// files from @param knowledgeFiles will be cleared.
    private static Collection<Node> buildCPUButtons(GameType gameType) {
        Collection<Node> list = new ArrayList<>();

        Button beginnerBtn = new Button();
        ItemBuilder.buildButton(beginnerBtn, "PRINCIPIANT", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        beginnerBtn.getStyleClass().add(SELECTED_CSS);
        list.add(beginnerBtn);

        Button intermediateBtn = new Button();
        ItemBuilder.buildButton(intermediateBtn, "NORMAL", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        intermediateBtn.getStyleClass().add(UNSELECTED_CSS);
        list.add(intermediateBtn);

        Button advancedBtn = new Button();
        ItemBuilder.buildButton(advancedBtn, "DIFICIL", MAX_BTN_WIDTH, ItemBuilder.BtnType.PRIMARY);
        advancedBtn.getStyleClass().add(UNSELECTED_CSS);
        list.add(advancedBtn);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS / 3));

        Button addKnowledgeBtn = new Button();
        ItemBuilder.buildButton(addKnowledgeBtn, "CONEIXEMENT", MAX_BTN_WIDTH, ItemBuilder.BtnType.SECONDARY);
        addKnowledgeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(System.getProperty("user.dir")));
                List<File> selected = fc.showOpenMultipleDialog(window);

                if (selected != null) {
                    List<String> files = new ArrayList<>();
                    System.out.println("Knowledge Added Files:");
                    for (File f : selected) {
                        files.add(f.getPath());
                        System.out.println(f.getPath());
                    }
                    if (knowledgeFiles != null) {
                        knowledgeFiles.clear();
                    }
                    knowledgeFiles = files;
                    setGameUp(gameType);
                }
            }
        });
        list.add(addKnowledgeBtn);

        /// Handle choosen difficulty
        beginnerBtn.setOnAction(e -> {
            if (advancedBtn.getStyleClass().contains(SELECTED_CSS)) {
                advancedBtn.getStyleClass().remove(SELECTED_CSS);
                advancedBtn.getStyleClass().add(UNSELECTED_CSS);
            }
            if (intermediateBtn.getStyleClass().contains(SELECTED_CSS)) {
                intermediateBtn.getStyleClass().remove(SELECTED_CSS);
                intermediateBtn.getStyleClass().add(UNSELECTED_CSS);
            }
            if (!beginnerBtn.getStyleClass().contains(SELECTED_CSS)) {
                beginnerBtn.getStyleClass().remove(SELECTED_CSS);
                beginnerBtn.getStyleClass().add(SELECTED_CSS);
                cpuDifficulty = 2;
            }
        });
        intermediateBtn.setOnAction(e -> {
            if (advancedBtn.getStyleClass().contains(SELECTED_CSS)) {
                advancedBtn.getStyleClass().remove(SELECTED_CSS);
                advancedBtn.getStyleClass().add(UNSELECTED_CSS);
            }
            if (beginnerBtn.getStyleClass().contains(SELECTED_CSS)) {
                beginnerBtn.getStyleClass().remove(SELECTED_CSS);
                beginnerBtn.getStyleClass().add(UNSELECTED_CSS);
            }
            if (!intermediateBtn.getStyleClass().contains(SELECTED_CSS)) {
                intermediateBtn.getStyleClass().remove(SELECTED_CSS);
                intermediateBtn.getStyleClass().add(SELECTED_CSS);
                cpuDifficulty = 4;
            }
        });
        advancedBtn.setOnAction(e -> {
            if (intermediateBtn.getStyleClass().contains(SELECTED_CSS)) {
                intermediateBtn.getStyleClass().remove(SELECTED_CSS);
                intermediateBtn.getStyleClass().add(UNSELECTED_CSS);
            }
            if (beginnerBtn.getStyleClass().contains(SELECTED_CSS)) {
                beginnerBtn.getStyleClass().remove(SELECTED_CSS);
                beginnerBtn.getStyleClass().add(UNSELECTED_CSS);
            }
            if (!advancedBtn.getStyleClass().contains(SELECTED_CSS)) {
                advancedBtn.getStyleClass().remove(SELECTED_CSS);
                advancedBtn.getStyleClass().add(SELECTED_CSS);
                cpuDifficulty = 6;
            }
        });

        addGoBackButton(list);

        return list;
    }

    /// @brief Function that displays the game options scene
    /// @pre ---
    /// @post Displays the game options and loads the desired game mode
    private static void gameOptions() {
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("ESCULL UN MODE"));
        list.addAll(buildOptionButtons());
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true));
        window.setScene(s);
    }

    /// @brief Function that displays the preconfigured game options scene
    /// @pre ---
    /// @post Once the file it is entered, goes to the game options
    private static void preconfiguredGame() {
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("PARTIDA \nPRE-CONFIGURADA"));
        list.addAll(buildPreconfiguredGameButtons());
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true));
        window.setScene(s);
    }

    private static void cpuConfiguration(GameType gameType) {
        lastGameState = GameState.GAME_MODE;

        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("DEFINEIX LA CPU"));
        list.addAll(buildCPUButtons(gameType));
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true));
        window.setScene(s);
    }

    /// @brief Starts the type of game the user has choosen
    /// @pre ---
    /// @post Loads the chess from the file entered (if null, the default) and
    /// configures the game to be played (cpu, knowledge and what's needed)
    private static void setGameUp(GameType gameType) {
        switch (gameType) {
            case PLAYER_PLAYER:
                try {
                    String res = null;
                    if (choosenConfigFile == null) {
                        res = FromJSONParserHelper.buildChess(DEF_GAME_LOCATION).toString();
                    } else {
                        res = FromJSONParserHelper.buildChess(choosenConfigFile).toString();
                    }
                    System.out.println(res);
                } catch (Exception e) {
                    System.out.println("Error");
                }
                break;
            case CPU_PLAYER:
                System.out.println("INSIDE CPU_PLAYER");
                break;
            case CPU_CPU:
                break;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        window.setWidth(900.0);
        window.setHeight(650.0);

        buildMainScene();
    }

    /// @brief Allows the user to select a file and returns it
    /// @pre ---
    /// @post Returns the file selected for the user. If has not selected any,
    /// returns null
    private static File fileSelector() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));
        File selected = fc.showOpenDialog(window);

        return selected;
    }
}