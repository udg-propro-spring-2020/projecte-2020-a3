/// @author Miquel de Domingo i Giralt
/// @file Turn.java
/// @class Turn 
/// @brief Holds the information of a turn 
public class Turn implements JSON, Cloneable {
    private PieceColor _color;
    private Pair<String, String> _move;
    private String _result;

    Turn(PieceColor color, Pair<String, String> move, String result) {
        this._color = color;
        this._move = move;
        this._result = result;
    }

    /// @brief To know the movement origin cell
    /// @pre ---
    /// @post Returns the movement origin cell
    public String origin() {
        return _move.first;
    }

    /// @brief To know the movement destination cell
    /// @pre ---
    /// @post Returns the movement destination cell
    public String destination() {
        return _move.second;
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

    /// @brief Returns the result of the turn
    /// @pre ---
    /// @post Returns the result of the movement
    public String turnResult() {
        return this._result;
    }

    /// @pre ---
    /// @post Returns a String of the object properties in JSON format (tabbed)
    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_START)
            .append(ToJSONParserHelper.propertyToJSON("torn", _color.toString(), true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("origen", _move.first, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("desti", _move.second, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("resultat", _result, true, false, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_END);
        return s.toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("[" + _color.toString() + ", " + _move.first + "-" + _move.second + ", " + _result + "]");

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
