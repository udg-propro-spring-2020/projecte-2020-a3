

public class Movement {
    private int x;
    private int y;
    private int capture;
    private int jump;

    Movement(int x, int y, int capture, int jump){
        this.x = x;
        this.y = y;
        this.capture = capture;
        this.jump = jump;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[")
         .append(x)
         .append(", ")
         .append(y)
         .append(", ")
         .append(capture)
         .append(", ")
         .append(jump)
         .append("] \n");
        
        return s.toString();
    }
    public int movX(){
        return x;
    }
    public int movY(){
        return y;
    }
    //captura: 0=no, 1=si, 2=mov possible nomes al matar
    public int captureSign(){
        return capture;
    }
}
