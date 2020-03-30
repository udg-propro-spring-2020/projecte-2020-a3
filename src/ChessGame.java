/*
 * @author Miquel de Domingo i Giralt
 */

import javafx.application.Application;

/* 
 * @class ChessGame
 * @brief Main class of the application. Handles whereas the app will be
 *        displayed in a UI or in the console depending on the given arguments
 */
public abstract class ChessGame {
	/*
	 * @pre args is [-g] or [-c] and [gameRules.json]
	 * @post Runs UI if -g or console if -c
	 */
	public static void main(String[] args) {
		try {
			/// Length == 2 -> mode + configuration file location
			/// Length == 1 -> mode
			if (args[0].equals("-g")) {
				/// Run UI Application - Ask for files when screen opens.
				Application.launch(UIChess.java, null);
			} else if (args[0].equals("-c")) {
				switch (args.length) {
					case 1:
						/// Run game with default configuration
						ConsoleGame.play(ChessJSONParser.buildChess(/* DEFAULT FILE LOCATION */));
						break;
					case 2:
						/// Build Chess with the given configuration
						ConsoleGame.play(ChessJSONParser.buildChess(args[1]));
						break;
					default:
						_displayError();
						break;
				}
			} else {
				_displayError();
			}
		} catch (Exception e) {
			/// Class constructors can return exception if file not found.
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void _displayError() {
		System.out.println("\nIncorrect arguments number.");
		System.out.println(
				"\nGame can be run with user interface [-g] or in the console [-c]. \nYour arguments were not valid.");
	}
}
