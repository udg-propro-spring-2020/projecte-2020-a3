/*
 * @author Miquel de Domingo i Giralt
 */

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

/* 
 * @class ConsoleGame
 * @brief Class that controls the game played in a console display.
 */
public class ConsoleGame {
	/// IN-GAME CONTROL VARIABLES
	private static String defaultConfigFileName = null;
	private static PieceColor currTurnColor = null;
	private static Integer turnNumber = null;
	private static Integer undoCount = null;
	private static List<Turn> turns = null;
	private static boolean dataSet = false;

	/// CONSTANTS
	private static String DEFAULT_CONFIGURATION = "./data/default_game.json";
	private static final List<Integer> VALID_OPTIONS = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));

	/// @brief Shows a menu asking how to start a game
	/// @pre ---
	/// @post Displays a menu with 3 options: 1. Play with the default rules 2. Play
	///       with modified rules (enter filename) 3. Enter a saved game
	public static void start() {
		showMenu();

		try {
			switch (readOption()) {
				case 1:
					System.out.println("Creant una partida normal...\n");
					defaultConfigFileName = FromJSONParserHelper.getConfigurationFileName(DEFAULT_CONFIGURATION);
					initiateGame(FromJSONParserHelper.buildChess(DEFAULT_CONFIGURATION));
					break;
				case 2:
					System.out.println("Creant una partida personalitzada...");
					configuredChessGame("Entra el nom del fitxer amb la configuració: ");
					break;
				case 3:
					System.out.println("Carregant una partida guardada...");
					configuredChessGame("Entra el nom del fitxer amb la partida: ");
					break;
				default:
					System.out.println("Sortint de l'aplicació...");
					break;
			}
		} catch (FileNotFoundException f) {
			System.out.println(f.getMessage());
		} 
	}

	/// @brief Shows the menu options
	/// @pre ---
	/// @post Prints the menu options
	private static void showMenu() {
		System.out.println("+---------------- MENU ----------------+");
		System.out.println("|                                      |");
		System.out.println("|  Escull una opcio (num):             |");
		System.out.println("|    1. Partida normal                 |");
		System.out.println("|    2. Entrar partida configurada     |");
		System.out.println("|    3. Entrar una partida carregada   |");
		System.out.println("|    0. Sortir                         |");
		System.out.println("|                                      |");
		System.out.println("+--------------------------------------+");
	}

	/// @brief Shows the different in-game commands
	/// @pre ---
	/// @post Prints the different in-game commands
	private static void showInstructions() {
		System.out.println("+--------------  COMANDES  ---------------+");
		System.out.println("|  [Quan es demana posicion d'origen]     |");
		System.out.println("|      - X: Acabar la partida             |");
		System.out.println("|      - D: Desfer moviment               |");
		System.out.println("|      - R: Refer moviment                |");
		System.out.println("|      - G: Guardar partida               |");
		System.out.println("|      - H: Mostrar ajuda                 |");
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
		System.out.println("+------- ESCULL JUGADORS -------+");
		System.out.println("|                               |");
		System.out.println("|    1. Jugador vs Jugador      |");
		System.out.println("|    2. Jugador vs CPU          |");
		System.out.println("|    3. CPU vs CPU              |");
		System.out.println("|    0. Sortir                  |");
		System.out.println("|                               |");
		System.out.println("+-------------------------------+");
	}

	/// @brief Shows the different difficulty options
	/// @pre ---
	/// @post Prints the different difficulty options
	private static void dificultyLevels() {
		System.out.println("+---- ESCULL LA DIFICULTAT ----+");
		System.out.println("|                              |");
		System.out.println("|    1. Principiant            |");
		System.out.println("|    2. Intermedi              |");
		System.out.println("|    3. Difícil                |");
		System.out.println("|    0. Sortir                 |");
		System.out.println("|                              |");
		System.out.println("+------------------------------+");
	}

	/// @brief Function that simply reads a line from the user input
	/// @pre ---
	/// @post Returs the read line (trimmed)
	private static String readInputLine() {
		BufferedReader br = new BufferedReader(
			new InputStreamReader(System.in)
		);
		boolean success = false;
		String res = "";
		do {
			try {
				res = br.readLine().trim();
				success = true;
			} catch (IOException e) {
				System.out.println("Error en l'entrada per teclat. Torna-ho a intentar:");
			}
		} while (!success);

		return res;
	}

	/// @brief Function to read a integer value
	/// @pre ---
	/// @post Returns a read integer which is in the VALID_OPTIONS list
	private static int readOption() {		
		int val = 0;
		do {
			System.out.print("Opció: ");

			try {
				val = Integer.parseInt(readInputLine());
			} catch (NumberFormatException e) {
				System.out.println("Has d'entrar un nombre");
			} 
		} while (!VALID_OPTIONS.contains(val));

		return val;
	}

	/// @brief Asks for the filename of the configuration and starts the game
	/// @pre ---
	/// @post If the file name from the user is correct, starts the game with that configuration.
	private static void configuredChessGame(String text) {
		boolean validFileLocation = false;

		while (!validFileLocation) {
			System.out.println("[Escriu EXIT per sortir]");
			System.out.println(text);
			try {
				String fileLocation = readInputLine();
				if (fileLocation.toUpperCase().equals("EXIT")) {
					System.out.println("Sortint de l'aplicació...");
					validFileLocation = true;
				} else {
					defaultConfigFileName = FromJSONParserHelper.getConfigurationFileName(fileLocation);
					Chess chess = FromJSONParserHelper.buildChess(fileLocation);
					/// If it gets here, there will be no exception of file not found
					validFileLocation = true;

					/// Start game
					initiateGame(chess);
				}
			} catch (FileNotFoundException e) {
				/// Keep asking for files
				System.out.println("Nom del fitxer no vàlid. \n");
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
				System.out.println("Vols posar nom als jugadors? [S/N]");
				String pOne = "Jugador 1";
				String pTwo = "Jugador 2";
				
				/// Optional - more fun :D
				if (readInputLine().toUpperCase().equals("S")) {
					System.out.print("Nom del jugador 1: ");
					pOne = readInputLine();
					System.out.print("Nom del jugador 2: ");
					pTwo = readInputLine();
				}

				/// Choose colors
				boolean pOneWhite = true;
				boolean valid = true;
				do {
					System.out.print("Color de " + pOne + " [B/N]: ");
					String s = readInputLine();
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
					String s = readInputLine();
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
		} while (!playerOption.equals("X") && !playerOption.equals("G"));
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
					/// While knowledge is not implemented, pass null
					cpuTurn(chess, cpu, null);
					/// When knowledge is implemented, get last movement
					/* Pair<Position, Position> lastMove = 
						new Pair<Position, Position>(
							new Position(turns.get(turnNumber).origin()),
							new Position(turns.get(turnNumber).destination())
						);
					cpuTurn(chess, cpu, null); */
				}
				turnNumber++;
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
		} while (!playerOption.equals("X") && !playerOption.equals("G"));
	}

	/// @brief Function that controls the game flow while it has not finished for two cpus
	/// @pre ---
	/// @post The game can only end. It cannot be stopped since the cpu can only do new moves.
	///       Finishes the game with the winning cpu.
	private static void twoCPUsGame(Chess chess) {
		System.out.println("NIVELL CPU 1");
		int diff = cpuDifficulty();
		Cpu cpu1 = new Cpu(null, chess, diff, PieceColor.White);

		System.out.println("NIVELL CPU 2");
		diff = cpuDifficulty();
		Cpu cpu2 = new Cpu(null, chess, diff, PieceColor.Black);

		do {
			if (currTurnColor == PieceColor.White) {
				cpuTurn(chess, cpu1, null);
			} else {
				cpuTurn(chess, cpu2, null);
			}
		} while (true);
		/// Condition to be analised 
	}

	/// @brief Controls a player turn 
	/// @brief ---
	/// @post Executes a player movement. If the player chooses to exit or save
	///       the game, it will be returned (X or G respectively). Elsewise returns empty string
	private static String playerTurn(Chess chess, int rows, int cols) {
		String oValue = null;
		String dValue = null;
		String result = "";

		System.out.println(chess.showBoard());
		boolean originMove = true;

		oValue = readMovement("Coordenada origen (ex. a6): ", rows, cols, currTurnColor, chess, originMove);

		switch (oValue) {
			case "X":
				/// Do not save anything
				System.out.println("Partida acabada!");
				result = "X";
				break;
			case "G":
				saveGame(chess);
				System.out.println("Partida guardada!");
				result = "G";
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
				System.out.println("Origen: " + oValue);
				dValue = readMovement("Coordenada destí  (ex. a6): ", rows, cols, currTurnColor, chess, originMove);

				if (!dValue.equals("O")) {
					System.out.println("Dest: " + dValue);

					/// Create positions with the read strings
					Position origin = new Position(oValue);
					Position dest = new Position(dValue);
					Pair<List<MoveAction>, List<Position>> moveResult = chess.checkMovement(origin, dest);

					if (moveResult.first.contains(MoveAction.Correct)) {
						chess.applyMovement(origin, dest, moveResult.second);

						/// If the user has undone x movements, and not redone all of them
						/// then the match mus continue from that and all the movements after the
						/// current turn must be delelted.
						if (undoCount > 0) {
							/// turnNumber == turns.size() - undoCount
							for (int i = turns.size() - 1; i >= turnNumber; i--) {
								turns.remove(i);
							}
						}

						/// Save turn
						saveTurn(new Pair<String, String>(origin.toJSON(), dest.toJSON()));

						/// Change turn
						toggleTurn();
					} else {
						System.out.println("Moviment incorrecte!");
					}
				}
			}
		}
		
		return result;
	}

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
				System.exit(0);
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
			System.out.println("Vols afegir coneixement a la " + name + " [S/N]? ");
			String s = readInputLine();
			if (s.toUpperCase().equals("S")) {
				valid = true;

				/// Get the file locations
				System.out.println("Entra cada ubicació separada per una línia [EXIT per acabar]: ");
				List<String> list = new ArrayList<>();
				String temp;

				do {
					System.out.print("Ubicació: ");
					temp = readInputLine();

					if (!temp.toUpperCase().equals("EXIT")) {
						list.add(temp);
					}
					
				} while (!temp.toUpperCase().equals("EXIT"));

				List<Pair<List<Pair<Position, Position>>, PieceColor>> complexList = new ArrayList<>();
				for (String location : list) {
					try {
						complexList.add(
							FromJSONParserHelper.matchInformation(location)
						);
					} catch (FileNotFoundException e) {
						System.out.println("Fitxer [" + location + "] no trobat.");
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
	private static void saveTurn(Pair<String, String> p) {
		turns.add(new Turn(currTurnColor, p, ""));
		turnNumber++;
	}

	/// @brief Controls a cpu turn
	/// @pre @p c & @p cpu cannot be null
	/// @post Executes a cpu turn
	private static void cpuTurn(Chess chess, Cpu cpu, Pair<Position, Position> lastMovement) {
		if (chess == null || cpu == null) {
			throw new NullPointerException("CpuTurn given arguments cannot be null");
		}
		
		Pair<Position, Position> cpuMove = cpu.doMovement(lastMovement);
		
		/// CPU movement is always correct
		chess.applyMovement(
			cpuMove.first,
			cpuMove.second,
			chess.checkMovement(cpuMove.first, cpuMove.second).second
		);
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
			System.out.print(t);
			s = readInputLine();

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
					default: {
						p.col = c.indexOf(s.charAt(0));
						if (p.col != -1 && p.col < cols) {
							try {
								p.row = Integer.parseInt(s.substring(1)) - 1;
								if (p.row >= 0 && p.row < rows) {
									if (originMove && !chess.emptyCell(p)) {
										if (chess.cellColor(p) == colorTurn) {
											stop = true;
											System.out.println("Moviment llegit: " + p.toString());
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
			return true;
		}
	}

	/// @brief Saves the game in a file
	/// @pre ---
	/// @post Saves the game in two JSON files, pulling away the configuration and
	///       the game developement.
	private static void saveGame(Chess chess) {
		try {
			/// Configuration
			File configurationFile = new File(defaultConfigFileName);

			if (configurationFile.exists()) {
				System.out.println("El fitxer de configuració ja existeix. Vols sobreesciure'l [S/N]?");
				String res = readInputLine();

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
			Long fileName = new Date().getTime();
			File gameFile = new File(fileName.toString() + ".json");
			gameFile.createNewFile();
			FileWriter gameWriter = new FileWriter(gameFile);
			gameWriter.write(ToJSONParserHelper.saveGameToJSON(chess, defaultConfigFileName, currTurnColor, turns, ""));
			gameWriter.close();			
		} catch (IOException e) {

		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}
	}
}
