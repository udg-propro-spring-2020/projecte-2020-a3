/*
 * @author Miquel de Domingo i Giralt
 * @file PieceType.java
 * @class PieceType
 * @brief Holds the common information of all pieces from the same type
 */

import java.util.List;

public class PieceType implements JSON {
    private String name;                            /// < Piece name
    private String symbol;                          /// < Piece symbol
    private String wImage;                          /// < Piece white image location
    private String bImage;                          /// < Piece black image location
    private int value;                              /// < Piece in-game value
    private boolean promotable;                     /// < Piece's capacity to be promotable
    private boolean invulnerable;                   /// < Piece's capacity to be invulnerable
    private List<Movement> movements;               /// < Piece's available movement options
    private List<Movement> initialMovements;        /// < Piece's special movements when first move

    /// @brief Piece constructor
    /// @param name             Piece's name
    /// @param symbol           Piece's symbol
    /// @param wImage           Piece's white image location
    /// @param bImage           Piece's black image location
    /// @param value            Piece's in-game value
    /// @param promotable       Piece's capacity to be promotable
    /// @param invulnerable     Piece's capacity to be invulnerable
    /// @param movements        Piece's available movement options
    /// @param initialMovements Piece's special movements when first move
    PieceType(String name, String symbol, String wImage, String bImage, int value, boolean promotable, boolean invulnerable, List<Movement> movements, List<Movement> initialMovements) {
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

    /// @brief To know the piece type name
    /// @pre ---
    /// @post Returns the piece type name
    public String ptName() {
        return this.name;
    }

    /// @brief To know the piece type symbol
    /// @pre ---
    /// @post Returns the piece type symbol
    public String ptSymbol() {
        return this.symbol;
    }

    /// @brief To know the piece type movements
    /// @pre ---
    /// @post Returns the piece type movements
    public List<Movement> ptMovements() {
        return this.movements;
    }

    /// @brief To know the piece type initial movements
    /// @pre ---
    /// @post Returns the piece type initial movements
    public List<Movement> ptInitMovements() {
        return this.initialMovements;
    }

    /// @brief To know the piece's value
    /// @pre --
    /// @post Returns the piece's value
    public int ptValue() {
        return this.value;
    }

    /// @brief To know the piece's promotability
    /// @pre --
    /// @post Returns if the piece is promotable
    public boolean ptPromotable() {
        return promotable;
    }

    /// @brief To know the piece's invulnerability
    /// @pre --
    /// @post Returns if the piece is invulnerable
    public boolean ptInvulnerable() {
        return invulnerable;
    }

    /// @brief To return the image location
    /// @pre Color is not null
    /// @post Returns the location of the image of the asked color
    public String colorImageLocation(PieceColor color) throws NullPointerException {
        if (color == null) {
            throw new NullPointerException("When asking for a piece image, its color c");
        } else {
            if (color == PieceColor.Black) {
                return this.bImage;
            } 

            return this.wImage;
        }
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        try {
            s.append(JSONParserHelper.TWO_TABS)
                .append(JSONParserHelper.OBJ_START)
                .append(JSONParserHelper.propertyToJSON("name", name, true, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.propertyToJSON("simbol", symbol, true, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.propertyToJSON("imatgeBlanca", wImage, true, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.propertyToJSON("imatgeNegre", bImage, true, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.propertyToJSON("valor", value, false, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.listToJSON("moviments", movements, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.listToJSON("movimentsInicials", initialMovements, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.propertyToJSON("promocio", promotable, false, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.propertyToJSON("invulnerabilitat", invulnerable, false, JSONParserHelper.THREE_TABS))
                .append(JSONParserHelper.TWO_TABS)
                .append(JSONParserHelper.OBJ_END);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return s.toString();
    }
}