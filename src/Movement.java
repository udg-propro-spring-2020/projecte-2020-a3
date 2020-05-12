/// @author Miquel de Domingo i Giralt
/// @file Movement.java
/// @class Movement
/// @brief Holds the information of the movement
public class Movement implements JSON, Cloneable {
    private int _x;         ///< Fixed int or a 50 (-50) - anything but 0 -
    private int _y;         ///< Fixed int or a 50 (-50) - anything but 0 -
    private int _capture;   ///< 0 (can't capture), 1 (can capture), 2 (movement valid only when capturing)
    private int _jump;      ///< 0 (can't jump), 1 (can jump not capture), 2 (jump and capture)

    /// @brief Default movement constructor
    /// @param x X value of the movement
    /// @param y Y value of the movement
    /// @param capture Type of capture it can do
    /// @param jump Type of jump it can do
    Movement(int x, int y, int capture, int jump){
        this._x = x;
        this._y = y;
        this._capture = capture;
        this._jump = jump;
    }

    /// @brief To know the X property value
    /// @pre ---
    /// @post Returns the X property of the movement
    public int movX(){
        return _x;
    }

   /// @brief To know the Y property value
    /// @pre ---
    /// @post Returns the Y property of the movement
    public int movY(){
        return _y;
    }
    
    /// @brief To know the capture sign property value
    /// @pre ---
    /// @post Returns the capture sign property of the movement
    public int captureSign(){
        return _capture;
    }

    /// @brief To know the jump value
    /// @pre ---
    /// @post Returns the jump value
    public int canJump(){
        return _jump;
    } 

    /// @brief Toggles x and y values sign
    /// @pre ---
    /// @post Changes x and y values sign to its opposite
    public void toggleDirection() {
        this._x *= -1;
        this._y *= -1;
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.FOUR_TABS)
            .append(ToJSONParserHelper.LIST_START)
            .append(ToJSONParserHelper.valueToJSON(_x == 50 
                ? "\"a\"" 
                : (_x == -50 ? "\"-a\"" : Integer.toString(_x)),
                ToJSONParserHelper.FIVE_TABS, true)
            )
            .append(ToJSONParserHelper.valueToJSON(_y == 50 
                ? "\"a\"" 
                : (_y == -50 ? "\"-a\"" : Integer.toString(_y)),
                ToJSONParserHelper.FIVE_TABS, true)
            )
            .append(ToJSONParserHelper.valueToJSON(_capture, ToJSONParserHelper.FIVE_TABS, true))
            .append(ToJSONParserHelper.valueToJSON(_jump, ToJSONParserHelper.FIVE_TABS, false))
            .append(ToJSONParserHelper.FOUR_TABS)
            .append(ToJSONParserHelper.LIST_END);
        
        return (s.toString());
    }

    @Override
    protected Object clone() {
        Movement cloned = null;
        
        try {
            cloned = (Movement) super.clone();
        } catch (CloneNotSupportedException c) {
            System.err.println("Movement clone exception");
        }

        return cloned;
    }
}
