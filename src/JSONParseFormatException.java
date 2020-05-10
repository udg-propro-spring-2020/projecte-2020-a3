/**
 * @author Miquel de Domingo i Giralt
 * @file JSONParseFormatException.java
 * @class JSONParseFormatException
 * @brief Exception thrown when there is a file format incongruence
 */
public class JSONParseFormatException extends Exception {
    /// @brief Enum to define the exception type
    public static enum ExceptionType {
        EMPTY_LIST, 
        ILLEGAL_NUMBER,
        ILLEGAL_NAME,
        ILLEGAL_MOVE,
        ILLEGAL_TYPE,
        END_OF_GAME;

        @Override
        public String toString() {
            switch (this) {
                case EMPTY_LIST:
                    return "ERROR 01: Llista buida.";
                case ILLEGAL_NUMBER:
                    return "ERROR 02: Nombre no vàlid.";
                case ILLEGAL_NAME:
                    return "ERROR 03: Nom de peça no vàlid.";
                case ILLEGAL_MOVE:
                    return "ERROR 04: Moviment no vàlid.";
                case ILLEGAL_TYPE:
                    return "ERROR 05: Tipus de peça no vàlid.";
                default:
                    return "ERROR 06: Final de partida no vàlid.";
            }
        }
    }

    ExceptionType type;         ///< Defines the exception type

    public JSONParseFormatException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }    

    /// @brief To know the exceptions type
    /// @pre ---
    /// @post Returns the type of the exception
    public String getType() {
        return this.type.toString();
    }
}