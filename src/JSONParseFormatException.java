/// @author Miquel de Domingo i Giralt
/// @file JSONParseFormatException.java
/// @class JSONParseFormatException
/// @brief Exception thrown when there is a file format incongruence
public class JSONParseFormatException extends Exception {
    /// @brief Enum to define the exception type
    public static enum ExceptionType {
        EMPTY_LIST,
        KING_MISSING,
        KING_REPEATED,
        KING_VALUE,
        ILLEGAL_NUMBER,
        ILLEGAL_NAME,
        ILLEGAL_MOVE,
        ILLEGAL_MOVE_VALUE,
        ILLEGAL_TYPE,
        ILLEGAL_COLOR,
        ILLEGAL_PROMOTION,
        ILLEGAL_INITIAL_POSITION,
        END_OF_GAME;

        @Override
        public String toString() {
            switch (this) {
                case EMPTY_LIST:
                    return "ERROR E1: Empty list";
                case KING_MISSING:
                    return "ERROR K1: King must exist in the game";
                case KING_REPEATED:
                    return "ERROR K2: King cannot be repeated";
                case KING_VALUE:
                    return "ERROR K3: Invalig king's value";
                case ILLEGAL_NUMBER:
                    return "ERROR I1: Invalid number";
                case ILLEGAL_NAME:
                    return "ERROR I2: Piece name not valid";
                case ILLEGAL_MOVE:
                    return "ERROR I3: Movement not valid";
                case ILLEGAL_MOVE_VALUE:
                    return "ERROR I4: Movement property not valid";
                case ILLEGAL_TYPE:
                    return "ERROR I5: Piece type not valid";
                case ILLEGAL_COLOR:
                    return "ERROR I6: Color not valid";
                case ILLEGAL_PROMOTION:
                    return "ERROR I7: Piece promotion not valid";
                case ILLEGAL_INITIAL_POSITION:
                    return "ERROR I8: Illegal/Repeated position";
                default:
                    return "ERROR E2: End of game not valid";
            }
        }
    }

    private ExceptionType _type;         ///< Defines the exception type

    /// @brief Exception constructor
    /// @param message Message to be held
    /// @param type Type of the exception
    JSONParseFormatException(String message, ExceptionType type) {
        super(message);
        this._type = type;
    }    

    /// @brief To know the exceptions type
    /// @pre ---
    /// @post Returns the type of the exception
    public String getType() {
        return this._type.toString();
    }
}