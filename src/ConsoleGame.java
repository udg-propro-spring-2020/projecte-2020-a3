/*
 * @author Miquel de Domingo i Giralt
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;

/* 
 * @class ConsoleGame
 * @brief Class that controls the game played in a console display.
 */
public class ConsoleGame {
	
	/// @brief Shows a menu asking how to start a game
	/// @pre ---
	/// @post Displays a menu with 3 options: 1. Play with the default rules 2. Play
	/// 	  with modified rules (enter filename) 3. Enter a saved game
	public static void start() {
		System.out.println("+---------------- MENU ----------------+");
		System.out.println("|                                      |");
		System.out.println("|  Escull una opcio (num):             |");
		System.out.println("|    1. Partida normal                 |");
		System.out.println("|    2. Entrar partida configurada     |");
		System.out.println("|    3. Entrar una partida carregada   |");
		System.out.println("|    0. Sortir                         |");
		System.out.println("|                                      |");
		System.out.println("+--------------------------------------+");

		Scanner in = new Scanner(System.in);
		final List<Integer> VALID_OPTIONS = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));

		try {
			System.out.print("Opció: ");
			int option = in.nextInt();
			while (!VALID_OPTIONS.contains(option)) {
				System.out.println("Opció no correcte");
				System.out.print("Opció: ");
				option = in.nextInt();
			}
			

			switch (option) {
				case 1:
					System.out.println("Creant una partida normal...\n");
					play(ChessJSONParser.buildChess("./data/default_game.json"));
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

			in.close();
		} catch (FileNotFoundException f) {
			System.out.println(f.getMessage());
		} catch (IOException i) {
			System.out.println(i.getMessage());
		}
	}

	/// @brief Asks for the filename of the configuration and starts the game
	/// @pre ---
	/// @post If the file name from the user is correct, starts the game with that configuration.
	private static void configuredChessGame(String text) {
		Scanner in = new Scanner(System.in);
		boolean validFileLocation = false;

		while (!validFileLocation) {
			System.out.println("[Escriu EXIT per sortir]");
			System.out.println(text);
			String fileLocation = in.nextLine();
			try {
				if (fileLocation.toUpperCase().equals("EXIT")) {
					System.out.println("Sortint de l'aplicació");
				} else {
					Chess c = ChessJSONParser.buildChess(fileLocation);
					/// If it gets here, there will be no exception of file not found
					validFileLocation = true;

					/// Start game
					play(c);
				}
			} catch (FileNotFoundException e) {
				/// Keep asking for files
				System.out.print("Nom del fitxer no vàlid.");
				System.out.print("Entra el nom del fitxer amb la configuració: ");
			} catch (IOException e) {
				System.out.print("Error en l'entrada per teclat.");
			}
		}
		in.close();
	}

	/// @brief Function that controls the game flow while it has not finished.
	/// @pre Chess is loaded.
	/// @post While the game has not finished nor been saved, will keep asking for
	/// 	  turns. Once it has finished, prints who the winner is.
	/// 
	public static void play(Chess c) throws IOException {
		String alph = "abcdefghijklmnopqrstuvwxyz";
		String oValue = null;
		String dValue = null;
		int rows = c.rows();
		int cols = c.cols();
		PieceColor currentTurn = PieceColor.White; 		/// Always start whites
		List<Turn> turns = new ArrayList<>();

		showInstructions();
		
		do {
			System.out.println(c.showBoard());
			boolean originMove = true;
			oValue = readMovement("Coordenada origen (ex. a6): ", rows, cols, currentTurn, c, originMove);
			
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
					/// Todo: go backwards one movement
					System.out.println("Moviment desfet!");
					break;
				case "R": 
					/// Todo: go forwards one movement
					System.out.println("Moviment refet!");
					break;
				default: {
					originMove = false;
					System.out.println("Origen: " + oValue);
					dValue = readMovement("Coordenada destí  (ex. a6): ", rows, cols, currentTurn, c, originMove);
					
					if (!dValue.equals("O")) {
						System.out.println("Dest: " + dValue);

						/// Create positions with the read strings
						Position origin = new Position(alph.indexOf(oValue.charAt(0)), Integer.parseInt(oValue.substring(1)) - 1);
						Position dest =  new Position(alph.indexOf(dValue.charAt(0)), Integer.parseInt(dValue.substring(1)) - 1); 
						Pair<Boolean, Position> r = c.checkMovement(origin, dest);

						if (r.first) {
							c.applyMovement(origin, dest, r.second);
							
							System.out.println("ERrr");
							/// Save turn
							Pair<String, String> p = new Pair<String, String>(origin.toJson(), dest.toJson());
							turns.add(new Turn(currentTurn, p, ""));

							/// Change turn
							currentTurn = currentTurn == PieceColor.White 
								? PieceColor.Black 
								: PieceColor.White;
						} else {
							System.out.println("Moviment incorrecte!");
						}
					}
				}
			}
		} while (!oValue.equals("X") && !oValue.equals("G"));

		// TODO: Handle end of game
	}

	private static void showInstructions() {
		System.out.println("+--------------  COMANDES  ---------------+");
		System.out.println("|  [Quan es demana posicion d'origen]     |");
		System.out.println("|      - X: Acabar la partida             |");
		System.out.println("|      - D: Desfer moviment               |");
		System.out.println("|      - R: Refer moviment                |");
		System.out.println("|      - G: Guardar partida               |");
		System.out.println("|                                         |");
		System.out.println("|   [Quan es demana posicion destí]       |");
		System.out.println("|      - O: Tornar a escollir origen      |");
		System.out.println("+-----------------------------------------+");
		System.out.println();
	}

	/// brief Reads a chess position.
	/// @pre ---
	/// @post Prints the text held in t and reads positions like CN in which C is a
	///      char (column of the chess table) and N a number (row of the chess
	///      table). While this positions is not valid it will keep asking for
	///      positions. If the coordinate is valid returns the position and if it is
	///      an X, returns a null position.
	///
	private static String readMovement(String t, int rows, int cols, PieceColor colorTurn, Chess ch, boolean originMove) throws IOException {
		Scanner in = new Scanner(System.in);
		String c = "abcdefghijklmnopqrstuvwxyz";
		String s;
		Position p = new Position(0, 0);
		boolean stop = false;
		
		do {
			System.out.print(t);
			s = in.nextLine();

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
					default: {
						p.col = c.indexOf(s.charAt(0));
						if (p.col != -1 && p.col < cols) {
							try {
								p.row = Integer.parseInt(s.substring(1)) - 1;
								if (p.row >= 0 && p.row < rows) {
									if (originMove && !ch.emptyCell(p)) { 
										if (ch.cellColor(p) == colorTurn){
											stop = true;								
											System.out.println("Moviment llegit: " + p.toString());
										} else {
											System.out.println("És el torn de " + colorTurn.toString());
											System.out.println("Escull una peça de " + colorTurn.toString());
										}
									} else if (!originMove){ 
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
}