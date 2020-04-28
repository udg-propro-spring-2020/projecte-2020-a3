/*
 * @author Miquel de Domingo i Giralt
 * @file Turn.java
 * @class Turn 
 * @brief Helds the information of a turn
 */
public class Turn implements JSON, Cloneable {
    private PieceColor color;
    private Pair<String, String> move;
    private String result;

    Turn(PieceColor color, Pair<String, String> move, String result) {
        this.color = color;
        this.move = move;
        this.result = result;
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

    /// @brief Returns the move as a pair of positions
    /// @pre ---
    /// @post Returns the move as a pair of positions
    public Pair<Position, Position> moveAsPair() {
        return new Pair<Position, Position>(
            new Position(origin()),
            new Position(destination())
        );
    }

    /// @pre ---
    /// @post Returns a String of the object properties in JSON format (tabbed)
    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_START)
            .append(ToJSONParserHelper.propertyToJSON("torn", color.toString(), true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("origen", move.first, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("desti", move.second, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("resultat", result, true, false, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_END);
        return s.toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("[" + color.toString() + ", " + move.first + "-" + move.second + ", " + result + "]");

        return s.toString();
    }

    @Override
    public Object clone() {
        Turn cloned = null;
        
        try {
            cloned = (Turn) super.clone();
        } catch (CloneNotSupportedException c) {
            System.err.println("Turn clone exception");
        }

        return cloned;
    }
}
