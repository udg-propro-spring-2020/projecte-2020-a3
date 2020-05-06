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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UIChess extends Application {
    /// CONSTANTS
    private static final String TITLE = "ESCACS";
    private static final String MENU = "MENU";
    private static final double MAX_BTN_WIDTH = 300.0;

    private static final String DEF_GAME_LOCATION = "./data/default_game.json";
    private static final String DEF_IMG_LOCATION = "./data/img/";
    private static final int IMG_PIXELS = 60;

    /// Game Control Options
    private static Stage window;
    private static String choosenConfigFile = null;
    private static List<String> knowledgeFiles = null;

    /// @brief Defines the type of the match currently playing
    private static enum GameType {
        PlayerPlayer, CpuPlayer, CpuCpu
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
    ///       not empty
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
    /// @post Adds a go back button to the last position of the collection
    private static void addGoBackButton(Collection<Node> list) {
        Button goBackButton = new Button();
        ItemBuilder.buildButton(
            goBackButton,
            "TORNAR",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.EXIT
        );
        goBackButton.setOnAction(e -> {
            buildMainScene();
        });
        list.add(goBackButton);
    }

    /// @brief Function that builds the buttons used in the menu
    /// @pre ---
    /// @post Returns a list containing the buttons with the options
    ///       of the main menu
    private static Collection<Node> buildMenuButtons() {
        Collection<Node> list = new ArrayList<>();
        Button defaultGameButton = new Button();
        ItemBuilder.buildButton(
            defaultGameButton,
            "Iniciar partida",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        defaultGameButton.setOnAction(e -> {
            setSceneTitle("INICIA PARTIDA NORMAL");
            gameOptions();
        });
        list.add(defaultGameButton);

        Button configuredGameButton = new Button();
        ItemBuilder.buildButton(
            configuredGameButton,
            "Preconfigura una partida",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        configuredGameButton.setOnAction(e -> {
            setSceneTitle("PARTIDA PRE-CONFIGURADA");
            preconfiguredGame();
        });
        list.add(configuredGameButton);

        Button loadGameButton = new Button();
        ItemBuilder.buildButton(
            loadGameButton,
            "Carrega una partida",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        loadGameButton.setOnAction(e -> {
            setSceneTitle("PARTIDA CARREGADA");
        });
        list.add(loadGameButton);

        Button exitGameButton = new Button();
        ItemBuilder.buildButton(
            exitGameButton,
            "Surt",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.EXIT
        );
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
        ItemBuilder.buildButton(
            playerVsPlayer, 
            "Jugador vs Jugador", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        playerVsPlayer.setOnAction(e -> {
            setGameUp(GameType.PlayerPlayer);
        });
        list.add(playerVsPlayer);

        Button cpuVsPlayer = new Button();
        ItemBuilder.buildButton(
            cpuVsPlayer, 
            "CPU vs Jugador", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        cpuVsPlayer.setOnAction(e -> {
            setGameUp(GameType.CpuPlayer);
        });
        list.add(cpuVsPlayer);

        Button cpuVsCpu = new Button();
        ItemBuilder.buildButton(
            cpuVsCpu, 
            "CPU vs CPU", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        cpuVsCpu.setOnAction(e -> {
            setGameUp(GameType.CpuCpu);
        });
        list.add(cpuVsCpu);

        addGoBackButton(list);

        return list;
    }

    /// @brief Builds the buttons for the preconfigurated game menu
    /// @pre ---
    /// @post Returns a collection with the buttons for the preconfigurated
    ///       game menu
    private static Collection<Node> buildPreconfiguredGameButtons() {
        FileChooser fc = new FileChooser();
        Collection<Node> list = new ArrayList<>();

        Button enterFileBtn = new Button();
        ItemBuilder.buildButton(
            enterFileBtn, 
            "AFEGIR FITXER", 
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );   
        enterFileBtn.setOnAction(
            new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event) {
                    String file = fc.showOpenDialog(window).getPath();
                    if (file != null) {
                        System.out.println(file);
                        choosenConfigFile = file;
                        gameOptions();
                        String res;
                        try {
                            res = FromJSONParserHelper.buildChess(choosenConfigFile).toString();
                        } catch(Exception e) {
                            res = "Error on openening the file";
                        }
                        System.out.println(res);             
                    }
                }
            }
        );
        list.add(enterFileBtn);

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

    /// @brief Starts the type of game the user has choosen
    /// @pre ---
    /// @post Loads the chess from the file entered (if null, the default) and
    ///       configures the game to be played (cpu, knowledge and what's needed)
    private static void setGameUp(GameType gameType) {
        switch (gameType) {
            case PlayerPlayer:
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
            case CpuPlayer:
                
                break;
            case CpuCpu:
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
}