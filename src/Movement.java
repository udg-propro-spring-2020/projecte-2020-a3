/*
 * @author Miquel de Domingo i Giralt
 * @file Movement.java
 * @class Movement
 * @brief Holds the information of the movement
 */
public class Movement implements JSON {
    /// @param X Fixed int or a 50 (-50) - meaning anything but 0 -
    private int x;
    /// @param Y Fixed int or a 50 (-50) - meaning anything but 0 -
    private int y;
    /// @param capture 0 (can't capture), 1 (can capture), 2 (movement valid only when capturing)
    private int capture;
    /// @param jump Whether it can jump other pieces or not
    private boolean jump;

    /// @brief Default movement constructor
    Movement(int x, int y, int capture, int jump){
        this.x = x;
        this.y = y;
        this.capture = capture;
        this.jump = jump == 1 ? true : false;
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

    /// @brief To know if the piece can jump
    /// @pre ---
    /// @post Returns if the piece can jump
    public boolean canJump(){
        return jump;
    } 

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append(ToJSONParserHelper.FOUR_TABS)
            .append(ToJSONParserHelper.LIST_START)
            .append(ToJSONParserHelper.FIVE_TABS)                 /// Indentation
            .append(
                x == 50 ? "\"a\"" : 
                    (x == -50 ? "\"-a\"" : Integer.toString(x))
            ).append(", \n")
            .append(ToJSONParserHelper.FIVE_TABS)                 /// Indentation
            .append(
                y == 50 ? "\"a\"" : 
                    (y == -50 ? "\"-a\"" : Integer.toString(y))
            ).append(", \n")
            .append(ToJSONParserHelper.FIVE_TABS)                 /// Indentation
            .append(Integer.toString(capture))
            .append(", \n")
            .append(ToJSONParserHelper.FIVE_TABS)                 /// Indentation
            .append(jump ? "1 \n" : "0 \n")
            .append(ToJSONParserHelper.FOUR_TABS)
            .append(ToJSONParserHelper.LIST_END);
        
        return (s.toString());
    }
}
