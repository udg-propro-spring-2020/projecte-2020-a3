/*
 * @author Miquel de Domingo i Giralt
 * @file Turn.java
 * @class Turn 
 * @brief Helds the information of a turn
 */
public class Turn {
    private PieceColor color;
    private Pair<String, String> move;
    private String result;
    private Pair<String, Position> kill;

    Turn(PieceColor color, Pair<String, String> move, String result, Pair<String, Position> kill) {
        this.color = color;
        this.move = move;
        this.result = result;
        this.kill = kill;
    }

    /// @brief To know the movement origin cell
    /// @pre ---
    /// @post Returns the movement origin cell
    public String origin() {
        return move.first;
    }

    /// @brief To know the movement destination cell
    /// @pre ---
    /// @post Returns the movement destination cell
    public String destination() {
        return move.second;
    }

    /// @brief To know if in the movement a piece has been killed
    /// @pre ---
    /// @post Returns the pair holding the killed piece and its positino. If
    ///       there was no piece killed, returns null.
    public Pair<String, Position> kill() {
        return kill;
    }

    /// @pre ---
    /// @post Returns a String of the object properties in JSON format (tabbed)
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
