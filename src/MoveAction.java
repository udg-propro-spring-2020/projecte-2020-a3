/*
 * @author David Caceres Gonzalez
 * @file MoveAction.java
 * @class MoveAction
 * @brief The move make some special actions
 */
public enum MoveAction { 
    Incorrect, Correct, Check, Checkmate, Promote, Castling;

    @Override
    public String toString() {
        switch(this) {
            case Correct:
                return new String("CORRECT");
            case Escac:
                return new String("ESCAC");
            case Escacimat:
                return new String("ESCACIMAT");
            case Promote:
                return new String("PROMOTION");
            case Castling:
                return new String("CASTLING");
            default:
                return new String("INCORRECT");
        }
    }
}