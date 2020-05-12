/// @author Miquel de Domingo i Giralt
/// @file Castling.java
/// @class Castling
/// @brief Holds the castling information
public class Castling implements JSON, Cloneable {
    private String _aPiece;             ///< Piece name A of the castling
    private String _bPiece;             ///< Piece name B of the castling
    private boolean _stand;             ///< If the pieces ara standing or in movement
    private boolean _emptyMid;          ///< If the space between the pieces is empty

    /// @brief Default castling constructor
    /// @param aPiece Name of the a piece
    /// @param bPiece Name of the a piece
    /// @param stand If both pieces are standing
    /// @param emptyMid If there's empty space between the pieces
    Castling(String aPiece, String bPiece, boolean stand, boolean emptyMid) {
        this._aPiece = aPiece;
        this._bPiece = bPiece;
        this._stand = stand;
        this._emptyMid = emptyMid;
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        
        s.append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_START)
            .append(ToJSONParserHelper.propertyToJSON("peçaA", _aPiece, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("peçaB", _bPiece, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("quiets", _stand, false, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("buitAlMig", _emptyMid, false, false, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_END);

        return s.toString();
    }

    @Override
    public Object clone() {
        Castling cloned = null;
        
        try {
            cloned = (Castling) super.clone();
        } catch (CloneNotSupportedException c) {
            System.out.println("Castling clone exception");
        }
        
        return cloned;
    }
}
