/*
 * @author Miquel de Domingo i Giralt
 * @file Position.java
 * @class Position
 * @brief Holds the position information 
 */
public class Position implements JSON{
    private static String alph = "abcdefghijklmnopqrstuvwxyz";
    public int row;
    public int col;
    
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
    public Position(String s) {
        this.row = Character.getNumericValue(s.charAt(1) - 1);
        this.col = alph.indexOf(s.charAt(0));
    }
    
    /// @pre --
    /// @post Returns the position's row
    public int row(){
        return this.row;
    }
    
    /// @pre --
    /// @post Returns the position's column
    public int col(){
        return this.col;
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(row)
            .append(col);
        return s.toString();
    }

    @Override
    public String toString() {
	    return "(" + row() + "," + col() + ")";
    }
}
