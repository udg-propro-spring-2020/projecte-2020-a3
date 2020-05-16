import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/// @author Miquel de Domingo i Giralt
/// @file ConsoleGame.java
/// @class ConsoleGame
/// @brief Class that controls the game played in a console display
public class ConsoleGame {
	/// IN-GAME CONTROL VARIABLES
	private static GameController _controller = null;				///< Referece to the game controller

	/// CONSTANTS
	private static String DEFAULT_CONFIGURATION = "./data/configuration.json";								///< Location of the default configuration
	private static final List<Integer> VALID_OPTIONS = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));	///< List of valid options of menu

	/// @brief Shows a menu asking how to start a game
	/// @pre ---
	/// @post Displays a menu with 3 options: 1. Play with the default rules 2. Play
	///       with modified rules (enter filename) 3. Enter a saved game
	public static void start() {
		showMenu();

		try {
			switch (readOption()) {
				case 1:
					System.out.println("Creating a normal game...");
					_controller = new GameController(DEFAULT_CONFIGURATION, false);
					initiateGame();
					break;
				case 2:
					System.out.println("Creating a personalised game...");
					configuredChessGame("Enter the configuration file location: ", false);
					break;
				case 3:
					System.out.println("Loading saved game...");
					configuredChessGame("Enter the saved game file location: ", true);
					break;
				default:
					System.out.println("Exiting application...");
					break;
			}
		} catch (FileNotFoundException f) {
			System.err.println(f.getMessage());
		} catch (JSONParseFormatException e) {
			System.err.println(e.getType());
			System.err.println(e.getMessage());
		}
	}

	/// @brief Shows the menu options
	/// @pre ---
	/// @post Prints the menu options
	private static void showMenu() {
		System.out.println();
		System.out.println("+------------------ MENU -----------------+");
		System.out.println("|                                         |");
		System.out.println("|  Enter an option (num):                 |");
		System.out.println("|    1. Normal game                       |");
		System.out.println("|    2. Configured game                   |");
		System.out.println("|    3. Saved game                        |");
		System.out.println("|    0. Exit application                  |");
		System.out.println("|                                         |");
		System.out.println("+-----------------------------------------+");
	}

	/// @brief Shows the different in-game commands
	/// @pre ---
	/// @post Prints the different in-game commands
	private static void showInstructions() {
		System.out.println();
		System.out.println("+--------------  COMMANDS  ---------------+");
		System.out.println("|  [When choosing for origin position]    |");
		System.out.println("|      - X: Exit game                     |");
		System.out.println("|      - G: Save game                     |");
		System.out.println("|      - D: Undo last movement            |");
		System.out.println("|      - R: Redo last movement            |");
		System.out.println("|      - T: Ask for draw                  |");
		System.out.println("|      - S: Surrender                     |");
		System.out.println("|      - H: Show help                     |");
		System.out.println("|                                         |");
		System.out.println("|   [When choosing destination position]  |");
		System.out.println("|      - O: Choose another origin         |");
		System.out.println("+-----------------------------------------+");
		System.out.println();
	}

	/// @brief Shows the choose players of game options
	/// @pre ---
	/// @post Prints the choose players of game options
	private static void playerOptions() {
		System.out.println();
		System.out.println("+-------------- PLAYER MODE --------------+");
		System.out.println("|                                         |");
		System.out.println("|         1. Player vs Player             |");
		System.out.println("|         2. Player vs Computer           |");
		System.out.println("|         3. Computer vs Computer         |");
		System.out.println("|         0. Exit application             |");
		System.out.println("|                                         |");
		System.out.println("+-----------------------------------------+");
	}

	/// @brief Shows the different difficulty options
	/// @pre ---
	/// @post Prints the different difficulty options
	private static void dificultyLevels() {
		System.out.println();
		System.out.println("+------- CPU DIFFICULTY -------+");
		System.out.println("|                              |");
		System.out.println("|    1. Beginner               |");
		System.out.println("|    2. Intermediate           |");
		System.out.println("|    3. Advanced               |");
		System.out.println("|    0. Exit                   |");
		System.out.println("|                              |");
		System.out.println("+------------------------------+");
	}

	/// @brief Function that simply reads a line from the user input
	/// @pre ---
	/// @post Returs the read line (trimmed)
	private static String readInputLine(boolean showText) {
		BufferedReader br = new BufferedReader(
			new InputStreamReader(System.in)
		);
		boolean success = false;
		String res = "";
		do {
			if (showText) {
				System.out.print("Option: ");
			}

			try {
				res = br.readLine().trim();
				success = true;
			} catch (IOException e) {
				System.err.println("Input error. Try again:");
			}
		} while (!success);

		return res;
	}

	/// @brief Function to read a integer value
	/// @pre ---
	/// @post Returns a read integer which is in the VALID_OPTIONS list
	private static int readOption() {		
		int val = -1;
		boolean success = false;
		do {
			try {
				val = Integer.parseInt(readInputLine(true));
				if (!(success = VALID_OPTIONS.contains(val))) {
					System.out.println("You have to choose a value from the list");
				}
			} catch (NumberFormatException e) {
				System.err.println("You have to enter a number");
			} 
		} while (!success);

		return val;
	}

	/// @brief Asks for the filename of the configuration and starts the game
	/// @pre ---
	/// @post If the file name from the user is correct, starts the game with that configuration.
	private static void configuredChessGame(String text, boolean hasStarted) {
		boolean validFileLocation = false;
		while (!validFileLocation) {
			System.out.println("[Write EXIT to close the app]");
			System.out.println(text);

			String fileLocation = readInputLine(false);
			if (fileLocation.toUpperCase().equals("EXIT")) {
				System.out.println("Exiting application...");
				validFileLocation = true;
			} else {
				try {
					if (hasStarted) {
						// Set up the controller for a started game
						_controller = new GameController(fileLocation, true);
					} else {
						// Set up the controller for a non-started game
						_controller = new GameController(fileLocation, false);
					}

					// If it gets here, there will be no exception of file not found
					validFileLocation = true;
					
					// Start game
					initiateGame();
				} catch (FileNotFoundException e) {
					System.err.println(e.getMessage());
				} catch (JSONParseFormatException e) {
					System.err.println(e.getType());
					System.err.println(e.getMessage());
				}
			}
		}
	}

	/// @brief Function that sets the game users and starts the game
	/// @pre Loaded game controller
	/// @post Asks for the players and chooses which game style to play
	private static void initiateGame() {
		playerOptions();
		switch (readOption()) {
			case 1: {
				// 2 Players
				System.out.print("Do you want to name the players? [Y/N]: ");
				String pOne = "Player 1";
				String pTwo = "Player 2";
				
				// Optional - more fun :D
				if (readInputLine(false).toUpperCase().equals("Y")) {
					System.out.print("Player 1 name: ");
					pOne = readInputLine(false);
					System.out.print("Player 2 name: ");
					pTwo = readInputLine(false);
				}

				// Choose colors
				boolean pOneWhite = true;
				boolean valid = true;
				do {
					System.out.print("Color of " + pOne + " [W/B]: ");
					String s = readInputLine(false);
					if (s.toUpperCase().equals("B")) {
						pOneWhite = false;
						valid = true;
					} else if (s.toUpperCase().equals("W")) {
						valid = true;
					} else {
						valid = false;
					}
				} while (!valid);

				if (pOneWhite) {
					twoPlayersGame(pOne, pTwo);
				} else {
					twoPlayersGame(pTwo, pOne);
				}

				break;
			}
			case 2: {
				// Player vs CPU
				System.out.println("Player vs Computer");
				boolean playerIsWhite = true;
				boolean valid = true;

				do {
					System.out.print("Player color [W/B]: ");
					String s = readInputLine(false);
					if (s.toUpperCase().equals("B")) {
						playerIsWhite = false;
						valid = true;
					} else if (!s.toUpperCase().equals("W")) {
						valid = false;
					}
				} while (!valid);
				
				playerCPUGame(playerIsWhite);

				break;
			}
			case 3: {
				// CPU vs CPU
				System.out.println("Computer vs Computer");
				twoCPUsGame();
				break;
			}
			case 0:
				System.out.println("Exiting application...");
				break;
		}
	}

	/// @brief Function that controls the game flow while it has not finished for two players
	/// @pre ---
	/// @post While the game has not finished nor been saved, will keep asking for
	/// 	  turns. If it finishes, prints the winner.
	private static void twoPlayersGame(String pOne, String pTwo) {
		String playerOption = "";

		showInstructions();

		do {
			if (_controller.currentTurnColor() == PieceColor.White) {
				System.out.println(pOne + " turn - WHITES");
			} else {
				System.out.println(pTwo + " turn - BLACKS");
			}

			playerOption = playerTurn();

			if (playerOption.equals("T")) {
				// PLAYER ASKS FOR TABLES
				// Save asking for tables
				_controller.saveEmptyTurn("TAULES SOL·LICITADES", _controller.currentTurnColor());

				// Get user response
				System.out.print("The opponent asks for a draw, accept? [Y/N]: ");
				String s = readInputLine(false);
				if (s.toUpperCase().equals("Y")) {
					_controller.saveEmptyTurn("TAULES ACCEPTADES", oppositeColor(_controller.currentTurnColor()));
					playerOption = "E";
				} else {
					System.out.println("Game continues");
					
					if (_controller.currentTurnColor() == PieceColor.White) {
						System.out.println(pOne + " turn - WHITES");
					} else {
						System.out.println(pTwo + " turn - BLACKS");
					}
					playerOption = playerTurn();			
				}
			} else if (playerOption.equals("S")) {
				if (_controller.currentTurnColor() == PieceColor.White) {
					System.out.println(pOne + " surrenders");
				} else {
					System.out.println(pTwo + " surrenders");
				}
				playerOption = "G";
				_controller.saveEmptyTurn("RENDICIÓ", _controller.currentTurnColor());
			}
		} while (
			!playerOption.equals("X") && 
			!playerOption.equals("G") &&
			!playerOption.equals("E") 
		);

		switch (playerOption) {
			case "G": {
				// Save game
				String fileName = saveGame("PARTIDA AJORNADA");
				System.out.println("Saved game with name: " + fileName);
				break;
			}
			case "E": {
				endOfGame(true);
				break;
			}
		}
	}

	/// @brief Function that controls the flow of the game between a cpu and a player
	/// @pre ---
	/// @post The player is the only one who can stop the game or save it. While the
	///       game has not finished, will keep askig for turns to the user. The cpu
	///       works automatically. If it finishes, prints the winner
	private static void playerCPUGame(boolean playerIsWhite) {
		String playerOption = "";

		int diff = cpuDifficulty();

		Knowledge knowledge = cpuKnowledge("CPU");

		Cpu cpu = new Cpu(knowledge, _controller.chess(), diff, playerIsWhite ? PieceColor.Black : PieceColor.White);
		
		do {
			if (_controller.currentTurnColor() == PieceColor.White && !playerIsWhite ||
				_controller.currentTurnColor() == PieceColor.Black && playerIsWhite) {
				// CPU
				MoveAction cpuResult = cpuTurn(cpu, _controller.lastMovement());

				// Change turn
				if (!(cpuResult == MoveAction.Escacimat)) {
					_controller.toggleTurn();
				}
			} else {
				if (_controller.zeroOrOneTurn()) {
					// Avoid showing instructions every now and then
					showInstructions();
				}

				System.out.println("Player turn");
				playerOption = playerTurn();

				
			}
		} while (
			!playerOption.equals("X") && 
			!playerOption.equals("G") &&
			!playerOption.equals("E") &&
			!playerOption.equals("T")
		);

		switch (playerOption) {
			case "G": {
				String fileName = saveGame("PARTIDA AJORNADA");
				System.out.println("Saved game with name: " + fileName);
				break;
			}
			case "E":
			case "T": {
				endOfGame(true);
				break;
			}
		}
	}

	/// @brief Function that controls the game flow while it has not finished for two cpus
	/// @pre ---
	/// @post The game can only end. It cannot be stopped since the cpu can only do new moves.
	///       Finishes the game with the winning cpu.
	private static void twoCPUsGame() {
		System.out.println("Computer 1 level");
		int diff = cpuDifficulty();
		
		System.out.println("Computer 2 level");
		diff = cpuDifficulty();

		Knowledge knowledge = cpuKnowledge("CPU");

		Cpu cpu1 = new Cpu(knowledge, _controller.chess(), diff, PieceColor.White);
		Cpu cpu2 = new Cpu(knowledge, _controller.chess(), diff, PieceColor.Black);

		MoveAction result = null;
		do {
			System.out.println(_controller.showBoard());
			if (_controller.currentTurnColor() == PieceColor.White) {
				result = cpuTurn(cpu1, _controller.lastMovement());
			} else {
				result = cpuTurn(cpu2, _controller.lastMovement());
			}
			_controller.toggleTurn();
		} while (result == null);
		
		// Game finished - can't never be draw
		endOfGame(false);
	}

	/// @brief Controls a player turn 
	/// @brief ---
	/// @post Executes a player movement. If the player chooses to exit or save
	///       the game, it will be returned (X or G respectively). If there's a checkmate
	///       returns E. Elsewise returns empty string
	private static String playerTurn() {
		String oValue = null;
		String dValue = null;
		String result = "";

		System.out.println(_controller.showBoard());
		boolean stop = false;
		boolean originMove = true;

		do {
			oValue = readMovement("Origin coordinate (ex. a6)", originMove);
			switch (oValue) {
				case "X":
					// Ask for saving
					System.out.println("Exit without saving? [Y/N]: ");
					String s = readInputLine(false);
	
					if (s.toUpperCase().equals("N")) {
						stop = true;
						result = "G";
					} else {
						stop = true;
						// Do not save the game
						System.out.println("Game finished!");
						result = "X";
					}
					break;
				case "G":			// Save game
				case "T":			// Tables
				case "S":			// Surrender
					result = oValue;
					stop = true;
					break;
				case "D":
					if (_controller.undoMovement()) {
						System.out.println("Movement undone!");
						stop = true;
					} else {
						System.out.println("Can't undo a movement!");
					}
					break;
				case "H":
					showInstructions();
					break;
				case "R":
					// There's no need to remove any of the movements done
					// since we will overlap the data
					if (_controller.redoMovement()) {
						System.out.println("Movement redone!");
						stop = true;
					} else {
						System.out.println("Can't redo movement!");
					}
	
					break;
				default: {
					originMove = false;
					dValue = readMovement("Destination coordinate (ex. a6): ", originMove);
	
					if (!dValue.equals("O")) {
						Position origin = new Position(oValue);
						Position destination = new Position(dValue);
						// Create positions with the read strings
						Pair<List<MoveAction>, List<Position>> moveResult = _controller.checkPlayerMovement(origin,destination);
	
						if (moveResult.first.contains(MoveAction.Correct)) {
							_controller.cancellUndoes();
							List<MoveAction> actions = _controller.applyPlayerMovement(origin, destination, moveResult.second);
	
							if (actions != null) {
								if (actions.contains(MoveAction.Promote)) {
									// Handle promotion of destination piece
									handlePromotion(destination);
								}
					
								// Save turn
								_controller.saveTurn(
									actions,
									new Pair<String, String>(
										origin.toString(),
										destination.toString()
									)
								);
					
								if (actions.contains(MoveAction.Escacimat)) {
									result = "E";
									System.out.println(_controller.currentTurnColor().toString() + " checkmate");
								} else {
									// Change turn
									_controller.toggleTurn();
								}

								stop = true;
							}
						} else {
							System.out.println("Incorrect movement!");
						}
					}
				}
			}
		} while(!stop);
		
		return result;
	}

	/// @brief Asks for the cpu difficulty and returns the equivalent
	/// @pre ---
	/// @post Returns the equivalent cpu level with the given input
	private static int cpuDifficulty() {
		dificultyLevels();

		int difficulty;
		switch (readOption()) {
			case 2:
				difficulty = 2;
				break;
			case 3:
				difficulty = 3;
				break;
			case 0:
				System.out.println("Exiting application");
				System.exit(-1);
			default:
				difficulty = 1;
		}

		return difficulty;
	}

	/// @brief Asks if wanted to add CPU knowledge
	/// @pre ---
	/// @post Returns the knowlegde added or null if any.
	private static Knowledge cpuKnowledge(String name) {
		boolean valid = true;
		Knowledge knowledge = null;
		do {
			System.out.print("Add knowledge to the " + name + "? [Y/N]: ");
			String s = readInputLine(false);
			if (s.toUpperCase().equals("S")) {
				valid = true;

				// Get the file locations
				System.out.println("Enter a location each line [STOP to finish]: ");
				List<String> list = new ArrayList<>();
				String temp;

				do {
					System.out.print("Location: ");
					temp = readInputLine(false);

					if (!temp.toUpperCase().equals("EXIT")) {
						list.add(temp);
					}
					
				} while (!temp.toUpperCase().equals("EXIT"));

				List<Pair<List<Turn>, PieceColor>> complexList = new ArrayList<>();
				for (String location : list) {
					try {
						complexList.add(
							FromJSONParserHelper.matchInformation(location, true)
						);
					} catch (FileNotFoundException e) {
						System.err.println("File [" + location + "] not found.");
					} catch (JSONParseFormatException e) {
						System.err.println(e.getType());
						System.err.println(e.getMessage());
					}
				}

				if (!complexList.isEmpty()) {
					knowledge = new Knowledge(complexList, _controller.chess());
				}

			} else if (!s.toUpperCase().equals("N")) {
				valid = false;
			} else {
				// If N
				valid = true;
			}
		} while (!valid);

		return knowledge;
	}


	/// @brief Controls a cpu turn
	/// @pre @p cpu cannot be null
	/// @post Executes a cpu turn. If there is a checkmate, returns a MoveAction. Otherwise
	///       returns null.
	private static MoveAction cpuTurn(Cpu cpu, Pair<Position, Position> lastMovement) {
		if (cpu == null) {
			throw new NullPointerException("CpuTurn given arguments cannot be null");
		}

		Pair<Position, Position> cpuMove = cpu.doMovement();
		List<MoveAction> result = _controller.applyCPUMovement(cpuMove.first, cpuMove.second);
		_controller.cancellUndoes();
		
		if (result.contains(MoveAction.Escacimat)) {
			return MoveAction.Escacimat;
		} else {
			return null;
		}
	}

	/// @brief Reads a chess position
	/// @pre ---
	/// @post Prints the text held in t and reads positions like CN in which C is a
	///       char (column of the chess table) and N a number (row of the chess
	///       table). While this positions is not valid it will keep asking for
	///       positions. If the coordinate is valid returns the position and if it is
	///       an X, returns a null position
	private static String readMovement(String t, boolean originMove) {
		String c = "abcdefghijklmnopqrstuvwxyz";
		int rows = _controller.rows();
		int cols = _controller.cols();
		String s;
		Position p = new Position(0, 0);
		boolean stop = false;

		do {
			System.out.println(t);
			s = readInputLine(true);

			// Check if user wants to enter the origin coordinate again
			if (s.equals("O") && originMove) {
				System.out.println("You first must enter an origin coordinate!");
			} else if (s.equals("O") && !originMove) {
				System.out.println("Enter the origin coordinate again");
				stop = true;
			} else {
				switch (s) {
					case "X":
						System.out.println("Exiting game...");
						stop = true;
						break;
					case "G":
						System.out.println("Saving game...");
						stop = true;
						break;
					case "D":
						System.out.println("Movement undone...");
						stop = true;
						break;
					case "R":
						System.out.println("Movement redone...");
						stop = true;
						break;
					case "H":
						System.out.println("Displaying menu...");
						stop = true;
						break;
					case "T":
						stop = true;
						break;
					case "S":
						stop = true;
						break;
					default: {
						p._col = c.indexOf(s.charAt(0));
						if (p._col != -1 && p._col < cols) {
							try {
								p._row = Integer.parseInt(s.substring(1)) - 1;
								if (p._row >= 0 && p._row < rows) {
									if (originMove && !_controller.emptyCell(p)) {
										if (_controller.cellColor(p) == _controller.currentTurnColor()) {
											stop = true;
										} else {
											System.out.println("Turn of " + _controller.currentTurnColor().value());
											System.out.println("Choose a piece of " + _controller.currentTurnColor().value());
										}
									} else if (!originMove) {
										stop = true;
									} else {
										stop = false;
									}
								} else {
									System.out.println("Row out of range. Try again...");
								}
							} catch (NumberFormatException e) {
								System.out.println("Incorrect format. Try again...");
							}
						} else {
							System.out.println("Column out of range. Try again...");
						}
					}
				}
			}
		} while (!stop);
		return s;
	}

	/// @brief Handles user promotion
	/// @pre User has a piece ready to promote
	/// @post Returns whether the user has promoted a piece or not
	private static void handlePromotion(Position piecePosition) {
		System.out.print("Do you want to promote the piece? [Y/N]: ");
		String s = readInputLine(false);
		if (s.toUpperCase().equals("Y")) {
			List<PieceType> tempList = new ArrayList<>();

			// Cannot become a king, so filter it
			for (PieceType t : _controller.typeList()) {
				if (!t.isKingType()) {
					tempList.add(t);
				}
			}
			
			// Display the possibilities
			System.out.println("Available types: ");
			for (int i = 1; i <= tempList.size(); i++) {
				System.out.println(i + ": " + tempList.get(i - 1).ptName());
			}
			
			boolean valid = false;
			do {
				System.out.println("Choose an option: ");
				try {
					int r = Integer.parseInt(readInputLine(false));

					if (r - 1 > tempList.size() || r - 1 < 0) {
						System.out.println("Number not valid.");
					} else {
						_controller.promotePiece(piecePosition, tempList.get(r - 1));
						valid = true;
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid format. Enter a number");
				}
			} while (!valid);
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

	/// @brief Handles the end of game
	/// @pre ---
	/// @post Prints the game result and saves the game developement
	private static void endOfGame(boolean draw) {
		String res = null;

		if (draw) {
			System.out.println("Draw accepted");
			System.out.println("Game finished");
			res = "TAULES";
		} else {
			System.out.println("Winner: " + _controller.currentTurnColor().toString());
			System.out.println("Game finished");
			res = _controller.currentTurnColor().toString() + " GUANYEN";
		}

		System.out.println("Do you want to save the game? [Y/N]");
		String s = readInputLine(false);
		
		if (s.toUpperCase().equals("Y")) {
			String fileName = saveGame(res);
			
			if (fileName == null) {
				System.out.println("Error on saving the game!");
			} else {
				System.out.println("Game saved with name: " + fileName);
			}
		}
	}

	/// @brief Saves the game in a file
	/// @pre ---
	/// @post Saves the game in two JSON files, pulling away the configuration and
	///       the game developement. Returns the fileName or null if there's an error
	private static String saveGame(String finalResult) {
		/// Configuration
		File configurationFile = new File(_controller.configurationFile());

		if (configurationFile.exists()) {
			System.out.println("Configuration file already existing. Overwrite it? [Y/N]: ");
			String res = readInputLine(false);

			if (res.toUpperCase().equals("Y")) {
				System.out.println("File overwritten");
				return _controller.saveGame(finalResult, true);
			} else {
				return _controller.saveGame(finalResult, false);
			}
		} else {
			return _controller.saveGame(finalResult, false);
		}
	}
}
