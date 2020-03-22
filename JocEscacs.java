
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
public class JocEscacs {
    /*
     * @pre args is [-g] or [-c] and [gameRules.json]
     * @post Runs UI if -g or console if -c
     */
    /*
 * @class ConsoleGame
 * @brief Functional class which keeps the game running while it has not finished. 
 */
    public static void play(String fileLocation){
        try {
            Chess c = new Chess(fileLocation);
            int result = -1;
            do {
                // TODO: Refer / desfer
                c.nextTurn();
                c.saveTurn();
                result = c.hasNotFinished();
            } while (result != 0 && result != 1);
            if (result == 0) {
                c.showResults();
            } else {
                c.saveGame();
            }
        } catch(Exception e) {
            /// File not found
            /// Icorrect file format
            /// ...
            e.getMessage();
            e.printStackTrace();
        }
    }

}



