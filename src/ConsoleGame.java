import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/// @author Miquel de Domingo i Giralt
/// @file ConsoleGame.java
/// @class ConsoleGame
/// @brief Class that controls the game played in a console display
public class ConsoleGame {
	/// IN-GAME CONTROL VARIABLES
	private static String defaultConfigFileName = null;				///< Keeps the configuration file name
	private static PieceColor currTurnColor = null;					///< Current turn color
	private static Integer turnNumber = null;						///< Current turn number
	private static Integer undoCount = null;						///< Amount of consequent undo movements
	private static List<Turn> turns = null;							///< List of turns
	private static boolean dataSet = false;							///< To know if the data has been initialized
	private static boolean lastTurnCheck = false;

	/// CONSTANTS
	private static String DEFAULT_CONFIGURATION = "./data/configuration.json";								///< Location of the default configuration
	private static String SAVED_GAMES_LOCATION = "./saved_games/";											///< Saved games directory
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
					System.out.println("Creant una partida normal...");
					defaultConfigFileName = DEFAULT_CONFIGURATION;
					initiateGame(FromJSONParserHelper.buildChess(DEFAULT_CONFIGURATION));
					break;
				case 2:
					System.out.println("Creant una partida personalitzada...");
					configuredChessGame("Entra el nom del fitxer amb la configuració: ", false);
					break;
				case 3:
					System.out.println("Carregant una partida guardada...");
					configuredChessGame("Entra el nom del fitxer amb la partida: ", true);
					break;
				default:
					System.out.println("Sortint de l'aplicació...");
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
		System.out.println("|  Escull una opcio (num):                |");
		System.out.println("|    1. Partida normal                    |");
		System.out.println("|    2. Entrar partida configurada        |");
		System.out.println("|    3. Entrar una partida carregada      |");
		System.out.println("|    0. Sortir                            |");
		System.out.println("|                                         |");
		System.out.println("+-----------------------------------------+");
	}

	/// @brief Shows the different in-game commands
	/// @pre ---
	/// @post Prints the different in-game commands
	private static void showInstructions() {
		System.out.println();
		System.out.println("+--------------  COMANDES  ---------------+");
		System.out.println("|  [Quan es demana posicion d'origen]     |");
		System.out.println("|      - X: Acabar la partida             |");
		System.out.println("|      - D: Desfer moviment               |");
		System.out.println("|      - R: Refer moviment                |");
		System.out.println("|      - G: Guardar partida               |");
		System.out.println("|      - H: Mostrar ajuda                 |");
		System.out.println("|      - T: Demanar Taules                |");
		System.out.println("|      - S: Rendir-se                     |");
		System.out.println("|                                         |");
		System.out.println("|   [Quan es demana posicion destí]       |");
		System.out.println("|      - O: Tornar a escollir origen      |");
		System.out.println("+-----------------------------------------+");
		System.out.println();
	}

	/// @brief Shows the choose players of game options
	/// @pre ---
	/// @post Prints the choose players of game options
	private static void playerOptions() {
		System.out.println();
		System.out.println("+------------ ESCULL JUGADORS ------------+");
		System.out.println("|                                         |");
		System.out.println("|         1. Jugador vs Jugador           |");
		System.out.println("|         2. Jugador vs CPU               |");
		System.out.println("|         3. CPU vs CPU                   |");
		System.out.println("|         0. Sortir                       |");
		System.out.println("|                                         |");
		System.out.println("+-----------------------------------------+");
	}

	/// @brief Shows the different difficulty options
	/// @pre ---
	/// @post Prints the different difficulty options
	private static void dificultyLevels() {
		System.out.println();
		System.out.println("+---- ESCULL LA DIFICULTAT ----+");
		System.out.println("|                              |");
		System.out.println("|    1. Principiant            |");
		System.out.println("|    2. Normal                 |");
		System.out.println("|    3. Difícil                |");
		System.out.println("|    0. Sortir                 |");
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
				System.out.print("Opció: ");
			}

			try {
				res = br.readLine().trim();
				success = true;
			} catch (IOException e) {
				System.err.println("Error en l'entrada per teclat. Torna-ho a intentar:");
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
					System.out.println("Has d'entrar un dels valors de la llista.");
				}
			} catch (NumberFormatException e) {
				System.err.println("Has d'entrar un nombre");
			} 
		} while (!success);

		return val;
	}

	/// @brief Asks for the filename of the configuration and starts the game
	/// @pre ---
	/// @post If the file name from the user is correct, starts the game with that configuration.
	private static void configuredChessGame(String text, boolean hasStarted) {
		boolean validFileLocation = false;
		Chess chess = null;
		while (!validFileLocation) {
			System.out.println("[Escriu EXIT per sortir]");
			System.out.println(text);

			String fileLocation = readInputLine(false);
			if (fileLocation.toUpperCase().equals("EXIT")) {
				System.out.println("Sortint de l'aplicació...");
				validFileLocation = true;
			} else {
				try {
					if (hasStarted) {
						/// Save configuration file name
						defaultConfigFileName = FromJSONParserHelper.getConfigurationFileName(fileLocation);

						/// Get the match information
						chess = FromJSONParserHelper.buildSavedChessGame(fileLocation);
						Pair<List<Turn>, PieceColor> info = FromJSONParserHelper.matchInformation(fileLocation, false);
						List<Turn> loadedTurns = info.first;
						currTurnColor = info.second;

						initiateData();
						if (!loadedTurns.isEmpty()) {
							for (Turn t : loadedTurns) {
								Pair<Position, Position> temp = t.moveAsPair();

								/// Apply movements to the game
								Pair<List<MoveAction>, List<Position>> checkResult = chess.checkMovement(temp.first, temp.second);

								/// All movements must be right!
								List<MoveAction> moveResult = chess.applyMovement(temp.first, temp.second, checkResult.second);
								
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
					} else {
						defaultConfigFileName = fileLocation;
						chess = FromJSONParserHelper.buildChess(defaultConfigFileName);
						System.out.println(defaultConfigFileName);
					}

					/// If it gets here, there will be no exception of file not found
					validFileLocation = true;
					
					/// Start game
					initiateGame(chess);
				} catch (FileNotFoundException e) {
					System.err.println(e.getMessage());
				} catch (JSONParseFormatException e) {
					System.err.println(e.getType());
					System.err.println(e.getMessage());
				}
			}
		}
	}

	/// @brief Functions that initiates the class data
	/// @pre ---
	/// @post Initiates data if not has been set 
	private static void initiateData() {
		if (!dataSet) {
			currTurnColor = PieceColor.White;			/// White always start
			turnNumber = 0;
			undoCount = 0;
			turns = new ArrayList<>();
			dataSet = true;
		}
	}

	/// @brief Function that sets the game users and starts the game
	/// @pre Loaded chess
	/// @post Asks for the players and chooses which game style to play
	private static void initiateGame(Chess chess) {
		initiateData();
		playerOptions();
		switch (readOption()) {
			case 1: {
				/// 2 Players
				System.out.print("Vols posar nom als jugadors? [S/N]: ");
				String pOne = "Jugador 1";
				String pTwo = "Jugador 2";
				
				/// Optional - more fun :D
				if (readInputLine(false).toUpperCase().equals("S")) {
					System.out.print("Nom del jugador 1: ");
					pOne = readInputLine(false);
					System.out.print("Nom del jugador 2: ");
					pTwo = readInputLine(false);
				}

				/// Choose colors
				boolean pOneWhite = true;
				boolean valid = true;
				do {
					System.out.print("Color de " + pOne + " [B/N]: ");
					String s = readInputLine(false);
					if (s.toUpperCase().equals("N")) {
						pOneWhite = false;
						valid = true;
					} else if (!s.toUpperCase().equals("B")) {
						valid = false;
					}
				} while (!valid);

				if (pOneWhite) {
					twoPlayersGame(chess, pOne, pTwo);
				} else {
					twoPlayersGame(chess, pTwo, pOne);
				}

				break;
			}
			case 2: {
				/// Player vs CPU
				System.out.println("Jugador vs CPU");
				boolean playerIsWhite = true;
				boolean valid = true;

				do {
					System.out.print("Color del jugador [B/N]: ");
					String s = readInputLine(false);
					if (s.toUpperCase().equals("N")) {
						playerIsWhite = false;
						valid = true;
					} else if (!s.toUpperCase().equals("B")) {
						valid = false;
					}
				} while (!valid);
				
				playerCPUGame(chess, playerIsWhite);

				break;
			}
			case 3: {
				/// CPU vs CPU
				System.out.println("Dues CPUs");
				twoCPUsGame(chess);
				break;
			}
			case 0:
				System.out.println("Sortint de l'aplicació...");
				break;
		}
	}

	/// @brief Function that controls the game flow while it has not finished for two players
	/// @pre ---
	/// @post While the game has not finished nor been saved, will keep asking for
	/// 	  turns. If it finishes, prints the winner.
	private static void twoPlayersGame(Chess chess, String pOne, String pTwo) {
		String playerOption = "";
		boolean draw = false;
		int rows = chess.rows();
		int cols = chess.cols();

		showInstructions();

		do {
			if (currTurnColor == PieceColor.White) {
				System.out.println("Torn de " + pOne + " - BLANQUES");
			} else {
				System.out.println("Torn de " + pTwo + " - NEGRES");
			}

			playerOption = playerTurn(chess, rows, cols);

			if (playerOption.equals("T")) {
				/// PLAYER ASKS FOR TABLES
				/// Save asking for tables
				saveEmptyTurn("TAULES SOL·LICITADES", currTurnColor);

				/// Get user response
				System.out.print("El contrincant demana taules, acceptes? [S/N]: ");
				String s = readInputLine(false);
				if (s.toUpperCase().equals("S")) {
					saveEmptyTurn("TAULES ACCEPTADES", oppositeColor(currTurnColor));
					playerOption = "E";
					draw = true;
				} else {
					System.out.println("La partida continua.");
					playerOption = playerTurn(chess, rows, cols);			
				}
			} else if (playerOption.equals("S")) {
				if (currTurnColor == PieceColor.White) {
					System.out.println(pOne + " es rendeix.");
				} else {
					System.out.println(pTwo + " es rendeix.");
				}
				playerOption = "G";
				saveEmptyTurn("RENDICIÓ", currTurnColor);
			}
		} while (
			!playerOption.equals("X") && 
			!playerOption.equals("G") &&
			!playerOption.equals("E") 
		);

		switch (playerOption) {
			case "G": {
				/// Save game
				String fileName = saveGame(chess, "PARTIDA AJORNADA");
				System.out.println("Partida guardada amb nom: " + fileName);
				break;
			}
			case "E": {
				endOfGame(chess, draw);
				break;
			}
		}
	}

	/// @brief Function that controls the flow of the game between a cpu and a player
	/// @pre ---
	/// @post The player is the only one who can stop the game or save it. While the
	///       game has not finished, will keep askig for turns to the user. The cpu
	///       works automatically. If it finishes, prints the winner
	private static void playerCPUGame(Chess chess, boolean playerIsWhite) {
		int rows = chess.rows();
		int cols = chess.cols();
		String playerOption = "";

		int diff = cpuDifficulty();

		Knowledge knowledge = cpuKnowledge(chess, "CPU");

		Cpu cpu = new Cpu(knowledge, chess, diff, playerIsWhite ? PieceColor.Black : PieceColor.White);
		
		do {
			if (currTurnColor == PieceColor.White && !playerIsWhite ||
				currTurnColor == PieceColor.Black && playerIsWhite) {
				/// CPU
				if (turnNumber == 0) {
					cpuTurn(chess, cpu, null);
				} else {
					cpuTurn(chess, cpu, lastMovement());
				}

				/// Change turn
				toggleTurn();
			} else {
				if (turnNumber % 10 == 0 || turnNumber % 10 == 1) {
					/// Avoid showing instructions every now and then
					showInstructions();
				}

				System.out.println("Torn del jugador");
				playerOption = playerTurn(chess, rows, cols);
			}
		} while (
			!playerOption.equals("X") && 
			!playerOption.equals("G") &&
			!playerOption.equals("E") &&
			!playerOption.equals("T")
		);

		if (playerOption.equals("E")) {
			endOfGame(chess, false);
		} else if (playerOption.equals("T")) {
			/// If user asks for tables, CPU accepts
			endOfGame(chess, true);
		}
	}

	/// @brief Function that controls the game flow while it has not finished for two cpus
	/// @pre ---
	/// @post The game can only end. It cannot be stopped since the cpu can only do new moves.
	///       Finishes the game with the winning cpu.
	private static void twoCPUsGame(Chess chess) {
		System.out.println("NIVELL CPU 1");
		int diff = cpuDifficulty();
		
		System.out.println("NIVELL CPU 2");
		diff = cpuDifficulty();

		Knowledge knowledge = cpuKnowledge(chess, "CPU");

		Cpu cpu1 = new Cpu(knowledge, chess, diff, PieceColor.White);
		Cpu cpu2 = new Cpu(knowledge, chess, diff, PieceColor.Black);

		MoveAction result = null;
		do {
			if (currTurnColor == PieceColor.White) {
				result = cpuTurn(chess, cpu1, lastMovement());
			} else {
				result = cpuTurn(chess, cpu2, lastMovement());
			}
		} while (result == null);
		
		/// Game finished - can't never be draw
		endOfGame(chess, false);
	}

	/// @brief Controls a player turn 
	/// @brief ---
	/// @post Executes a player movement. If the player chooses to exit or save
	///       the game, it will be returned (X or G respectively). If there's a checkmate
	///       returns E. Elsewise returns empty string
	private static String playerTurn(Chess chess, int rows, int cols) {
		String oValue = null;
		String dValue = null;
		String result = "";

		System.out.println(chess.showBoard());
		boolean originMove = true;

		oValue = readMovement("Coordenada origen (ex. a6)", rows, cols, currTurnColor, chess, originMove);
		switch (oValue) {
			case "X":
				/// Ask for saving
				System.out.println("Vols sortir sense guardar la partida? [S/N]: ");
				String s = readInputLine(false);

				if (s.toUpperCase().equals("N")) {
					result = "G";
				} else {
					/// Do not save the game
					System.out.println("Partida acabada!");
					result = "X";
				}

				break;
			case "G":			/// Save game
			case "T":			/// Tables
			case "S":			/// Surrender
				result = oValue;
				System.out.println("Here");
				break;
			case "D":
				if (undoMovement(chess, turnNumber)) {
					/// Previous turn
					turnNumber--;
					/// Increase undone movements
					undoCount++;

					System.out.println("Moviment desfet!");
				}
				break;
			case "H":
				showInstructions();
				break;
			case "R":
				/// There's no need to remove any of the movements done
				/// since we will overlap the data
				if (redoMovement(chess, undoCount)) {
					/// Next turn
					turnNumber++;
					/// Decrement the undone movements
					undoCount--;

					System.out.println("Moviment refet!");
				}

				break;
			default: {
				originMove = false;
				dValue = readMovement("Coordenada destí  (ex. a6): ", rows, cols, currTurnColor, chess, originMove);

				if (!dValue.equals("O")) {
					/// Create positions with the read strings
					Position origin = new Position(oValue);
					Position dest = new Position(dValue);
					Pair<List<MoveAction>, List<Position>> checkResult = chess.checkMovement(origin, dest);

					if (checkResult.first.contains(MoveAction.Correct)) {
						List<MoveAction> moveResult = chess.applyMovement(origin, dest, checkResult.second);

						for (MoveAction m : moveResult) {
							System.out.println(m.toString());
						}
						/// If the user has undone x movements, and not redone all of them
						/// then the match mus continue from that and all the movements after the
						/// current turn must be delelted.
						if (undoCount > 0) {
							/// turnNumber == turns.size() - undoCount
							for (int i = turns.size() - 1; i >= turnNumber; i--) {
								turns.remove(i);
							}
						}

						if (moveResult.contains(MoveAction.Promote)) {
							/// Handle promotion
							handlePromotion(chess, dest);
						}

						/// Save turn
						saveTurn(
							moveResult,
							new Pair<String, String>(
								origin.toString(),
								dest.toString()
							)
						);

						if (moveResult.contains(MoveAction.Escacimat)) {
							/// End of game
							System.out.println("Escac i mat de " + currTurnColor.toString());
							result = "E";
						} else {
							/// Change turn
							toggleTurn();
						}
					} else {
						System.out.println("Moviment incorrecte!");
					}
				}
			}
		}
		
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
				difficulty = 4;
				break;
			case 3:
				difficulty = 6;
				break;
			case 0:
				System.out.println("Sortint de l'aplicació...");
				System.exit(-1);
			default:
				difficulty = 2;
		}

		return difficulty;
	}

	/// @brief Asks if wanted to add CPU knowledge
	/// @pre ---
	/// @post Returns the knowlegde added or null if any.
	private static Knowledge cpuKnowledge(Chess chess, String name) {
		boolean valid = true;
		Knowledge knowledge = null;
		do {
			System.out.print("Vols afegir coneixement a la " + name + "? [S/N]: ");
			String s = readInputLine(false);
			if (s.toUpperCase().equals("S")) {
				valid = true;

				/// Get the file locations
				System.out.println("Entra cada ubicació separada per una línia [EXIT per acabar]: ");
				List<String> list = new ArrayList<>();
				String temp;

				do {
					System.out.print("Ubicació: ");
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
						System.err.println("Fitxer [" + location + "] no trobat.");
					} catch (JSONParseFormatException e) {
						System.err.println(e.getType());
						System.err.println(e.getMessage());
					}
				}

				if (!complexList.isEmpty()) {
					knowledge = new Knowledge(complexList, chess);
				}

			} else if (!s.toUpperCase().equals("N")) {
				valid = false;
			} else {
				/// If N
				valid = true;
			}
		} while (!valid);

		return knowledge;
	}

	/// @brief Changes turn value
	/// @pre @p currTurnColor != null
	/// @post Changes currTurnValue to the oposite
	private static void toggleTurn() {
		if (currTurnColor == null) {
			throw new NullPointerException("ToggleTurn cannot toggle a null value");
		}

		currTurnColor = (currTurnColor == PieceColor.White) 
			? PieceColor.Black
			: PieceColor.White;
	}

	/// @brief Saves turn information
	/// @pre @p p cannot be null
	/// @post Creates a new turn with the given movement and increments @p turnNumber.
	private static void saveTurn(List<MoveAction> results, Pair<String, String> p) {
		MoveAction res = null;
		if (results.contains(MoveAction.Escacimat)) {
			res = MoveAction.Escacimat;
		} else if (results.contains(MoveAction.Escac)) {
			res = MoveAction.Escac;
		}

		if (res == null) {
			turns.add(new Turn(currTurnColor, p, ""));	
		} else {
			turns.add(new Turn(currTurnColor, p, res.toString()));
		}
		
		turnNumber++;
	}

	/// @brief Saves empty turn
	/// @pre ---
	/// @post Adds a turn to the list containing only a result value
	private static void saveEmptyTurn(String result, PieceColor color) {
		turns.add(
			new Turn(color, new Pair<String, String>("", ""), result)
		);
		turnNumber++;
	}

	/// @brief Controls a cpu turn
	/// @pre @p c & @p cpu cannot be null
	/// @post Executes a cpu turn. If there is a checkmate, returns a MoveAction. Otherwise
	///       returns null.
	private static MoveAction cpuTurn(Chess chess, Cpu cpu, Pair<Position, Position> lastMovement) {
		if (chess == null || cpu == null) {
			throw new NullPointerException("CpuTurn given arguments cannot be null");
		}
		
		Pair<Position, Position> cpuMove = cpu.doMovement(lastMovement);

		Pair<List<MoveAction>, List<Position>> moveResult = chess.checkMovement(cpuMove.first, cpuMove.second);
		
		/// CPU movement is always correct
		chess.applyMovement(
			cpuMove.first,
			cpuMove.second,
			chess.checkMovement(cpuMove.first, cpuMove.second).second
		);

		/// Save turn
		saveTurn(
			moveResult.first,
			new Pair<String, String>(
				cpuMove.first.toString(),
				cpuMove.second.toString()
			)
		);

		if (moveResult.first.contains(MoveAction.Escacimat)) {
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
	private static String readMovement(String t, int rows, int cols, PieceColor colorTurn, Chess chess, boolean originMove) {
		String c = "abcdefghijklmnopqrstuvwxyz";
		String s;
		Position p = new Position(0, 0);
		boolean stop = false;

		do {
			System.out.println(t);
			s = readInputLine(true);

			/// Check if user wants to enter the origin coordinate again
			if (s.equals("O") && originMove) {
				System.out.println("Primer has d'entrar una coordenada d'origen!");
			} else if (s.equals("O") && !originMove) {
				System.out.println("Torna a entrar la coordenada d'origen");
				stop = true;
			} else {
				switch (s) {
					case "X":
						System.out.println("Sortint del joc (la partida no es guardarà)...");
						stop = true;
						break;
					case "G":
						System.out.println("Guardant partida...");
						stop = true;
						break;
					case "D":
						System.out.println("Desfent moviment...");
						stop = true;
						break;
					case "R":
						System.out.println("Refent moviment...");
						stop = true;
						break;
					case "H":
						System.out.println("Mostrant menu...");
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
									if (originMove && !chess.emptyCell(p)) {
										if (chess.cellColor(p) == colorTurn) {
											stop = true;
										} else {
											System.out.println("És el torn de " + colorTurn.toString());
											System.out.println("Escull una peça de " + colorTurn.toString());
										}
									} else if (!originMove) {
										stop = true;
									} else {
										stop = false;
									}
								} else {
									System.out.println("Fila fora de rang. Torna-hi...");
								}
							} catch (NumberFormatException e) {
								System.out.println("Format incorrecte. Torna-hi...");
							}
						} else {
							System.out.println("Columna fora de rang. Torna-hi...");
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
	private static void handlePromotion(Chess chess, Position piecePosition) {
		System.out.print("Vols promocionar la peça? [S/N]: ");
		String s = readInputLine(false);
		if (s.toUpperCase().equals("S")) {
			List<PieceType> tempList = new ArrayList<>();

			/// Cannot become a king, so filter it
			for (PieceType t : chess.typeList()) {
				if (!t.isKingType()) {
					tempList.add(t);
				}
			}
			
			/// Display the possibilities
			System.out.println("Tipus disponibles: ");
			for (int i = 1; i <= tempList.size(); i++) {
				System.out.println(String.valueOf(i) + ": " + tempList.get(i).ptName());
			}
			
			boolean valid = false;
			do {
				System.out.println("Entra una opció: ");
				try {
					int r = Integer.parseInt(readInputLine(false));

					if (r - 1 > tempList.size()) {
						System.out.println("Número no vàlid.");
					} else {
						chess.promotePiece(piecePosition, tempList.get(r - 1));
						valid = true;
					}
				} catch (NumberFormatException e) {
					System.out.println("El format és incorrecte, entra un número.");
				}
			} while (!valid);

			
		} 
	}

	/// @brief Undoes one movement
	/// @pre ---
	/// @post If possible, undoes one movement. It is only possible to undo
	///       if there has been one movement
	private static boolean undoMovement(Chess chess, int turnNumber) {
		if (turnNumber == 0) {
			/// Can't undo any movement
			System.out.println("No és possible desfer el moviment!");
			System.out.println("Per desfer un moviment se n'ha de fer un!");
			return false;
		} else {
			/// Get current turn values
			chess.undoMovement();
			toggleTurn();
			return true;
		}
	}

	/// @brief Redoes one movement
	/// @pre turnNumber pointing after the last position of list
	/// @post If possible, redoes one movement. It is only possible to redo if
	///       there has been at least one undone movement
	private static boolean redoMovement(Chess chess, int undoCount) {
		if (undoCount == 0) {
			System.out.println("No és possible refer el moviment!");
			System.out.println("Per refer un moviment se n'ha de desfer un!");
			return false;
		} else {
			/// Get the current turn values
			chess.redoMovement();
			toggleTurn();
			return true;
		}
	}

	/// @brief To get the last movement of the game
	/// @pre @p turnNumber > 0
	/// @post Returns the last movement of the game
	private static Pair<Position, Position> lastMovement() {
		return new Pair<Position, Position>(
			new Position(turns.get(turnNumber - 1).origin()),
			new Position(turns.get(turnNumber - 1).destination())
		);
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
	private static void endOfGame(Chess chess, boolean draw) {
		String res = null;
		System.out.println("Partida acabada");

		if (draw) {
			System.out.println("S'han acceptat taules.");
			System.out.println("Fi de la partida.");
			res = "TAULES";
		} else {
			System.out.println("Guanyador: " + currTurnColor.toString());
			System.out.println("Fi de la partida.");
			res = currTurnColor.toString() + " GUANYEN";
		}

		System.out.println("Vols guardar la partida? [S/N]");
		String s = readInputLine(false);
		
		if (s.toUpperCase().equals("S")) {
			String fileName = saveGame(chess, res);
			
			if (fileName == null) {
				System.out.println("Error en guardar la partida!");
			} else {
				System.out.println("Partida guardada amb nom: " + fileName);
			}
		}
	}

	/// @brief Saves the game in a file
	/// @pre ---
	/// @post Saves the game in two JSON files, pulling away the configuration and
	///       the game developement. Returns the fileName or null if there's an error
	private static String saveGame(Chess chess, String finalResult) {
		try {
			/// Configuration
			File configurationFile = new File(defaultConfigFileName);

			if (configurationFile.exists()) {
				System.out.println("El fitxer de configuració ja existeix. Vols sobreesciure'l? [S/N]: ");
				String res = readInputLine(false);

				if (res.toUpperCase().equals("S")) {
					configurationFile.createNewFile();
					FileWriter configWriter = new FileWriter(configurationFile);
					configWriter.write(ToJSONParserHelper.saveChessConfigToJSON(chess));
					configWriter.close();		

					System.out.println("Fitxer sobreescrit.");
				}
			} else {
				configurationFile.createNewFile();
				FileWriter configWriter = new FileWriter(configurationFile);
				configWriter.write(ToJSONParserHelper.saveChessConfigToJSON(chess));
				configWriter.close();
			}

			/// Game
			createSavedGameDirectory();
			Long fileName = new Date().getTime();
			File gameFile = new File(SAVED_GAMES_LOCATION + fileName.toString() + ".json");
			gameFile.createNewFile();
			FileWriter gameWriter = new FileWriter(gameFile);
			gameWriter.write(ToJSONParserHelper.saveGameToJSON(chess, defaultConfigFileName, currTurnColor, turns, finalResult));
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
	private static void createSavedGameDirectory() {
		File newDir = new File(SAVED_GAMES_LOCATION);
		if (!newDir.exists()) {
			if (!newDir.mkdir()) {
				System.out.println("Fatal error on creating saved games folder.");
			}
		}
	}
}
