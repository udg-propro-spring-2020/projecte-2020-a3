/// @author Miquel de Domingo i Giralt
/// @file Turn.java
/// @class Turn 
/// @brief Holds the information of a turn 
public class Turn implements JSON, Cloneable {
    private PieceColor _color;
    private Pair<String, String> _move;
    private String _result;
    private boolean _promotionTurn;

    /// @brief Basic turn constructor
    Turn(PieceColor color, Pair<String, String> move, String result) {
        this._color = color;
        this._move = move;
        this._result = result;
        this._promotionTurn = false;
    }

    /// @brief Promotion turn constructor
    Turn(PieceColor color, PieceType original, PieceType promoted) {
        this._color = color;
        this._result = promotionString(original, promoted);
        this._promotionTurn = true;
    }

    /// @brief To know the turn color
    /// @pre ---
    /// @post Returns the color property of the turn
    public PieceColor color() {
        return this._color;
    }

    /// @brief To know the movement origin cell
    /// @pre ---
    /// @post Returns the movement origin cell
    /// @throws UnsuportedOperationException if the method is called when it is a not promotion turn
    public String origin() throws UnsupportedOperationException {
        if (_promotionTurn) {
            throw new UnsupportedOperationException("Origin cannot be called when it is a promotion turn");
        }

        return _move.first;
    }

    /// @brief To know the movement destination cell
    /// @pre ---
    /// @post Returns the movement destination cell
    /// @throws UnsuportedOperationException if the method is called when it is a not promotion turn
    public String destination() {
        if (_promotionTurn) {
            throw new UnsupportedOperationException("Destination cannot be called when it is a promotion turn");
        }

        return _move.second;
    }

    /// @brief Returns the move as a pair of positions
    /// @pre ---
    /// @post Returns the move as a pair of positions
    /// @throws UnsuportedOperationException if the method is called when it is a not promotion turn
    public Pair<Position, Position> moveAsPair() {
        if (_promotionTurn) {
            throw new UnsupportedOperationException("MoveAsPair cannot be called when it is a promotion turn");
        }

        return new Pair<Position, Position>(
            new Position(origin()),
            new Position(destination())
        );
    }

    /// @brief Returns the pieces involved in a promotion
    /// @pre This is a promotion turn
    /// @post Returns a pair of strings as the promotion (first is original, second promoted)
    /// @throws UnsuportedOperationException if the method is called when it is a not promotion turn
    public Pair<String, String> promotionAsPair() throws UnsupportedOperationException {
        if (!_promotionTurn) {
            throw new UnsupportedOperationException("PromotionAsPair cannot be called when it is NOT a promotion turn");
        }
        
        // Separate by the :
        String[] partOne = _result.split(":");
        // Separate by the -
        String[] partTwo = partOne[1].trim().split("-");

        return new Pair<String, String>(partTwo[0], partTwo[1]);
    }

    /// @brief Returns the result of the turn
    /// @pre ---
    /// @post Returns the result of the movement
    public String turnResult() {
        return this._result;
    }

    /// @brief Returns if this is a promotion turn
    /// @pre ---
    /// @post Returns if this is a promotion turn
    public boolean isPromotionTurn() {
        return this._promotionTurn;
    }

    /// @brief Creates a String describing a promition
	/// @pre @p original && @p promoted != null
	/// @post Creates a promotion String like PROMOCIÓ: ORIGINAL-PROMOTED
	private static String promotionString(PieceType original, PieceType promoted) {
		StringBuilder s = new StringBuilder();
		s.append("PROMOCIÓ: ")
			.append(original.ptName())
			.append("-")
			.append(promoted.ptName());
		
		return s.toString();
	}

    /// @pre ---
    /// @post Returns a String of the object properties in JSON format (tabbed)
    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_START)
            .append(ToJSONParserHelper.propertyToJSON("torn", _color.toString(), true, true, ToJSONParserHelper.THREE_TABS));

        if (!_promotionTurn) {
            s.append(ToJSONParserHelper.propertyToJSON("origen", _move.first, true, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("desti", _move.second, true, true, ToJSONParserHelper.THREE_TABS));
        } else {
            s.append(ToJSONParserHelper.propertyToJSON("origen", "", true, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("desti", "", true, true, ToJSONParserHelper.THREE_TABS));
        }

        s.append(ToJSONParserHelper.propertyToJSON("resultat", _result, true, false, ToJSONParserHelper.THREE_TABS))
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
