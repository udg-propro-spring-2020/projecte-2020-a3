/*
 * @author David Cáceres González
 */

import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

/* 
 * @class Chess
 * @brief Class that controls the movements and pieces
 */
public class Chess implements Cloneable {
    private int rows;
    private int cols;
    private int chessLimits;
    private int inactiveLimits;
    private List<PieceType> pList;
    private List<String> initPositions;
    private List<Pair<Position, Piece>> whiteInitPos;
    private List<Pair<Position, Piece>> blackInitPos;
    private List<Castling> castlings;
    private Piece[][] board;
    private List<Piece[][]> boardArray;
    private int actualTurn;
    private List<List<Pair<Position, Piece>>> whitePiecesTurn;
    private List<List<Pair<Position, Piece>>> blackPiecesTurn;

    
    
    //Per ajudar amb coneixement / CPU
    private List<Pair<Position, Piece>> pListWhite;
    private List<Pair<Position, Piece>> pListBlack;

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
    Chess(int rows, int cols, int chessLimits, int inactiveLimits, List<PieceType> pList, List<String> initPositions, List<Castling> castlings,  
    List<Pair<Position, Piece>> whiteInitPos, List<Pair<Position, Piece>> blackInitPos, PieceColor nextTurnColor, List<Turn> turnList) {
        this.rows = rows;
        this.cols = cols;
        this.chessLimits = chessLimits;
        this.inactiveLimits = inactiveLimits;
        this.pList = pList;
        this.initPositions = initPositions;
        this.castlings = castlings;
        this.whiteInitPos = whiteInitPos;
        this.blackInitPos = blackInitPos;
        this.boardArray = new ArrayList<Piece[][]>();
        this.actualTurn = 0;
        this.whitePiecesTurn = new ArrayList<List<Pair<Position, Piece>>>();
        this.blackPiecesTurn = new ArrayList<List<Pair<Position, Piece>>>();

        this.pListWhite = new ArrayList<Pair<Position, Piece>>();
        this.pListBlack = new ArrayList<Pair<Position, Piece>>();
        if (this.rows < 4 || this.cols < 4 || this.cols > 16 || this.rows > 16)
                throw new RuntimeException("El nombre de files i columnes ha de ser entre 4 i 16");
        board = new Piece[this.rows][this.cols];
        
        //createInitialPositions(whiteInitPos,blackInitPos);
        createBoard();
        //Chess ch = this.copy(this);
        //System.out.println(ch.showBoard());
        //hashCode();
        //Position p=new Position(7,1);
        //Position p2=new Position(3,0)/*
        /*Position p1=new Position(1,0);
        Position p2=new Position(3,0);
        applyMovement(p1,p2,null);
        Position p3=new Position(6,1);
        Position p4=new Position(4,1);
        applyMovement(p3,p4,null);
        Position p5=new Position(3,0);
        Position p6=new Position(4,1);
        List<Position> lp = new ArrayList<Position>();
        lp.add(p6);
        applyMovement(p5,p6,lp);*/
        /*board[1][7]=null;
        board[6][3]=null;
        board[6][2]=null;
        board[0][6]=null;*/
        //applyMovement(p,p2,null);
        /*Position p4=new Position(7,3);
        Position p5=new Position(5,3);*/
        //applyMovement(p4,p5,null);
        //destinyWithValues(p3);

       System.out.println(showBoard());
    }
    Chess copy(Chess c){
        Chess ch = new Chess(c.rows,c.cols,c.chessLimits,
        c.inactiveLimits,c.pList,c.initPositions,c.castlings,
        c.whiteInitPos,c.blackInitPos,c.boardArray,c.actualTurn,
        c.whitePiecesTurn, c.blackPiecesTurn,c.pListWhite,
        c.pListBlack, c.board);
        return ch;
    }
    Chess (int rows, int cols, int chessLimits, int inactiveLimits, List<PieceType> pList, List<String> initPositions, List<Castling> castlings,  
    List<Pair<Position, Piece>> whiteInitPos, List<Pair<Position, Piece>> blackInitPos, List<Piece[][]> boardArray,int actualTurn,
    List<List<Pair<Position, Piece>>> whitePiecesTurn, List<List<Pair<Position, Piece>>> blackPiecesTurn, List<Pair<Position, Piece>> pListWhite, 
    List<Pair<Position, Piece>> pListBlack, Piece[][] board){
        this.rows = rows;
        this.cols = cols;
        this.chessLimits = chessLimits;
        this.inactiveLimits = inactiveLimits;
        this.pList = pList;
        this.initPositions = initPositions;
        this.castlings = castlings;
        this.whiteInitPos = whiteInitPos;
        this.blackInitPos = blackInitPos;
        this.boardArray = boardArray;
        this.actualTurn = actualTurn;
        this.whitePiecesTurn = whitePiecesTurn;
        this.blackPiecesTurn = blackPiecesTurn;
        this.pListWhite = pListWhite;
        this.pListBlack = pListBlack;
        this.board = board;
    }

    @Override
    public Object clone(){
        Chess chess = null;
        try{
            chess = (Chess)super.clone();
        }
        catch(CloneNotSupportedException e){
            System.out.println("Chess clone exception");
        }
        List<PieceType> pListCopy = new ArrayList<PieceType>();
        this.pList.forEach((p)->pListCopy.add( (PieceType)p.clone() ));
        chess.pList = pListCopy;        
        
        List<String> initialPositionsCopy = new ArrayList<String>();
        this.initPositions.forEach((s)->initialPositionsCopy.add(s));
        chess.initPositions = initialPositionsCopy;

        List<Castling> castlingsCopy = new ArrayList<Castling>();
        this.castlings.forEach((p)->castlingsCopy.add( (Castling)p.clone() ));
        chess.castlings = castlingsCopy;

        List<Pair<Position, Piece>> whiteInitPosCopy = new ArrayList<Pair<Position, Piece>>();
        this.whiteInitPos.forEach((p)->whiteInitPosCopy.add( (Pair<Position, Piece>) p.clone() ));
        chess.whiteInitPos = whiteInitPosCopy; 

        List<Pair<Position, Piece>> blackInitPosCopy = new ArrayList<Pair<Position, Piece>>();
        this.blackInitPos.forEach((p)->blackInitPosCopy.add( (Pair<Position, Piece>)p.clone() ));
        chess.blackInitPos = blackInitPosCopy;

        List<Piece[][]> boardArrayCopy = new ArrayList<Piece[][]>();
        this.boardArray.forEach((p)->{
            Piece[][] matrix = new Piece[this.rows][this.cols];
            for(int i=0;i<this.rows;i++)
                for(int j=0;j<this.cols;j++)
                    if(p[i][j]!=null)
                        matrix[i][j] = (Piece) p[i][j].clone();
            boardArrayCopy.add(matrix);
        });
        chess.boardArray = boardArrayCopy;

        List<List<Pair<Position, Piece>>> whitePiecesTurnCopy = new ArrayList<List<Pair<Position, Piece>>>();
        this.whitePiecesTurn.forEach((p)->{
            List<Pair<Position, Piece>> turns = new ArrayList<Pair<Position, Piece>>();
            p.forEach((pair)->turns.add( (Pair<Position, Piece>) pair.clone() ));
            whitePiecesTurnCopy.add(turns);
        } );
        chess.whitePiecesTurn = whitePiecesTurnCopy;

        List<List<Pair<Position, Piece>>> blackPiecesTurnCopy = new ArrayList<List<Pair<Position, Piece>>>();
        this.blackPiecesTurn.forEach((p)->{
            List<Pair<Position, Piece>> turns = new ArrayList<Pair<Position, Piece>>();
            p.forEach((pair)->turns.add( (Pair<Position, Piece>) pair.clone() ));
            blackPiecesTurnCopy.add(turns);
        } );
        chess.blackPiecesTurn = blackPiecesTurnCopy;

        List<Pair<Position, Piece>> pListWhiteCopy = new ArrayList<Pair<Position, Piece>>();
        this.pListWhite.forEach((p)->pListWhiteCopy.add( (Pair<Position, Piece>)p.clone() ));
        chess.pListWhite = pListWhiteCopy;

        List<Pair<Position, Piece>> pListBlackCopy = new ArrayList<Pair<Position, Piece>>();
        this.pListBlack.forEach((p)->pListBlackCopy.add( (Pair<Position, Piece>)p.clone() ));
        chess.pListBlack = pListBlackCopy;

        Piece[][] boardCopy = new Piece[this.rows][this.cols];
            for(int i=0;i<this.rows;i++)
                for(int j=0;j<this.cols;j++)
                    if(board[i][j]!=null)
                        boardCopy[i][j] = (Piece) board[i][j].clone();
        chess.board = boardCopy;

        return chess;
    }

    /*
     * @brief Creates the board
     * @pre Piece's initial positions are not empty
     * @post Every piece is on her board's position
     */
    private void createBoard(){
        for ( int i=0; i<blackInitPos.size(); i++) {
            board[whiteInitPos.get(i).first.row()][whiteInitPos.get(i).first.col()] = whiteInitPos.get(i).second;            
            board[blackInitPos.get(i).first.row()][blackInitPos.get(i).first.col()] = blackInitPos.get(i).second;
        }
        pListWhite = whiteInitPos;
        pListBlack = blackInitPos;

        copyChessTurn();
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
		for (int j = 0; j < cols(); ++j)
			l += "+---";
		l += "+\n";
		s = l;
		for (int i = 0; i < rows(); ++i) {
			if (i < 9)
                            s += " ";
			s += (i+1) + " | ";
			for (int j = 0; j < cols(); ++j) {
				Piece p = board[i][j];
				if (p == null) s += " ";
				else{
                    if(board[i][j].color() == PieceColor.White)
                        s += board[i][j].type().ptSymbol();
                    else
                        s += Character.toLowerCase(board[i][j].type().ptSymbol().charAt(0));
                }                         
				s += " | "; 
			}
			s += "\n" + l;
		}
		s += "     ";
		for (int j = 0; j < cols(); ++j)
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
     * @brief Pieces initial positions
     * @pre --
     * @post Return the pieces initial positions
     */
    public List<String> initialPositions(){
        return this.initPositions;
    }

    /*
     * @brief Number of chess limits
     * @pre --
     * @post Return the limits of chess in a game
     */
    public int chessLimits(){
        return this.chessLimits;
    }

    /*
     * @brief Type list
     * @pre --
     * @post Return the types of the pieces
     */
    public List<PieceType> typeList(){
        return this.pList;
    }

    /*
     * @brief Number of inactive turn limits
     * @pre --
     * @post Return the limits of turns without kill in a game
     */
    public int inactiveLimits(){
        return this.inactiveLimits;
    }

    /*
     * @brief Chess castilngs
     * @pre --
     * @post Return the chess castlings
     */
    public List<Castling> castlings(){
        return this.castlings;
    }


    /*
     * @brief List of white pieces
     * @pre --
     * @post Return a list of white pieces
     */
    public List<Pair<Position,Piece>> pListWhite(){
        return pListWhite;
    }


    /*
     * @brief List of black pieces
     * @pre --
     * @post Return a list of black pieces
     */
    public List<Pair<Position,Piece>> pListBlack(){
        return pListBlack;
    }

    /*
     * @brief List of initial white positions
     * @pre --
     * @post Return the initial list of white positions
     */
    public List<Pair<Position, Piece>> whiteInitPos(){
        return whiteInitPos;
    }

    /*
     * @brief List of initial black positions
     * @pre --
     * @post Return the initial list of black positions
     */
    public List<Pair<Position, Piece>> blackInitPos(){
        return blackInitPos;
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
     * @brief Checks if a piece is in any of the central squares of the movement. If the origin piece
     * can kill while jumping, the positions containing pieces are added to a list. If the origin piece
     * cannot jump, boolean gets true value.
     * @pre A movement is going to be realised 
     * @post Return if there's any piece that the origin piece can't pass across and fill the list if necessary
     */
    private boolean checkPieceOnTheWay(int x0, int y0, int x1, int y1, Movement act, List<Position> piecesToKill){
        boolean pieceOnTheWay = false;
        int initialX=x0;
        int finalX=x1;
        int initialY=y0;
        int finalY=y1;
        //Piece pOrig = board[x0][y0];
        if(x1 < x0){//Si movem peça en direccio de baix cap a dalt, mirem de adalt cap abaix (cambiem 8 5 per 5 8 per tenir un sol for) 
            initialX=x1;
            finalX=x0;
        }
        if(y1 < y0){//Si movem peça en direccio de baix cap a dalt, mirem de adalt cap abaix (cambiem 8 5 per 5 8 per tenir un sol for) 
            initialY=y1;
            finalY=y0;
        }
        if(x0==x1){//es mou en la mateixa fila
            //System.out.println("entro a row");           
            //System.out.println("entro a row i miro de "+initialY+" fins a "+finalY);
            for(int i=initialY+1; i<finalY; i++){
                if(board[x0][i] != null){
                    if(act.canJump()==2){
                        piecesToKill.add(new Position(x0,i));
                    }else{
                        pieceOnTheWay = true;
                    }
                }
            }
        }else if(y0==y1){//es mou en la mateixa columna         
            //System.out.println("entro a col i miro de "+initialX+" fins a "+finalX);
            for(int i=initialX+1; i<finalX; i++){
                if(board[i][y0] != null){
                    if(act.canJump()==2){
                        piecesToKill.add(new Position(i,y0));
                    }else{
                        pieceOnTheWay = true;
                    }
                }
            }
        }else{//es mou en diagonal
            //System.out.println("entro a diagonal");
            int j=initialY+1;
            for(int i=initialX+1; i<finalX; i++){
                if(board[i][j] != null){
                    if(act.canJump()==2){
                        piecesToKill.add(new Position(i,j));
                    }else{
                        pieceOnTheWay = true;
                    }
                   // System.out.println(board[i][j].symbol());
                }
                j++;
            }
        }
        System.out.println("Aquesta peça n'ha trobat pel mig? "+pieceOnTheWay);
        return pieceOnTheWay;
    }

    /*
     * @brief Checks if the movement is in range
     * @pre --
     * @post Return if the movement is in range
     */
    private boolean destinyInLimits(int x1, int y1){
        return ((y1 >=0 && y1 < cols()) && (x1 >=0 && x1 < rows()));
    }

    /*
     * @brief Checks the promotion
     * @pre --
     * @post Return if the piece can promote
     */
    private boolean canPromote(Piece pOrig, Position destiny){
        boolean promote = false;
        if((pOrig.color()==PieceColor.White && destiny.row()==rows()) || (pOrig.color()==PieceColor.Black && destiny.row()==0)){
            promote = pOrig.type().ptPromotable();
        }
        return promote;
    }

    /*
     * @brief Promote a piece
     * @pre The piece want to promote
     * @post Piece has been promoted
     */
    public void promotePiece(Piece originalPiece, Piece pieceToPromote){
        //actualment esta fet perque el consoleGame demani al jugador quina peça escull per promocionar
        //i es retorna la peça. Altres opcions, arriba un char ("T"), fer-ho per posicions...
        originalPiece = new Piece(pieceToPromote);
        //Comprobar si s'ha cambiat tot, systout de peces
    }



    /*
     * @brief Checks if the movement is possible. It validates the cell status, the piece that is going to be killed if it's the case,
     * the possiblity of the piece to jump and kill and checks if the movement is on the piece's movement list.
     * @pre A movement is going to be realised 
     * @post Return if the moviment is possible to execute, a position's list of the pieces to kill and a possible move efect
     */
    public Pair<List<MoveAction>,List<Position>> checkMovement(Position origin, Position destiny) {
        /*
        - pair.second = list de peces a matar
            - si es mou varies caselles i pot matar saltant afegir al list (a les combinades no)
            - si desti hi ha èça i pot matar afegir al list
        - per ferho passem el pair a parametre de piece on the way i alla dins, si es jump = 2, afegim les positions al .second

        */
        Piece pOrig = board[origin.row()][origin.col()];
        Pair<List<MoveAction>,List<Position>> r = new Pair<>(new ArrayList<MoveAction>(),new ArrayList<Position>());
        boolean blackDirection = false; //Saber si juga el negre
		int x0 = origin.row();
		int y0 = origin.col();
		int x1 = destiny.row();
		int y1 = destiny.col();
        Piece p = board[x0][y0];
        List<Movement> pieceMovements = p.type().ptMovements();
        boolean enemiePieceOnDestiny = false;
                if(board[x1][y1]!=null)//Hi ha peça al desti?
                    enemiePieceOnDestiny = diferentOwnerPiece(board[x0][y0],board[x1][y1]);
                //System.out.println("Hi ha peça");   
                //System.out.println(enemiePieceOnDestiny);        
        //Hi ha peça al origen
        if(destinyInLimits(x1,y1)){
            int xMove=x1-x0;
            int yMove=y1-y0;
            if(board[x0][y0].color()==PieceColor.Black){//Si la peça es negre (minuscula) invertim moviment
                xMove = -1*xMove;
                yMove = -1*yMove;//AFEGIR ATRIBUT DIRECTION (1 o -1) A PIECE I QUAN ARRIBA AL FINAL DE TAULER, CAMBIAR-LO
            }                 
            List<Movement> allMoves = new ArrayList<Movement>();
            List<Movement> movesToRead = new ArrayList<Movement>();
            //destinyWithValues(origin);
            if(!p.hasMoved()){    
                p.toggleMoved();
                if(p.type().ptInitMovements()!=null){  
                    movesToRead=p.type().ptInitMovements();
                }
            }else{
                movesToRead=p.type().ptMovements();
            }
            int i = 0;
            boolean found = false;
            boolean pieceOnTheWay = false;
            boolean diagonalCorrect = true;
            while(i < movesToRead.size() && !found){
                List<Position> piecesToKill = new ArrayList<Position>();
                Movement act = movesToRead.get(i);         
                System.out.println(act.movX()+" "+act.movY());            
                if((yMove == act.movY() || act.movY() == 50) && (xMove == act.movX() || act.movX() == 50)){                        
                    if(act.movY()==50 && act.movX()==50){
                        diagonalCorrect = Math.abs(yMove) == Math.abs(xMove); //En cas de moure'ns varies caselles en diagonal, comprobar si es coherent
                        System.out.println("La diagonal "+Math.abs(yMove)+"-"+Math.abs(xMove)+" es correcte? "+diagonalCorrect);
                    }
                    if(diagonalCorrect){
                        if((xMove > 1 || yMove > 1) && act.canJump()!=1){//Si s'ha de desplaçar mes de una posicio i no salta o mata saltant
                            pieceOnTheWay = checkPieceOnTheWay(x0,y0,x1,y1,act,piecesToKill);//passem directament r.second??
                            if(piecesToKill.size()!=0) r.second.addAll(piecesToKill);  
                            System.out.println("Hi ha peça al cami? "+pieceOnTheWay);
                        }
                        //Si pot matar saltant haura recopilat totes les peces que pot matar al camí
                        if(!pieceOnTheWay){ //Si pots saltar les peces o no n'hi ha pel mig
                            if(enemiePieceOnDestiny && act.captureSign() != 0){//Si hi ha peça i la pot matar
                                r.first.add(MoveAction.Correct);
                                //System.out.println(pp.toString());
                                r.second.add(new Position(x1,y1)); 
                                found = true;
                                System.out.println("Mov matar");
                            }else if(!enemiePieceOnDestiny && act.captureSign() != 2){//Si no hi ha peça enemiga i no es un moviment que nomes fa per matar
                                if(board[x1][y1]!=null){ //La peça que vols matar es teva, ja que no s'ha detectat peça enemiga pero n'hi ha una
                                    System.out.println("La peça que vols matar es teva");
                                
                                }else{
                                    r.first.add(MoveAction.Correct);
                                    //r.second = null; 
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
        }
        if(r.first.size()==0){
            r.first.add(MoveAction.Incorrect);
        }else{
            if(canPromote(pOrig, destiny))
                r.first.add(MoveAction.Promote);
        }   
        //if(r.first) applyMovement(origin, destiny, r.second);
        for(int i=0;i<r.first.size();i++)
            System.out.println(r.first.get(i).toString());    
    
    return r;
    }



    /*
    
    SET/ADD
    NULL AL TAULER
    LLISTES COPY
    
    
    */
    public void copyChessTurn(){
        Piece[][] boardCopy = new Piece[rows()][cols()];
        for(int i=0;i<rows();i++){
            for(int j=0;j<cols();j++){
                //System.out.println(rows()+" "+cols()+" "+this.board[i][j].type().ptName());
                if(this.board[i][j]!=null){
                    Piece p = new Piece(this.board[i][j]);
                    boardCopy[i][j] = p;
                }
                else
                    boardCopy[i][j] = null;
            }
        }
        if(boardArray.size()>actualTurn)
            boardArray.set(actualTurn,boardCopy);
        else
            boardArray.add(actualTurn,boardCopy);

        List<Pair<Position,Piece>> whiteListCopy = new ArrayList<Pair<Position, Piece>>();
        for(int i=0;i<pListWhite.size();i++){
            Position wPos = new Position(pListWhite.get(i).first);
            Piece wPiece = new Piece(pListWhite.get(i).second);
            Pair<Position,Piece> wPair = new Pair<>(wPos,wPiece);
            whiteListCopy.add(wPair);
        }
        if(whitePiecesTurn.size()>actualTurn)
            whitePiecesTurn.set(actualTurn,whiteListCopy);
        else
            whitePiecesTurn.add(actualTurn,whiteListCopy);

        List<Pair<Position,Piece>> blackListCopy = new ArrayList<Pair<Position, Piece>>();
        for(int i=0;i<pListBlack.size();i++){
            Position bPos = new Position(pListBlack.get(i).first);
            Piece bPiece = new Piece(pListBlack.get(i).second);
            Pair<Position,Piece> bPair = new Pair<>(bPos,bPiece);
            blackListCopy.add(bPair);
        }
        if(blackPiecesTurn.size()>actualTurn)
            blackPiecesTurn.set(actualTurn,blackListCopy);
        else  
            blackPiecesTurn.add(actualTurn,blackListCopy);

        actualTurn++;
    }

    /** @brief Aplica un moviment i fa canvis a les llistes de peces i les seves posicions
	@pre \p origen i \p desti són posicions vàlides del tauler;
	si \p matar no és null, aleshores és una posició vàlida del
	tauler.
	@post La fitxa de la posició \p origen s'ha mogut a la posició
	\p destí, i si \p matar no és null, s'ha eliminat la fitxa
	d'aquesta posició.
    S'ha modificat la llista de peces corresponent i creat una copia del tauler i peces d'aquest turn
    */
    public void applyMovement(Position origin, Position destiny, List<Position> deathPositions) {
        //Chess ch = new Chess(this);
        //System.out.println("actualTurn "+actualTurn);
        List<Piece> deadPieces = new ArrayList<Piece>();
        if (deathPositions != null){
            //deletePiece(board[death.row()][death.col()]);  
            for(int i=0; i<deathPositions.size(); i++){
                Piece deadPiece = board[deathPositions.get(i).row()][deathPositions.get(i).col()]; 
                deadPieces.add(deadPiece);     
                board[deathPositions.get(i).row()][deathPositions.get(i).col()] = null;
            }
        }
        
        changePiecesList(origin,destiny,deadPieces);
		board[destiny.row()][destiny.col()] = board[origin.row()][origin.col()];
        board[origin.row()][origin.col()] = null;
        //this.isEqual(ch);
        copyChessTurn();
        /*for(int i=0;i<whitePiecesTurn.get(actualTurn).size(); i++){
            System.out.println("Original "+pListWhite.get(i).first.toString()+" copia "+whitePiecesTurn.get(actualTurn).get(i).first.toString());
        }*/
        /*if(actualTurn==2)
        undoMovement();*/
            
    }

    public void remakeBoard(){
        //System.out.println("taulers "+boardArray.size());
        int val = actualTurn-1;
        Piece[][] boardCopy = this.boardArray.get(val);
        for(int i=0;i<rows();i++){
            for(int j=0;j<cols();j++){
                this.board[i][j]=boardCopy[i][j];
            }
        }
        this.pListWhite=whitePiecesTurn.get(val);
        this.pListBlack=blackPiecesTurn.get(val);

        /*for(int i=0;i<whitePiecesTurn.size(); i++){
            System.out.println("--------------"+i+" valor: "+val+"-----------------------");
            if(i==val){
                for(int j=0;j<whitePiecesTurn.get(i).size(); j++){
                    System.out.println(whitePiecesTurn.get(i).get(j).first.toString());
                }
            }
        }
        System.out.println("Peces actuals B");
        for(int j=0;j<pListWhite.size(); j++){
            System.out.println(pListWhite.get(j).first.toString());
        }
        System.out.println("Peces actuals N");
        for(int j=0;j<pListBlack.size(); j++){
            System.out.println(pListBlack.get(j).first.toString());
        }*/
    }

    /*
     * @brief Change the board for the previous on the list of boards
     * @pre It's not the first turn
     * @post Board has been updated
     */
    public void undoMovement(){
        //System.out.println("Normal undo"+showBoard());
        
        actualTurn--;
        remakeBoard(/*boardArray.get(actualTurn)*/);
        //redoMovement();
    }

    /*
     * @brief Change the board for the next on the list of boards
     * @pre undoMovement has been used before
     * @post Board has been updated
     */
    public void redoMovement(){
        //System.out.println("Normal redo"+showBoard());
        actualTurn++;
        //System.out.println("redo "+actualTurn);
        remakeBoard(/*boardArray.get(actualTurn)*/);
        
        //System.out.println("Guardat redo"+showBoard());
    }

    /*
     * @brief Change the piece's position and remove a piece from the according list if necessary
     * @pre --
     * @post Lists of pieces has been updated
     */
    private void changePiecesList(Position origin, Position destiny, List<Piece> deadPieces){
        List<Pair<Position,Piece>> listToChange = new ArrayList<Pair<Position,Piece>>();
        List<Pair<Position,Piece>> listToRemoveOn = new ArrayList<Pair<Position,Piece>>();
        boolean search=true;
        Piece pOrig = board[origin.row()][origin.col()];
        
        if(pOrig.color() == PieceColor.White){
            listToChange = pListWhite;
            listToRemoveOn = pListBlack;
        }else{
            listToChange = pListBlack;
            listToRemoveOn = pListWhite;
        }
        int i=0;
        while(i<listToChange.size() && search){
            if(listToChange.get(i).second.equals(pOrig)){
                listToChange.get(i).first = destiny;
                search = false;
            }
            i++;    
        }
        if(deadPieces!=null){
            for(int j=0; j<deadPieces.size(); j++){
                i=0;
                search=true;
                while(i<listToRemoveOn.size() && search){
                    if(listToRemoveOn.get(i).second.equals(deadPieces.get(j))){
                        //System.out.println("He matat "+listToRemoveOn.get(i).second.type().ptName());
                        listToRemoveOn.remove(i);
                        search = false;
                    }
                    i++;
                }
            }
        }/*
        for(int j=0;j<listToChange.size();j++){
            System.out.println("Piece: "+listToChange.get(j).second.type().ptName()+"   Pos"+listToChange.get(j).first.row()+" "+listToChange.get(j).first.col()+"    "+listToChange.get(j).first.toString());
        }*/

    }

    /*
     * @brief Checks all possible piece's movements and their values
     * @pre -- 
     * @post 
    */
    private boolean controller(Position origin, Position destiny, Movement mov, List<Pair<Position, Integer>> destinyWithValues){
        Pair<Position, Integer> act = new Pair<>(destiny, 0);
        int value = 0;
        boolean continueFunc = true;    
        if(board[destiny.row()][destiny.col()] == null && mov.captureSign() != 2){ //Moviment normal sense matar
            act = new Pair<>(destiny, 0);
            destinyWithValues.add(act);
        }else if(board[destiny.row()][destiny.col()] != null && mov.captureSign() != 0 && diferentOwnerPiece(board[origin.row()][origin.col()],board[destiny.row()][destiny.col()])){
            value = board[destiny.row()][destiny.col()].type().ptValue();
            act = new Pair<>(destiny, value);
            destinyWithValues.add(act);
            continueFunc=false;      //Arriba fins que mata una                      
        }else{                
            continueFunc=false;  //Troba una peça aliada
        }             
    
        return continueFunc;
    }

    /*Sempre mirem ambod costats: msg forum sobre a -a */
    public List<Pair<Position, Integer>> destinyWithValues(Position origin){
        List<Pair<Position, Integer>> destinyWithValues = new ArrayList<Pair<Position, Integer>>();
        Position destiny;
        int value = 0;        
        Piece p = board[origin.row()][origin.col()];
        List<Movement> movesToRead = new ArrayList<Movement>(); 

        if(!p.hasMoved()){    
            if(p.type().ptInitMovements()!=null){  
                movesToRead=p.type().ptInitMovements();
            }
        }else{
            movesToRead=p.type().ptMovements();
        }
        /*
        for(int i=0; i<movesToRead.size(); i++){
            Movement mov = movesToRead.get(i);
            System.out.println(mov.movX()+" "+mov.movY());
        }*/
       // movesToRead.addAll(p.type().ptMovements());
        for(int i=0; i<movesToRead.size(); i++){
            boolean continueFunc = true; //False si es troba amb una peça pel cami
            Movement mov = movesToRead.get(i);
            //System.out.println(mov.movX()+" "+mov.movY());
            if((mov.movX() == 50 || mov.movX() == -50) && (mov.movY() == 50 || mov.movY() == -50)){//Diagonals            
                continueFunc = true;
                int y=1;
                int x=1;
                if(mov.movX() == 50 && mov.movY() == 50 || mov.movX() == -50 && mov.movY() == -50){
                    while(destinyInLimits(origin.row()+x,origin.col()+y) && continueFunc){    //De dreta a esquerra , de dalt a baix 
                        //System.out.println((origin.row()+x)+" d "+(origin.col()+y));                                                       
                        destiny = new Position(origin.row()+x, origin.col()+y);
                        continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                        x++;
                        y++;
                    }
                    continueFunc = true;
                    y=1;
                    x=1;
                    while(destinyInLimits(origin.row()-x,origin.col()-y) && continueFunc){    //De esquerra a dreta, de baix a dalt 
                        //System.out.println((origin.row()-x)+" d- "+(origin.col()-y));                                                              
                        destiny = new Position(origin.row()-x, origin.col()-y);
                        continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                        x++;
                        y++;
                    }
                }
                if(mov.movX() == -50 && mov.movY() == 50 || mov.movX() == +50 && mov.movY() == -50){
                    while(destinyInLimits(origin.row()-x,origin.col()+y) && continueFunc){    //De dreta a esquerra, de baix a dalt 
                        //System.out.println((origin.row()-x)+" d-+ "+(origin.col()+y));                                                             
                        destiny = new Position(origin.row()-x, origin.col()+y);
                        continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                        x++;
                        y++;
                    }
                    continueFunc=true;
                    y=1;
                    x=1;
                    while(destinyInLimits(origin.row()+x,origin.col()-y) && continueFunc){    //De esquerra a dreta, de dalt a baix mentre estiguis dins els limits
                        //System.out.println((origin.row()+x)+" d+- "+(origin.col()-y));                                                              
                        destiny = new Position(origin.row()+x, origin.col()-y);
                        continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                        x++;
                        y++;
                    }
                }
            }else if((mov.movY() == 50 || mov.movY() == -50)){//Es mou horitzontal
                int y=1;
                while(destinyInLimits(origin.row()+mov.movX(), origin.col()+y) && continueFunc){    //De dreta a esquerra  
                    //System.out.println(mov.movX()+" "+y);                                                       
                    destiny = new Position(origin.row()+mov.movX(), origin.col()+y);
                    continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                    y++;
                }
                continueFunc=true;
                y=1;
                while(destinyInLimits(origin.row()+mov.movX(), origin.col()-y) && continueFunc){     //De esquerra a dreta 
                    //System.out.println(mov.movX()+" "+y);                                                       
                    destiny = new Position(origin.row()-mov.movX(), origin.col()-y);
                    continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                    y++;
                }
            }
            else if((mov.movX() == 50 || mov.movX() == -50)){//Es mou en vertical, nomes la fila cambia
                int x=1;
                while(destinyInLimits(origin.row()+x, origin.col()+mov.movY()) && continueFunc){  //De dalt a baix 
                    //System.out.println(x+" "+mov.movY());                                                       
                    destiny = new Position(origin.row()+x, origin.col()+mov.movY());
                    continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                    x++;
                }
                continueFunc=true; 
                x=1;
                while(destinyInLimits(origin.row()-x, origin.col()-mov.movY()) && continueFunc){  //De dalt a baix 
                    //System.out.println(x+" "+mov.movY());                                                       
                    destiny = new Position(origin.row()-x, origin.col()-mov.movY());
                    continueFunc = controller(origin,destiny,mov,destinyWithValues);                    
                    x++;
                }
            }else{      
                //System.out.println("F");
                if(p.color() == PieceColor.Black){
                    destiny = new Position(origin.row()-mov.movX(), origin.col()-mov.movY());
                }else{
                    destiny = new Position(origin.row()+mov.movX(), origin.col()+mov.movY());
                }
                if(destinyInLimits(destiny.row(),destiny.col())){
                    continueFunc=controller(origin,destiny,mov,destinyWithValues);
                    System.out.println("entro pel peo i miro si pot anar a "+destiny.row()+" "+destiny.col());
                }
            }
        }
        for(int i=0;i<destinyWithValues.size();i++)System.out.println(destinyWithValues.get(i).first.toString()+" "+destinyWithValues.get(i).second);
        return destinyWithValues;
    }

    
    public String chessStringView(PieceColor pc){
        String s = "";
        Piece[] p = new Piece[rows()*cols()];
        if(pc==PieceColor.White){
            for(int i=0; i<rows(); i++){
                for(int j=0; j<cols(); j++){
                    if(board[i][j]==null)
                        s += "-";
                    else if(board[i][j].color()==PieceColor.Black)
                        s += board[i][j].type().ptSymbol().toLowerCase();
                    else
                        s += board[i][j].type().ptSymbol();
                }
            }
        }else{
            for(int i=rows()-1; i>=0; i--){
                for(int j=cols()-1; j>=0; j--){
                    if(board[i][j]==null)
                        s += "-";
                    else if(board[i][j].color()==PieceColor.White)
                        s += board[i][j].type().ptSymbol().toLowerCase();
                    else
                        s += board[i][j].type().ptSymbol();
                }
            }
        }
        return s;
    }
    
    /*
     * @brief Checks if this chess is the same as another chess looking all his board cell and pieces
     * @pre Chess is not null 
     * @post Return if this chess is the same as another chess
     */
    public boolean equals(Chess c){
        int i = 0;
        int j = 0;
        boolean same = true;
        while(i < rows() && same){
            //System.out.println("fila "+i);
            j=0;
            while(j < cols() && same){
                Position p = new Position (i,j);
                if(!emptyCell(p) && !c.emptyCell(p)){ //les dues plenes
                    //System.out.println("columna "+j+" de la fila "+i+" amb valors "+board[i][j].name()+" i "+c.board[i][j].name());
                    if(board[i][j].color() != c.board[i][j].color() || board[i][j].type().ptName() != c.board[i][j].type().ptName()){
                        same = false;
                    }
                }else{ //una es plena i l'altre no
                    if(emptyCell(p) && !c.emptyCell(p) || !emptyCell(p) && c.emptyCell(p)){
                        same = false;
                    }
                }
                j++;
            }
            i++;
        }
        //System.out.println("Els chess son iguals? "+same);
        return same;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(); 
        s.append("Rows: " + rows + ",\n")
        .append("Columns: " + cols + ",\n")
        .append("Chess Limits: " + chessLimits + ",\n")
        .append("Inactive Turns Limits: " + inactiveLimits + ",\n")
        .append("PIECES: \n");
        
        for (PieceType p : pList) {
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
