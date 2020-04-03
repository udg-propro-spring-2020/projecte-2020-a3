/*
 * @author Miquel de Domingo i Giralt
 */

import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/* 
 * @class ConsoleGame
 * @brief Class that controls the game played in a console display.
 */
public class ConsoleGame {
	/*
	 * @brief Shows a menu asking how to start a game
	 * @pre ---
	 * @post Displays a menu with 3 options: 1. Play with the default rules 2. Play
	 * 	 	 with modified rules (enter filename) 3. Enter a saved game
	 */
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
			System.out.println("Opció: ");
			int option = in.nextInt();
			while (!VALID_OPTIONS.contains(option)) {
				System.out.println("Opció no correcte");
				System.out.println("Opció: ");
				option = in.nextInt();
			}
			//in.close();

			switch (option) {
				case 1:
					System.out.println("Creant una partida normal...");
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
		} catch (FileNotFoundException f) {
			System.out.println(f.getMessage());
		} catch (IOException i) {
			System.out.println(i.getMessage());
		}
	}

	/*
	 * @brief Asks for the filename of the configuration and starts the game
	 * @pre ---
	 * @post If the file name from the user is correct, starts the game with that configuration.
	 */
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

	/*
	 * @brief Function that controls the game flow while it has not finished.
	 * @pre Chess is loaded.
	 * @post While the game has not finished nor been saved, will keep asking for
	 * 		 turns. Once it has finished, prints who the winner is.
	 */
	public static void play(Chess c) throws IOException {
		System.out.println("Entro a play");
		Position origin = null;
		Position dest = null;
		int rows = c.rows();
		int cols = c.cols();

		System.out.println("\nESCACS\n");
		do {
			System.out.println();
			origin = readMovement("Coordenada origen (ex. a6): ", rows, cols);
			if (origin != null) {
				dest = readMovement("Coordenada destí  (ex. a6): ", rows, cols);
				if (dest != null) {
					Pair<Boolean, Position> r = c.checkMovement(origin, dest);
					if (r.first) {
						c.applyMovement(origin, dest, r.second);
						System.out.println(c.showBoard());
					} else {
						System.out.println("\nMoviment incorrecte!");
					}
				}
			}
		} while (origin != null && dest != null);

		// TODO: Handle end of game
	}

	/*
	 * @brief Reads a chess position.
	 * @pre ---
	 * @post Prints the text held in t and reads positions like CN in which C is a
	 *       char (column of the chess table) and N a number (row of the chess
	 *       table). While this positions is not valid it will keep asking for
	 *       positions. If the coordinate is valid returns the position and if it is
	 *       an X, returns a null position.
	 */
	private static Position readMovement(String t, int rows, int cols) throws IOException {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String c = "abcdefghijklmnopqrstuvwxyz";
		Position p = new Position(0, 0);
		boolean valid = false;
		do {
			System.out.print(t);
			String s = br.readLine();
			if (s.equals("X")) {
				p = null;
				valid = true;
			} else if (s.length() >= 2) {
				p.col = c.indexOf(s.charAt(0));
				if (p.col != -1 && p.col < cols) {
					try {
						p.row = Integer.parseInt(s.substring(1)) - 1;
						if (p.row >= 0 && p.row < rows) {
							valid = true;
							System.out.println("Moviment llegit: " + p.toString());
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
		} while (!valid);
		return p;
	}
}
