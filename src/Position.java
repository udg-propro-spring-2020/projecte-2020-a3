/**
 * @author Miquel de Domingo i Giralt
 * @file Position.java
 * @class Position
 * @brief Holds the position information 
 */
public class Position implements JSON, Cloneable {
    /// TRANSFORMATION CONSTANTS
    private static String alph = "abcdefghijklmnopqrstuvwxyz";

    private char colLetter;         ///< Col character value
    public int row;                 ///< Integer row value
    public int col;                 ///< Integer column value
    
    /// @brief Create a positon from the chess coordinates
    /// @pre ---
    /// @post Creates a position from the given coordinates
    Position(int row, int col) {
	    this.row = row; 
        this.col = col; 
        colLetter = alph.charAt(col);
    }

    /// @brief Copy constructor
    /// @pre @p copy cannot be null
    /// @post Creates a Position copy of @p copy
    /// @throw NullPointerException if @p copy is null
    Position(Position copy) throws NullPointerException{
        if (copy == null) {
            throw new NullPointerException("Copy Position cannot pass a null element");
        }
        this.row = copy.row;
        this.col = copy.col;
        this.colLetter = copy.colLetter;
    }

    /// @brief Construct a position from a string
    /// @pre s == XY where X is the column and Y the row
    /// @post Creates a position from the given string
    Position(String s) {
        this.row = Character.getNumericValue(s.charAt(1) - 1);
        this.col = alph.indexOf(s.charAt(0));
        this.colLetter = alph.charAt(col);
    }
    
    /// @brief Return the row's value
    /// @pre --
    /// @post Returns the position's row
    public int row(){
        return this.row;
    }
    
    /// @brief Return the col's value
    /// @pre --
    /// @post Returns the position's column
    public int col(){
        return this.col;
    }

    @Override
    public String toJSON() {        
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.propertyToJSON("pos", this.toString(), true, true, ToJSONParserHelper.THREE_TABS));
        return s.toString();
    }

    @Override
    public Object clone() {
        Position cloned = null;
        
        try {
            cloned = (Position) super.clone();
        } catch (CloneNotSupportedException c) {
            System.err.println("Position clone exception");
        }

        return cloned;
    }

    @Override
    public String toString() {
	    StringBuilder value = new StringBuilder()
            .append(colLetter)
            .append(row + 1);
        return value.toString();
    }
}
