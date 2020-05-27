/** 
 * @author David Caceres Gonzalez
 * @file MoveAction.java
 * @class MoveAction
 * @brief The move make some special actions
 */
public enum MoveAction { 
    Incorrect, Correct, Check, Checkmate, Promote, Castling, Drowned;

    @Override
    public String toString() {
        switch(this) {
            case Correct:
                return new String("CORRECT");
            case Check:
                return new String("ESCAC");
            case Checkmate:
                return new String("ESCAC I MAT");
            case Promote:
                return new String("PROMOTION");
            case Castling:
                return new String("CASTLING");
            case Drowned:
                return new String("REI OFEGAT");
            default:
                return new String("INCORRECT");
        }
    }
}