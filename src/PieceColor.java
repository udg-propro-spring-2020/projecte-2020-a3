/*
 * @author Miquel de Domingo i Giralt
 */

/*
 * @brief Describes piece color 
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