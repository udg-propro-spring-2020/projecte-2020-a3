import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/// @author Miquel de Domingo i Giralt
/// @file GameController.java
/// @class GameController
/// @brief Controls the flow of the game independetly of the interface
/// @details Class created to abstract the information related to the game flow such as
///          turn control, turn management, game loading, game saving and others
public class GameController {
    /// GAME CONTROL VARIABLES
    private String _defaultConfigFileName = null;				///< Keeps the configuration file name
	private PieceColor _currTurnColor = null;					///< Current turn color
	private Integer _turnNumber = null;						    ///< Current turn number
	private Integer _undoCount = null;						    ///< Amount of consequent undo movements
	private List<Turn> _turns = null;							///< List of turns
	private boolean _dataSet = false;							///< To know if the data has been initialized
    private Chess _chess = null;                                ///< Game chess

    // CONSTANTS
	private static String SAVED_GAMES_LOCATION = "./saved_games/";											///< Saved games directory

    /// @brief Builds a game controller for a chess with the given file location
    /// @details If saved game is true, it will apply all the turns that are saved
    ///          in the game flow file
    public GameController(String fileLocation, boolean savedGame) throws FileNotFoundException, JSONParseFormatException {
        if (savedGame) {
            loadSavedGameToChess(fileLocation);
        } else {
            loadChess(fileLocation);
        }
    }

    // CONSTRUCTORS HELPERS
    /// @brief Loads the game from a configuration file
    /// @pre @p fileLocation != null
    /// @post Loads the game from a configuration file
    private void loadChess(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        initiateData();
        _defaultConfigFileName = fileLocation;
        _chess = FromJSONParserHelper.buildChess(fileLocation);
        System.out.println(_defaultConfigFileName);
    }

    /// @brief Loads the game data from a saved game
    /// @pre @p fileLocation != @c null
    /// @post Loads the game data from a saved game
    private void loadSavedGameToChess(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        // Save configuration file name
        _defaultConfigFileName = FromJSONParserHelper.getConfigurationFileName(fileLocation);

        // Retrieve match information
        _chess = FromJSONParserHelper.buildSavedChessGame(fileLocation);
        Pair<List<Turn>, PieceColor> info = FromJSONParserHelper.matchInformation(fileLocation, false);
        List<Turn> loadedTurns = info.first;
        _currTurnColor = info.second;

        initiateData();
        if (!loadedTurns.isEmpty()) {
            for (Turn t : loadedTurns) {
                // TODO: Analise promotion and others
                Pair<Position, Position> temp = t.moveAsPair();

                // Apply movements to the game
                Pair<List<MoveAction>, List<Position>> checkResult = _chess.checkMovement(temp.first, temp.second);

                // All movements must be right!
                List<MoveAction> moveResult = _chess.applyMovement(temp.first, temp.second, checkResult.second);
                
                saveTurn(
                    moveResult,
                    new Pair<String, String>(
                        temp.first.toString(),
                        temp.second.toString()
                    )
                );

                toggleTurn();
            }
        }
    }

    // GAME MANIPULATION METHODS
    /// @brief Functions that initiates the class data
	/// @pre ---
	/// @post Initiates data if not has been set 
    private void initiateData() {
        if (!_dataSet) {
			_currTurnColor = PieceColor.White;			// White always start
			_turnNumber = 0;
			_undoCount = 0;
			_turns = new ArrayList<>();
			_dataSet = true;
		}
    }

    /// @brief Checks and applies a player movement
    /// @pre ---
    /// @post Checks and applies a player movement. Returns the result of the movement as
    ///       a String (null if not valid)
    public Pair<List<MoveAction>, List<Position>> checkPlayerMovement(Position origin, Position destination) {
        Pair<List<MoveAction>, List<Position>> checkResult = _chess.checkMovement(origin, destination);
        
        return checkResult;
    }

    /// @brief Applies a player movement if possible and returns the result
    /// @pre @p origin && @p destination != null
    /// @post Applies a player movement if possible and returns the result. The result
    ///       will be null if the player has to defend the king
    public List<MoveAction> applyPlayerMovement(Position origin, Position destination, List<Position> list) {
        List<MoveAction> result = _chess.applyMovement(origin, destination, list);

        // Evaluate for check
        List<Pair<Position, Piece>> colorList = (_currTurnColor == PieceColor.White) 
            ? _chess.pListWhite()
            : _chess.pListBlack();
        if (_chess.isEscac(colorList)) {
            _chess.undoMovement();
            return null;
        }

        return result;
    }

    /// @brief Checks and applies a cpu movement
    /// @pre @p origin && @p dest != null
    /// @post Checks and applies the cpu movement to the chess and returns the list of MoveActions
    public List<MoveAction> applyCPUMovement(Position origin, Position destination) {
        Pair<List<MoveAction>, List<Position>> moveResult = _chess.checkMovement(origin, destination);
		
		// CPU movement is always correct
		List<MoveAction> result = _chess.applyMovement(
			origin,
			destination,
			moveResult.second
		);

		// Save turn
		saveTurn(
			moveResult.first,
			new Pair<String, String>(
				origin.toString(),
				destination.toString()
			)
        );
        
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

    /// @brief Saves turn information
	/// @pre @p p cannot be null
	/// @post Creates a new turn with the given movement and increments @p turnNumber.
	public void saveTurn(List<MoveAction> results, Pair<String, String> p) {
		MoveAction res = null;
		if (results.contains(MoveAction.Escacimat)) {
			res = MoveAction.Escacimat;
		} else if (results.contains(MoveAction.Escac)) {
			res = MoveAction.Escac;
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
		_turns.add(
			new Turn(color, new Pair<String, String>("", ""), result)
		);
		_turnNumber++;
    }

	/// @brief Undoes one movement
	/// @pre ---
	/// @post If possible, undoes one movement. It is only possible to undo
	///       if there has been one movement
	public boolean undoMovement() {
		if (_turnNumber == 0) {
            /// Can't undo any movement
			return false;
		} else {
			/// Get current turn values
			_chess.undoMovement();
            toggleTurn();
            
            // Previous turn
            _turnNumber--;
            ///Increase undone movements
            _undoCount++;
			return true;
		}
    }
    
    /// @brief Redoes one movement
	/// @pre turnNumber pointing after the last position of list
	/// @post If possible, redoes one movement. It is only possible to redo if
	///       there has been at least one undone movement
	public boolean redoMovement() {
		if (_undoCount == 0) {
			return false;
		} else {
			// Get the current turn values
			_chess.redoMovement();
            toggleTurn();

            // Next turn
            _turnNumber++;
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
		try {
			/// Configuration
			File configurationFile = new File(_defaultConfigFileName);

			if (newConfigFile) {
                configurationFile.createNewFile();
                FileWriter configWriter = new FileWriter(configurationFile);
                configWriter.write(ToJSONParserHelper.saveChessConfigToJSON(_chess));
                configWriter.close();
            }

			/// Game
			createSavedGameDirectory();
			Long fileName = new Date().getTime();
			File gameFile = new File(SAVED_GAMES_LOCATION + fileName.toString() + ".json");
            gameFile.createNewFile();

			FileWriter gameWriter = new FileWriter(gameFile);
			gameWriter.write(ToJSONParserHelper.saveGameToJSON(_chess, _defaultConfigFileName, _currTurnColor, _turns, finalResult));
			gameWriter.close();	

			return fileName.toString() + ".json";
		} catch (IOException e) {
			return null;
		} catch (NullPointerException e) {
			System.err.println(e.getMessage());
			return null;
		}
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

    // METHODS TO RETRIEVE GAME INFORMATION
    /// @brief Returns the chess
    /// @pre ---
    /// @post Returns the chess
    public Chess chess() {
        return _chess;
    }
    
    /// @brief Returns the current turn color
    /// @pre ---
    /// @post Returns the current turn color
    public PieceColor currentTurnColor() {
        return _currTurnColor;
    }

    /// @brief Returns if the turn number ends with 0 or 1
    /// @pre ---
    /// @post Returns true if the turn number ends with 0 or 1
    public boolean zeroOrOneTurn() {
        return _turnNumber % 10 == 0 || _turnNumber % 10 == 1;
    }

    /// @brief Returns the configuration file of the game
    /// @pre ---
    /// @post Returns the configuration file of the game
    public String configurationFile() {
        return _defaultConfigFileName;
    }

    /// @brief To get the last movement of the game
	/// @pre ---
    /// @post Returns the last movement of the game. If there are no turns
    ///       returns null value
	public Pair<Position, Position> lastMovement() {
		if (_turnNumber == 0) {
            return null;
        } 

        return new Pair<Position, Position>(
			new Position(_turns.get(_turnNumber - 1).origin()),
			new Position(_turns.get(_turnNumber - 1).destination())
		);
	}

    // METHODS TO RETRIEVE CHESS INFORMATION
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

    /// @brieg Returns the type list of the chess game
    /// @pre ---
    /// @post Returns a list of PieceType of the chess
    public List<PieceType> typeList() {
        return _chess.typeList();
    }

    /// @brief Returns if a board cell is empty
    /// @pre --
    /// @post Returns if a board cell is empty
    public boolean emptyCell(Position p) {
        return _chess.emptyCell(p);
    }
}