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
    }

    /**
     * @brief Obtain the piece's symbol
     * @pre --
     * @return @c this.symbol 
     * @post Returns the piece's symbol which can't be @c null
     */
    public String symbol(){
        return symbol;
    }
    
    /**
     * @brief Obtain the piece's name
     * @pre --
     * @return @c this.name 
     * @post Returns the piece's name which can't be @c null
     */
    public String name(){
        return name;
    }

    /**
     * @brief Obtain the piece's list of movements
     * @pre --
     * @return @c this.movements
     * @post Returns the piece's list of movements which can't be @c null 
     */
    public List<Movement> movements(){
        return movements;
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
}

