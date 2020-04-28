/**
 * @author Miquel de Domingo i Giralt
 * @file Castling.java
 * @class Castling
 * @brief Holds the castling information
 */
public class Castling implements JSON, Cloneable {
    private String aPiece;
    private String bPiece;
    private boolean stand;
    private boolean emptyMid;

    Castling(String aPiece, String bPiece, boolean stand, boolean emptyMid) {
        this.aPiece = aPiece;
        this.bPiece = bPiece;
        this.stand = stand;
        this.emptyMid = emptyMid;
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        
        s.append(ToJSONParserHelper.TWO_TABS)
            .append(ToJSONParserHelper.OBJ_START)
            .append(ToJSONParserHelper.propertyToJSON("peçaA", aPiece, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("peçaB", bPiece, true, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("quiets", stand, false, true, ToJSONParserHelper.THREE_TABS))
            .append(ToJSONParserHelper.propertyToJSON("buitAlMig", emptyMid, false, false, ToJSONParserHelper.THREE_TABS))
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
