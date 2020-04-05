/*
 * @author Miquel de Domingo i Giralt
 */

import java.util.List;

/*
 * @file Piece.java
 * @class Piece
 * @brief Holds the information of the piece
 */
public class Piece {
    private String name;                            ///< Piece name
    private String symbol;                          ///< Piece symbol
    private String wImage;                          ///< Piece white image location
    private String bImage;                          ///< Piece black image location
    private int value;                              ///< Piece in-game value
    private boolean promotable;                     ///< Piece's capacity to be promotable
    private boolean invulnerable;                   ///< Piece's capacity to be invulnerable
    private List<Movement> movements;               ///< Piece's available movement options
    private List<Movement> initialMovements;        ///< Piece's special movements when first move
    private Position initialPosition;               ///< Piece's position when game starts

    /**
     * @brief Piece constructor
     * @param name Piece's name
     * @param symbol Piece's symbol
     * @param wImage Piece's white image location
     * @param bImage Piece's black image location
     * @param value Piece's in-game value
     * @param promotable Piece's capacity to be promotable
     * @param invulnerable Piece's capacity to be invulnerable
     * @param movements Piece's available movement options
     * @param initialMovements Piece's special movements when first move
     */
    Piece(String name, String symbol, String wImage, String bImage, int value, boolean promotable, boolean invulnerable, List<Movement> movements, List<Movement> initialMovements){
        this.name = name;
        this.symbol = symbol;
        this.wImage = wImage;
        this.bImage = bImage;
        this.value = value;
        this.promotable = promotable;
        this.invulnerable = invulnerable;
        this.movements = movements;
        this.initialMovements = initialMovements;
        this.initialPosition = null;
    }

    /// @brieg Copy constructor
    /// @pre p is not null
    /// @post Copies p properties to this
    Piece(Piece p, Position pos){
        if (p ==  null) {
            throw new NullPointerException();
        } else {
            this.name = p.name;
            this.symbol = p.symbol;
            this.wImage = p.wImage;
            this.bImage = p.bImage;
            this.value = p.value;
            this.promotable = p.promotable;
            this.invulnerable = p.invulnerable;
            this.movements = p.movements;
            this.initialMovements = p.initialMovements;
            this.initialPosition = pos;
        }
    }

    /// @brief Obtain the piece's symbol
    /// @pre ---
    /// @return @c this.symbol 
    /// @post Returns the piece's symbol which can't be @c null
    public String symbol(){
        return symbol;
    }
    
    /// @brief Obtain the piece's name
    /// @pre --
    /// @return @c this.name 
    /// @post Returns the piece's name which can't be @c null
    public String name(){
        return name;
    }

    /// @brief Obtain the piece's list of movements
    /// @pre --
    /// @return @c this.movements
    /// @post Returns the piece's list of movements which can't be @c null 
    public List<Movement> movements(){
        return movements;
    }
    
    /**
     * @brief Obtain the piece's list of initial movements
     * @pre --
     * @return @c this.initialMovements
     * @post Returns the piece's list of initialMovements which can be @c null 
     */
    public List<Movement> initialMovements(){
        return initialMovements;
    }
    
    /// @brief Obtain the piece's invulnerability
    /// @pre --
    /// @return @c this.invulnerable
    /// @post Returns if the piece is invulnerable    
    public boolean invulnerability(){
        return invulnerable;
    }

    /// @brief Change the symbol of the piece
    /// @pre --
    /// @post The piece's symbol is lower case 
    public void symbolToLowerCase(){
        char act = Character.toLowerCase(this.symbol().charAt(0));
        symbol = Character.toString(act);
    }

    /**
     * @brief Obtain the piece's initial position
     * @pre --
     * @return @c this.initialPosition
     * @post Returns the piece's initial position which can't be @c null 
     */
    public Position initialPosition(){
        return initialPosition;
    }

    public boolean firstMove(int x0, int y0){
        return (this.initialPosition().row() == x0 && this.initialPosition().col() == y0);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\t")
         .append(name)
         .append(", ")
         .append(symbol)
         .append(", ")
         .append(wImage)
         .append(", ")
         .append(bImage)
         .append(", ")
         .append(value)
         .append(", ")
         .append(promotable)
         .append(", ")
         .append(invulnerable)
         .append("\n \t\tMOVEMENTS: \n");
        
        for (Movement m : movements) {
            s.append("\t\t" + m.toString());
        }

        if (initialMovements.isEmpty()) {
            s.append("\t\tINITIAL MOVEMENTS: NONE\n");
        } else {
            s.append("\t\tINITIAL MOVEMENTS:\n");
            for (Movement m : initialMovements) {
                s.append("\t\t" + m.toString());
            }
        }
        
        return s.toString();
    }

    public PieceColor color(){
        if(Character.isUpperCase(this.symbol().charAt(0))) return PieceColor.White;
        else return PieceColor.Black;
    }
}

