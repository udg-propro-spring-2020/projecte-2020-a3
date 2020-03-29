import java.util.ArrayList;

public class Piece {
    private String name;
    private String symbol;
    private String wImage;
    private String bImage;
    private int value;
    private boolean promotable;
    private boolean invulnerable;
    private ArrayList<Movement> movements;
    private ArrayList<Movement> initialMovements;

    Piece(String name, String symbol, String wImage, String bImage, int value, boolean promotable, boolean invulnerable, ArrayList<Movement> movements, ArrayList<Movement> initialMovements){
        this.name = name;
        this.symbol = symbol;
        this.wImage = wImage;
        this.bImage = bImage;
        this.value = value;
        this.promotable = promotable;
        this.invulnerable = invulnerable;
        this.movements = movements;
        this.initialMovements = initialMovements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\t")
         .append(name)
         .append(", ")
         .append(symbol)
         .append(", ")
         .append(wImage)
         .append(", ")
         .append(bImage)
         .append(", ")
         .append(value)
         .append(", ")
         .append(promotable)
         .append(", ")
         .append(invulnerable)
         .append("\n \t\tMOVEMENTS: \n");
        
        for (Movement m : movements) {
            s.append("\t\t" + m.toString());
        }

        if (initialMovements.isEmpty()) {
            s.append("\t\tINITIAL MOVEMENTS: NONE\n");
        } else {
            s.append("\t\tINITIAL MOVEMENTS:\n");
            for (Movement m : initialMovements) {
                s.append("\t\t" + m.toString());
            }
        }
        
        return s.toString();
    }
    public String symbol(){
        return symbol;
    }
    public String name(){
        return name;
    }
    public ArrayList<Movement> movements(){
        return movements;
    }
}

