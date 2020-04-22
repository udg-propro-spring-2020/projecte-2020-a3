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
        //Position p=new Position(7,1);
        //Position p2=new Position(3,0)/*
        Position p3=new Position(1,0);
        /*board[1][7]=null;
        board[6][3]=null;
        board[6][2]=null;
        board[0][6]=null;
        //applyMovement(p,p2,null);
        Position p4=new Position(7,3);
        Position p5=new Position(5,3);
        applyMovement(p4,p5,null);*/
        //destinyWithValues(p3);

       // System.out.println(showBoard());
    }

    /*
    Chess(Chess c){
        this.rows = c.rows;
        this.cols = c.cols;
        this.chessLimits = c.chessLimits;
        this.inactiveLimits = c.inactiveLimits;
        this.pList = c.pList;
        this.initPositions = c.initPositions;
        this.castlings = c.castlings;
        this.initPositionsWhite = c.initPositionsWhite;
        this.initPositionsBlack = c.initPositionsBlack;
        this.pListWhite = c.pListWhite;
        this.pListBlack = c.pListBlack;
        board = new Piece[this.rows][this.cols];
        createBoard();
    }*/


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
				else s += board[i][j].type().ptSymbol();                        
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
            //System.out.println("entro a row");           
            //System.out.println("entro a row i miro de "+initialY+" fins a "+finalY);
            for(int i=initialY+1; i<finalY; i++){
                if(board[x0][i] != null){
                    pieceOnTheWay = true;
                }
            }
        }else if(y0==y1){//es mou en la mateixa columna         
            //System.out.println("entro a col i miro de "+initialX+" fins a "+finalX);
            for(int i=initialX+1; i<finalX; i++){
                if(board[i][y0] != null){
                    pieceOnTheWay = true;
                }
            }
        }else{//es mou en diagonal
            //System.out.println("entro a diagonal");
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



    private boolean destinyInLimits(int x1, int y1){
        return ((y1 >=0 && y1 < cols()) && (x1 >=0 && x1 < rows()));
    }
    /*
     * @brief Checks if the movement is possible. It validates the cell status, the piece that is going to be killed if it's the case,
     * the possiblity of the piece to jump and kill and checks if the movement is on the piece's movement list.
     * @pre A movement is going to be realised 
     * @post Return if the moviment is possible to execute and the position of the piece to kill 
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
        List<Movement> pieceMovements = p.type().ptMovements();
        //Hi ha peça al origen
        if(destinyInLimits(x1,y1)){
            if(board[x1][y1]!=null)//Hi ha peça al desti?
                enemiePieceOnDestiny = diferentOwnerPiece(board[x0][y0],board[x1][y1]);
                //System.out.println("Hi ha peça");   
                //System.out.println(enemiePieceOnDestiny);        
            
            int xMove=x1-x0;
            int yMove=y1-y0;
            if(board[x0][y0].color()==PieceColor.Black){//Si la peça es negre (minuscula) invertim moviment
                xMove = -1*xMove;
                yMove = -1*yMove;
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
                Movement act = movesToRead.get(i);         
                //System.out.println(act.toString());            
                if((yMove == act.movY() || act.movY() == 50) && (xMove == act.movX() || act.movX() == 50)){                        
                    if(act.movY()==50 && act.movX()==50){
                        diagonalCorrect = Math.abs(yMove) == Math.abs(xMove); //En cas de moure'ns varies caselles en diagonal, comprobar si es coherent
                        //System.out.println("La diagonal "+Math.abs(yMove)+"-"+Math.abs(xMove)+" es correcte? "+diagonalCorrect);
                    }
                    if(diagonalCorrect){
                        if((xMove > 1 || yMove > 1) && !act.canJump()){//Si s'ha de desplaçar mes de una posicio i no pot saltar
                            pieceOnTheWay = checkPieceOnTheWay(x0,y0,x1,y1);
                            //System.out.println("Hi ha peça al cami? "+pieceOnTheWay);
                        }
                        if(!pieceOnTheWay){ //Si pot saltar, no s'activa   
                            if(enemiePieceOnDestiny && act.captureSign() != 0){//Si hi ha peça i la pot matar
                                r.first = true;
                                r.second = new Position(x1,y1); 
                                found = true;
                                //System.out.println("Mov matar");
                            }else if(!enemiePieceOnDestiny && act.captureSign() != 2){//Si no hi ha peça enemiga i no es un moviment que nomes fa per matar
                                if(board[x1][y1]!=null){ //La peça que vols matar es teva, ja que no s'ha detectat peça enemiga pero n'hi ha una
                                    System.out.println("La peça que vols matar es teva");
                                
                                }else{
                                    r.first = true;
                                    r.second = null; 
                                    found = true;
                                    //System.out.println("Mov normal");
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
        //System.out.println(r.first);          
    return r;
    }


    public void copyChessTurn(){
        Piece[][] boardCopy = new Piece[rows()][cols()];
        for(int i=0;i<rows();i++){
            for(int j=0;j<cols();j++){
                boardCopy[i][j]=this.board[i][j];
            }
        }
        boardArray.add(actualTurn,boardCopy);
        whitePiecesTurn.add(actualTurn,pListWhite);
        blackPiecesTurn.add(actualTurn,pListBlack);
        actualTurn++;
    }

    /** @brief Aplica un moviment
	@pre \p origen i \p desti són posicions vàlides del tauler;
	si \p matar no és null, aleshores és una posició vàlida del
	tauler.
	@post La fitxa de la posició \p origen s'ha mogut a la posició
	\p destí, i si \p matar no és null, s'ha eliminat la fitxa
	d'aquesta posició.
    S'ha modificat la llista de peces corresponent i creat una copia del tauler i peces d'aquest turn
    */
    public void applyMovement(Position origin, Position destiny, Position death) {
        //Chess ch = new Chess(this);
        //System.out.println("actualTurn "+actualTurn);
        Piece deadPiece = null;
        if (death != null){
            //deletePiece(board[death.row()][death.col()]);  
            deadPiece = board[death.row()][death.col()];      
            board[death.row()][death.col()] = null;
        }
        
        changePiecesList(origin,destiny,deadPiece);
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
        int val = actualTurn;
        val--;
        Piece[][] boardCopy = this.boardArray.get(val);
        for(int i=0;i<rows();i++){
            for(int j=0;j<cols();j++){
                this.board[i][j]=boardCopy[i][j];
            }
        }
        this.pListWhite=this.whitePiecesTurn.get(val);
        this.pListBlack=this.blackPiecesTurn.get(val);
        for(int i=0;i<whitePiecesTurn.get(val).size(); i++){
            System.out.println("Original "+whitePiecesTurn.get(val).get(i).first.toString()+" copia "+whitePiecesTurn.get(val).get(i).first.toString());
        }
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
        System.out.println("redo "+actualTurn);
        remakeBoard(/*boardArray.get(actualTurn)*/);
        
        //System.out.println("Guardat redo"+showBoard());
    }

    /*
     * @brief Change the piece's position and remove a piece from the according list if necessary
     * @pre --
     * @post Lists of pieces has been updated
     */
    private void changePiecesList(Position origin, Position destiny, Piece deadPiece){
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
        if(deadPiece!=null){
            i=0;
            search=true;
            while(i<listToRemoveOn.size() && search){
                if(listToRemoveOn.get(i).second.equals(deadPiece)){
                    //System.out.println("He matat "+listToRemoveOn.get(i).second.type().ptName());
                    listToRemoveOn.remove(i);
                    search = false;
                }
                i++;
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




    /*
     * @brief Checks if this chess is the same as another chess looking all his board cell and pieces
     * @pre Chess is not null 
     * @post Return if this chess is the same as another chess
     */
    public boolean isEqual(Chess c){
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
