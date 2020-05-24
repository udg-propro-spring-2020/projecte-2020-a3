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
	private static int _inactiveTurns = 0;							///< Current amount of inactive turns
	private static int _whiteCheckTurns = 0;						///< Current amount of consecutive checks of white
	private static int _blackCheckTurns = 0;						///< Current amount of consecutive checks of black

	/// CONSTANTS
	private static String DEFAULT_CONFIGURATION = "data/configuration.json";								///< Location of the default configuration
	private static final List<Integer> VALID_OPTIONS = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));	///< List of valid options of menu
	private static int INACTIVE_THRESHOLD = 40;																///< Inactive turns threshold

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
		boolean skipToggle = false;		// Use to preserve consistency between loaded and saved files

		showInstructions();

		do {
			System.err.println(_controller.currentTurnColor());
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
					skipToggle = true;
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
				_controller.saveEmptyTurn("RENDICIÓ", _controller.currentTurnColor());
				skipToggle = false;
			} else if (playerOption.equals("G")) {
				skipToggle = true;
			}

			if (checkLimits()) {
				playerOption = "I";
			} else if (!skipToggle) {
				_controller.toggleTurn();
			}
		} while (
			!playerOption.equals("X") && 
			!playerOption.equals("G") &&
			!playerOption.equals("E") &&
			!playerOption.equals("I") &&
			!playerOption.equals("S")
		);
			
		switch (playerOption) {
			case "G": {
				// Save game
				System.out.println(_controller.currentTurnColor());
				String fileName = saveGame("PARTIDA AJORNADA");
				if (fileName == null) {
					System.out.println("Error on saving the game!");
				} else {
					System.out.println("Game saved with name: " + fileName);
				}
				break;
			}
			case "E": {
				endOfGame(true, false);
				break;
			}
			case "I": {
				endOfGame(true, true);
				break;
			}
			case "S": {
				endOfGame(false, false);
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
		boolean lastTurnDraw = false;

		int diff = cpuDifficulty();

		Knowledge knowledge = cpuKnowledge("CPU");

		Cpu cpu = new Cpu(knowledge, _controller.chess(), diff, playerIsWhite ? PieceColor.Black : PieceColor.White);
		
		do {
			if (_controller.currentTurnColor() == PieceColor.White && !playerIsWhite ||
				_controller.currentTurnColor() == PieceColor.Black && playerIsWhite) {
				// CPU
				MoveAction cpuResult = null;
				if (lastTurnDraw) {
					System.out.println("The computer will never surrender!");
				} else {
					cpuResult = cpuTurn(cpu);
				}

				// Change turn
				if (cpuResult == null || !(cpuResult == MoveAction.Escacimat)) {
					if (checkLimits()) {
						playerOption = "I";
					} else {
						_controller.toggleTurn();
					}
				} else {
					playerOption = "C";
				}
			} else {
				if (_controller.turnNumber() % 10 == 0) {
					// Avoid showing instructions every now and then
					showInstructions();
				}

				System.out.println("Player turn");
				playerOption = playerTurn();

				if (playerOption.equals("T")) {
					// If player asks for a draw, cpu will never accept
					_controller.saveEmptyTurn(
						"TAULES SOL·LICITADES",
						_controller.currentTurnColor()
					);
					System.out.println("The CPU has not accepted the draw");
					playerOption = "";
					lastTurnDraw = true;
				} else if (playerOption.equals("S")) {
					_controller.saveEmptyTurn("RENDICIÓ", _controller.currentTurnColor());
					System.out.println(_controller.currentTurnColor().value() +  " surrenders!");
					playerOption = "C";
				}

				if (inactiveLimitReached()) {
					playerOption = "I";
				} else {
					// Change turn
					_controller.toggleTurn();
				}
			}
		} while (
			!playerOption.equals("X") && 
			!playerOption.equals("G") &&
			!playerOption.equals("I") &&
			!playerOption.equals("C") 
		);

		switch (playerOption) {
			case "C": {
				// One of them wins
				endOfGame(false, false);
				break;
			}
			case "G": {
				String fileName = saveGame("PARTIDA AJORNADA");
				if (fileName == null) {
					System.out.println("Error on saving the game!");
				} else {
					System.out.println("Game saved with name: " + fileName);
				}
				break;
			}
			case "I": {
				endOfGame(true, true);
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
		int diffOne = cpuDifficulty();
		
		System.out.println("Computer 2 level");
		int diffTwo = cpuDifficulty();

		Knowledge knowledge = cpuKnowledge("CPU");

		Cpu cpu1 = new Cpu(knowledge, _controller.chess(), diffOne, PieceColor.White);
		Cpu cpu2 = new Cpu(knowledge, _controller.chess(), diffTwo, PieceColor.Black);

		MoveAction result = null;
		boolean inactivity = false;
		do {
			System.out.println(_controller.currentTurnColor().value());
			System.out.println(_controller.showBoard());
			if (_controller.currentTurnColor() == PieceColor.White) {
				result = cpuTurn(cpu1);
			} else {
				result = cpuTurn(cpu2);
			}

			if (checkLimits()) {
				inactivity = true;
			} else {
				if (result == null) {
					_controller.toggleTurn();
				}
			}

			System.out.println("[SCAPE FOR NEXT TURN - G TO SAVE THE GAME - X TO CLOSE THE APP]");
			// "Pause the program"
			String in = readInputLine(false);
			switch (in) {
				case "X": 
					System.exit(1);
				case "G":
					String fileName = _controller.saveGame("PARTIDA APLAÇADA", false);
					System.out.println("Saved game with name: " + fileName);
					break;
			}
		} while (result == null && !inactivity);
		
		// Game finished - can't never be draw
		if (inactivity) {
			// Draw due to inactivity
			endOfGame(true, true);
		} else {
			// One computer has won
			endOfGame(false, false);
		}
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
						Pair<List<MoveAction>, List<Position>> moveResult = _controller.checkPlayerMovement(origin, destination);
	
						if (moveResult.first.contains(MoveAction.Correct)) {
							_controller.cancellUndoes();
							
							// Apply movement and check if castling
							List<MoveAction> actions = _controller.applyPlayerMovement(origin, destination, moveResult.second);
							if (actions == null) {
								// Player has left his king in a check position
								System.out.println("Your king is in a dangered position. You must defend it.");
							} else if (moveResult.first.contains(MoveAction.Castling)) {
								// Save the turn
								_controller.saveCastlingTurn(moveResult.second);

								// Since there will be no killing, increment the inactive turn
								_inactiveTurns++;
								stop = true;
							} else {
								if (moveResult.second.isEmpty()) {
									// Turn with no captures
									_inactiveTurns++;
								} else {
									// Otherwise
									_inactiveTurns = 0;
								}
		
								if (actions != null) {
									// Save turn
									_controller.saveTurn(
										actions,
										new Pair<String, String>(
											origin.toString(),
											destination.toString()
										)
									);
						
									// Check promotion
									if (actions.contains(MoveAction.Promote)) {
										// Handle promotion of destination piece
										handlePromotion(destination);
									}				
								}

								if (actions.contains(MoveAction.Escac)) {
									handleCheck();
								} else if (actions.contains(MoveAction.Escacimat)) {
									result = "C";
									System.out.println(_controller.currentTurnColor().value() + " checkmate");
								} else {
									if (_controller.currentTurnColor() == PieceColor.White) {
										_whiteCheckTurns = 0;
									} else {
										_blackCheckTurns = 0;
									}
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
			if (s.toUpperCase().equals("Y")) {
				valid = true;

				// Get the file locations
				System.out.println("Enter a location each line [EXIT to finish]: ");
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
						complexList.add(_controller.readKnowledge(location));
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
	private static MoveAction cpuTurn(Cpu cpu) {
		if (cpu == null) {
			throw new NullPointerException("CpuTurn given arguments cannot be null");
		}

		// Ask CPU for movement
		Pair<Position, Position> cpuMove = cpu.doMovement();

		// Check CPU movement
		Pair<List<MoveAction>, List<Position>> checkResult = _controller.checkCPUMovement(cpuMove.first, cpuMove.second);

		List<MoveAction> result = null;
		result = _controller.applyCPUMovement(
			cpuMove.first, 
			cpuMove.second,
			checkResult.second
		);
		_controller.cancellUndoes();

		// CPU movement will always be correct
		if (checkResult.first.contains(MoveAction.Castling)) {
			// Case CPU does a castling move
			_controller.saveCastlingTurn(checkResult.second);
			_inactiveTurns++;
		} else {	
			// Saving turn
			_controller.saveTurn(
				result,
				new Pair<String, String>(
					cpuMove.first.toString(),
					cpuMove.second.toString()
				)
			);

			// Handle promotion
			if (result.contains(MoveAction.Promote)) {
				_controller.promotePiece(cpuMove.second, _controller.mostValuableType());
				_controller.savePromotionTurn(
					_controller.currentTurnColor(),
					_controller.pieceAtCell(cpuMove.second).type(),
					_controller.mostValuableType()
				);
			}
	
			if (checkResult.second.isEmpty()) {
				// Inactive turn
				_inactiveTurns++;
			} else {
				_inactiveTurns = 0;
			}
		}

		if (result.contains(MoveAction.Escac)) {
			handleCheck();
		} else {
			if (_controller.currentTurnColor() == PieceColor.White) {
				_whiteCheckTurns = 0;
			} else {
				_blackCheckTurns = 0;
			}
		}
		
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
			List<PieceType> tempList = _controller.promotableTypes();
			
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
						// Save promotion turn
						_controller.savePromotionTurn(
							_controller.currentTurnColor(),
							_controller.pieceAtCell(piecePosition).type(),
							tempList.get(r - 1)
						);
						// Apply promotion
						_controller.promotePiece(piecePosition, tempList.get(r - 1));

						valid = true;
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid format. Enter a number");
				}
			} while (!valid);
		} 
	}

	/// @brief Handles when the turn result is a check
	/// @pre ---
	/// @post Increments the total check turn of the current color 
	private static void handleCheck() {
		System.out.println("Check on " + _controller.currentTurnColor().value() + "'s king'");
		if (_controller.currentTurnColor() == PieceColor.White) {
			_whiteCheckTurns++;
		} else {
			_blackCheckTurns++;
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

	/// @brief Returns if the players have reached the inactive limit
	/// @pre ---
	/// @post Returns true if the players have played as many turns without killing 
	///       as there are in the configuration. If the config limit is grater than the
	///       threshold, it will return false
	private static boolean inactiveLimitReached() {
		if (_controller.evenTurn() && _controller.inactiveLimit() < INACTIVE_THRESHOLD) {
			// Check for inactivity
			if ((_inactiveTurns / 2) >= _controller.inactiveLimit()) {
				// Game finished due to inactivity
				return true;
			}
		}

		return false;
	}

	/// @brief Returns if the players have reached the consecutive check limit
	/// @pre ---
	/// @post Returns true if there has been a color doing as many consecutive checks
	///       as the limit set
	private static boolean checkLimitReached() {
		return _whiteCheckTurns >= _controller.checkLimit() ||
			   _blackCheckTurns >= _controller.checkLimit();
	}

	/// @brief To know if one of the limits has been reached
	/// @pre ---
	/// @post If a limit has been reached, saves an empty turn with that limit and returns true
	private static boolean checkLimits() {
		if (checkLimitReached()) {
			// End of game
			_controller.saveEmptyTurn("TAULES PER ESCAC CONTINU", _controller.currentTurnColor());
			return true;
		} else if (inactiveLimitReached()) {
			_controller.saveEmptyTurn("TAULES PER INNACCIÓ", _controller.currentTurnColor());
			return true;
		}

		return false;
	}

	/// @brief Handles the end of game
	/// @pre ---
	/// @post Prints the game result and saves the game developement
	private static void endOfGame(boolean draw, boolean inactivity) {
		String res = null;

		if (inactivity) {
			System.out.println("Draw due to inactivity or consecutive check");
			System.out.println("Game finished");
			res = "TAULES";
		} else if (draw) {
			System.out.println("Draw accepted");
			System.out.println("Game finished");
			res = "TAULES";
		} else {
			System.out.println("Winner: " + _controller.currentTurnColor().value());
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
