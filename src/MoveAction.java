/*
 * @author David Caceres Gonzalez
 * @file MoveAction.java
 * @class MoveAction
 * @brief The move make some special actions
 */
public enum MoveAction { 
    Incorrect, Correct, Escac, Escacimat, Promote, Castling;

    @Override
    public String toString() {
        switch(this) {
            case Correct:
                return new String("Correcte");
            case Escac:
                return new String("Escac");
            case Escacimat:
                return new String("Escac i mat");
            case Promote:
                return new String("Promoció");
            case Castling:
                return new String("Castling");
            default:
                return new String("Incorrecte");
        }
    }
}