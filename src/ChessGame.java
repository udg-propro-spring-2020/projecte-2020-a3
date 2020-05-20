import javafx.application.Application;

/// @author Miquel de Domingo i Giralt
/// @file ChessGame.java
/// @class ChessGame
/// @brief Main class of the application
/// @details Handles whereas the app will be displayed in a UI or in the console depending on the given arguments
public abstract class ChessGame {
	/// @brief Runs the application
	/// @pre args is [-g] or [-c] 
	/// @post Runs UI if -g or console if -c
	public static void main(String[] args) {
		Application.launch(UIChess.class, args);
		try {
			if (args.length > 0) {
				if (args[0].equals("-g")) {
					/// Run UI Application - Ask for files when screen opens.
					System.out.println("UI Application running.");
				} else if (args[0].equals("-c")) {
					ConsoleGame.start();
				} else {
					_displayHelp();
				}
			} else {
				_displayHelp();
			}
		} catch (Exception e) {
			/// Class constructors can return exception if file not found.
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}

	/// @brief Mostra un missatge d'ajuda
	/// @pre Nombre d'arguments entrar incorrecte
	/// @post Mostra per pantalla el missatge d'ajuda sobre com executar l'aplicació.
	private static void _displayHelp() {
		System.out.println("Nombre incorrecte d'arguments");
		System.out.println("El joc es pot executar amb interfície gràfica [-g] o en consola [-c].");
		System.out.println("Exemple: ChessGame -c");
	}
}
