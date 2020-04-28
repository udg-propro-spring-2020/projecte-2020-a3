import java.util.ArrayList;
import java.util.List;

/*
 * @author Miquel de Domingo i Giralt
 * @file Piece.java
 * @class Piece
 * @brief Holds the information of the piece
 */

public class Piece implements JSON, Cloneable {
    private PieceType type;         ///> Type of the piece
    private String symbol;          ///> Piece's symbol
    private boolean moved;          ///> Whether the piece has been moved or not
    private PieceColor color;       ///> Piece's color
    private boolean direction;      ///> Piece's direction, true (black), false (white)

    Piece(PieceType type, boolean moved, PieceColor color) {
        this.type = type;
        this.moved = moved;
        this.color = color;

        /// Since type symbol will ALWAYS be uppercase, we only nee to change it
        /// if the piece color is black.
        this.symbol = color.toString().equals("NEGRES") 
            ? type.ptSymbol().toLowerCase()
            : type.ptSymbol();
        
        /// Set the direction
        this.direction = this.color.toString().equals("NEGRES")
            ? true
            : false;
    }

    /// @brief Default constructor with symbol
    Piece(PieceType type, String symbol, boolean moved, PieceColor color) {
        this.type = type;
        this.moved = moved;
        this.color = color;

        /// Since type symbol will ALWAYS be uppercase, we only nee to change it
        /// if the piece color is black.
        this.symbol = color.toString().equals("NEGRES") 
            ? type.ptSymbol().toLowerCase()
            : type.ptSymbol();
    }

    /// @brief Copy constructor
    /// @pre @p copy cannot be null
    /// @post Creates a Piece copy of @p copy
    /// @throw NullPointerException if @p copy is null
    Piece(Piece copy) {
        if (copy == null) {
            throw new NullPointerException("Copy Piece cannot pass a null element");
        } else {
            this.type = copy.type;
            this.moved = copy.moved;
            this.color = copy.color;
            this.symbol = copy.symbol;
        }
    }

    /// @brief To know the piece's type
    /// @pre ---
    /// @post Returns the piece's type
    public PieceType type() {
        return this.type;
    }
    
    /// @brief To know the piece's symbol value
    /// @pre ---
    /// @post Returns the symbol value
    public String symbol() {
        return this.symbol;
    }

    /// @brief To know if the piece has moved or not
    /// @pre ---
    /// @post Returns the piece's moved current value
    public boolean hasMoved() {
        return this.moved;
    }

    /// @brief To know the piece's color
    /// @pre ---
    /// @post Returns the piece's color
    public PieceColor color() {
        return this.color;
    }

    /// @brief Toggles piece color
    /// @pre ---
    /// @post Inverts the value of the moved property
    public void toggleMoved() {
        this.moved = false;
    }

    /// @brief Returns the possible movements list from the piece type
    /// @pre ---
    /// @post Returns the possible movements list from the piece type
    public List<Movement> pieceMovements() {
        List<Movement> temp = new ArrayList<>();
        /// Since the object will change, we have to clone it
        type.ptMovements().forEach(
            (m) -> temp.add((Movement) m.clone())
        );

        if (!this.moved) {
            /// Clone it again
            type.ptInitMovements().forEach(
                (m) -> temp.add((Movement) m.clone())
            );
        }
        
        /// Filter data if needed
        if (this.direction) {
            temp.forEach(
                (m) -> m.toggleDirection()
            );
        }

        return temp;
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.propertyToJSON("tipus", type.ptName().toUpperCase(), true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("moguda", moved ? true : false, false, false, ToJSONParserHelper.THREE_TABS));
        return null;
    }

    @Override
    public Object clone() {
        Piece cloned = null;

        try {
            cloned = (Piece) super.clone();
        } catch (CloneNotSupportedException c) {
            System.err.println("Piece clone exception");
        }

        /// Cloning piece type value
        cloned.type = (PieceType) this.type.clone();

        return cloned;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(symbol);
    }
}
