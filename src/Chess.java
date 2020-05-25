/*
 * @author David Cáceres González
 */

import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.NullPointerException;

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
    private List<Turn> turnList;
    private PieceColor nextTurnColor;
    private Piece[][] board;
    private List<Piece[][]> boardArray;
    private int currentTurn;
    private List<List<Pair<Position, Piece>>> whitePiecesTurn;
    private List<List<Pair<Position, Piece>>> blackPiecesTurn;
    private final static int unlimitadeMove = 50; //That variable means that piece can move any number of cells
    private List<Pair<Position, Piece>> pListWhite;
    private List<Pair<Position, Piece>> pListBlack;

    /**
     * @brief Chess default constructor
     * @param rows Board's row number
     * @param cols Board's column number
     * @param pList List of pieces
     * @param chessLimits Number maximum of chess in a game
     * @param inactiveLimits Number maximum of inactive turns (without kill) in a game
     * @param initPositions Piece's initial default positions
     * @param castlings Special move
     */
    Chess(int rows, int cols, List<PieceType> pList, List<String> initPositions, int chessLimits, int inactiveLimits, List<Castling> castlings) {
        this.rows = rows;
        this.cols = cols;
        this.chessLimits = chessLimits;
        this.inactiveLimits = inactiveLimits;
        this.pList = pList;
        this.initPositions = initPositions;
        this.castlings = castlings;
        this.boardArray = new ArrayList<Piece[][]>();
        this.currentTurn = 0;
        this.whitePiecesTurn = new ArrayList<List<Pair<Position, Piece>>>();
        this.blackPiecesTurn = new ArrayList<List<Pair<Position, Piece>>>();
        this.whiteInitPos = new ArrayList<Pair<Position,Piece>>();
        this.blackInitPos = new ArrayList<Pair<Position,Piece>>();
        this.board = new Piece[rows()][cols()];
        this.pListWhite = new ArrayList<Pair<Position, Piece>>();
        this.pListBlack = new ArrayList<Pair<Position, Piece>>();
        
        createInitPos();
        createBoard();
    }

    /**
     * @brief Chess configuration constructor
     * @param chess Default chess
     * @param whiteInitPos List with a pair that contains white pieces and their positions
     * @param blackInitPos List with a pair that contains black pieces and their positions
     */
    Chess(Chess chess, List<Pair<Position, Piece>> whiteInitPos, List<Pair<Position, Piece>> blackInitPos) {
        if(chess==null) throw new NullPointerException("Chess given argument cannot be null");
        this.rows = chess.rows;
        this.cols = chess.cols;
        this.chessLimits = chess.chessLimits;
        this.inactiveLimits = chess.inactiveLimits;
        this.pList = chess.pList;
        this.initPositions = chess.initPositions;
        this.castlings = chess.castlings;
        this.boardArray = chess.boardArray;
        this.currentTurn = chess.currentTurn;
        this.whitePiecesTurn = chess.whitePiecesTurn;
        this.blackPiecesTurn = chess.blackPiecesTurn;
        this.pListWhite = new ArrayList<Pair<Position, Piece>>();
        this.pListBlack = new ArrayList<Pair<Position, Piece>>();
        this.board = chess.board;
        this.whiteInitPos = whiteInitPos;
        this.blackInitPos = blackInitPos;

        createBoard();
    }

    /*
     * @brief Makes a copy of a chess
     * @pre Chess is not null
     * @post Return a copy of a chess
     */
    Chess copy(Chess c){
        Chess ch = new Chess(c.rows,c.cols,c.chessLimits,
        c.inactiveLimits,c.pList,c.initPositions,c.castlings,
        c.whiteInitPos,c.blackInitPos,c.boardArray,c.currentTurn,
        c.whitePiecesTurn, c.blackPiecesTurn,c.pListWhite,
        c.pListBlack, c.board);
        return ch;
    }

    /*
     * @brief Constructor to make a copy of a chess, used when cloning
     * @pre --
     * @post Chess has been copied
     */
    Chess (int rows, int cols, int chessLimits, int inactiveLimits, List<PieceType> pList, List<String> initPositions, List<Castling> castlings,  
    List<Pair<Position, Piece>> whiteInitPos, List<Pair<Position, Piece>> blackInitPos, List<Piece[][]> boardArray,int currentTurn,
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
        this.currentTurn = currentTurn;
        this.whitePiecesTurn = whitePiecesTurn;
        this.blackPiecesTurn = blackPiecesTurn;
        this.pListWhite = pListWhite;
        this.pListBlack = pListBlack;
        this.board = board;
    }

    /*
     * @brief Clone method
     * @pre --
     * @post Makes a chess clone
     */
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
     * @brief Create an organized pieceType's list using the initial piece order
     * @pre --
     * @post Return an organized pieceType's list
     */
    private List<PieceType> initPieceType(){
        boolean found=false;
        List<PieceType> listPieceType = new ArrayList<PieceType>();
        for(int i=0; i<initPositions.size(); i++){
            if(initPositions.get(i).isEmpty()){
                listPieceType.add(null);
            }
            else{
                int j=0;
                found=false;
                while(j<pList.size() && !found){
                    if(pList.get(j).ptName().equals(initPositions.get(i))){
                        listPieceType.add(pList.get(j));
                        found = true;
                    }
                    j++;
                }
            }
        }
        return listPieceType;
    }

    /*
     * @brief Fill the lists that contains the piece's current and initial position
     * @pre initPositions is not null
     * @post The lists that control the piece's positions has been filled
     */
    private void createInitPos(){
        List<PieceType> listPieceType = new ArrayList<PieceType>();
        listPieceType = initPieceType();
        int whiteRowCounter=0;
        int colCounter=0;
        int blackRowCounter=rows()-1;
        int insertCounter=0;
        while(whiteRowCounter<2){
            colCounter=0;
            while(colCounter<cols()){
                if(listPieceType.get(insertCounter)==null){
                    whiteInitPos.add(new Pair<Position,Piece>(new Position (whiteRowCounter,colCounter),null));
                    blackInitPos.add(new Pair<Position,Piece>(new Position (blackRowCounter,colCounter),null));
                }else{
                    whiteInitPos.add(new Pair<Position,Piece>(new Position (whiteRowCounter,colCounter),new Piece(listPieceType.get(insertCounter), false, PieceColor.White)));
                    blackInitPos.add(new Pair<Position,Piece>(new Position (blackRowCounter,colCounter),new Piece(listPieceType.get(insertCounter), false, PieceColor.Black)));
                }
                colCounter++;
                insertCounter++;
            }
            whiteRowCounter++;
            blackRowCounter--;
        }
    }

    /*
     * @brief Creates the board and alive pieces list
     * @pre Piece's initial positions are not empty
     * @post Every piece is on her board's position and lists has been created
     */
    private void createBoard(){
        for (int i=0; i<blackInitPos.size(); i++) {
            if(blackInitPos.get(i).second==null){
                board[whiteInitPos.get(i).first.row()][whiteInitPos.get(i).first.col()] = null;
                board[blackInitPos.get(i).first.row()][blackInitPos.get(i).first.col()] = null;
            }else{
                pListWhite.add(new Pair<Position,Piece>(new Position (whiteInitPos.get(i).first.row(),whiteInitPos.get(i).first.col()), new Piece(whiteInitPos.get(i).second)));
                pListBlack.add(new Pair<Position,Piece>(new Position (blackInitPos.get(i).first.row(),blackInitPos.get(i).first.col()), new Piece(blackInitPos.get(i).second)));

                board[whiteInitPos.get(i).first.row()][whiteInitPos.get(i).first.col()] = new Piece(whiteInitPos.get(i).second);
                board[blackInitPos.get(i).first.row()][blackInitPos.get(i).first.col()] = new Piece(blackInitPos.get(i).second);
            }            
        }
    }

    /*
     * @brief Check if the piece can make her vertical move looking the movement stats and 
     * the cells between the origin and destiny position. If the piece can kill while jumping, add
     * the killed pieces to a list.
     * @pre The piece can try her movement in board limits
     * @post Return if the piece can make her vertical move without crashing with other pieces
     */
    private boolean checkVerticalMove(Position origin, Position destiny, Movement currentMovement, List<Position> piecesToKill, Piece originPiece){
        boolean pieceOnTheWay = false;
        if(origin.row()<destiny.row()){ //from top to bottom
            for(int i=origin.row(); i<destiny.row(); i++){
                if(pieceAt(i,origin.col())!=null && pieceAt(i,origin.col())!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(i,origin.col())))
                            piecesToKill.add(new Position(i,origin.col()));
                    }else
                        pieceOnTheWay = true;
                }
            }
        }else{ //from bottom to top
            for(int i=origin.row(); i>destiny.row(); i--){ 
                if(pieceAt(i,origin.col())!=null && pieceAt(i,origin.col())!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(i,origin.col())))
                            piecesToKill.add(new Position(i,origin.col()));
                    }else
                        pieceOnTheWay = true;
                }
            }
        }
        return pieceOnTheWay;
    }
    
    /*
     * @brief Check if the piece can make her horizontal move looking the movement stats and 
     * the cells between the origin and destiny position. If the piece can kill while jumping, add
     * the killed pieces to a list.
     * @pre The piece can try her movement in board limits
     * @post Return if the piece can make her horizontal move without crashing with other pieces
     */
    private boolean checkHorizontalMove(Position origin, Position destiny, Movement currentMovement, List<Position> piecesToKill, Piece originPiece){
        boolean pieceOnTheWay = false;
        if(origin.col()<destiny.col()){ //from left to right
            for(int i=origin.col(); i<destiny.col(); i++){
                if(pieceAt(origin.row(),i) != null && pieceAt(origin.row(),i)!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(origin.row(),i)))
                            piecesToKill.add(new Position(origin.row(),i));
                    }else
                        pieceOnTheWay = true;
                } 
            }
        }else{ //from right to left
            for(int i=origin.col(); i>destiny.col(); i--){
                if(pieceAt(origin.row(),i) != null && pieceAt(origin.row(),i)!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(origin.row(),i)))
                            piecesToKill.add(new Position(origin.row(),i));
                    }else
                        pieceOnTheWay = true;
                }
            }
        }
        return pieceOnTheWay;
    }
    
    /*
     * @brief Check if the piece can make her diagonal move looking the movement stats and 
     * the cells between the origin and destiny position. If the piece can kill while jumping, add
     * the killed pieces to a list.
     * @pre The piece can try her movement in board limits
     * @post Return if the piece can make her diagonal move without crashing with other pieces
     */
    private boolean checkDiagonalMove(Position origin, Position destiny, Movement currentMovement, List<Position> piecesToKill, Piece originPiece){
        boolean pieceOnTheWay = false;
        if(origin.row()<destiny.row() && origin.col()<destiny.col()){ //from top-left to bottom-right
            int i = origin.row();
            for(int j=origin.col(); j<destiny.col(); j++){
                if(pieceAt(i,j) != null && pieceAt(i,j)!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(i,j)))
                            piecesToKill.add(new Position(i,j));
                    }else
                        pieceOnTheWay = true;
                }
                i++;
            }
        }
        else if(origin.row()<destiny.row() && origin.col()>destiny.col()){ //from top-right to bottom-left
            int i = origin.row();
            for(int j=origin.col(); j>destiny.col(); j--){
                if(pieceAt(i,j) != null && pieceAt(i,j)!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(i,j)))
                            piecesToKill.add(new Position(i,j));
                    }else
                        pieceOnTheWay = true;
                }
                i++;
            }
        }
        else if(origin.row()>destiny.row() && origin.col()<destiny.col()){ //from bottom-left to top-right
            int i = origin.row();
            for(int j=origin.col(); j<destiny.col(); j++){
                if(pieceAt(i,j) != null && pieceAt(i,j)!=originPiece){
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(i,j)))
                            piecesToKill.add(new Position(i,j));
                    }else
                        pieceOnTheWay = true;
                }
                i--;
            }
        }
        else{ //from bottom-right to top-left
            int i = origin.row();
            for(int j=origin.col(); j>destiny.col(); j--){
                if(pieceAt(i,j) != null && pieceAt(i,j)!=originPiece){ 
                    if(currentMovement.canJump()==2){
                        if(diferentOwnerPiece(originPiece, pieceAt(i,j)))
                            piecesToKill.add(new Position(i,j));
                    }else
                        pieceOnTheWay = true;
                }
                i--;
            }
        }
        return pieceOnTheWay;
    }

    /*
     * @brief Checks if a piece is in any of the central squares of the movement. If the origin piece
     * can kill while jumping, the positions containing pieces are added to a list. If the origin piece
     * cannot jump, the return gets true value.
     * @pre A movement is going to be realised 
     * @post Return if there's any piece that the origin piece can't pass across and fill the list if necessary
     */
    private boolean checkPieceOnTheWay(Position origin, Position destiny, Movement currentMovement, List<Position> piecesToKill){
        boolean pieceOnTheWay = false;
        Piece originPiece = pieceAt(origin.row(),origin.col());
        if(origin.row()==destiny.row()){ //same row movement
            pieceOnTheWay = checkHorizontalMove(origin, destiny, currentMovement, piecesToKill, originPiece);
        }else if(origin.col()==destiny.col()){ //same column movement        
            pieceOnTheWay = checkVerticalMove(origin, destiny, currentMovement, piecesToKill, originPiece);
        }else{ //diagonal movement
            pieceOnTheWay = checkDiagonalMove(origin, destiny, currentMovement, piecesToKill, originPiece);
        }
        return pieceOnTheWay;
    }

    /*
     * @brief Checks if the movement is in range looking the limits
     * @pre Parameters are not null
     * @post Return if the movement is in range
     */
    private boolean destinyInLimits(int row, int col){
        return ((col >=0 && col < cols()) && (row >=0 && row < rows()));
    }

    /*
     * @brief Checks if a piece is on her last row so it can promote
     * @pre Piece is at her destiny position
     * @post Return if the piece can promote
     */
    private boolean canPromote(Position origin, Position destiny, boolean calledByMoveDestiniesController){
        Piece piece = pieceAt(destiny.row(),destiny.col()); //Movement has already been realised so piece is at her destiny
        boolean promote = false;
        if(piece!=null){ //Not a castling move. If it was a castling, destiny position would not have a piece
            if((piece.color()==PieceColor.White && destiny.row()==rows()-1) || (piece.color()==PieceColor.Black && destiny.row()==0)){
                if(origin.row() != destiny.row()){
                    promote = piece.type().ptPromotable();
                    if(!calledByMoveDestiniesController) piece.toggleDirection();
                }
            }
        }
        return promote;
    }

    /*
     * @brief Check if the king can't escape from a check
     * @pre Lists are not empty
     * @post Return if the player can realize any move that makes him escape from a check
     */
    private boolean isCheckmate(List<Pair<Position,Piece>> listEvadeCheckmate, List<Pair<Position,Piece>> listDoingCheck){
        boolean checkmate = true;
        Pair<List<MoveAction>,List<Position>> checkMovementResult = new Pair<>(new ArrayList<MoveAction>(),new ArrayList<Position>());
        int i = 0;
        while(i<listEvadeCheckmate.size() && checkmate){
            Piece piece = listEvadeCheckmate.get(i).second;
            Position origin = listEvadeCheckmate.get(i).first;
            List<Pair<Position, Integer>> pieceDestinies = allPiecesDestiniesWithValues(origin);
            //System.out.println("Comporvarem: "+pieceAt(origin.row(),origin.col()).type().ptName()+" -> "+origin.toString());
            int j = 0;
            while(j<pieceDestinies.size() && checkmate){
                //System.out.println("La "+piece.type().ptName()+" vol realitzar el moviment a "+pieceDestinies.get(j).first.toString());
                Position destiny = pieceDestinies.get(j).first;
                applyMovement(origin, destiny, checkMovementResult.second, true);
                if(!isCheck(listDoingCheck)){
                    checkmate = false;
                    //System.out.println("Trobem escape "+piece.type().ptName()+" "+destiny.toString());
                }
                undoMovement();
                j++;
            }
            i++;
        }
        return checkmate;
    }

    /*
     * @brief Checks if the king is checked by any enemie piece
     * @pre List is not empty
     * @post Return if the king is checked
     */
    public boolean isCheck(List<Pair<Position,Piece>> listDoingMove){
        boolean checkKing = false;
        boolean found = false;
        List<List<Pair<Position, Integer>>> allMovesWithValues = new ArrayList<List<Pair<Position, Integer>>>();
        listDoingMove.forEach((p)->allMovesWithValues.add(allPiecesDestiniesWithValues(p.first)));
        int i = 0;

        while(i<allMovesWithValues.size() && !found){
            List<Pair<Position, Integer>> listMoveValues = new ArrayList<Pair<Position, Integer>>();
            listMoveValues = allMovesWithValues.get(i);
            int j = 0;
            while(j<listMoveValues.size()){
                if(listMoveValues.get(j).second == 100){
                    checkKing = true;
                    found = true;
                }
                j++;
            }
            i++;
        }
        return checkKing;
    }

    /*
     * @brief Promote a piece
     * @pre Position is not null and pieceType exists
     * @post Piece has been promoted
     */
    public void promotePiece(Position originalPiecePosition, PieceType promotionPieceType){
        Piece originalPiece = pieceAt(originalPiecePosition.row(),originalPiecePosition.col());     
        originalPiece.promoteType(promotionPieceType);
    }

    /*
     * @brief Current position of the king
     * @pre PieceColor exists
     * @post Return the king's position
     */
    public Position kingPosition(PieceColor pieceColorValue){
        Position position = new Position(0,0);
        boolean found = false;
        List<Pair<Position,Piece>> listToCheck = new ArrayList<Pair<Position,Piece>>();
        if(pieceColorValue == PieceColor.White)
            listToCheck = pListWhite;
        else
            listToCheck = pListBlack;
        int i=0;
        while(i<listToCheck.size() && !found){
            if(listToCheck.get(i).second.type().ptSymbol().charAt(0)=='R')
                position = listToCheck.get(i).first; 
            i++;
        }
        return position;
    }

    /*
     * @brief Apply a castling and check if it makes any special action. It also updates some pieces lists and makes
     * a copy from the current board and lists
     * @pre The movement is possible
     * @post Retun a list of possible special actions. Alive piece's list has been updated and a turn chess has been saved
     */
    public void applyCastling(List<Position> castlingsPositions){
        Position firstPieceOrigin = castlingsPositions.get(0);
        Position secondPieceOrigin = castlingsPositions.get(1);
        Position firstPieceDestiny = castlingsPositions.get(2);
        Position secondPieceDestiny = castlingsPositions.get(3);
        Piece firstPiece = pieceAt(firstPieceOrigin.row(), firstPieceOrigin.col());
        if(!firstPiece.hasMoved())   
            firstPiece.toggleMoved();
        Piece secondPiece = pieceAt(secondPieceOrigin.row(), secondPieceOrigin.col());
        if(!secondPiece.hasMoved())   
            secondPiece.toggleMoved();

        changePiecesList(firstPieceOrigin,firstPieceDestiny,null);
        changePiecesList(secondPieceOrigin,secondPieceDestiny,null);

		board[firstPieceDestiny.row()][firstPieceDestiny.col()] = pieceAt(firstPieceOrigin.row(),firstPieceOrigin.col());
        board[firstPieceOrigin.row()][firstPieceOrigin.col()] = null;
        board[secondPieceDestiny.row()][secondPieceDestiny.col()] = pieceAt(secondPieceOrigin.row(),secondPieceOrigin.col());
        board[secondPieceOrigin.row()][secondPieceOrigin.col()] = null;
    }

    /*
     * @brief Save the destiny position of both castling pieces
     * @pre Positions and middle value are not null
     * @post Destiny positions has been saved
     */
    private void findNewPositions(Position firstPieceCastling, Position secondPieceCastling, int middle, Pair<List<MoveAction>,List<Position>> checkResult ){
        Position newFirstPieceDestiny;
        Position newSecondPieceDestiny;
        if(firstPieceCastling.col()<secondPieceCastling.col()){
            newFirstPieceDestiny = new Position(firstPieceCastling.row(),firstPieceCastling.col()+middle);
            newSecondPieceDestiny = new Position(firstPieceCastling.row(),firstPieceCastling.col()+(middle-1));
        }else{
            newFirstPieceDestiny = new Position(firstPieceCastling.row(),firstPieceCastling.col()-middle);
            newSecondPieceDestiny = new Position(firstPieceCastling.row(),firstPieceCastling.col()-(middle-1));
        }
        checkResult.second.add(newFirstPieceDestiny);
        checkResult.second.add(newSecondPieceDestiny);
    }


    /*
     * @brief Checks if castling destinies are empty or if the second piece goes to the first piece position
     * and middle cell is empty
     * @pre Positions and middle value are not null
     * @post Return if destinies are correct 
     */
    private boolean checkDestinies(Position firstPieceCastling, Position secondPieceCastling, int middle){
        boolean possibleDestinies = false;
        int currentRow = firstPieceCastling.row();
        if(firstPieceCastling.col()<secondPieceCastling.col()) //Chose direction by the columns
            possibleDestinies = (pieceAt(currentRow,firstPieceCastling.col()+middle)==null && 
                (pieceAt(currentRow,firstPieceCastling.col()+(middle-1))==null || 
                    pieceAt(currentRow, firstPieceCastling.col()+(middle-1)).equals(pieceAt(currentRow, firstPieceCastling.col()))));
        else    
            possibleDestinies = (pieceAt(currentRow,firstPieceCastling.col()-middle)==null && 
                (pieceAt(currentRow,firstPieceCastling.col()-(middle-1))==null || 
                    pieceAt(currentRow, firstPieceCastling.col()-(middle-1)).equals(pieceAt(currentRow, firstPieceCastling.col()))));
        return possibleDestinies;
    }
    
    /*
     * @brief Checks if cells between two pieces are all empty
     * @pre Positions are not null
     * @post Return if cells between two pieces are all empty
     */
    private boolean checkMiddleCells(Position firstPiecePos, Position secondPiecePos){
        int countMiddleCells = Math.abs(firstPiecePos.col()-secondPiecePos.col())-1; //-1 to avoid origin cell
        boolean found = false;
        boolean emptyCells = true;
        Position position = new Position(0,0);
        int i=1;
        if(firstPiecePos.col() < secondPiecePos.col())
            position = firstPiecePos;
        else
            position = secondPiecePos;
        
        while(i<countMiddleCells && !found){
            if(pieceAt(position.row(),position.col()+i)!=null){
                found=true;
                emptyCells=false;
            }
            i++;
        }
        return emptyCells;
    }

    /*
     * @brief Check if a movement is a castling, looking all the castling properties. It works always sorting the pieces
     * by his representation on his castling
     * @pre Origin position isn't null
     * @post Return true if the castling is possible
     */
    private boolean isCastling(Position firstPiecePos, Position secondPiecePos, Pair<List<MoveAction>,List<Position>> checkResult){
        boolean found = false;
        boolean correctCastling = false;
        boolean emptyDestinies = false;
        int i = 0;
        int countMiddleCells = Math.abs(firstPiecePos.col()-secondPiecePos.col())-1;//-1 to avoid the origin col
        int middle = (countMiddleCells/2)+1;
        Piece firstPiece = pieceAt(firstPiecePos.row(), firstPiecePos.col());
        Piece secondPiece = pieceAt(secondPiecePos.row(), secondPiecePos.col());
        Position firstPieceCastling = firstPiecePos; //This one should move middle+1 / middle-1
        Position secondPieceCastling = secondPiecePos;
        if(secondPiece!=null){
            if(secondPiece.color().equals(firstPiece.color())){
                while(i<castlings.size() && !found){
                    Castling castling = castlings.get(i);
                    if((castling.aPiece().equals(firstPiece.type().ptName()) && castling.bPiece().equals(secondPiece.type().ptName())) || 
                        (castling.bPiece().equals(firstPiece.type().ptName()) && castling.aPiece().equals(secondPiece.type().ptName()))){
                        if(castling.aPiece().equals(secondPiece.type().ptName())){ //Chosing the piece going to the middle cell
                            firstPieceCastling = secondPiecePos; 
                            secondPieceCastling = firstPiecePos;
                        }
                        checkResult.second.add(firstPieceCastling); //Origin positions
                        checkResult.second.add(secondPieceCastling);
                        if((castling.stand() && !firstPiece.hasMoved() && !secondPiece.hasMoved()) || !castling.stand()){
                            emptyDestinies = checkDestinies(firstPieceCastling, secondPieceCastling, middle);
                            if((castling.emptyMid() && checkMiddleCells(firstPiecePos,secondPiecePos)) || !castling.emptyMid() && emptyDestinies){
                                findNewPositions(firstPieceCastling,secondPieceCastling, middle, checkResult);
                                checkResult.first.add(MoveAction.Castling);
                                correctCastling = true;
                            }
                        }
                    }
                    i++;
                }
            }
        }
        return correctCastling;
    }

    /*
     * @brief Checks if the movement is possible. It validates two possible movement type (castling or normal move). 
     * @pre Origin is not null 
     * @post Return if the moviment is possible to execute and a position's list that contains the pieces to kill if
     * normal move case. Else it contains origin and destiny castling positions.
     */
    public Pair<List<MoveAction>,List<Position>> checkMovement(Position origin, Position destiny) {
        Pair<List<MoveAction>,List<Position>> checkResult = new Pair<>(new ArrayList<MoveAction>(),new ArrayList<Position>());
        Piece piece = pieceAt(origin.row(),origin.col());
        List<Movement> movesToRead = piece.pieceMovements();
        boolean enemiePieceOnDestiny = false;
 
        if(destinyInLimits(destiny.row(),destiny.col())){
            int rowMove=destiny.row()-origin.row();
            int colMove=destiny.col()-origin.col();           
            int i = 0;
            boolean found = false;
            boolean pieceOnTheWay = false;
            boolean correctDiagonal = true;
            if(!isCastling(origin,destiny,checkResult)){
                while(i < movesToRead.size() && !found){
                    List<Position> piecesToKill = new ArrayList<Position>();
                    Movement currentMovement = movesToRead.get(i);         
                    if((colMove == currentMovement.movY() || Math.abs(currentMovement.movY()) == unlimitadeMove) && (rowMove == currentMovement.movX() || Math.abs(currentMovement.movX()) == unlimitadeMove)){                        
                        if(Math.abs(currentMovement.movY())==unlimitadeMove && Math.abs(currentMovement.movX())==unlimitadeMove){
                            correctDiagonal = Math.abs(colMove) == Math.abs(rowMove);
                        }
                        if(correctDiagonal){
                            if((rowMove > 1 || colMove > 1 ||  rowMove < -1 || colMove < -1) && currentMovement.canJump()!=1){ //Piece can't jump or kill while jumping
                                pieceOnTheWay = checkPieceOnTheWay(origin,destiny,currentMovement,piecesToKill);
                                if(piecesToKill.size()!=0) 
                                    checkResult.second.addAll(piecesToKill);  
                            }
                            if(!pieceOnTheWay){ //If piece can jump or there is not any piece on the way
                                if(pieceAt(destiny.row(),destiny.col())!=null)
                                    enemiePieceOnDestiny = diferentOwnerPiece(pieceAt(origin.row(),origin.col()),pieceAt(destiny.row(),destiny.col()));
                                if(enemiePieceOnDestiny && currentMovement.captureSign() != 0){
                                    if(!pieceAt(destiny.row(),destiny.col()).type().ptInvulnerable()){
                                        checkResult.first.add(MoveAction.Correct);
                                        checkResult.second.add(new Position(destiny.row(),destiny.col())); 
                                        found = true;
                                    }
                                }else if(!enemiePieceOnDestiny && currentMovement.captureSign() != 2){ //Only killing move control
                                    if(pieceAt(destiny.row(),destiny.col())==null){ //At this point, destiny piece is from the same player doing the move
                                        checkResult.first.add(MoveAction.Correct);
                                        found = true;
                                    }
                                }              
                            }  
                        }
                    }
                    i++; 
                }   
            }else{
                checkResult.first.add(MoveAction.Correct);
                checkResult.first.add(MoveAction.Castling);
            }
            if(checkResult.first.size()==0){
                checkResult.first.add(MoveAction.Incorrect);
            }
        }
    return checkResult;
    }

    /*
     * @brief Creates a copy of the tunr, cloning the board and the piece's lists and save them into
     * one list
     * @pre Board and piece lists exist
     * @post The board and piece's list of the turn has been saved
     */
    private void copyChessTurn(){
        Piece[][] boardCopy = new Piece[rows()][cols()];
        for(int i=0;i<this.rows;i++)
            for(int j=0;j<this.cols;j++)
                if(board[i][j]!=null)
                    boardCopy[i][j] = (Piece) board[i][j].clone();

        List<Pair<Position,Piece>> whiteListCopy = new ArrayList<Pair<Position, Piece>>();
        this.pListWhite.forEach((p)->whiteListCopy.add( (Pair<Position, Piece>)p.clone() ));
        List<Pair<Position,Piece>> blackListCopy = new ArrayList<Pair<Position, Piece>>();
        this.pListBlack.forEach((p)->blackListCopy.add( (Pair<Position, Piece>)p.clone() ));

        if(whitePiecesTurn.size()>currentTurn){
            whitePiecesTurn.set(currentTurn,whiteListCopy);
            blackPiecesTurn.set(currentTurn,blackListCopy);
            boardArray.set(currentTurn,boardCopy);
        }
        else{
            whitePiecesTurn.add(currentTurn,whiteListCopy);
            blackPiecesTurn.add(currentTurn,blackListCopy);
            boardArray.add(currentTurn,boardCopy);
        }
            
        currentTurn++;
    }

    /*
     * @brief Apply a movement and check if it makes any special action like promote. It also updates some pieces lists and makes
     * a copy from the current board and lists
     * @pre The movement is possible
     * @post Retun a list of possible special actions, dead and alive piece's list has been updated and a turn chess has been saved
     */
    public List<MoveAction> applyMovement(Position origin, Position destiny, List<Position> workingPositions, boolean calledByIsCheckmate) { 
        //Working positions represents killed pieces in normal move case or origin-destiny positions in castling move
        copyChessTurn();
        List<MoveAction> actions = new ArrayList<MoveAction>();
        PieceColor originPieceColor = pieceAt(origin.row(),origin.col()).color();
        if(pieceAt(destiny.row(),destiny.col())!=null && pieceAt(origin.row(),origin.col()).color().equals(pieceAt(destiny.row(),destiny.col()).color())){
            applyCastling(workingPositions);
        }
        else{
            Piece piece = pieceAt(origin.row(), origin.col());
            if(!piece.hasMoved())   
                piece.toggleMoved();
            
            List<Piece> listDeadPieces = new ArrayList<Piece>();
            if (workingPositions != null){
                for(int i=0; i<workingPositions.size(); i++){
                    Piece deadPiece = pieceAt(workingPositions.get(i).row(),workingPositions.get(i).col()); 
                    listDeadPieces.add(deadPiece);     
                    board[workingPositions.get(i).row()][workingPositions.get(i).col()] = null;
                }
            }
            changePiecesList(origin,destiny,listDeadPieces);
            board[destiny.row()][destiny.col()] = pieceAt(origin.row(),origin.col());
            board[origin.row()][origin.col()] = null;
        }
        //Updated lists
        List<Pair<Position,Piece>> listDoingMove = new ArrayList<Pair<Position,Piece>>();
        List<Pair<Position,Piece>> listCounterMove = new ArrayList<Pair<Position,Piece>>();
        if(originPieceColor == PieceColor.White){
            listDoingMove = pListWhite;
            listCounterMove = pListBlack;
        }
        else{ 
            listDoingMove = pListBlack;
            listCounterMove = pListWhite;
        }
        if(!calledByIsCheckmate){
            if(canPromote(origin, destiny, false))
                actions.add(MoveAction.Promote);
            if(isCheck(listDoingMove)){
                if(isCheckmate(listCounterMove, listDoingMove))
                    actions.add(MoveAction.Checkmate);
                else
                    actions.add(MoveAction.Check);
            }else{
                if(isCheckmate(listCounterMove, listDoingMove))
                    actions.add(MoveAction.Checkmate);
            }
        }
        return actions;
    }

    /*
     * @brief Recreates a chess by updating his piece's list and board
     * @pre currentTurn > 0
     * @post Chess has been updated
     */
    private void remakeBoard(){
        Piece[][] boardCopy = this.boardArray.get(currentTurn);
        for(int i=0;i<rows();i++){
            for(int j=0;j<cols();j++){
                this.board[i][j]=boardCopy[i][j];
            }
        }
        this.pListWhite=whitePiecesTurn.get(currentTurn);
        this.pListBlack=blackPiecesTurn.get(currentTurn);
    }

    /*
     * @brief Change the board for the previous on the list of boards, saving the current board
     * @pre It's not the first turn
     * @post Board has been updated and a copy of current turn has been saved
     */
    public void undoMovement(){
        copyChessTurn();
        currentTurn=currentTurn-2;
        remakeBoard();
    }

    /*
     * @brief Change the board for the next on the list of boards
     * @pre undoMovement has been used before
     * @post Board has been updated
     */
    public void redoMovement(){
        currentTurn++;
        remakeBoard();
    }

    /*
     * @brief Change the piece's position and remove a piece from the according list if necessary
     * @pre --
     * @post Lists of pieces has been updated
     */
    private void changePiecesList(Position origin, Position destiny, List<Piece> listDeadPieces){
        List<Pair<Position,Piece>> listToChange;
        List<Pair<Position,Piece>> listToRemoveOn;
        boolean search=true;
        Piece originPiece = pieceAt(origin.row(),origin.col());
        if(originPiece==null) System.out.println(showBoard());
        if(originPiece.color() == PieceColor.White){
            listToChange = pListWhite;
            listToRemoveOn = pListBlack;
        }else{
            listToChange = pListBlack;
            listToRemoveOn = pListWhite;
        }
        int i=0;
        while(i<listToChange.size() && search){
            if(listToChange.get(i).second.equals(originPiece)){
                listToChange.get(i).first = destiny;
                search = false;
            }
            i++;    
        }
        if(listDeadPieces!=null){
            for(int j=0; j<listDeadPieces.size(); j++){
                i=0;
                search=true;
                while(i<listToRemoveOn.size() && search){
                    if(listToRemoveOn.get(i).second.equals(listDeadPieces.get(j))){
                        listToRemoveOn.remove(i);
                        search = false;
                    }
                    i++;
                }
            }
        }
    }

    /*
     * @brief Controll all the possible destinies in a piece movement and add it into a list if it's correct
     * @pre Destiny is inside the board limits
     * @post Return if the piece can continue checking destinies with the current movement. Correct destinies has been added to the list
    */
    private boolean moveDestiniesController(Position origin, Position destiny, Movement currentMove, List<Pair<Position, Integer>> destiniesWithValues){
        Pair<Position, Integer> currentDesinyValue = new Pair<>(destiny, 0);
        int value = 0;
        boolean keepSearching = true;    
        if(pieceAt(destiny.row(),destiny.col()) == null && currentMove.captureSign() != 2){ //No needed to kill
            currentDesinyValue = new Pair<>(destiny, 0);
            if(canPromote(origin,destiny,true))
                currentDesinyValue.second+=1;
            destiniesWithValues.add(currentDesinyValue);
        }else if(pieceAt(destiny.row(),destiny.col()) != null && currentMove.captureSign() != 0 && diferentOwnerPiece(pieceAt(origin.row(),origin.col()),pieceAt(destiny.row(),destiny.col()))){
            value = pieceAt(destiny.row(),destiny.col()).type().ptValue();
            currentDesinyValue = new Pair<>(destiny, value);
            destiniesWithValues.add(currentDesinyValue);
            keepSearching=false;  //Killed a piece                     
        }else{                
            keepSearching=false;  //Found an ally piece
        }             
    
        return keepSearching;
    }
    
    /*
     * @brief Checks all possible piece's movements and their values
     * @pre Origin is not null
     * @post Return all possible piece's destinies and its movement value
    */
    public List<Pair<Position, Integer>> allPiecesDestiniesWithValues(Position origin){
        if(origin==null)throw new NullPointerException("Trying to read a null position (allPiecesDestiniesWithValues)");

        List<Pair<Position, Integer>> destiniesWithValues = new ArrayList<Pair<Position, Integer>>();
        Position destiny;
        int value = 0;        
        Piece piece = pieceAt(origin.row(),origin.col());
        List<Movement> movesToRead = new ArrayList<Movement>(); 
        movesToRead=piece.pieceMovements();

        for(int i=0; i<movesToRead.size(); i++){
            boolean keepSearching = true; //Keep looking movement possible destinies while movement is correct
            Movement currentMove = movesToRead.get(i);
            if((currentMove.movX() == unlimitadeMove || currentMove.movX() == -unlimitadeMove) && (currentMove.movY() == unlimitadeMove || currentMove.movY() == -unlimitadeMove)){ //Diagonals            
                keepSearching = true;
                int y=1;
                int x=1;
                if(currentMove.movX() == unlimitadeMove && currentMove.movY() == unlimitadeMove || currentMove.movX() == -unlimitadeMove && currentMove.movY() == -unlimitadeMove){
                    while(destinyInLimits(origin.row()+x,origin.col()+y) && keepSearching){ //From top-right to bottom-left
                        destiny = new Position(origin.row()+x, origin.col()+y);
                        keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                        x++;
                        y++;
                    }
                    keepSearching = true;
                    y=1;
                    x=1;
                    while(destinyInLimits(origin.row()-x,origin.col()-y) && keepSearching){ //From bottom-left to top-right
                        destiny = new Position(origin.row()-x, origin.col()-y);
                        keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                        x++;
                        y++;
                    }
                }
                if(currentMove.movX() == -unlimitadeMove && currentMove.movY() == unlimitadeMove || currentMove.movX() == unlimitadeMove && currentMove.movY() == -unlimitadeMove){
                    while(destinyInLimits(origin.row()-x,origin.col()+y) && keepSearching){ //From bottom-right to top-left 
                        destiny = new Position(origin.row()-x, origin.col()+y);
                        keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                        x++;
                        y++;
                    }
                    keepSearching=true;
                    y=1;
                    x=1;
                    while(destinyInLimits(origin.row()+x,origin.col()-y) && keepSearching){    //From top-left to bottom-right
                        destiny = new Position(origin.row()+x, origin.col()-y);
                        keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                        x++;
                        y++;
                    }
                }
            }else if((currentMove.movY() == unlimitadeMove || currentMove.movY() == -unlimitadeMove)){ //Horizontal move
                int y=1;
                while(destinyInLimits(origin.row()+currentMove.movX(), origin.col()+y) && keepSearching){ //From right to left
                    destiny = new Position(origin.row()+currentMove.movX(), origin.col()+y);
                    keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                    y++;
                }
                keepSearching=true;
                y=1;
                while(destinyInLimits(origin.row()+currentMove.movX(), origin.col()-y) && keepSearching){  //From left to right
                    destiny = new Position(origin.row()-currentMove.movX(), origin.col()-y);
                    keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                    y++;
                }
            }
            else if((currentMove.movX() == unlimitadeMove || currentMove.movX() == -unlimitadeMove)){ //Vertical move
                int x=1;
                while(destinyInLimits(origin.row()+x, origin.col()+currentMove.movY()) && keepSearching){ //From top to bottom
                    destiny = new Position(origin.row()+x, origin.col()+currentMove.movY());
                    keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                    x++;
                }
                keepSearching=true; 
                x=1;
                while(destinyInLimits(origin.row()-x, origin.col()-currentMove.movY()) && keepSearching){ //From bottom to top
                    destiny = new Position(origin.row()-x, origin.col()-currentMove.movY());
                    keepSearching = moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);                    
                    x++;
                }
            }else{      
                    if(destinyInLimits(origin.row()+currentMove.movX(), origin.col()+currentMove.movY())){
                        destiny = new Position(origin.row()+currentMove.movX(), origin.col()+currentMove.movY());
                        keepSearching=moveDestiniesController(origin,destiny,currentMove,destiniesWithValues);
                    }
            }
        }
        return destiniesWithValues;
    }

    /*
     * @brief Create an string containing all chess pieces. The point of view depends on the piece color
     * @pre Board is created 
     * @post Return all possible piece's destinies and its kill value
    */
    public String chessStringView(PieceColor pieceColorValue){
        String s = "";
        Piece[] p = new Piece[rows()*cols()];
        if(pieceColorValue==PieceColor.White){
            for(int i=0; i<rows(); i++){
                for(int j=0; j<cols(); j++){
                    if(pieceAt(i,j)==null)
                        s += "-";
                    else if(pieceAt(i,j).color()==PieceColor.Black)
                        s += pieceAt(i,j).type().ptSymbol().toLowerCase();
                    else
                        s += pieceAt(i,j).type().ptSymbol();
                }
            }
        }else{
            for(int i=rows()-1; i>=0; i--){
                for(int j=cols()-1; j>=0; j--){
                    if(pieceAt(i,j)==null)
                        s += "-";
                    else if(pieceAt(i,j).color()==PieceColor.White)
                        s += pieceAt(i,j).type().ptSymbol().toLowerCase();
                    else
                        s += pieceAt(i,j).type().ptSymbol();
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
    public boolean equals(Chess chess){
        int row = 0;
        int col = 0;
        boolean same = true;
        while(row < rows() && same){
            col=0;
            while(col < cols() && same){
                Position position = new Position (row,col);
                if(!emptyCell(position) && !chess.emptyCell(position)){
                    if(pieceAt(row,col).color() != chess.pieceAt(row,col).color() || pieceAt(row,col).type().ptName() != chess.pieceAt(row,col).type().ptName())
                        same = false;
                }else{
                    if(emptyCell(position) && !chess.emptyCell(position) || !emptyCell(position) && chess.emptyCell(position))
                        same = false;
                }
                col++;
            }
            row++;
        }
        return same;
    }

    /*
     * @brief Returns the piece's color
     * @pre Position is not null
     * @post Return the piece's color
     */
    public PieceColor cellColor(Position position){
        return pieceAt(position.row(),position.col()).color();
    }
    
    /*
     * @brief Checks if the cell has any piece
     * @pre Position is not null
     * @post Return if the cell is empty
     */
    public boolean emptyCell(Position position){
        return pieceAt(position.row(),position.col())==null;
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
     * @brief Chess castlings
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
     * @pre Pieces are not null
     * @post Return if a piece is from a diferent player
     */
    private boolean diferentOwnerPiece(Piece originPiece, Piece destinyPiece){
        return (!originPiece.color().equals(destinyPiece.color()));
    }

    /*
     * @brief Check if there is any piece at specified position
     * @pre x and y inside the chess limits and not nulls
     * @post Return the piece at the specified position or a null if it doesn't exist
     */
    public Piece pieceAt(int x, int y){
        return board[x][y];
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
