/// @author Miquel de Domingo i Giralt
/// @file Position.java
/// @class Position
/// @brief Holds the position information 
public class Position implements JSON, Cloneable {
    /// TRANSFORMATION CONSTANTS
    private static final String ALPH = "abcdefghijklmnopqrstuvwxyz";        ///< Alphabet letters sorted

    private char _colLetter;         ///< Col character value
    public int _row;                 ///< Integer row value
    public int _col;                 ///< Integer column value
    
    /// @brief Create a positon from the chess coordinates
    /// @pre ---
    /// @post Creates a position from the given coordinates
    Position(int row, int col) {
	    this._row = row; 
        this._col = col; 
        this._colLetter = ALPH.charAt(col);
    }

    /// @brief Copy constructor
    /// @pre @p copy cannot be null
    /// @post Creates a Position copy of @p copy
    /// @throw NullPointerException if @p copy is null
    Position(Position copy) throws NullPointerException{
        if (copy == null) {
            throw new NullPointerException("Copy Position cannot pass a null element");
        }
        this._row = copy._row;
        this._col = copy._col;
        this._colLetter = copy._colLetter;
    }

    /// @brief Construct a position from a string
    /// @pre s == XY where X is the column and Y the row
    /// @post Creates a position from the given string
    Position(String s) {
        this._row = Character.getNumericValue(s.charAt(1) - 1);
        this._col = ALPH.indexOf(s.charAt(0));
        this._colLetter = ALPH.charAt(_col);
    }
    
    /// @brief Return the row's value
    /// @pre --
    /// @post Returns the position's row
    public int row(){
        return this._row;
    }
    
    /// @brief Return the col's value
    /// @pre --
    /// @post Returns the position's column
    public int col(){
        return this._col;
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
            .append(_colLetter)
            .append(_row + 1);
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
       Position p = (Position) obj;

       return row() == p.row() && col() == p.col();
    }

    @Override
    public int hashCode() {
        return col() + (row() * 10);
    }
}
