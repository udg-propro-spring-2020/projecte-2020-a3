/*
 * @author Miquel de Domingo i Giralt
 */

/*
 * @brief Describes piece color 
 */
enum PieceColor { 
    Black, White;

    @Override
    public String toString() {
        switch(this) {
            case Black:
                return new String("Black");
            default:
                /// What's not black, is white
                return new String("White");
        }
    }
}

/*
 * @file Turn.java
 * 
 * @class Turn
 * 
 * @brief Helds the information of a turn
 */
public class Turn {
    private PieceColor color;
    private Pair<String, String> move;
    private String result;

    Turn(PieceColor color, Pair<String, String> move, String result) {
        this.color = color;
        this.move = move;
        this.result = result;
    }

    /*
     * @pre ---
     * @post Returns a String of the object properties in JSON format (tabbed)
     */
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append("\t\t{\n")                             /// Start
            .append("\t\t\t")                           /// Identation
            .append("\"torn\": ")                       /// Key
            .append("\"" + color.toString() + "\", \n") /// Value
            .append("\t\t\t")                           /// Identation
            .append("\"origen\": ")                     /// Key
            .append("\"" + move.first + "\", \n")       /// Value
            .append("\t\t\t")                           /// Identation
            .append("\"desti\": ")                      /// Key
            .append("\"" + move.second + "\", \n")      /// Value
            .append("\t\t\t")                           /// Identation
            .append("\"resultat\": ")                   /// Key
            .append("\"" + result + "\", \n")           /// Value
            .append("\t\t}\n");                         /// End

        return s.toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("[" + color.toString() + ", " + move.first + "-" + move.second + ", " + result + "]");

        return s.toString();
    }
}
