/*
<<<<<<< HEAD
 * @author Miquel de Domingo i Giralt
 */

/*
 * @brief Describes piece color 
=======
 * @file PieceColor.java
 * @class PieceColor
 * @brief Piece color enum
>>>>>>> 62071a8d0f37261f0ebc4f0b4051a260f54fe6ed
 */
public enum PieceColor { 
    Black, White;

    @Override
    public String toString() {
        switch(this) {
            case Black:
                return new String("NEGRES");
            default:
                /// What's not black, is white
                return new String("BLANQUES");
        }
    }
}