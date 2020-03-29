
import javafx.application.Application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cacer
 */
public abstract class ChessGame {
	/*
	 * @pre args is [-g] or [-c] and [gameRules.json]
	 * @post Runs UI if -g or console if -c
	 */
	public static void main(String[] args) {
		/*try {
			if (args.length == 2) {
				if (args[0].equals("-g")) {
					/// Run UI Application
					/// File validation ?
					Application.launch(UIGame.class, args);
				} else if (args[0].equals("-c")) {
					/// File validation ?
					ConsoleGame.play(new Chess(args[1]));//Passar params
				} else {
					_help();
				}
			} else {
				System.out.println("\nIncorrect arguments number.");
				_help();
			}
		} catch(Exception e) {
			/// Class constructors can return exception if file not found.
			System.out.printl(e.showMessage());
			e.printStackTrace();
		}*/
	}

	private static void _help() {
		System.out.println("\nGame can be run with user interface [-g] or in the console [-c]. \nYour arguments were not valid.");
	}
}