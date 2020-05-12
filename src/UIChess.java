import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/// @author Miquel de Domingo i Giralt
/// @file UIChess.java
/// @class UIChess
/// @brief Class that controls the game played in a graphic interface
public class UIChess extends Application {
    /// CONSTANTS
    private static final String TITLE = "ESCACS";                   ///< Main title of the window
    private static final String MENU = "MENU";                      ///< Menu title of the window
    private static final double MAX_BTN_WIDTH = 300.0;              ///< Max width of the buttons

    private static final String DEF_GAME_LOCATION = "./data/configuration.json";    ///< Game default configuration location
    private static final String DEF_IMG_LOCATION = "./data/img/";                   ///< Default image location
    private static final String DEF_WHITE_TILE_LOCATION = "w.png";                  ///< Default white tile image name
    private static final String DEF_BLACK_TILE_LOCATION = "b.png";                  ///< Default black tile image name
    private static final int IMG_PIXELS = 60;                                       ///< Default images size (60x60)
    private static final double SPACER_PIXELS = 40.0;                               ///< Default height of a spacer

    private static final String SELECTED_CSS = "selected";          ///< Selected button CSS class
    private static final String UNSELECTED_CSS = "unselected";      ///< Unselected button CSS class

    /// Game Control Options
    private Stage _window;                                          ///< Main window of the applicatino
    private String _choosenConfigFile = null;                       ///< Configuration file entered by the user
    private int _cpuDifficulty = 2;                                 ///< CPU difficulty chosen by the user
    private List<String> _knowledgeFiles = null;                    ///< Knowledge file names entered by the user
    private GameState _lastGameState = GameState.GAME_INIT;         ///< Game state before the current
    private Group _tiles = null;                                    ///< Group of tiles of the board
    private Group _pieces = null;                                   ///< Group of pieces of the board

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
    public void main(String[] args) {
        launch(args);
    }

    /// @brief Sets the scene title
    /// @pre ---
    /// @post Changes the scene title to the given one only if it is not null and
    ///       not empty
    private void setSceneTitle(String title) {
        if (!(title == null || title.isEmpty())) {
            _window.setTitle(title); 
        }
    }

    /// @brief Buils the main scene displaying a menu
    /// @pre ---
    /// @post Displays the 4 buttons of the menu
    private void buildMainScene() {
        _window.setTitle(MENU + " - " + TITLE);

        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("CHESS"));
        list.addAll(buildMenuButtons());
        VBox body = ItemBuilder.buildVBox(16.0, list, true);       
        
        _window.setScene(ItemBuilder.buildScene(body));
        _window.show();
    }
    
    /// @brief Adds a go back button to a collection
    /// @pre ---
    /// @post Adds a go back button to the last position of the collection and a spacer
    ///       of @p SPACER_PIXELS as height above it
    private void addGoBackButton(Collection<Node> list) {
        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS));

        Button goBackButton = new Button();
        ItemBuilder.buildButton(
            goBackButton,
            "GO BACK",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.EXIT
        );
        goBackButton.setOnAction(e -> {
            System.out.println(_lastGameState.toString());
            switch (_lastGameState) {
                case GAME_INIT: 
                    buildMainScene();
                    break;
                case GAME_MODE:
                    _lastGameState = GameState.GAME_INIT;
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
    ///       of the main menu
    private Collection<Node> buildMenuButtons() {
        Collection<Node> list = new ArrayList<>();
        Button defaultGameButton = new Button();
        ItemBuilder.buildButton(
            defaultGameButton,
            "START GAME",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        defaultGameButton.setOnAction(e -> {
            setSceneTitle("NORMAL GAME");
            gameOptions();
        });
        list.add(defaultGameButton);

        Button configuredGameButton = new Button();
        ItemBuilder.buildButton(
            configuredGameButton,
            "CONFIGURE A GAME",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        configuredGameButton.setOnAction(e -> {
            setSceneTitle("CONFIGURED GAME");
            preconfiguredGame();
        });
        list.add(configuredGameButton);

        Button loadGameButton = new Button();
        ItemBuilder.buildButton(
            loadGameButton,
            "LOAD A GAME",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        loadGameButton.setOnAction(e -> {
            setSceneTitle("LOADING GAME");
        });
        list.add(loadGameButton);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS));

        Button exitGameButton = new Button();
        ItemBuilder.buildButton(
            exitGameButton,
            "EXIT",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.EXIT
        );
        exitGameButton.setOnAction(e -> {
            _window.close();
        });
        list.add(exitGameButton);

        return list;
    }

    /// @brief Function to build the option game menus
    /// @pre ---
    /// @post Builds the game option buttons 
    private Collection<Node> buildOptionButtons() {
        Collection<Node> list = new ArrayList<>();

        Button playerVsPlayer = new Button();
        ItemBuilder.buildButton(
            playerVsPlayer, 
            "PLAYER VS PLAYER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        playerVsPlayer.setOnAction(e -> {
            setGameUp(GameType.PLAYER_PLAYER);
        });
        list.add(playerVsPlayer);

        Button cpuVsPlayer = new Button();
        ItemBuilder.buildButton(
            cpuVsPlayer, 
            "PLAYER VS COMPUTER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        cpuVsPlayer.setOnAction(e -> {
            cpuConfiguration(GameType.CPU_PLAYER);
        });
        list.add(cpuVsPlayer);

        Button cpuVsCpu = new Button();
        ItemBuilder.buildButton(
            cpuVsCpu, 
            "COMPUTER VS COMPUTER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
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
    ///       game menu
    private Collection<Node> buildPreconfiguredGameButtons() {
        Collection<Node> list = new ArrayList<>();
        
        Button enterFileBtn = new Button();
        ItemBuilder.buildButton(
            enterFileBtn, 
            "ADD FILE", 
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
            );   
        enterFileBtn.setOnAction(
            new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event) {
                    File selected = fileSelector();

                    if (selected != null) {
                        String file = selected.getPath();
                        System.out.println(file);
                        _choosenConfigFile = file;

                        gameOptions();
                    }
                }
            }
        );
        list.add(enterFileBtn);

        addGoBackButton(list);

        return list;
    }

    /// @brief Builds the buttons to let the user configure the cpu difficulty and
    ///        (if wanted) knowledge
    /// @pre ---
    /// @post Builds the buttons to allow the user to configure the cpu. If they 
    ///       want, knowledge can be adde (1 to n files). Before adding, all saved 
    ///       files from @p knowledgeFiles will be cleared.
    private Collection<Node> buildCPUButtons(GameType gameType) {
        Collection<Node> list = new ArrayList<>();

        Button beginnerBtn = new Button();
        ItemBuilder.buildButton(
            beginnerBtn, 
            "BEGINNER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        beginnerBtn.getStyleClass().add(SELECTED_CSS);
        list.add(beginnerBtn);
        
        Button intermediateBtn = new Button();
        ItemBuilder.buildButton(
            intermediateBtn, 
            "INTERMEDIATE", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        intermediateBtn.getStyleClass().add(UNSELECTED_CSS);
        list.add(intermediateBtn);
                
        Button advancedBtn = new Button();
        ItemBuilder.buildButton(
            advancedBtn, 
            "ADVANCED", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY
        );
        advancedBtn.getStyleClass().add(UNSELECTED_CSS);
        list.add(advancedBtn);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS / 3));

        Button addKnowledgeBtn = new Button();
        ItemBuilder.buildButton(
            addKnowledgeBtn,
            "KNOWLEDGE",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.SECONDARY
        );
        addKnowledgeBtn.setOnAction(
            new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event) {
                    FileChooser fc = new FileChooser();
                    fc.setInitialDirectory(
                        new File(System.getProperty("user.dir"))
                    );
                    List<File> selected = fc.showOpenMultipleDialog(_window);

                    if (selected != null) {
                        List<String> files = new ArrayList<>();
                        System.out.println("Knowledge Added Files:");
                        for (File f : selected) {
                            files.add(f.getPath());
                            System.out.println(f.getPath());
                        }
                        if (_knowledgeFiles != null) {
                            _knowledgeFiles.clear();
                        }
                        _knowledgeFiles = files;
                    }
                }
            }
        );
        list.add(addKnowledgeBtn);

        Button continueBtn = new Button();
        ItemBuilder.buildButton(
            continueBtn,
            "CHOOSE GAME TYPE",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.ACCENT
        );
        continueBtn.setOnAction(e -> {
            setGameUp(gameType);
        });
        list.add(continueBtn);
        
        // Handle choosen difficulty
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
                _cpuDifficulty = 2;
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
                _cpuDifficulty = 4;
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
                _cpuDifficulty = 6;
            }
        });

        addGoBackButton(list);

        return list;
    }

    /// @brief Builds the buttons for the in-game options
    /// @pre ---
    /// @post Builds the buttons to allow the user to undo, redo, save game and
    ///       other options the user has while playing the game
    private Collection<Node> buildInGameButtons() {
        Collection<Node> list = new ArrayList<>();

        Button undoBtn = new Button();
        ItemBuilder.buildButton(
            undoBtn,
            "UNDO",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        undoBtn.setOnAction(e -> {
            System.out.println("Desfent...");
        });
        list.add(undoBtn);

        Button redoBtn = new Button();
        ItemBuilder.buildButton(
            redoBtn,
            "REDO",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        redoBtn.setOnAction(e -> {
            System.out.println("Refent...");
        });
        list.add(redoBtn);

        Button drawBtn = new Button();
        ItemBuilder.buildButton(
            drawBtn,
            "DRAW",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        drawBtn.setOnAction(e -> {
            System.out.println("Demanant taules...");
        });
        list.add(drawBtn);

        Button saveMatch = new Button();
        ItemBuilder.buildButton(
            saveMatch,
            "SAVE GAME",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        saveMatch.setOnAction(e -> {
            System.out.println("Guardant partida...");
        });
        list.add(saveMatch);

        return list;
    }

    /// @brief Function that displays the game options scene
    /// @pre ---
    /// @post Displays the game options and loads the desired game mode
    private void gameOptions() {
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("CHOOSE A GAME MODE"));
        list.addAll(buildOptionButtons());
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true));
        _window.setScene(s);
    }

    /// @brief Function that displays the preconfigured game options scene
    /// @pre ---
    /// @post Once the file it is entered, goes to the game options
    private void preconfiguredGame() {        
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("CONFIGURED GAME"));
        list.addAll(buildPreconfiguredGameButtons());
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true));
        _window.setScene(s);
    }

    /// @brief Handles the configuration of the CPU by the user
    /// @pre @p gameType is @p CPUvsPlayer or @p CPUvsCPU
    private void cpuConfiguration(GameType gameType) {
        _lastGameState = GameState.GAME_MODE;
        
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("DEFINE THE \n COMPUTER"));
        list.addAll(buildCPUButtons(gameType));
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true));
        _window.setScene(s);
    }

    /// @brief Starts the type of game the user has choosen
    /// @pre ---
    /// @post Loads the chess from the file entered (if null, the default) and
    ///       configures the game to be played (cpu, knowledge and what's needed)
    private void setGameUp(GameType gameType) {
        // Build the chess
        Chess chess = null;
        // On any fatal loading error, the application will exit
        try {
            if (_choosenConfigFile == null) {
                chess = FromJSONParserHelper.buildChess(DEF_GAME_LOCATION);
            } else {
                chess = FromJSONParserHelper.buildChess(_choosenConfigFile);
            }
            System.out.println(chess.toString());
        } catch (FileNotFoundException e) {
            displayErrorPopUp(
                "FILE ERROR",
                "An error ocurred when oppening the file. \nCheck it and try again."
            );

            System.exit(-1);
        } catch (JSONParseFormatException e) {
            displayErrorPopUp(
                e.getType(),
                "The configuration file contains an illegal format. \nCheck it and try again."
            );

            System.exit(-1);
        }

        // Set scene
        Parent p = buildBoard(chess);
        Scene scene = new Scene(p);
        switch (gameType) {
            case PLAYER_PLAYER:
                _window.setScene(scene);
                _window.sizeToScene();
                break;
            case CPU_PLAYER:
                System.out.println("INSIDE CPU_PLAYER");
                for (String s : _knowledgeFiles) {
                    System.out.println(s);
                }
                System.out.println("Cpu level: " + _cpuDifficulty);
                break;
            case CPU_CPU:
                break;
        }
    }

    /// @brief Builds the board
    /// @pre ---
    /// @post Builds the board with the given information
    private Parent buildBoard(Chess chess) {
        Image whiteTile = null;                 // White tile image
        Image blackTile = null;                 // Black tile image

        try {
            whiteTile = new Image(
                new FileInputStream(
                    DEF_IMG_LOCATION + DEF_WHITE_TILE_LOCATION
                )
            );
            blackTile = new Image(
                new FileInputStream(
                    DEF_IMG_LOCATION + DEF_BLACK_TILE_LOCATION
                )
            );
        } catch (FileNotFoundException e) {
            displayErrorPopUp(
                "FATAL ERROR",
                "Could not find the board images."    
            );
            System.out.println("FATAL ERROR");
            System.out.println("IMAGES NOT FOUND");
            System.out.println("To solve this problem, check if the images are in the default folder.");
            System.out.println("If not, download the game again.");
            System.exit(-1);
        }
        
        Pane background = new Pane();
        /// Minimum values = 4 * IMG_PIXELS
        /// Maximum values = 16 * IMG_PIXELS
        background.setPrefSize(chess.cols() * IMG_PIXELS, chess.rows() * IMG_PIXELS);
        /// Initialise variables and set to parent
        _tiles = new Group();
        _pieces = new Group();
        background.getChildren().addAll(_tiles, _pieces);
        
        /// Start creating the board
        for (int i = 0; i < chess.rows(); i++) {
            for (int j = 0; j < chess.cols(); j++) {
                ImageTile img = null;
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        img = new ImageTile(whiteTile, IMG_PIXELS);
                    } else {
                        img = new ImageTile(blackTile, IMG_PIXELS);
                    }
                } else {
                    if (j % 2 == 0) {
                        img = new ImageTile(blackTile, IMG_PIXELS);
                    } else {
                        img = new ImageTile(whiteTile, IMG_PIXELS);
                    }
                }
                img.setX(j * IMG_PIXELS);
                img.setY(i * IMG_PIXELS);
                _tiles.getChildren().add(img);

                /// Check if there's a piece
                Piece pieceIn = chess.pieceAt(i, j);
                if (pieceIn != null) {
                    UIPiece piece = buildPiece(pieceIn, j, i);
                    _pieces.getChildren().add(piece);
                }
            }
        }

        return background;
    }

    /// @brief Constructs a UIPiece object
    /// @pre ---
    /// @post Returns a UIPiece with the given values and with the event
    ///       handling configuration
    private UIPiece buildPiece(Piece in, int x, int y) {
        UIPiece piece = new UIPiece(in, IMG_PIXELS, x, y);
        /// TODO: Handle events
        return piece;
    }

    /// @brief Displays an error pop up 
    /// @pre @p text is not empty
    /// @post Displays an error pop up with text until the users closes it
    private void displayErrorPopUp(String title, String text) {
        ItemBuilder.buildPopUp(
            title,
            text,
            true
        ).showAndWait();
    }

    /// @brief Allows the user to select a file and returns it
    /// @pre ---
    /// @post Returns the file selected for the user. If has not selected any, 
    ///       returns null
    private File fileSelector() {
        FileChooser fc = new FileChooser();
            fc.setInitialDirectory(
                new File(System.getProperty("user.dir"))
            );
        File selected = fc.showOpenDialog(_window);

        return selected;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        _window = primaryStage;

        _window.setWidth(900.0);
        _window.setHeight(650.0);

        buildMainScene();
    }
}