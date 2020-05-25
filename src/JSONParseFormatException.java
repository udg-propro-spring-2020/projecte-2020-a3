/// @author Miquel de Domingo i Giralt
/// @file JSONParseFormatException.java
/// @class JSONParseFormatException
/// @brief Exception thrown when there is a file format incongruence
public class JSONParseFormatException extends Exception {
    /// @brief Enum to define the exception type
    public static enum ExceptionType {
        EMPTY_LIST,                     ///< Used when a list should not be empty
        KING_MISSING,                   ///< Used when a king piece is missing
        KING_REPEATED,                  ///< Used when there are multiple kings in the same team
        KING_VALUE,                     ///< Used when the king does not have the max value or it is not correct
        ILLEGAL_NUMBER,                 ///< Used when a number has an illegal format
        ILLEGAL_NAME,                   ///< Used when a piece has an illegal piece type name
        ILLEGAL_MOVE,                   ///< Used when a move is not correct
        ILLEGAL_MOVE_PROPERTY,          ///< Used when a move property is not correct         
        ILLEGAL_TYPE,                   ///< Used when a piece type does not exist or it is nor correct
        ILLEGAL_COLOR,                  ///< Used when a piece has a color which is not a PieceColor
        ILLEGAL_PROMOTION,              ///< Used when a promotion is not correct or not in the correct turn
        ILLEGAL_INITIAL_POSITION,       ///< Used when pieces share the same initial position or when it is not valid
        END_OF_GAME;                    ///< Used when the end of game it is no valid

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
                case ILLEGAL_MOVE_PROPERTY:
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