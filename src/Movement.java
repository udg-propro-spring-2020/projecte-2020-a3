public class Movement {
    /// @param X Fixed int or a 50 (-50) - meaning anything but 0 -
    private int x;
    /// @param Y Fixed int or a 50 (-50) - meaning anything but 0 -
    private int y;
    /// @param capture 0 (can't capture), 1 (can capture), 2 (movement valid only when capturing)
    private int capture;
    /// @param jump Whether it can jump other pieces or not
    private boolean jump;

    /*
     * @brief Default movement constructor
     */
    Movement(int x, int y, int capture, int jump){
        this.x = x;
        this.y = y;
        this.capture = capture;
        this.jump = jump == 1 ? true : false;
    }

    /*
     * @pre ---
     * @post Returns the X property of the movement
     */
    public int movX(){
        return x;
    }

    /*
     * @pre ---
     * @post Returns the Y property of the movement
     */
    public int movY(){
        return y;
    }
    
    /*
     * @pre ---
     * @post Returns the capture sign
     */
    public int captureSign(){
        return capture;
    }

    /*
     * @pre ---
     * @post Returns a String of the object properties in JSON format (tabbed)
     */
    public String toJSON() {
        StringBuilder s = new StringBuilder();
        s.append("\t\t\t\t[\n")
            .append("\t\t\t\t\t")                 /// Indentation
            .append(
                x == 50 ? "\"a\"" : 
                    (x == -50 ? "\"-a\"" : Integer.toString(x)
                )
            ).append(", \n")
            .append("\t\t\t\t\t")                 /// Indentation
            .append(
                y == 50 ? "\"a\"" : 
                    (y == -50 ? "\"-a\"" : Integer.toString(y)
                )
            ).append(", \n")
            .append("\t\t\t\t\t")                 /// Indentation
            .append(Integer.toString(capture))
            .append(", \n")
            .append("\t\t\t\t\t")                 /// Indentation
            .append(jump ? "1 \n" : "0 \n")
            .append("\t\t\t\t]\n");
        
        return (s.toString());
    }
}
