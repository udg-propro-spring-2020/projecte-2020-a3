/*
 * @author David Cáceres González
 */

import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

/* 
 * @class Chess
 * @brief Class that controls the movements and pieces
 */
public class Chess {
    private int rows;
    private int cols;
    private int chessLimits;
    private int inactiveLimits;
    private List<Piece> pList;
    private List<String> initPositions;
    private HashMap<Position, Piece> initPositionsWhite;
    private HashMap<Position, Piece> initPositionsBlack;
    private List<Castling> castlings;
    private Piece[][] board;
    
    
    //Per ajudar amb coneixement / CPU
    private List<Piece> pListWhite;
    private List<Piece> pListBlack;

    /**
     * @brief Chess constructor
     * @param rows Board's row number
     * @param cols Board's column number
     * @param chessLimits Number maximum of chess in a game
     * @param inactiveLimits Number maximum of inactive turns (without kill) in a game
     * @param pList List of pieces
     * @param castlings Special move
     * @param initPositions Piece's initial default positions
     * @param whiteInitPos White piece's initial positions
     * @param blackInitPos Black piece's initial positions
     * @param nextTurnColor Turn's color
     * @param turnList Turn's list
     */
    Chess(int rows, int cols, int chessLimits, int inactiveLimits, List<Piece> pList, List<String> initPositions, List<Castling> castlings,  
    List<Pair<String,String>> whiteInitPos, List<Pair<String,String>> blackInitPos, PieceColor nextTurnColor, List<Turn> turnList) {
        this.rows = rows;
        this.cols = cols;
        this.chessLimits = chessLimits;
        this.inactiveLimits = inactiveLimits;
        this.pList = pList;
        this.initPositions = initPositions;
        this.castlings = castlings;
        
        initPositionsWhite = new HashMap<Position,Piece>();
        initPositionsBlack = new HashMap<Position,Piece>();
        pListWhite = new ArrayList<Piece>();
        pListBlack = new ArrayList<Piece>();
        if (this.rows < 4 || this.cols < 4 || this.cols > 16 || this.rows > 16)
                throw new RuntimeException("El nombre de files i columnes ha de ser entre 4 i 16");
        board = new Piece[this.rows][this.cols];
        
        createInitialPositions(whiteInitPos,blackInitPos);
        createBoard();
        System.out.println(showBoard());
    }

    /*
     * @brief Create the initial position of the pieces
     * @pre --
     * @post Two HashMap with a pair of position / piece are created
     */
    private void createInitialPositions(List<Pair<String,String>> whiteInitPos, List<Pair<String,String>> blackInitPos){
        String c = "abcdefghijklmnopqrstuvwxyz";
        for(int i = 0; i < whiteInitPos.size(); i++){
            int j = 0;
            boolean found = false;
            while(!found && j < pList.size()){
                if(pList.get(j).name().charAt(0) == whiteInitPos.get(i).second.charAt(0)){
                    Position wPos = new Position (Character.getNumericValue(whiteInitPos.get(i).first.charAt(1)-1),c.indexOf(whiteInitPos.get(i).first.charAt(0)));
                    Piece wPiece = new Piece(pList.get(j), wPos);
                    pListWhite.add(wPiece);
                    initPositionsWhite.put(wPos,wPiece);

                    Position bPos = new Position (Character.getNumericValue(blackInitPos.get(i).first.charAt(1)-1),c.indexOf(blackInitPos.get(i).first.charAt(0)));
                    Piece bPiece = new Piece(pList.get(j), bPos);
                    bPiece.symbolToLowerCase();
                    pListBlack.add(bPiece);
                    initPositionsBlack.put(bPos,bPiece);
                    found=true;
                }
                j++;
            }
        }
    }

    /*
     * @brief Creates the board
     * @pre Piece's initial positions are not empty
     * @post Every piece is on her board's position
     */
    private void createBoard(){
        for ( Position pos : initPositionsWhite.keySet() ) {
            board[pos.row()][pos.col()] = initPositionsWhite.get(pos);
        }
        for ( Position pos : initPositionsBlack.keySet() ) {
            board[pos.row()][pos.col()] = initPositionsBlack.get(pos);
        }
    }

    /*
     * @brief Returns the piece's color
     * @pre --
     * @post Return the piece's color
     */
    public PieceColor cellColor(Position p){
        return board[p.row()][p.col()].color();
    }
    
    /*
     * @brief Checks if the cell has any piece
     * @pre --
     * @post Return if the cell is empty
     */
    public boolean emptyCell(Position p){
        return board[p.row()][p.col()]==null;
    }

    /*
     * @brief Show the board
     * @pre --
     * @post Show the board and every piece on her cell
     */
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
			s += (i+1) + " | ";
			for (int j = 0; j < cols; ++j) {
				Piece p = board[i][j];
				if (p == null) s += " ";
				else s += board[i][j].symbol();                        
				s += " | "; 
			}
			s += "\n" + l;
		}
		s += "     ";
		for (int j = 0; j < cols; ++j)
			s += c.charAt(j) + "   ";
		return s;
    }

    /*
     * @brief Board's row number
     * @pre --
     * @post Return the board's row number
     */
    public int rows(){
        return rows;
    }

    /*
     * @brief Board's column number
     * @pre --
     * @post Return the board's column number
     */
    public int cols(){
        return cols;
    }
    
    /*
     * @brief Checks if a piece is from a diferent player
     * @pre A piece is going to be killed
     * @post Return if a piece is from a diferent player
     */
    private boolean diferentOwnerPiece(Piece originPiece, Piece destinyPiece){
        return originPiece.color() != destinyPiece.color();
    }
    
    /*
     * @brief Checks if a piece is in any of the central squares of the movement
     * @pre A movement is going to be realised 
     * @post Return if there's any piece that the origin piece can't pass across
     */
    private boolean checkPieceOnTheWay(int x0, int y0, int x1, int y1){
        boolean pieceOnTheWay = false;
        int initialX=x0;
        int finalX=x1;
        int initialY=y0;
        int finalY=y1;
        if(x1 < x0){//Si movem peça en direccio de baix cap a dalt, mirem de adalt cap abaix (cambiem 8 5 per 5 8 per tenir un sol for) 
            initialX=x1;
            finalX=x0;
        }
        if(y1 < y0){//Si movem peça en direccio de baix cap a dalt, mirem de adalt cap abaix (cambiem 8 5 per 5 8 per tenir un sol for) 
            initialY=y1;
            finalY=y0;
        }
        if(x0==x1){//es mou en la mateixa fila
            System.out.println("entro a row");           
            System.out.println("entro a row i miro de "+initialY+" fins a "+finalY);
            for(int i=initialY+1; i<finalY; i++){
                if(board[x0][i] != null){
                    pieceOnTheWay = true;
                }
            }
        }else if(y0==y1){//es mou en la mateixa columna         
            System.out.println("entro a col i miro de "+initialX+" fins a "+finalX);
            for(int i=initialX+1; i<finalX; i++){
                if(board[i][y0] != null){
                    pieceOnTheWay = true;
                }
            }
        }else{//es mou en diagonal
            System.out.println("entro a diagonal");
            int j=initialY+1;
            for(int i=initialX+1; i<finalX; i++){
                if(board[i][j] != null){
                    pieceOnTheWay = true;
                   // System.out.println(board[i][j].symbol());
                }
                j++;
            }
        }
        return pieceOnTheWay;
    }

    /*
     * @brief Checks if the movement is possible. It validates the cell status, the piece that is going to be killed if it's the case,
     * the possiblity of the piece to jump and kill and checks if the movement is on the piece's movement list.
     * @pre A movement is going to be realised 
     * @post Return if the moviment is possible to execute
     */
    public Pair<Boolean,Position> checkMovement(Position origin, Position destiny) {
		Pair<Boolean,Position> r = new Pair<>(false,null);
        boolean enemiePieceOnDestiny = false;
        boolean blackDirection = false; //Saber si juga el negre
		int x0 = origin.row;
		int y0 = origin.col;
		int x1 = destiny.row;
		int y1 = destiny.col;
        Piece p = board[x0][y0];
        List<Movement> pieceMovements = p.movements();
        //Hi ha peça al origen
        if(board[x1][y1]!=null){//Hi ha peça al desti?
            enemiePieceOnDestiny = diferentOwnerPiece(board[x0][y0],board[x1][y1]);
            //System.out.println("Hi ha peça");   
            //System.out.println(enemiePieceOnDestiny);        
        }
        int xMove=x1-x0;
        int yMove=y1-y0;
        if(!Character.isUpperCase(board[x0][y0].symbol().charAt(0))){//Si la peça es negre (minuscula) invertim moviment
            xMove = -1*xMove;
            yMove = -1*yMove;
        }                 
        List<Movement> allMoves = new ArrayList<Movement>();
        List<Movement> movesToRead = new ArrayList<Movement>();
        if(p.initialMovements()!=null){  
            
            allMoves.addAll(p.movements());
            allMoves.addAll(p.initialMovements());
            //pieceMovements.addAll(p.initialMovements());
        }
        if(p.firstMove(x0,y0)){
            
            movesToRead=allMoves;
        }else{
            movesToRead=p.movements();
        }
        int i = 0;
        boolean found = false;
        boolean pieceOnTheWay = false;
        boolean diagonalCorrect = true;
        while(i < movesToRead.size() && !found){
                Movement act = movesToRead.get(i);         
                System.out.println(act.toString());            
                if((yMove == act.movY() || act.movY() == 50) && (xMove == act.movX() || act.movX() == 50)){                        
                    if(act.movY()==50 && act.movX()==50){
                        diagonalCorrect = Math.abs(yMove) == Math.abs(xMove); //En cas de moure'ns varies caselles en diagonal, comprobar si es coherent
                        System.out.println("La diagonal "+Math.abs(yMove)+"-"+Math.abs(xMove)+" es correcte? "+diagonalCorrect);
                    }
                    if(diagonalCorrect){
                        if((xMove > 1 || yMove > 1) && !act.canJump()){//Si s'ha de desplaçar mes de una posicio i no pot saltar
                            pieceOnTheWay = checkPieceOnTheWay(x0,y0,x1,y1);
                            System.out.println("Hi ha peça al cami? "+pieceOnTheWay);
                        }
                        if(!pieceOnTheWay){ //Si pot saltar, no s'activa   
                            if(enemiePieceOnDestiny && act.captureSign() != 0){//Si hi ha peça i la pot matar
                                r.first = true;
                                r.second = new Position(x1,y1); 
                                found = true;
                                System.out.println("Mov matar");
                            }else if(!enemiePieceOnDestiny && act.captureSign() != 2){//Si no hi ha peça enemiga i no es un moviment que nomes fa per matar
                                if(board[x1][y1]!=null){ //La peça que vols matar es teva, ja que no s'ha detectat peça enemiga pero n'hi ha una
                                    System.out.println("La peça que vols matar es teva");
                                }else{
                                    r.first = true;
                                    r.second = null; 
                                    found = true;
                                    System.out.println("Mov normal");
                                }
                            }                   
                        }else{
                            System.out.println("La teva peça no en pot saltar d'altres");
                        }  
                    }else{
                        System.out.println("La diagonal es incorrecte");
                    }
                }
            i++; 
            //captura: 0=no, 1=si, 2=mov possible nomes al matar
        }                     
		return r;
    }

    /** @brief Aplica un moviment
	@pre \p origen i \p desti són posicions vàlides del tauler;
	si \p matar no és null, aleshores és una posició vàlida del
	tauler.
	@post La fitxa de la posició \p origen s'ha mogut a la posició
	\p destí, i si \p matar no és null, s'ha eliminat la fitxa
	d'aquesta posició.
    S'ha modificat la llista de peces corresponent
    */
    public void applyMovement(Position origin, Position destiny, Position death) {
        if (death != null){
            int i=0;
            boolean found = false;
            char deathSymbol = board[death.row()][death.col()].symbol().charAt(0);//symbol de la peça a matar
            if(Character.isUpperCase(deathSymbol)){//Treballem amb blanques
                while(i < pListWhite.size() && !found){
                    //System.out.println(pListWhite.get(i).symbol());
                    if(pListWhite.get(i).symbol().charAt(0) == deathSymbol){
                        pListWhite.remove(i);
                        found = true;
                    }
                    i++;
                }
            }else{
                while(i < pListBlack.size() && !found){
                    if(pListBlack.get(i).symbol().charAt(0) == deathSymbol){
                        pListBlack.remove(i);
                        found = true;
                    }
                    i++;
                }
            }
            board[death.row()][death.col()] = null;
            //Afegir a List mortes ?
        }

		board[destiny.row()][destiny.col()] = board[origin.row()][origin.col()];
		board[origin.row()][origin.col()] = null;      
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(); 
        s.append("Rows: " + rows + ",\n")
        .append("Columns: " + cols + ",\n")
        .append("Chess Limits: " + chessLimits + ",\n")
        .append("Inactive Turns Limits: " + inactiveLimits + ",\n")
        .append("PIECES: \n");
        
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
