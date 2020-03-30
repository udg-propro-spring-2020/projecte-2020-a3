/*
 * @author Miquel de Domingo i Giralt
 */

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/* 
 * @class ConsoleGame
 * @brief Class that controls the game played in a console display.
 */
public class ConsoleGame {
	/*
	 * @brief Function that controls the game flow while it has not finished.
	 * @pre Chess is loaded.
	 * @post While the game has not finished nor been saved, will keep asking for turns.
	 *       Once it has finished, prints who the winner is.
	 */
	public static void play(Chess c) throws IOException {
		Position origin = null;
		Position destiny = null;
		int rows = c.rows();
		int cols = c.cols();
		System.out.println("\nESCACS\n");
		do {
			System.out.println();
			origin = readMovement("Coordenada origen (ex. a6): ", rows, cols);
			if (origin != null) {
				destiny = readMovement("Coordenada destí  (ex. a6): ", rows, cols);
				if (destiny != null) {
					Pair<Boolean, Position> r = c.checkMovement(origin, destiny);
					if (r.first) {
						c.applyMovement(origin, destiny, r.second);
						System.out.println(c.showBoard());
					} else {
						System.out.println("\nMoviment incorrecte!");
					}
				}
			}
		} while (origin != null && destiny != null);
	}

	/**
	 * @brief Reads a chess positions.
	 * @pre ---
	 * @post Prints the text held in t and reads positions like CN in which 
	 *       C is a char (column of the chess table) and N a number (row of 
	 *       the chess table). While this positions is not valid it will keep asking
	 *	 for positions. If the coordinate is valid returns the position and if
	 *	 it is an X, returns a null position.
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

	
