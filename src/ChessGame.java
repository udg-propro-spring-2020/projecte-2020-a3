
import javafx.application.Application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public abstract class ChessGame {
	/*
	 * @pre args is [-g] or [-c] and [gameRules.json]
	 * 
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
