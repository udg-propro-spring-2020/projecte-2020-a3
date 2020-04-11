/*
 * @author Miquel de Domingo i Giralt
 * @file Position.java
 * @class Position
 * @brief Holds the position information 
 */
public class Position {
    private static String alph = "abcdefghijklmnopqrstuvwxyz";

    public int row;
    public int col;
    
    public Position(int row, int col) {
	    this.row = row; 
        this.col = col; 
    }

    /// @brief Construct a position from a string
    /// @pre s == XY where X is the column and Y the row
    public Position(String s) {
        this.row = Character.getNumericValue(s.charAt(1));
        this.col = alph.indexOf(s.charAt(0));
    }

    /**
     * @pre --
     * @post Returns the position's row
     */
    public int row(){
        return this.row;
    }
    
    /**
     * @pre --
     * @post Returns the position's column
     */
    public int col(){
        return this.col;
    }

    public String toJson() {
        StringBuilder s = new StringBuilder();
        s.append(row).append(col);
        return s.toString();
    }

    @Override
    public String toString() {
	    return "(" + row() + "," + col() + ")";
    }
}