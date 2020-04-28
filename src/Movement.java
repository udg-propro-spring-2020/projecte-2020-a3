/*
 * @author Miquel de Domingo i Giralt
 * @file Movement.java
 * @class Movement
 * @brief Holds the information of the movement
 */
public class Movement implements JSON, Cloneable {
    /// @param X Fixed int or a 50 (-50) - meaning anything but 0 -
    private int x;
    /// @param Y Fixed int or a 50 (-50) - meaning anything but 0 -
    private int y;
    /// @param capture 0 (can't capture), 1 (can capture), 2 (movement valid only when capturing)
    private int capture;
    /// @param jump 0 (can't jump), 1 (can jump not capture), 2 (jump and capture)
    private int jump;

    /// @brief Default movement constructor
    Movement(int x, int y, int capture, int jump){
        this.x = x;
        this.y = y;
        this.capture = capture;
        this.jump = jump;
    }

    /// @brief To know the X property value
    /// @pre ---
    /// @post Returns the X property of the movement
    public int movX(){
        return x;
    }

   /// @brief To know the Y property value
    /// @pre ---
    /// @post Returns the Y property of the movement
    public int movY(){
        return y;
    }
    
    /// @brief To know the capture sign property value
    /// @pre ---
    /// @post Returns the capture sign property of the movement
    public int captureSign(){
        return capture;
    }

    /// @brief To know the jump value
    /// @pre ---
    /// @post Returns the jump value
    public int canJump(){
        return jump;
    } 

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.FOUR_TABS)
            .append(ToJSONParserHelper.LIST_START)
            .append(ToJSONParserHelper.valueToJSON(x == 50 
                ? "\"a\"" 
                : (x == -50 ? "\"-a\"" : Integer.toString(x)),
                ToJSONParserHelper.FIVE_TABS, true)
            )
            .append(ToJSONParserHelper.valueToJSON(y == 50 
                ? "\"a\"" 
                : (y == -50 ? "\"-a\"" : Integer.toString(y)),
                ToJSONParserHelper.FIVE_TABS, true)
            )
            .append(ToJSONParserHelper.valueToJSON(capture, ToJSONParserHelper.FIVE_TABS, true))
            .append(ToJSONParserHelper.valueToJSON(jump, ToJSONParserHelper.FIVE_TABS, false))
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
