import java.util.ArrayList;
import java.util.List;

/// @author Miquel de Domingo i Giralt
/// @file PieceType.java
/// @class PieceType
/// @brief Holds the common information of all pieces from the same type
public class PieceType implements JSON, Cloneable {
    private String _name;                           /// < Piece name
    private String _symbol;                         /// < Piece symbol
    private String _wImage;                         /// < Piece white image location
    private String _bImage;                         /// < Piece black image location
    private int _value;                             /// < Piece in-game value
    private boolean _promotable;                    /// < Piece's capacity to be promotable
    private boolean _invulnerable;                  /// < Piece's capacity to be invulnerable
    private List<Movement> _movements;              /// < Piece's available movement options
    private List<Movement> _initialMovements;       /// < Piece's special movements when first move

    /// @brief Piece constructor
    /// @param name Piece's name
    /// @param symbol Piece's symbol
    /// @param wImage Piece's white image location
    /// @param bImage Piece's black image location
    /// @param value Piece's in-game value
    /// @param promotable Piece's capacity to be promotable
    /// @param invulnerable Piece's capacity to be invulnerable
    /// @param movements Piece's available movement options
    /// @param initialMovements Piece's special movements when first move
    PieceType(String name, String symbol, String wImage, String bImage, int value, boolean promotable,
            boolean invulnerable, List<Movement> movements, List<Movement> initialMovements) {
        this._name = name;
        this._symbol = symbol;
        this._wImage = wImage;
        this._bImage = bImage;
        this._value = value;
        this._promotable = promotable;
        this._invulnerable = invulnerable;
        this._movements = movements;
        this._initialMovements = initialMovements;
    }

    /// @brief To know the piece type name
    /// @pre ---
    /// @post Returns the piece type name
    public String ptName() {
        return this._name;
    }

    /// @brief To know the piece type symbol
    /// @pre ---
    /// @post Returns the piece type symbol
    public String ptSymbol() {
        return this._symbol;
    }

    /// @brief To know the piece type white image name
    /// @pre ---
    /// @post Returns the piece type white image name
    public String ptwImage() {
        return this._wImage;
    }

    /// @brief To know the piece type black image name
    /// @pre ---
    /// @post Returns the piece type black image name
    public String ptbImage() {
        return this._bImage;
    }

    /// @brief To know the piece type movements
    /// @pre ---
    /// @post Returns the piece type movements
    public List<Movement> ptMovements() {
        return this._movements;
    }

    /// @brief To know the piece type initial movements
    /// @pre ---
    /// @post Returns the piece type initial movements
    public List<Movement> ptInitMovements() {
        return this._initialMovements;
    }

    /// @brief To know the piece's value
    /// @pre --
    /// @post Returns the piece's value
    public int ptValue() {
        return this._value;
    }

    /// @brief To know the piece's promotability
    /// @pre --
    /// @post Returns if the piece is promotable
    public boolean ptPromotable() {
        return _promotable;
    }

    /// @brief To know the piece's invulnerability
    /// @pre --
    /// @post Returns if the piece is invulnerable
    public boolean ptInvulnerable() {
        return _invulnerable;
    }

    /// @brief To know if this is the piece type of the king
    /// @pre ---
    /// @post Returns true if this is the piece type of the king
    public boolean isKingType() {
        return ptName().toUpperCase().equals("REI");
    }

    /// @brief To return the image location
    /// @pre Color is not null
    /// @post Returns the location of the image of the asked color
    public String colorImageLocation(PieceColor color) throws NullPointerException {
        if (color == null) {
            throw new NullPointerException("When asking for a piece image, its color c");
        } else {
            if (color == PieceColor.Black) {
                return this._bImage;
            }

            return this._wImage;
        }
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        try {
            s.append(ToJSONParserHelper.TWO_TABS)
                .append(ToJSONParserHelper.OBJ_START)
                .append(ToJSONParserHelper.propertyToJSON("nom", _name, true, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("simbol", _symbol, true, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("imatgeBlanca", _wImage, true, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("imatgeNegra", _bImage, true, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("valor", _value, false, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.objectListToJSON("moviments", _movements, false, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.objectListToJSON("movimentsInicials", _initialMovements, false, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("promocio", _promotable, false, true, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.propertyToJSON("invulnerabilitat", _invulnerable, false, false, ToJSONParserHelper.THREE_TABS))
                .append(ToJSONParserHelper.TWO_TABS)
                .append(ToJSONParserHelper.OBJ_END);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return s.toString();
    }

    @Override
    public Object clone() {
        PieceType cloned = null;
        
        try {
            cloned = (PieceType) super.clone();
        } catch (CloneNotSupportedException c) {
            System.err.println("PieceType clone exception");
        }

        /// Cloning movements
        List<Movement> movAux = new ArrayList<>();
        this.ptMovements().forEach(
            (m) -> movAux.add(
                (Movement) m.clone()
            )
        );

        /// Cloning initial movements
        List<Movement> initAux = new ArrayList<>();
        this.ptInitMovements().forEach(
            (m) -> initAux.add(
                (Movement) m.clone()
            )
        );

        /// Adding the movements
        cloned._movements = movAux;
        cloned._initialMovements = initAux; 

        return cloned;
    }
}