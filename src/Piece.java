/*
 * @author Miquel de Domingo i Giralt
 * @file Piece.java
 * @class Piece
 * @brief Holds the information of the piece
 */

public class Piece {
    private static int idGenerator = 0;

    private int id;                 ///> Unique piece identifier
    private PieceType type;         ///> Type of the piece
    private String symbol;          ///> Piece's symbol
    private boolean moved;          ///> Whether the piece has been moved or not
    private PieceColor color;       ///> Piece's color

    Piece(PieceType type, boolean moved, PieceColor color) {
        this.id = idGenerator;
        this.type = type;
        this.moved = moved;
        this.color = color;

        /// Since type symbol will ALWAYS be uppercase, we only nee to change it
        /// if the piece color is black.
        this.symbol = color.toString().equals("NEGRES") 
            ? type.ptSymbol().toLowerCase()
            : type.ptSymbol();

        idGenerator++;          /// Incement ID
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
        this.moved = !this.moved;
    }

    /// @brief Piece comparator
    /// @pre ---
    /// @post Returns if the given piece equals the current 
    /// @trhows NullPointerException if the given piece is null
    public boolean equals(Piece p) throws NullPointerException {
        if (p == null) {
            throw new NullPointerException("Null piece");
        } else {
            return p.id == this.id;
        }
    }
}
