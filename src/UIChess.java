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
import javafx.scene.input.MouseEvent;
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
    private static final int IMG_PIXELS = 60;                       ///< Default images size (60x60)
    private static final double SPACER_PIXELS = 40.0;               ///< Default height of a spacer

    private static final String DEF_GAME_LOCATION = "data/configuration.json";    ///< Game default configuration location
    private static final String DEF_IMG_LOCATION = "data/img/";                   ///< Default image location
    private static final String DEF_WHITE_TILE_LOCATION = "w.png";                  ///< Default white tile image name
    private static final String DEF_BLACK_TILE_LOCATION = "b.png";                  ///< Default black tile image name

    private static final String SELECTED_CSS = "selected";          ///< Selected button CSS class
    private static final String UNSELECTED_CSS = "unselected";      ///< Unselected button CSS class

    /// Game Control Options
    private Stage _window;                                          ///< Main window of the applicatino
    private String _choosenConfigFile = null;                       ///< Configuration file location entered by the user
    private String _choosenGameFile = null;                         ///< Game file location if the user wants to load a game
    private int _cpuDifficulty = 2;                                 ///< CPU difficulty chosen by the user - intermediate by default
    private List<String> _knowledgeFiles = null;                    ///< Knowledge file names entered by the user
    private GameState _lastGameState = GameState.GAME_INIT;         ///< Game state before the current
    private GameType _gameType = null;                              ///< To know the selected game type
    private GameController _controller = null;                      ///< Game flow controller
    private Group _tiles = null;                                    ///< Group of tiles of the board
    private Group _pieces = null;                                   ///< Group of pieces of the board
    private List<Pair<Integer, UIPiece>> _deathPieces;              ///< Controls the death pieces and the turn in which they died
    private List<Pair<Integer, UIPiece>> _revivedPieces;            ///< Controls the revived pieces and the turn in which they were revived
    private boolean _blockPlayer = false;                           ///< To control when the player can move a piece

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
        _lastGameState = GameState.GAME_INIT;

        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("CHESS"));
        list.addAll(buildMenuButtons());
        VBox body = ItemBuilder.buildVBox(16.0, list, true, true);
        
        resetData();

        _window.setScene(ItemBuilder.buildScene(body));
        _window.show();
    }

    /// @brief Resets some of the atributes to their default
    /// @pre ---
    /// @post Resets some of the atributes to their default
    private void resetData() {
        _choosenConfigFile = null;
        _choosenGameFile = null;
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
            ItemBuilder.BtnType.EXIT,
            false
        );
        goBackButton.setOnAction(e -> {
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
            ItemBuilder.BtnType.PRIMARY,
            false
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
            ItemBuilder.BtnType.PRIMARY,
            false
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
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        loadGameButton.setOnAction(e -> {
            setSceneTitle("LOADING GAME");
            loadSavedGame();
        });
        list.add(loadGameButton);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS));

        Button exitGameButton = new Button();
        ItemBuilder.buildButton(
            exitGameButton,
            "EXIT",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.EXIT,
            false
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
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        playerVsPlayer.setOnAction(e -> {
            _gameType = GameType.PLAYER_PLAYER;
            setGameUp();
        });
        list.add(playerVsPlayer);

        Button cpuVsPlayer = new Button();
        ItemBuilder.buildButton(
            cpuVsPlayer, 
            "PLAYER VS COMPUTER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        cpuVsPlayer.setOnAction(e -> {
            _gameType = GameType.CPU_PLAYER;
            cpuConfiguration();
        });
        list.add(cpuVsPlayer);

        Button cpuVsCpu = new Button();
        ItemBuilder.buildButton(
            cpuVsCpu, 
            "COMPUTER VS COMPUTER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        cpuVsCpu.setOnAction(e -> {
            _gameType = GameType.CPU_CPU;
            cpuConfiguration();
        });
        list.add(cpuVsCpu);

        addGoBackButton(list);

        return list;
    }

    /// @brief Builds the buttons and events to load a file
    /// @pre ---
    /// @post Returns a collection with the buttons and buttons events to load a file
    ///       If @p savedGame is true, it will save a started game location. If false,
    ///       it will save a game configuration.
    private Collection<Node> buildLoadFileButton(boolean savedGame) {
        Collection<Node> list = new ArrayList<>();
        
        Button enterFileBtn = new Button();
        ItemBuilder.buildButton(
            enterFileBtn, 
            "ADD FILE", 
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY,
            false
        );   
        enterFileBtn.setOnAction(
            new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event) {
                    File selected = fileSelector();

                    if (selected != null) {
                        String file = selected.getPath();
                        
                        if (savedGame) {
                            _choosenGameFile = file;
                        } else {
                            _choosenConfigFile = file;
                        }

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
    private Collection<Node> buildCPUButtons() {
        Collection<Node> list = new ArrayList<>();

        Button beginnerBtn = new Button();
        ItemBuilder.buildButton(
            beginnerBtn, 
            "BEGINNER", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        beginnerBtn.getStyleClass().add(SELECTED_CSS);
        list.add(beginnerBtn);
        
        Button intermediateBtn = new Button();
        ItemBuilder.buildButton(
            intermediateBtn, 
            "INTERMEDIATE", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        intermediateBtn.getStyleClass().add(UNSELECTED_CSS);
        list.add(intermediateBtn);
                
        Button advancedBtn = new Button();
        ItemBuilder.buildButton(
            advancedBtn, 
            "ADVANCED", 
            MAX_BTN_WIDTH, 
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        advancedBtn.getStyleClass().add(UNSELECTED_CSS);
        list.add(advancedBtn);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS / 3));

        Button addKnowledgeBtn = new Button();
        ItemBuilder.buildButton(
            addKnowledgeBtn,
            "KNOWLEDGE",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.SECONDARY,
            false
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
                        for (File f : selected) {
                            files.add(f.getPath());
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
            "START GAME",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.ACCENT,
            false
        );
        continueBtn.setOnAction(e -> {
            setGameUp();
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
                _cpuDifficulty = 1;
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
                _cpuDifficulty = 2;
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
                _cpuDifficulty = 3;
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
            MAX_BTN_WIDTH / 2,
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        undoBtn.setOnAction(e -> handleUndo());
        list.add(undoBtn);

        Button redoBtn = new Button();
        ItemBuilder.buildButton(
            redoBtn,
            "REDO",
            MAX_BTN_WIDTH / 2,
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        redoBtn.setOnAction(e -> handleRedo());
        list.add(redoBtn);

        Button drawBtn = new Button();
        ItemBuilder.buildButton(
            drawBtn,
            "DRAW",
            MAX_BTN_WIDTH / 2,
            ItemBuilder.BtnType.PRIMARY,
            false
        );
        drawBtn.setOnAction(e -> handleDraw());
        list.add(drawBtn);

        list.add(ItemBuilder.buildSpacer(SPACER_PIXELS));

        Button surrenderBtn = new Button();
        ItemBuilder.buildButton(
            surrenderBtn,
            "SURRENDER",
            MAX_BTN_WIDTH / 2,
            ItemBuilder.BtnType.ACCENT,
            false
        );
        surrenderBtn.setOnAction(e -> handleSurrender());
        list.add(surrenderBtn);

        Button saveGameBtn = new Button();
        ItemBuilder.buildButton(
            saveGameBtn,
            "SAVE GAME",
            MAX_BTN_WIDTH / 2,
            ItemBuilder.BtnType.SECONDARY,
            false
        );
        saveGameBtn.setOnAction(e -> handleSaveGame());
        list.add(saveGameBtn);

        Button exitGameBtn = new Button();
        ItemBuilder.buildButton(
            exitGameBtn,
            "EXIT",
            MAX_BTN_WIDTH / 2,
            ItemBuilder.BtnType.EXIT,
            false
        );
        exitGameBtn.setOnAction(e -> {
            boolean res = buildConfirmationPopUp(
                "EXIT GAME", 
                "Do you want to save before leaving?"
            );
            
            if (res) {
                String fileName = _controller.saveGame("PARTIDA AJORNADA", false);
                savedGamePopUp(fileName);
            }
            resetToMainScene();
        });
        list.add(exitGameBtn);

        return list;
    }

    /// @brief Returns a button that executes a cpu movement
    /// @pre If game type is CPU_PLAYER, then the player must be blocked and the black cpu null.
    ///      If game type is CPU_CPU, the white cpu color must be white and, the black must be black
    /// @post Returns a button that executes a cpu movement. If the game type is 
    ///       PLAYER_CPU, the player will be unlocked to move in the end. If the game type is CPU_CPU
    ///       the move will be done by the cpu's whose color is the same as the current turn color
    private Button buildInGameCPUButton(Cpu white, Cpu black) {
        Button cpuButton = new Button();
        ItemBuilder.buildButton(
            cpuButton, 
            "NEXT TURN", 
            MAX_BTN_WIDTH / 2, 
            ItemBuilder.BtnType.ACCENT,
            false
        );
        cpuButton.setOnAction(e -> {
            System.out.println(_gameType.toString());
            if (isTurnOfCPU()) {
                Pair<Position, Position> move = null;
                if (_controller.currentTurnColor() == PieceColor.White || black == null) {
                    // If black equals null, means that the game type is CPU_PLAYER
                    move = white.doMovement();
                } else {
                    move = black.doMovement();
                }

                // Check movement
                Pair<List<MoveAction>, List<Position>> checkResult = _controller.checkCPUMovement(move.first, move.second);

                // Apply movement - always a correct movement
                List<MoveAction> result = null;
                UIPiece piece = getUIPieceAt(move.first);
                result = applyPieceMovement(piece, checkResult, move.first, move.second);
                
                _controller.cancellUndoes();
                
                // CPU movement will always be correct
                if (checkResult.first.contains(MoveAction.Castling)) {
                    // Case CPU does a castling move
                    _controller.saveCastlingTurn(checkResult.second);
                } else {	
                    // Saving turn
                    _controller.saveTurn(
                        result,
                        new Pair<String, String>(
                            move.first.toString(),
                            move.second.toString()
                        )
                    );

                    // Handle promotion of the CPU - automated
                    if (result.contains(MoveAction.Promote)) {
                        PieceType oldType = _controller.pieceAtCell(move.second).type();
                        _controller.promotePiece(move.second, _controller.mostValuableType());
                        _controller.savePromotionTurn(
                            _controller.currentTurnColor(),
                            oldType,
                            _controller.mostValuableType()
                        );

                        // Promote in UI
                        piece.promoteType(_controller.pieceAtCell(move.second));
                    }
                }

                if (result.contains(MoveAction.Escacimat)) {
                    handleEndOfGame();
                }

                _controller.toggleTurn();

                if (_gameType == GameType.CPU_PLAYER) {
                    _blockPlayer = false;
                }
            } else {
                System.out.println("Player turn");
            }
        });
        
        return cpuButton;
    }

    private PieceType buildPromotionPopUp() {
        return ItemBuilder.buildPromotionPopUp(
            "PROMOTION", 
            "Choose the type to promote to",
            _controller.promotableTypes()
        );
    }

    // IN-GAME HANDLING OPTIONS
    /// @brief Function that handles de logic of a promotion
    /// @pre There is a piece in @p piecePosition
    /// @post Promotes the piece in @p piecePosition both in the UI and the chess
    private void handlePromotion(Position piecePosition) {
        // Let the user choose
        PieceType promoted = buildPromotionPopUp();

        // Save promotion turn
        _controller.savePromotionTurn(
            _controller.currentTurnColor(),
            _controller.pieceAtCell(piecePosition).type(),
            promoted
        );

        // Update in chess
        _controller.promotePiece(piecePosition, promoted);
        
        // Update in the UI
        UIPiece tempPiece = getUIPieceAt(piecePosition);
        tempPiece.promoteType(_controller.pieceAtCell(piecePosition));
    }

    /// @brief Function that handles the event of the undo button
    /// @pre The user pressed the undo button
    /// @post If possible, undoes one movement. If a pieces were killed in that movement, 
    ///       all of them revive now and are deleted from the death list
    private void handleUndo() {
        if (!_controller.canUndo()) {
            ItemBuilder.buildPopUp(
                "UNDO ERROR", 
                "There are no movements to be undone!",
                true
            ).showAndWait();
        } else {
            // Check the turn type
            Turn lastTurn = _controller.lastTurn();
            if (lastTurn.isCastlingTurn()) {
                handleCastlingUndo(lastTurn);
            } else if (lastTurn.isPromotionTurn()) {
                handlePromotionUndo(lastTurn);
            } else {
                handleDefaultUndo();
            }

            _controller.toggleTurn();
            // Check if player has to be blocked or unblocked
            if (_gameType == GameType.CPU_PLAYER) {
                if (_controller.currentTurnColor() == PieceColor.Black) {
                    // Player has to be blocked
                    _blockPlayer = true;
                } else {
                    // Player has to be unblocked
                    _blockPlayer = false;
                }
            }
        }
    }

    /// @brief Default undo action handling
    /// @pre The turn to undo is a normal turn
    /// @post Applies the default undo logic: revives all dead pieces during that turn and
    ///       moves piece back to its original position
    private void handleDefaultUndo() {
        // Get last movement <Origin, Destination>
        Pair<Position, Position> temp = _controller.lastMovement();
    
        // Undo from chess
        _controller.undoMovement();

        // Undo in the UI
        UIPiece toChange = getUIPieceFromPiece(_controller.pieceAtCell(temp.first));
        toChange.move(temp.first.col(), temp.first.row());

        // Add pieces that died in the turn to the UI
        List<Pair<Integer, UIPiece>> listToDelete = new ArrayList<>();
        for (Pair<Integer, UIPiece> p : _deathPieces) {
            if (p.first == _controller.turnNumber()) {
                _pieces.getChildren().add(p.second);
                listToDelete.add(p);
            }
        }
        _deathPieces.removeAll(listToDelete);
        _revivedPieces.addAll(listToDelete);
    }

    /// @brief Undoes a turn that is a castling turn
    /// @pre The turn to undo is a castling turn
    /// @post Resets both pieces moved during the castling to its original positions
    private void handleCastlingUndo(Turn castling) {
        // Undo from chess
        _controller.undoMovement();

        // <SecondOrigin, FirstOrigin> <SecondDestination, FirstDestination>
        Pair<Pair<Position, Position>, Pair<Position, Position>> move = castling.castlingAsPair();

        // Move first piece 
        UIPiece firstPiece = getUIPieceAt(move.second.first);
        firstPiece.move(move.first.first.col(), move.first.first.row());
        // Move second piece
        UIPiece secondPiece = getUIPieceAt(move.second.second);
        secondPiece.move(move.first.second.col(), move.first.second.row());
        // No need to check for dead pieces
    }

    /// @brief Undoes a turn that is a promotion turn
    /// @pre The turn to undo is a promotion turn
    /// @post Moves the piece back to its position and resets the type to the original
    private void handlePromotionUndo(Turn promotion) {
        // Get last movement to know which piece was moved <Origin, Destination>
        Pair<Position, Position> move = _controller.lastMovement();

        // Undo from chess
        _controller.undoMovement();

        // Get the piece and change it's values
        UIPiece tempPiece = getUIPieceAt(move.second);
        Piece promoted = _controller.pieceAtCell(move.first);
        tempPiece.promoteType(promoted);

        // Move the piece to its original position
        tempPiece.move(move.first.col(), move.first.row());
    }

    /// @brief Function thant handles the event of the redo button
    /// @pre The user pressed the draw button
    /// @post If possible, redoes the movement. Applies the movement and kills all the
    ///       pieces that died due to the move
    private void handleRedo() {
        if (!_controller.canRedo()) {
            ItemBuilder.buildPopUp(
                "REDO ERROR", 
                "There are no movements to be redone!",
                true
            ).showAndWait();
        } else {
            // Redo from chess
            _controller.redoMovement();

            // Check the turn type
            Turn lastTurn = _controller.lastTurn();
            if (lastTurn.isCastlingTurn()) {
                handleCastlingRedo(lastTurn);
            } else if (lastTurn.isPromotionTurn()) {
                handlePromotionRedo(lastTurn);
            } else {
                handleDefaultRedo();
            }

            _controller.toggleTurn();

            // Check if player has to be blocked or unblocked
            if (_gameType == GameType.CPU_PLAYER) {
                if (_controller.currentTurnColor() == PieceColor.Black) {
                    // Player has to be blocked
                    _blockPlayer = true;
                } else {
                    // Player has to be unblocked
                    _blockPlayer = false;
                }
            }
        }
    }

    /// @brief Default redo action handling
    /// @pre The turn to redo is a normal turn
    /// @post Applies the default redo logic: kills all pieces killed during that turn and
    ///       moves piece back to its desitination position
    private void handleDefaultRedo() {
        // Get last undone movement <Origin, Destination>
        Pair<Position, Position> temp = _controller.lastMovement();

        // Redo in the UI 
        // Move piece
        UIPiece toChange = getUIPieceFromPiece(_controller.pieceAtCell(temp.second));
        toChange.move(temp.second.col(), temp.second.row());
        // Kill revived
        List<Pair<Integer, UIPiece>> listOfRevived = new ArrayList<>();
        List<Position> listToKill = new ArrayList<>();
        for (Pair<Integer, UIPiece> p : _revivedPieces) {
            if (p.first == _controller.turnNumber() - 1) {
                listToKill.add(
                    new Position(
                        boardPosition(p.second.oldY()),
                        boardPosition(p.second.oldX())
                    )
                );
                listOfRevived.add(p);
            }
        }
        killPieces(listToKill, _controller.turnNumber() - 1);
        _revivedPieces.removeAll(listOfRevived);
    }

    /// @brief Redoes a turn that is a castling turn
    /// @pre The turn to redo is a castling turn
    /// @post Resets both pieces moved during the castling to its destination positions
    private void handleCastlingRedo(Turn castling) {
        // Extract the castling information
        // <SecondOrigin, FirstOrigin> <SecondDestination, FirstDestination>
        Pair<Pair<Position, Position>, Pair<Position, Position>> move = castling.castlingAsPair();

        // Move first piece
        UIPiece firstPiece = getUIPieceAt(move.first.first);
        firstPiece.move(move.second.first.col(), move.second.first.row());
        // Move second piece
        UIPiece secondPiece = getUIPieceAt(move.first.second);
        secondPiece.move(move.second.second.col(), move.second.second.row());
        // No need to check for dead pieces
    }
    
    /// @brief Redoes a turn that is a promotion turn
    /// @pre The turn to redo is a promotion turn
    /// @post Moves the piece back to its destination position and changes the type
    ///       to the choosen to promote
    private void handlePromotionRedo(Turn promotion) {
        // The last turn is an empty turn, since it contains the promotion information
        // Get the last non-empty turn, which will be the one that contains the movement
        // <Origin, Destination>
        Pair<Position, Position> move = _controller.lastNotEmptyTurn().moveAsPair();

        // Move and promote the piece
        UIPiece tempPiece = getUIPieceAt(move.first);
        tempPiece.promoteType(_controller.pieceAtCell(move.second));

        // Move the piece
        tempPiece.move(move.second.col(), move.second.row());
    }

    /// @brief Function that handles the event of the draw button
    /// @pre The user pressed the draw button
    /// @post Saves an empty turn and displays a confirmation pop up. The user then
    ///       has to choose whether he accepts the draw or not. If so, it ends the game
    ///       with a draw
    private void handleDraw() {
        PieceColor currColor = _controller.currentTurnColor();
        // Save action
        _controller.saveEmptyTurn("TAULES SOL·LICITADES", currColor);

        // Message to display
        if (_gameType == GameType.PLAYER_PLAYER) {
            boolean res = buildConfirmationPopUp(
                "DRAW",
                currColor.toString() + " asks for a draw.\nAccept?"
            );
    
            // Response
            if (res) {
                // End of game
                _controller.saveEmptyTurn("TAULES ACCEPTADES", oppositeColor(_controller.currentTurnColor()));
                String fileName = _controller.saveGame("TAULES", false);
                savedGamePopUp(fileName);
                resetToMainScene();
            }
        } else {
            ItemBuilder.buildPopUp(
                "NEVER SURRENDER! 🤖", 
                "Keep in mind that the a robot never surrenders!", 
                true
            ).showAndWait();
        }
    }

    /// @brief Function that handles the end of a game
    /// @pre Last move result is check mate 
    /// @post One of the players has done a check mate to the other. The game is finished
    ///       and asked if wanted to be saved. If so, saves the game
    private void handleEndOfGame() {
        boolean res = buildConfirmationPopUp(
            _controller.currentTurnColor().value() + " WINS!",
            _controller.currentTurnColor().value() + " WINS! \nDo you want to save the game?"
        );

        if (res) {
            String fileName = _controller.saveGame("ESCAC I MAT", false);
            savedGamePopUp(fileName);
        }

        resetToMainScene();
    }

    /// @brief Function that handles the event of saving the game button
    /// @pre ---
    /// @post Saves the game and goes back to the main scene
    private void handleSaveGame() {
        String fileName = _controller.saveGame("PARTIDA AJORNADA", false);
        if (fileName != null) {
            savedGamePopUp(fileName);
            boolean res = buildConfirmationPopUp(
                "CONTINUE PLAYING",
                "Do you want tot continue playing?"
            );
    
            if (!res) {
                // Get back to the menu
                resetToMainScene();
            }
        } else {
            displayErrorPopUp(
                "ERROR", 
                "There was an error saving the file.\nTry again.\nIf the error persists, talk to the developers."
            );
        }
    }

    /// @brief Function that handles the event of a player surrendering
    /// @pre ---
    /// @post Saves the game and goes back to the main scene
    private void handleSurrender() {
        boolean res = buildConfirmationPopUp(
            "Are you sure?",
            "Do you really want to surrender?"
        );

        if (res) {
            _controller.saveEmptyTurn("RENDICIÓ", _controller.currentTurnColor());
            PieceColor loser = _controller.currentTurnColor();
            _controller.toggleTurn();

            ItemBuilder.buildPopUp(
                "SURRENDER", 
                loser.value() + " has surrendered!\n" + _controller.currentTurnColor().value() + " wins!",
                true
            ).showAndWait();

            handleEndOfGame();
        }
    }

    /// @brief Displays the main scene
    /// @pre ---
    /// @post Changes the current scene and builds the main scene. It also
    ///       resets the default window size
    private void resetToMainScene() {
        defaultWindowSize();
        buildMainScene();
    }

    /// @brief Displays a pop up showing the saved game file name
    /// @pre No errors on saving the game
    /// @post Displays a pop up showing the saved game file name
    private void savedGamePopUp(String fileName) {
        ItemBuilder.buildPopUp(
            "SAVED GAME",
            "Saved game with name: \n" + fileName, 
            true
        ).showAndWait();
    }

    /// @brief Builds a yes/no popup, shows it and returns the response
    /// @pre ---
    /// @post Builds a yes/no popup displaying the given message and returns the
    ///       user response as a boolean (true = yes, false = no)
    private boolean buildConfirmationPopUp(String title, String message) {
        return ItemBuilder.buildConfirmationPopUp(title, message);
    }

    /// @brief Function that displays the game options scene
    /// @pre ---
    /// @post Displays the game options and loads the desired game mode
    private void gameOptions() {
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("CHOOSE A GAME MODE"));
        list.addAll(buildOptionButtons());
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true, true));
        _window.setScene(s);
    }

    /// @brief Function that displays the preconfigured game options scene
    /// @pre ---
    /// @post Once the file it is entered, goes to the game options
    private void preconfiguredGame() {        
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("CONFIGURED GAME"));
        list.addAll(buildLoadFileButton(false));
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true, true));
        _window.setScene(s);
    }

    /// @brief Function that displays the load a saved game scene
    /// @pre --
    /// @post Once the file is entered, loads the game options
    private void loadSavedGame() {
        Collection<Node> list = new ArrayList<>(); 
        list.add(ItemBuilder.buildTitle("LOAD A GAME"));
        list.addAll(buildLoadFileButton(true));
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true, true));
        _window.setScene(s);
    }

    /// @brief Handles the configuration of the CPU by the user
    /// @pre @p gameType is @p CPUvsPlayer or @p CPUvsCPU
    private void cpuConfiguration() {
        _lastGameState = GameState.GAME_MODE;
        
        Collection<Node> list = new ArrayList<>();
        list.add(ItemBuilder.buildTitle("DEFINE THE \n COMPUTER"));
        list.addAll(buildCPUButtons());
        Scene s = ItemBuilder.buildScene(ItemBuilder.buildVBox(16.0, list, true, true));
        _window.setScene(s);
    }

    /// @brief Starts the type of game the user has choosen
    /// @pre ---
    /// @post Loads the chess from the file entered (if null, the default) and
    ///       configures the game to be played (cpu, knowledge and what's needed)
    private void setGameUp() {
        // On any fatal loading error, the application will exit
        try {
            _deathPieces = new ArrayList<>();
            _revivedPieces = new ArrayList<>();
            if (_choosenGameFile != null) {
                _controller = new GameController(_choosenGameFile, true);
                // Load deaths
                loadSavedGameDeaths();
            } else if (_choosenConfigFile != null) {
                // Load saved game
                _controller = new GameController(_choosenConfigFile, false);
            } else {
                _controller = new GameController(DEF_GAME_LOCATION, false);
            }
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

        // Set scene title
        setSceneTitle("CHESS");

        // Build the interface
        switch (_gameType) {
            case PLAYER_PLAYER:
                // Buttons
                VBox buttons = ItemBuilder.buildVBox(12.0, buildInGameButtons(), true, true);
                buttons.setPrefWidth(300);
                _window.setScene(buildGameScene(buttons));
                _window.sizeToScene();
                break;
            case CPU_PLAYER:
                playerCPUGame();
                break;
            case CPU_CPU:
                twoCPUsGame();
                break;
        }
    }

    /// @brief Builds the board
    /// @pre ---
    /// @post Builds the board with the given information
    private Parent buildBoard() {
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
        // Minimum values = 4 * IMG_PIXELS
        // Maximum values = 16 * IMG_PIXELS
        background.setPrefSize(_controller.cols() * IMG_PIXELS, _controller.rows() * IMG_PIXELS);
        // Initialise variables and set to parent
        _tiles = new Group();
        _pieces = new Group();
        background.getChildren().addAll(_tiles, _pieces);
        
        // Start creating the board
        for (int i = 0; i < _controller.rows(); i++) {
            for (int j = 0; j < _controller.cols(); j++) {
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
                Position temp = new Position(i, j);
                _tiles.getChildren().add(img);

                // Check if there's a piece
                Piece pieceIn = _controller.pieceAtCell(temp);
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

        // EVENTS
        piece.setOnMousePressed(
            (MouseEvent m) -> {
                if (_controller.currentTurnColor() == piece.color() && !_blockPlayer) {
                    piece.setMouseX(m.getSceneX());
                    piece.setMouseY(m.getSceneY());
                } else {
                    System.out.println("Turn of: " + _controller.currentTurnColor().value());
                }
            }
        );

        piece.setOnMouseDragged(
            (MouseEvent m) -> {
                if (_controller.currentTurnColor() == piece.color() && !_blockPlayer) {
                    piece.relocate(m.getSceneX(), m.getSceneY());
                } 
            }
        );

        piece.setOnMouseReleased(
            (MouseEvent m) -> {
                if (_controller.currentTurnColor() == piece.color() && !_blockPlayer) {
                    // Origin
                    int oX = boardPosition(piece.oldX());
                    int oY = boardPosition(piece.oldY());
                    Position origin = new Position(oY, oX);
                    // Destination
                    int dX = boardPosition(piece.getLayoutX());
                    int dY = boardPosition(piece.getLayoutY());
                    Position dest = new Position(dY, dX);

                    // Check if valid movement
                    Pair<List<MoveAction>, List<Position>> checkResult = _controller.checkPlayerMovement(origin, dest);
                    if (checkResult.first.contains(MoveAction.Correct)) {
                        // Correct movement
                        _controller.cancellUndoes();                                // Cancelling undoes
                        List<MoveAction> actions = applyPieceMovement(piece, checkResult, origin, dest);       // Applying movment

                        if (actions == null) {
                            displayErrorPopUp(
                                "GOD SAVE THE KING",
                                "Your king is in danger. You have to protect him!"    
                            );
                            // Reset move
                            piece.move(origin.col(), origin.row());
                        } else {
                            if (checkResult.first.contains(MoveAction.Castling)) {
                                // Save castling turn
                                _controller.saveCastlingTurn(checkResult.second);

                                // Result of castling may be a checkmate
                                if (actions.contains(MoveAction.Escacimat)) {
                                    handleEndOfGame();
                                }
                            } else {
                                // Save normal turn
                                _controller.saveTurn(
                                    actions, 
                                    new Pair<String, String> (
                                        origin.toString(),
                                        dest.toString()
                                    )
                                );

                                if (actions.contains(MoveAction.Promote)) {
                                    handlePromotion(dest);
                                }
                            }

                            if (actions.contains(MoveAction.Escacimat)) {
                                handleEndOfGame();
                            }

                            _controller.toggleTurn();
                                    
                            // Block the user
                            if (_gameType == GameType.CPU_PLAYER) {
                                _blockPlayer = true; 
                            }
                        }
                    } else {
                        piece.cancelMove();
                    }
                }
            }
        );

        return piece;
    }

    /// @brief Builds the game scene
    /// @pre ---
    /// @post Builds the game scene and sets the given buttons to the right of the layout
    private Scene buildGameScene(Node buttons) {
        // Set scene
        return new Scene(
            ItemBuilder.buildBorderPane(
                buildBoard(), 
                null,
                null, 
                buttons,
                null
            )
        );
    }

    /// @brief Initiates and controls a player vs cpu game
    /// @pre Game type choose is @c PLAYER_CPU 
    /// @post The game ends from a saved game or a winner
    private void playerCPUGame() {
        // Build the cpu
        Cpu cpu = null;

        // Read the knowledge, if there is
        if (_knowledgeFiles != null) {
            List<Pair<List<Turn>, PieceColor>> knowledgeList = new ArrayList<>();
            for (String location : _knowledgeFiles) {
                try {
                    knowledgeList.add(_controller.readKnowledge(location));
                } catch (FileNotFoundException e) {
                    System.err.println("File [" + location + "] not found.");
                } catch (JSONParseFormatException e) {
                    displayErrorPopUp(
                        e.getType(),
                        "The developement file contains an illegal format. \nCheck it and try again."
                    );
                    System.exit(-1);
                }
            }
            cpu = new Cpu(
                new Knowledge(knowledgeList, _controller.chess()),
                _controller.chess(),
                _cpuDifficulty,
                PieceColor.Black
            );
        } else {
            cpu = new Cpu(
                null, 
                _controller.chess(), 
                _cpuDifficulty, 
                PieceColor.Black
            );
        }

        // Create cpu adapted menu
        ArrayList<Node> buttons = new ArrayList<>();
        buttons.addAll(buildInGameButtons());
        buttons.add(buttons.size() - 4, buildInGameCPUButton(cpu, null));

        // Create layout
        VBox layout = ItemBuilder.buildVBox(12.0, buttons, true, true);
        layout.setPrefWidth(300);
        _window.setScene(buildGameScene(layout));
        _window.sizeToScene();
    }

    /// @brief Initiates and controls a cpu vs cpu game
    /// @pre Game type choose is @c CPU_CPU 
    /// @post The game ends from a saved game or a winner
    private void twoCPUsGame() {
        // Build the cpu
        Cpu white = null;
        Cpu black = null;

        // Read the knowledge, if there is
        if (_knowledgeFiles != null) {
            List<Pair<List<Turn>, PieceColor>> knowledgeList = new ArrayList<>();
            for (String location : _knowledgeFiles) {
                try {
                    knowledgeList.add(_controller.readKnowledge(location));
                } catch (FileNotFoundException e) {
                    System.err.println("File [" + location + "] not found.");
                } catch (JSONParseFormatException e) {
                    displayErrorPopUp(
                        e.getType(),
                        "The developement file contains an illegal format. \nCheck it and try again."
                    );
                    System.exit(-1);
                }
            }
            white = new Cpu(
                new Knowledge(knowledgeList, _controller.chess()),
                _controller.chess(),
                _cpuDifficulty,
                PieceColor.White
            );
            black = new Cpu(
                new Knowledge(knowledgeList, _controller.chess()),
                _controller.chess(),
                _cpuDifficulty,
                PieceColor.Black
            );
        } else {
            white = new Cpu(
                null,
                _controller.chess(),
                _cpuDifficulty,
                PieceColor.White
            );
            black = new Cpu(
                null,
                _controller.chess(),
                _cpuDifficulty,
                PieceColor.Black
            );
        }

        // Create cpu adapted menu
        ArrayList<Node> buttons = new ArrayList<>();
        buttons.addAll(buildInGameButtons());
        buttons.add(buttons.size() - 4, buildInGameCPUButton(white, black));

        // Create layout
        VBox layout = ItemBuilder.buildVBox(12.0, buttons, true, true);
        layout.setPrefWidth(300);
        _window.setScene(buildGameScene(layout));
        _window.sizeToScene();
    }

    /// @brief Loads to the death list the pieces that died while loading the game
    /// @pre ---
    /// @post Gets the death list from the controller and adds it to the in-game lists
    private void loadSavedGameDeaths() {
        List<Pair<Piece, Pair<Position, Integer>>> deaths = _controller.loadingGameDeaths();
        // <Position of death, turn of death>
        if (deaths != null) {
            for (Pair<Piece, Pair<Position, Integer>> death : deaths) {
                UIPiece temp = buildPiece(
                    death.first,                // Piece 
                    death.second.first.col(),   // Position of death x
                    death.second.first.row()    // Position of death y
                );
    
                // Instead of saving it to _pieces, we add it to the death list with the turn number 
                _deathPieces.add(
                    new Pair<Integer,UIPiece>(death.second.second, temp)
                );
            }
        }
    }

    /// @brief Function that handles the event of a UIPiece killing another
    /// @pre The movement has been check and it is correct
    /// @post Applies the movement to the chess and calculates, if there has, which are the
    ///       pieces that got killed by that move. It removes them from the UI and addds them in
    ///       the death list. If the function is called to apply a redone movement
    private List<MoveAction> applyPieceMovement(UIPiece piece, Pair<List<MoveAction>, List<Position>> moveResult, Position origin, Position dest) {
        // Apply to chess        
        List<MoveAction> result = _controller.applyPlayerMovement(origin, dest, moveResult.second);
        
        // Check if castling
        if (moveResult.first.contains(MoveAction.Castling)) {
            // Both pieces have to be moved
            Position tempPos = moveResult.second.get(2); 
            // Get the destination piece
            UIPiece tempPiece = getUIPieceFromPiece(
                _controller.pieceAtCell(tempPos)
            );

            // Apply move to the destination piece
            tempPiece.move(tempPos.col(), tempPos.row());
            // Apply move to the origin piece
            tempPos = moveResult.second.get(3);
            piece.move(tempPos.col(), tempPos.row());
        } else {
            piece.move(dest.col(), dest.row());
            // Check if killed
            if (!moveResult.second.isEmpty()) {
                killPieces(moveResult.second, _controller.turnNumber());            
            }
        }


        return result;
    }

    /// @brief Kills all the pieces from the list
    /// @pre ---
    /// @post Removes from the UI all the pieces from the list and adds them to the death list
    private void killPieces(List<Position> list, int turn) {
        UIPiece temp = null;
        for (Position p : list) {
            // Can kill more than one piece
            for (Node n : _pieces.getChildren()) {
                // Saving the reference to the piece to delete
                temp = (UIPiece) n;
                if (pieceInPosition(temp, p) && temp.color() != _controller.currentTurnColor()) {
                    break;
                }
            }
            // We keep the reference
            _deathPieces.add(
                new Pair<Integer, UIPiece>(turn, temp)
            );
            _pieces.getChildren().remove(temp);
        }
    }

    /// @brief Returns the opposite color of @p color
	/// @pre ---
	/// @post Returns the opposite color of @p color
	private static PieceColor oppositeColor(PieceColor color) {
		return color == PieceColor.White 
			? PieceColor.Black
			: PieceColor.White;
	}

    /// @brief Calculates the board y position of a given value
    /// @pre @p value >= 0
    /// @post Returns the equivalent position of a given y coordinate from the screen
    private int boardPosition(double value) {
        return (int) value / IMG_PIXELS;
    }

    /// @brief Returns if a graphic piece is in a given position of the board
    /// @pre @p piece && @p position != null
    /// @post Returns true if a UIPiece is in the given position of the board
    private boolean pieceInPosition(UIPiece piece, Position position) {
        return boardPosition(piece.oldX()) == position.col() &&
                boardPosition(piece.oldY()) == position.row();
    }

    /// @brief Returns if a graphic cell is in a given position of the board
    /// @pre @p cell && @p position != null
    /// @post Returns true if a ImageTile is in the given position of the board
    private boolean cellInPosition(ImageTile img, Position position) {
        return boardPosition(img.getX()) == position.col() &&
                boardPosition(img.getY()) == position.row();
    }

    /// @brief Returns a UIPiece that contains the piece given
    /// @pre @p piece != null
    /// @post Returns the UIPiece that contains an equal piece to the given on
    private UIPiece getUIPieceFromPiece(Piece piece) {
        UIPiece result = null;
        
        for (Node temp : _pieces.getChildren()) {
            result = (UIPiece) temp;

            if (result.piece().equals(piece)) {
                break;
            }
        }

        return result;
    }

    /// @brief To know if the CPU can move
    /// @pre ---
    /// @post Returns true if the CPU can execute a move
    private boolean isTurnOfCPU() {
        return (
            _gameType == GameType.CPU_CPU ||
            (_gameType == GameType.CPU_PLAYER && _controller.currentTurnColor() == PieceColor.Black)
        );
    }

    /// @brief To know what UIPiece is held in a position of the chess
    /// @pre ---
    /// @post Returns the UIPiece which is in the position @p p of the chess
    public UIPiece getUIPieceAt(Position p) {
        UIPiece result = null;

        for (Node temp : _pieces.getChildren()) {
            result = (UIPiece) temp;

            if (pieceInPosition(result, p)) {
                break;
            }
        }

        return result;
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

    /// @brief Sets the window to the default size
    /// @pre ---
    /// @post Sets the window with 800px to and the height to 650px
    private void defaultWindowSize() {
        _window.setWidth(800.0);
        _window.setHeight(650.0);
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

        // Set stage properties
        defaultWindowSize();
        //_window.setResizable(false);

        buildMainScene();
    }
}
