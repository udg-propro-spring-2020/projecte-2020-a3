/**
 * @author Miquel de Domingo i Giralt
 * @file PieceColor.java
 * @class PieceColor
 * @brief Piece color enum
 */
public enum PieceColor { 
    Black, White;

    @Override
    public String toString() {
        switch(this) {
            case Black:
                return new String("NEGRES");
            default:
                // What's not black, is white
                return new String("BLANQUES");
        }
    }

    /// @brief Returns a string with the name in English
    /// @pre ---
    /// @post Returns a string with the name in English
    public String value() {
        switch(this) {
            case Black:
                return new String("Blacks");
            default:
                return new String("Whites");
        }
    }
}