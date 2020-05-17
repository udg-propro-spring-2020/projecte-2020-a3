/// @author Miquel de Domingo i Giralt
/// @file Turn.java
/// @class Turn 
/// @brief Holds the information of a turn 
public class Turn implements JSON, Cloneable {
    private PieceColor _color;
    private Pair<String, String> _move;
    private String _result;
    private boolean _promotionTurn;
    private boolean _castlingTurn;

    /// @brief Basic turn constructor
    Turn(PieceColor color, Pair<String, String> move, String result) {
        this._color = color;
        this._move = move;
        this._result = result;
        this._promotionTurn = false;
        this._castlingTurn = false;
    }

    /// @brief Promotion turn constructor
    /// @details The turn is defined as a castling turn
    Turn(PieceColor color, PieceType original, PieceType promoted) {
        this._color = color;
        this._result = promotionString(original, promoted);
        this._promotionTurn = true;
        this._castlingTurn = false;
    }

    /// @brief Castling turn constructor
    /// @details Sets the result to an empty string and the turn is defined as a castling turn
    Turn(PieceColor color, Pair<String, String> move) {
        this._color = color;
        this._move = move;
        this._promotionTurn = false;
        this._castlingTurn = true;
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
    /// @throws UnsupportedOperationException if the method is called when it is a not promotion turn
    public String origin() throws UnsupportedOperationException {
        if (_promotionTurn) {
            throw new UnsupportedOperationException("Origin cannot be called when it is a promotion turn");
        } else if (_castlingTurn) {
            throw new UnsupportedOperationException("Origin cannot be called when it is a castling turn");
        }

        return _move.first;
    }

    /// @brief To know the movement destination cell
    /// @pre ---
    /// @post Returns the movement destination cell
    /// @throws UnsupportedOperationException if the method is called when it is a not promotion turn
    public String destination() {
        if (_promotionTurn) {
            throw new UnsupportedOperationException("Destination cannot be called when it is a promotion turn");
        } else if (_castlingTurn) {
            throw new UnsupportedOperationException("Destination cannot be called when it is a castling turn");
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
        } else if (_castlingTurn) {
            throw new UnsupportedOperationException("MoveAsPair cannot be called when it is a castling turn");
        }

        return new Pair<Position, Position>(
            new Position(origin()),
            new Position(destination())
        );
    }

    /// @brief Returns the pieces involved in a promotion
    /// @pre This is a promotion turn
    /// @post Returns a pair of strings as the promotion (first is original, second promoted)
    /// @throws UnsupportedOperationException if the method is called when it is a NOT promotion turn
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

    /// @brief Returns the pieces involved in the casling
    /// @pre This is a castling turn
    /// @post Returns a pair of pairs containing the positions of the castling (origin, destination)
    /// @throws UnsupportedOperationException if the methods is called when it is NOT a castling turn
    public Pair<Pair<String, String>, Pair<String, String>> castlingAsPair() throws UnsupportedOperationException {
        if (!_castlingTurn) {
            throw new UnsupportedOperationException("CastlingAsPair cannot be called when it is NOT a castling turn");
        }

        // First pair
        String partOne[] = _move.first.split("-");
        Pair<String, String> origin = new Pair<String, String>(partOne[0], partOne[1]);

        // Second pair
        String partTwo[] = _move.second.split("-");
        Pair<String, String> destination = new Pair<String, String>(partTwo[0], partTwo[1]);

        // Result 
        return new Pair<Pair<String,String>,Pair<String,String>>(origin, destination);
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
