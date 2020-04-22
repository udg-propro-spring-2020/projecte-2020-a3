/*
 * @author Miquel de Domingo i Giralt
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/* 
 * @class ConsoleGame
 * @brief Class that controls the game played in a console display.
 */
public class ConsoleGame {
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
					initiateGame(FromJSONParserHelper.buildChess("./data/default_game.json"));
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
					Chess c = FromJSONParserHelper.buildChess(fileLocation);
					/// If it gets here, there will be no exception of file not found
					validFileLocation = true;

					/// Start game
					initiateGame(c);
				}
			} catch (FileNotFoundException e) {
				/// Keep asking for files
				System.out.println("Nom del fitxer no vàlid. \n");
			} 
		}
	}

	/// @brief Function that sets the game users and starts the game
	/// @pre Loaded chess
	/// @post Asks for the players and chooses which game style to play
	private static void initiateGame(Chess c) {
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
					twoPlayersGame(c, pOne, pTwo);
				} else {
					twoPlayersGame(c, pTwo, pOne);
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
				
				playerCPUGame(c, playerIsWhite);

				break;
			}
			case 3: {
				/// CPU vs CPU
				System.out.println("Dues CPUs");
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
	/// 	  turns. Once it has finished, prints who the winner is
	private static void twoPlayersGame(Chess chess, String pOne, String pTwo) {
		String oValue = null;
		String dValue = null;
		int rows = chess.rows();
		int cols = chess.cols();
		PieceColor currTurnColor = PieceColor.White; 		/// Always start whites
		int turnNumber = 0;
		int undoCount = 0;
		List<Turn> turns = new ArrayList<>();

		showInstructions();

		do {
			System.out.println(chess.showBoard());
			boolean originMove = true;

			if (currTurnColor == PieceColor.White) {
				System.out.println("Torn de " + pOne + " - BLANQUES");
			} else {
				System.out.println("Torn de " + pTwo + " - NEGRES");
			}

			oValue = readMovement("Coordenada origen (ex. a6): ", rows, cols, currTurnColor, chess, originMove);

			switch (oValue) {
				case "X":
					/// Do not save anything
					System.out.println("Partida acabada!");
					break;
				case "G":
					/// Todo: Save game to JSON and print filename
					System.out.println("Partida guardada!");
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
						Pair<Boolean, Position> moveResult = chess.checkMovement(origin, dest);

						if (moveResult.first) {
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
							Pair<String, String> p = new Pair<String, String>(origin.toJson(), dest.toJson());
							turns.add(new Turn(currTurnColor, p, ""));

							turnNumber++;

							/// Change turn
							currTurnColor = (currTurnColor == PieceColor.White) 
								? PieceColor.Black
								: PieceColor.White;
						} else {
							System.out.println("Moviment incorrecte!");
						}
					}
				}
			}
		} while (!oValue.equals("X") && !oValue.equals("G"));
	}

	private static void playerCPUGame(Chess chess, boolean playerIsWhite) {
		String oValue = null;
		String dValue = null;
		int rows = chess.rows();
		int cols = chess.cols();
		PieceColor currTurnColor = PieceColor.White; 		/// Always start whites
		int turnNumber = 0;
		int undoCount = 0;
		List<Turn> turns = new ArrayList<>();

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

		Cpu cpu = new Cpu(null, chess, difficulty, playerIsWhite ? PieceColor.Black : PieceColor.White);
		
		do {
			if (currTurnColor == PieceColor.White && !playerIsWhite ||
				currTurnColor == PieceColor.White && playerIsWhite) {
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
                                currTurnColor = (currTurnColor == PieceColor.White) 
					? PieceColor.Black
					: PieceColor.White;

			} else {
				showInstructions();
			
				System.out.println(chess.showBoard());
				boolean originMove = true;

				System.out.println("Torn del jugador");
				oValue = readMovement("Coordenada origen (ex. a6): ", rows, cols, currTurnColor, chess, originMove);

				switch (oValue) {
					case "X":
						/// Do not save anything
						System.out.println("Partida acabada!");
						break;
					case "G":
						/// Todo: Save game to JSON and print filename
						System.out.println("Partida guardada!");
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
							Pair<Boolean, Position> moveResult = chess.checkMovement(origin, dest);

							if (moveResult.first) {
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
								Pair<String, String> p = new Pair<String, String>(origin.toJson(), dest.toJson());
								turns.add(new Turn(currTurnColor, p, ""));

								turnNumber++;

								/// Change turn
								currTurnColor = (currTurnColor == PieceColor.White) 
									? PieceColor.Black
									: PieceColor.White;
							} else {
								System.out.println("Moviment incorrecte!");
							}
						}
					}
				}
			}
		} while (!oValue.equals("X") && !oValue.equals("G"));
	}

	//private static void twoCPUsGame(Chess chess) {}

	//private static void playerMovement() {}

	private static void cpuTurn(Chess c, Cpu cpu, Pair<Position, Position> lastMovement) {
		Pair<Position, Position> cpuMove = cpu.doMovement(lastMovement);
		
		/// CPU movement is always correct
		c.applyMovement(
			cpuMove.first,
			cpuMove.second,
			c.checkMovement(cpuMove.first, cpuMove.second).second
		);
	}

	/// @brief Reads a chess position
	/// @pre ---
	/// @post Prints the text held in t and reads positions like CN in which C is a
	///       char (column of the chess table) and N a number (row of the chess
	///       table). While this positions is not valid it will keep asking for
	///       positions. If the coordinate is valid returns the position and if it is
	///       an X, returns a null position
	private static String readMovement(String t, int rows, int cols, PieceColor colorTurn, Chess ch, boolean originMove) {
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
									if (originMove && !ch.emptyCell(p)) {
										if (ch.cellColor(p) == colorTurn) {
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
}
