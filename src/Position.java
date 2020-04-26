/*
 * @author Miquel de Domingo i Giralt
 * @file Position.java
 * @class Position
 * @brief Holds the position information 
 */
public class Position implements JSON{
    /// TRANSFORMATION CONSTANTS
    private static String alph = "abcdefghijklmnopqrstuvwxyz";

    private char rowLetter;         ///< Row character value
    public int row;                 ///< Integer row value
    public int col;                 ///< Integer column value
    
    /// @brief Create a positon from the chess coordinates
    /// @pre ---
    /// @post Creates a position from the given coordinates
    Position(int row, int col) {
	    this.row = row; 
        this.col = col; 
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
    }

    /// @brief Construct a position from a string
    /// @pre s == XY where X is the column and Y the row
    /// @post Creates a position from the given string
    Position(String s) {
        rowLetter = s.charAt(1);
        this.row = Character.getNumericValue(s.charAt(1) - 1);
        this.col = alph.indexOf(s.charAt(0));
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
        StringBuilder value = new StringBuilder()
            .append(rowLetter)
            .append(col);
        
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.propertyToJSON("pos", value.toString(), true, true, ToJSONParserHelper.THREE_TABS));
        return s.toString();
    }

    @Override
    public String toString() {
	    return "(" + row() + "," + col() + ")";
    }
}
