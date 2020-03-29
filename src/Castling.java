public class Castling {
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
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[")
         .append(aPiece)
         .append(", ")
         .append(bPiece)
         .append(", ")
         .append(stand)
         .append(", ")
         .append(emptyMid)
         .append("]");
        return s.toString();
    }
}
