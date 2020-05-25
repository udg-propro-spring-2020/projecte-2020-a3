import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/// @author Miquel de Domingo i Giralt
/// @file GameController.java
/// @class GameController
/// @brief Controls the flow of the game independetly of the interface
/// @details Class created to abstract the information related to the game flow such as
///          turn control, turn management, game loading, game saving and others
public class GameController {
    /// GAME CONTROL VARIABLES
    private String _defaultConfigFileName = null;   ///< Keeps the configuration file name
    private PieceColor _currTurnColor = null;       ///< Current turn color
    private Integer _turnNumber = null;             ///< Current turn number
    private Integer _undoCount = null;              ///< Amount of consequent undo movements
    private List<Turn> _turns = null;               ///< List of turns
    private boolean _dataSet = false;               ///< To know if the data has been initialized
    private Chess _chess = null;                    ///< Game chess
    ///< Holder <Piece, Position, TurnNumber> of the pieces that died when loading a saved game
    private List<Pair<Piece, Pair<Position, Integer>>> _loadedGameDeaths = null;
    private boolean _fromSavedGame = false;             ///< To know if the game is loaded from a saved game
    private boolean _inactiveResetOnWhites = false;     ///< To know if the inactive count has been reset on a black turn

    // CONSTANTS
    private static String SAVED_GAMES_LOCATION = "./saved_games/";      ///< Saved games directory
    private static int INACTIVE_THRESHOLD = 40;                         ///< Inactive turns threshold

    /// @brief Builds a game controller for a chess with the given file location
    /// @details If saved game is true, it will apply all the turns that are saved
    ///          in the game flow file
    public GameController(String fileLocation, boolean savedGame)
            throws FileNotFoundException, JSONParseFormatException {
        if (savedGame) {
            _fromSavedGame = true;
            loadSavedGameToChess(fileLocation);
        } else {
            _fromSavedGame = false;
            loadChess(fileLocation);
        }
    }

    // ! CONSTRUCTOR HELPERS
    /// @brief Loads the game from a configuration file
    /// @pre @p fileLocation != null
    /// @post Loads the game from a configuration file
    private void loadChess(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        initiateData();
        _defaultConfigFileName = fileLocation;
        _chess = FromJSONParserHelper.buildChess(fileLocation);
    }

    /// @brief Loads the game data from a saved game
    /// @pre @p fileLocation != @c null
    /// @post Loads the game data from a saved game
    private void loadSavedGameToChess(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        // Save configuration file name
        _defaultConfigFileName = FromJSONParserHelper.getConfigurationFileName(fileLocation);

        // Retrieve match information
        _chess = FromJSONParserHelper.buildSavedChessGame(fileLocation);
        Pair<List<Turn>, PieceColor> info = FromJSONParserHelper.matchInformation(fileLocation, mapOfPieceTypes(),
                false);
        List<Turn> loadedTurns = info.first;
        // White color always start
        _currTurnColor = PieceColor.White;

        initiateData();
        if (!loadedTurns.isEmpty()) {
            Turn lastTurn = null;
            for (Turn t : loadedTurns) {
                if (t.isPromotionTurn()) {
                    if (lastTurn == null) {
                        // If nothing has moved, there cannot be a promotion
                        throw new JSONParseFormatException(
                            "First turn cannot be a promotion",
                            JSONParseFormatException.ExceptionType.ILLEGAL_PROMOTION
                        );
                    } else if (lastTurn.isPromotionTurn()) {
                        // Two promotions cannot be consequent
                        throw new JSONParseFormatException(
                            "A promotion cannot be followed by another promotion",
                                JSONParseFormatException.ExceptionType.ILLEGAL_PROMOTION
                            );
                    }

                    // <Original, Promoted> names
                    Pair<String, String> promoted = t.promotionAsPair();
                    Map<String, PieceType> typeMap = mapOfPieceTypes();

                    // Promote piece
                    _chess.promotePiece(
                        new Position(lastTurn.destination()),
                        typeMap.get(promoted.second)
                    );

                    // Save turn with the PieceTypes
                    savePromotionTurn(
                        t.color(),
                        typeMap.get(promoted.first),
                        typeMap.get(promoted.second)
                    );
                } else if (!t.isEmptyTurn()) {
                    Pair<Position, Position> temp = null;
                    if (t.isCastlingTurn()) {
                        // Castling movement has the original move in the first position of the pair
                        temp = t.castlingAsPair().first;
                    } else {
                        // Normal movement
                        temp = t.moveAsPair();
                    }

                    // Apply movements to the game
                    Pair<List<MoveAction>, List<Position>> checkResult = _chess.checkMovement(temp.first, temp.second);

                    // All movements must be right!
                    // Add to deaths list - only add if it is not a castling
                    if (!t.isCastlingTurn()) {
                        for (Position p : checkResult.second) {
                            _loadedGameDeaths.add(
                                new Pair<Piece, Pair<Position, Integer>>(
                                    _chess.pieceAt(p.row(), p.col()),           // Piece
                                    new Pair<Position, Integer>(p, _turnNumber) // Position - Turn
                                )
                            );
                        }
                    }

                    // Apply movement also checks for castling
                    List<MoveAction> actions = _chess.applyMovement(temp.first, temp.second, checkResult.second, false);

                    if (t.isCastlingTurn()) {
                        saveCastlingTurn(checkResult.second);
                    } else {
                        saveTurn(actions, new Pair<String, String>(temp.first.toString(), temp.second.toString()));
                    }

                    toggleTurn();
                } else {
                    // Current turn is empty turn
                    saveEmptyTurn(t.turnResult(), t.color());
                    toggleTurn();
                }

                lastTurn = t;
            }
        }
    }

    /// @brief Reads the match information from the file
    /// @pre @p list != null
    /// @post Returns a Pair containing the information extracted from the location file
    public Pair<List<Turn>, PieceColor> readKnowledge(String location) throws FileNotFoundException, JSONParseFormatException {
        return FromJSONParserHelper.matchInformation(location, mapOfPieceTypes(), true);
    }

    /// @brief Returns a map of the piece types
    /// @pre ---
    /// @post Creates a map of the piece types in which the key is their name
    public Map<String, PieceType> mapOfPieceTypes() {
        return FromJSONParserHelper.mapFromTypes(typeList());
    }

    // GAME MANIPULATION METHODS
    /// @brief Functions that initiates the class data
    /// @pre ---
    /// @post Initiates data if not has been set
    private void initiateData() {
        if (!_dataSet) {
            _currTurnColor = PieceColor.White; // White always start
            _turnNumber = 0;
            _undoCount = 0;
            _turns = new ArrayList<>();
            _loadedGameDeaths = new ArrayList<>();
            _dataSet = true;
        }
    }

    // ! IN-GAME METHODS
    /// @brief Checks and applies a player movement
    /// @pre ---
    /// @post Checks and applies a player movement. Returns the result of the
    //        movement (null if not valid)
    public Pair<List<MoveAction>, List<Position>> checkPlayerMovement(Position origin, Position destination) {
        Pair<List<MoveAction>, List<Position>> checkResult = _chess.checkMovement(origin, destination);

        return checkResult;
    }

    /// @brief Applies a player movement if possible and returns the result
    /// @pre @p origin && @p destination != null
    /// @post Applies a player movement if possible and returns the result. The result
    ///       will be null if the player has to defend the king
    public List<MoveAction> applyPlayerMovement(Position origin, Position destination, List<Position> list) {
        List<MoveAction> result = _chess.applyMovement(origin, destination, list, false);

        // Evaluate for check
        List<Pair<Position, Piece>> colorList = (_currTurnColor == PieceColor.White) 
                ? _chess.pListBlack()
                : _chess.pListWhite();

        if (_chess.isCheck(colorList)) {
            _chess.undoMovement();
            return null;
        }

        return result;
    }

    /// @brief Checks and applies a player movement
    /// @pre ---
    /// @post Checks and applies a player movement. Returns the result of the movement as
    ///       a String (null if not valid)
    public Pair<List<MoveAction>, List<Position>> checkCPUMovement(Position origin, Position destination) {
        Pair<List<MoveAction>, List<Position>> checkResult = _chess.checkMovement(origin, destination);

        return checkResult;
    }

    /// @brief Applies a cpu movement
    /// @pre @p origin && @p dest != null
    /// @post Applies the cpu movement to the chess and returns the list of MoveActions
    public List<MoveAction> applyCPUMovement(Position origin, Position destination, List<Position> list) {
        // CPU movement is always correct
        List<MoveAction> result = _chess.applyMovement(origin, destination, list, false);

        return result;
    }

    /// @brief Changes turn value
    /// @pre @p currTurnColor != null
    /// @post Changes currTurnValue to the oposite
    public void toggleTurn() {
        if (_currTurnColor == null) {
            throw new NullPointerException("ToggleTurn cannot toggle a null value");
        }

        _currTurnColor = (_currTurnColor == PieceColor.White) 
            ? PieceColor.Black 
            : PieceColor.White;
    }

    /// @brief Changes if the inactive limit was reseted on a white turn
    /// @pre ---
    /// @post Sets @p value to the inactive limit reset controller
    public void setInactiveResetOnWhites(boolean value) {
        _inactiveResetOnWhites = value;
    }

    /// @brief Saves turn information
    /// @pre @p p cannot be null
    /// @post Creates a new turn with the given movement and increments @p turnNumber
    public void saveTurn(List<MoveAction> results, Pair<String, String> p) {
        MoveAction res = null;
        if (results.contains(MoveAction.Checkmate)) {
            res = MoveAction.Checkmate;
        } else if (results.contains(MoveAction.Check)) {
            res = MoveAction.Check;
        }

        if (res == null) {
            _turns.add(new Turn(_currTurnColor, p, ""));
        } else {
            _turns.add(new Turn(_currTurnColor, p, res.toString()));
        }

        _turnNumber++;
    }

    /// @brief Saves empty turn
    /// @pre ---
    /// @post Adds a turn to the list containing only a result value
    public void saveEmptyTurn(String result, PieceColor color) {
        _turns.add(new Turn(color, result));
        _turnNumber++;
    }

    /// @brief Saves a promotion turn
    /// @pre ---
    /// @post Creates a promotion turn, with the current color and adds it to the list.
    ///       Turn number increases by one
    public void savePromotionTurn(PieceColor color, PieceType original, PieceType promoted) {
        _turns.add(new Turn(color, original, promoted));
        _turnNumber++;
    }

    /// @brief Saves a castling turn
    /// @pre list.size() == 4
    /// @post Creates a new castling type turn from the current color and adds it to the list.
    ///       Turn number increases by one
    public void saveCastlingTurn(List<Position> list) {
        // Origin string
        String auxOrigin = list.get(0).toString() + "-" + list.get(1).toString();

        // Destination string
        String auxDest = list.get(2).toString() + "-" + list.get(3).toString();

        // Saving the castling
        _turns.add(
            new Turn(_currTurnColor, new Pair<String, String>(auxOrigin, auxDest))
        );
        _turnNumber++;
    }

    /// @brief To know if a movement can be undone
    /// @pre ---
    /// @post Returns true if a movement can be undone. False otherwise
    public boolean canUndo() {
        return !(_turnNumber == 0);
    }

    /// @brief Undoes one movement
    /// @pre ---
    /// @post If possible, undoes one movement. It is only possible to undo
    /// if there has been one movement
    public boolean undoMovement() {
        if (!canUndo()) {
            /// Can't undo any movement
            return false;
        } else {
            /// Get current turn values
            _chess.undoMovement();

            // Previous turn
            if (canUndo() && _turns.get(_turnNumber - 1).isEmptyTurn()) {
                // Since will be an empty turn, we have to descrease once more
                _turnNumber--;
            }
            _turnNumber--;

            /// Increase undone movements
            _undoCount++;
            return true;
        }
    }

    /// @brief Decreases the undone movements count
    /// @pre ---
    /// @post If the undo count value is > 0, it will decrease it by 1
    public void decreaseUndoCount() {
        if (_undoCount > 0) {
            _undoCount--;
        }
    }

    /// @brief To know if a movement can be redone
    /// @pre ---
    /// @post Returns true if a movement can be redone. False otherwise
    public boolean canRedo() {
        return !(_undoCount == 0);
    }

    /// @brief Redoes one movement
    /// @pre turnNumber pointing after the last position of list
    /// @post If possible, redoes one movement. It is only possible to redo if
    ///       there has been at least one undone movement
    public boolean redoMovement() {
        if (!canRedo()) {
            return false;
        } else {
            // Get the current turn values
            _chess.redoMovement();

            // Next turn
            _turnNumber++;
            if (canRedo() && _turns.get(_turnNumber - 1).isEmptyTurn()) {
                // Since will be an empty turn, we have to increase once more
                _turnNumber++;
            }
            // Decrement the undone movements
            _undoCount--;
            return true;
        }
    }

    /// @brief Cancells all the undoes since a new move has been applied
    /// @pre ---
    /// @post Removes all the undone movements from the list and resets the undo count
    public void cancellUndoes() {
        // If the user has undone x movements, and not redone all of them
        // then the match mus continue from that and all the movements after the
        // current turn must be delelted.
        if (_undoCount > 0) {
            // turnNumber == turns.size() - undoCount
            for (int i = _turns.size() - 1; i >= _turnNumber; i--) {
                _turns.remove(i);
            }
            _undoCount = 0;
        }

    }

    /// @brief Saves the game in a file
    /// @pre ---
    /// @post Saves the game in two JSON files, pulling away the configuration and
    ///       the game developement. Returns the fileName or null if there's an error
    public String saveGame(String finalResult, boolean newConfigFile) {
        File gameFile = null;
        FileWriter gameWriter = null;
        try {
            // Configuration
            File configurationFile = new File(_defaultConfigFileName);

            if (newConfigFile) {
                configurationFile.createNewFile();
                FileWriter configWriter = new FileWriter(configurationFile);
                configWriter.write(ToJSONParserHelper.saveChessConfigToJSON(_chess));
                configWriter.close();
            }

            // Game
            createSavedGameDirectory();
            Long fileName = new Date().getTime();
            gameFile = new File(SAVED_GAMES_LOCATION + fileName.toString() + ".json");
            gameFile.createNewFile();

            gameWriter = new FileWriter(gameFile);
            List<Turn> turnsToSave = getTurnsToSave();
            gameWriter.write(
                ToJSONParserHelper.saveGameToJSON(
                    _chess, 
                    _defaultConfigFileName, 
                    _currTurnColor,
                    turnsToSave, 
                    finalResult
                )
            );
            gameWriter.close();

            return fileName.toString() + ".json";
        } catch (IOException e) {
            // First close the game writer
            try {
                gameWriter.close();
            } catch (IOException io) {
                System.err.println("Error when closing the game writer");
            }

            // Then delete the file
            gameFile.delete();
            return null;
        } catch (NullPointerException e) {
            // First close the game writer
            try {
                gameWriter.close();
            } catch (IOException io) {
                System.err.println("Error when closing the game writer");
            }

            // Then delete the file
            gameFile.delete();
            System.err.println(e.getMessage());
            return null;
        }
    }

    /// @brief To know the turns that have to be saved
    /// @pre ---
    /// @post Returns the turn that are from the 0 position to the current turn
    private List<Turn> getTurnsToSave() {
        return _turns.subList(0, _turnNumber);
    }

    /// @brief If not exists, creates a directory for the saved games
    /// @pre ---
    /// @post Creates a directory for the saved games if does not exist
    /// @throws IOException If an error occurs while trying to create a directory
    private void createSavedGameDirectory() throws IOException {
        File newDir = new File(SAVED_GAMES_LOCATION);
        if (!newDir.exists()) {
            if (!newDir.mkdir()) {
                throw new IOException("Fatal error on creating saved games folder.");
            }
        }
    }

    // ! METHODS TO RETRIEVE GAME INFORMATION
    /// @brief Returns the chess
    /// @pre ---
    /// @post Returns the chess
    public Chess chess() {
        return _chess;
    }

    /// @brief Returns the list of deaths occurred when loading a saved game
    /// @pre ---
    /// @post Returns the list of deaths occurred when loading a saved game. If game
    ///       controller was initialized not from a saved game, returns null
    public List<Pair<Piece, Pair<Position, Integer>>> loadingGameDeaths() {
        if (!_fromSavedGame) {
            return null;
        } else {
            return _loadedGameDeaths;
        }
    }

    /// @brief Returns the current turn color
    /// @pre ---
    /// @post Returns the current turn color
    public PieceColor currentTurnColor() {
        return _currTurnColor;
    }

    /// @brief Returns if the current turn is even
    /// @pre ---
    /// @post Returns true if the current turn is even. False otherwise
    public boolean evenTurn() {
        return _turnNumber % 2 == 0;
    }

    /// @brief Returns the turn number
    /// @pre ---
    /// @post Returns the turn number
    public int turnNumber() {
        return _turnNumber;
    }

    /// @brief Returns the configuration file of the game
    /// @pre ---
    /// @post Returns the configuration file of the game
    public String configurationFile() {
        return _defaultConfigFileName;
    }

    /// @brief To get the last turn of the game
    /// @pre ---
    /// @post Returns the last turn of the game. If there are no turns
    ///       returns null value
    public Turn lastTurn() {
        if (_turnNumber == 0) {
            return null;
        }

        return _turns.get(_turnNumber - 1);
    }

    /// @brief To get the last non-empty turn
    /// @pre ---
    /// @post Returns the last non-empty turn of the game. If there are no turns
    ///       returns a null value
    public Turn lastNotEmptyTurn() {
        if (_turnNumber == 0) {
            return null;
        }

        for (int i = _turnNumber - 1; i >= 0; i--) {
            Turn temp = _turns.get(i);
            if (!temp.isEmptyTurn()) {
                return temp;
            }
        }
        return null;
    }

    /// @brief To get the last movement of the game
    /// @pre ---
    /// @post Returns the last movement of the game. If there are no turns
    ///       returns null value
    public Pair<Position, Position> lastMovement() {
        if (_turnNumber == 0) {
            return null;
        }

        if (!_turns.get(_turnNumber - 1).isEmptyTurn()) {
            return new Pair<Position, Position>(
                new Position(_turns.get(_turnNumber - 1).origin()),
                new Position(_turns.get(_turnNumber - 1).destination())
            );
        } else {
            return new Pair<Position, Position>(
                new Position(_turns.get(_turnNumber - 2).origin()),
                new Position(_turns.get(_turnNumber - 2).destination())
            );
        }
    }

    /// @brief To get the last undone movement
    /// @pre @p undoCount > 0
    /// @post Returns the last movement undone
    public Pair<Position, Position> lastUndoneMovement() {
        // Last undone movement is the on at the _turnNumber value
        return new Pair<Position, Position>(
            new Position(_turns.get(_turnNumber).origin()),
            new Position(_turns.get(_turnNumber).destination())
        );
    }

    /// @brief To know if the last turn of the current color was a check
    /// @pre ---
    /// @post Returns true if the last turn of the current color was a check. False otherwise
    public boolean wasLastTurnCheck() {
        if (_turnNumber < 3) {
            return false;
        }

        // Get two turns before
        Turn t = _turns.get(_turnNumber - 3);
        return t.turnResult().equals(MoveAction.Check.toString());
    }

    /// @brief Returns if the players have reached the inactive limit
    /// @pre ---
    /// @post Returns true if the players have played as many turns without killing
    ///       as there are in the configuration. If the config limit is grater than the
    ///       threshold, it will return false
    public boolean inactiveLimitReached(int inactiveTurns) {
        if (_inactiveResetOnWhites) {
            if (!evenTurn() && inactiveLimit() < INACTIVE_THRESHOLD) {
                // Check for inactivity
                if ((inactiveTurns / 2) >= inactiveLimit()) {
                    // Game finished due to inactivity
                    return true;
                }
            }
        } else {
            if (evenTurn() && inactiveLimit() < INACTIVE_THRESHOLD) {
                // Check for inactivity
                if ((inactiveTurns / 2) >= inactiveLimit()) {
                    // Game finished due to inactivity
                    return true;
                }
            }
            
        }

        return false;
    }

    /// @brief Returns if the players have reached the consecutive check limit
    /// @pre ---
    /// @post Returns true if there has been a color doing as many consecutive checks
    ///       as the limit set
    public boolean checkLimitReached(int whiteCheckTurns, int blackCheckTurns) {
        return whiteCheckTurns >= checkLimit() || blackCheckTurns >= checkLimit();
    }

    // ! METHODS TO RETRIEVE CHESS INFORMATION
    /// @brief Returns a String as the board of the chess
    /// @pre ---
    /// @post Returns a String as the board of the chess
    public String showBoard() {
        return _chess.showBoard();
    }

    /// @brief Promotes a Piece to the given PieceType
    /// @pre @p piece && @type != null
    /// @post Promotes the Piece at @p position to the type @p type
    public void promotePiece(Position position, PieceType type) {
        if (position == null || type == null) {
            throw new IllegalArgumentException("PromotePiece null object references");
        }

        _chess.promotePiece(position, type);
    }

    /// @brief Returns the number of columns
    /// @pre ---
    /// @post Returns the number of columns
    public int cols() {
        return _chess.cols();
    }

    /// @brief Returns the number of rows
    /// @pre ---
    /// @post Returns the number of rows
    public int rows() {
        return _chess.rows();
    }

    /// @brief Returns the piece of a cell
    /// @pre ---
    /// @post Returns (if exists) the piece at the position @p p of the board
    public Piece pieceAtCell(Position p) {
        return _chess.pieceAt(p.row(), p.col());
    }

    /// @brief Returns the color of a cell
    /// @pre --
    /// @post Returns a PieceColor of the cell at position @p p
    public PieceColor cellColor(Position p) {
        return _chess.cellColor(p);
    }

    /// @brief Returns the type list of the chess game
    /// @pre ---
    /// @post Returns a list of PieceType of the chess
    public List<PieceType> typeList() {
        return _chess.typeList();
    }

    /// @brief Returns the type list of the possibile promotion types
    /// @pre ---
    /// @post Returns the type list of the possible promotion types
    public List<PieceType> promotableTypes() {
        List<PieceType> tempList = new ArrayList<>();
        // Cannot become a king, so filter it
        for (PieceType t : typeList()) {
            if (!t.isKingType()) {
                tempList.add(t);
            }
        }

        return tempList;
    }

    /// @brief Returns the type corresponding to the given name
    /// @pre ---
    /// @post If the type does not exist, returns null. Else, returns the type
    public PieceType typeFromString(String name) {
        PieceType temp = null;

        for (PieceType pt : typeList()) {
            if (pt.ptName().equals(name)) {
                temp = pt;
                break;
            }
        }

        return temp;
    }

    /// @brief Returns if a board cell is empty
    /// @pre --
    /// @post Returns if a board cell is empty
    public boolean emptyCell(Position p) {
        return _chess.emptyCell(p);
    }

    /// @brief Returns the inactive turns limit
    /// @pre ---
    /// @post Returns the inactive turns limit value
    public int inactiveLimit() {
        return _chess.inactiveLimits();
    }

    /// @brief Returns the consecutive check limit
    /// @pre ---
    /// @post Returns the consecutive check limit value
    public int checkLimit() {
        return _chess.chessLimits();
    }

    /// @brief Returns the most valuable PieceType after the king
    /// @pre ---
    /// @post Returns the most valuable PieceType after the king
    public PieceType mostValuableType() {
        int maxValue = Integer.MIN_VALUE;
        PieceType result = null;
        for (PieceType type : _chess.typeList()) {
            if (!type.isKingType()) {
                if (type.ptValue() > maxValue) {
                    result = type;
                    maxValue = type.ptValue();
                }
            }
        }
        return result;
    }
}