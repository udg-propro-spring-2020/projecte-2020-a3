import java.util.ArrayList;

public class Chess {
    private int rows;
    private int cols;
    private int chessLimits;
    private int inactiveLimits;
    private ArrayList<Piece> pList;
    private ArrayList<String> initPositions;
    private ArrayList<Castling> castlings;
    private Piece[][] board;

    // Per ajudar amb coneixement / CPU
    private ArrayList<Piece> pListWhites;
    private ArrayList<Piece> pListBlacks;

    Chess(int rows, int cols, int chessLimits, int inactiveLimits, ArrayList<Piece> pList,
            ArrayList<String> initPositions, ArrayList<Castling> castlings) {
        this.rows = rows;
        this.cols = cols;
        this.chessLimits = chessLimits;
        this.inactiveLimits = inactiveLimits;
        this.pList = pList;
        this.initPositions = initPositions;
        this.castlings = castlings;

        pListWhites = new ArrayList<Piece>();
        pListBlacks = new ArrayList<Piece>();

        if (this.rows < 4 || this.cols < 4 || this.cols > 16 || this.rows > 16)
            throw new RuntimeException("El nombre de files i columnes ha de ser entre 4 i 16");
        board = new Piece[this.rows][this.cols];
        createBoard();
        System.out.println(showBoard());
    }

    /*
     * Per crear el tauler s'ha usat un array auxiliar el qual omplim amb les peces
     * del tauler inicial ordenadoes. Al for de mes enbaix s'omplen les dues
     * primeres files i les dues ultimes, les demes queden a null. També s'omplen
     * les array de peces vives de cada jugador.
     */
    private void createBoard() {
        ArrayList<Piece> initialPieces = new ArrayList<Piece>();

        for (int i = 0; i < initPositions.size(); i++) {
            int j = 0;
            boolean found = false;
            while (!found && j < pList.size()) {
                if (pList.get(j).name().equals(initPositions.get(i))) {
                    initialPieces.add(pList.get(j));
                    pListWhites.add(pList.get(j));
                    pListBlacks.add(pList.get(j));
                    found = true;
                }
                j++;
            }
        }
        int act = 0;
        int aux = rows() - 1;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < cols(); ++j) {
                board[i][j] = initialPieces.get(act);
                board[aux][j] = initialPieces.get(act);
                act++;
            }
            aux--;
        }
    }

    public String showBoard() {
        String s;
        String c = "abcdefghijklmnopqrstuvwxyz";
        String l = "   ";
        for (int j = 0; j < cols; ++j)
            l += "+---";
        l += "+\n";
        s = l;
        for (int i = 0; i < rows; ++i) {
            if (i < 9)
                s += " ";
            s += (i + 1) + " | ";
            for (int j = 0; j < cols; ++j) {
                Piece p = board[i][j];
                if (p == null)
                    s += " ";
                else
                    s += board[i][j].symbol();
                s += " | ";
            }
            s += "\n" + l;
        }
        s += "     ";
        for (int j = 0; j < cols; ++j)
            s += c.charAt(j) + "   ";
        return s;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public boolean diferentOwnerPiece(Piece originPiece, Piece destinyPiece, boolean enemiePieceOnDestiny) {
        char destinyPieceSymbol = destinyPiece.symbol().charAt(0); // simbol de la figura
        char originPieceSymbol = originPiece.symbol().charAt(0);
        if (Character.isUpperCase(originPieceSymbol) != Character.isUpperCase(destinyPieceSymbol)) {// Si son de
                                                                                                    // jugadors
                                                                                                    // diferents
            enemiePieceOnDestiny = true;
        } else {
            System.out.println("La peça que vols matar es teva");
            enemiePieceOnDestiny = true;// S'ha de borrar, nomes per poder matar mateix jugador
            // return r; //Intenta moure una peça al lloc d'una altre, les dues seves
        }
        return enemiePieceOnDestiny;
    }

    public Pair<Boolean, Position> checkMovement(Position origin, Position destiny) {
        Pair<Boolean, Position> r = new Pair<>(false, null);
        boolean enemiePieceOnDestiny = false;
        int x0 = origin.row;
        int y0 = origin.col;
        int x1 = destiny.row;
        int y1 = destiny.col;
        Piece p = board[x0][y0];
        ArrayList<Movement> pieceMovements = p.movements();

        if (board[x1][y1] != null) {// Hi ha peça al desti?
            enemiePieceOnDestiny = diferentOwnerPiece(board[x0][y0], board[x1][y1], enemiePieceOnDestiny);
            System.out.println("Hi ha peça");
        }
        int i = 0;
        boolean found = false;
        System.out.println("Mov: " + (x1 - x0) + " " + (y1 - y0));
        while (i < pieceMovements.size() && !found) {
            Movement act = pieceMovements.get(i);
            System.out.println(act.toString());
            if (y1 - y0 == act.movY() && x1 - x0 == act.movX()) {// fer-ho per les dues parts del tauler
                if (enemiePieceOnDestiny && act.captureSign() != 0) {// Si hi ha peça i la pot matar
                    r.first = true;
                    r.second = new Position(x1, y1);
                    found = true;
                    System.out.println("Mov matar");
                } else if (!enemiePieceOnDestiny && act.captureSign() != 2) {// Si no hi ha peça i no es un moviment que
                                                                             // nomes fa per matar
                    r.first = true;
                    r.second = null;
                    found = true;
                    System.out.println("Mov normal");
                }
            }
            // else if(y1-y0 == pieceMovements.get(i).MovY() && x1-x0 ==
            // pieceMovements.get(i).MovX() && pieceMovements.get(i).)
            // captura: 0=no, 1=si, 2=mov possible nomes al matar

            // Comprovar si la peça te 50 o no
            // Comprobar si pot saltar (peces pel mig del recorregut
            // Comprobar si hi ha peça al desti i matar
            i++;
        }
        return r;
    }

    /**
     * @brief Aplica un moviment
     * @pre \p origen i \p desti són posicions vàlides del tauler; si \p matar no és
     *      null, aleshores és una posició vàlida del tauler.
     * @post La fitxa de la posició \p origen s'ha mogut a la posició \p destí, i si
     *       \p matar no és null, s'ha eliminat la fitxa d'aquesta posició.
     */
    public void applyMovement(Position origin, Position destiny, Position death) {
        if (death != null) {
            board[death.row][death.col] = null;
            // Afegir a arrayList mortes
            // Treure de l'arrayList vives
        }
        board[destiny.row][destiny.col] = board[origin.row][origin.col];
        board[origin.row][origin.col] = null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Rows: " + rows + ",\n").append("Columns: " + cols + ",\n")
                .append("Chess Limits: " + chessLimits + ",\n")
                .append("Inactive Turns Limits: " + inactiveLimits + ",\n").append("PIECES: \n");

        for (Piece p : pList) {
            s.append(p.toString());
        }

        s.append("STARTING CHESS POSITIONS: \n");

        int count = 0;
        for (String r : initPositions) {
            if (count == cols) {
                s.append("\n");
            }
            s.append(r + "\t");
            count++;
        }

        if (castlings.isEmpty()) {
            s.append("\nCASTLINGS: NONE\n");
        } else {
            s.append("\nCASTLINGS: \n");
            for (Castling c : castlings) {
                s.append("\t" + c);
            }
        }

        s.append("\n**** END ****");
        return s.toString();
    }
}
