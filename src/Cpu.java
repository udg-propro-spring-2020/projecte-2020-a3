import java.util.List;
import java.util.Iterator;

/** @file Cpu.java
    @brief Automatic player.
 */

/**
    @class Cpu
    @brief Automatic player with knowledge of previous games.
 */

public class Cpu{

    private Knowledge _knowledge;   ///< Knwoledge of winner sequencies
    private Chess _chess;           ///< Reference to global chess object
    private int _profundity;       ///< Profunidty level for search the possibilities movements tree.
    private PieceColor _color;      ///< Color of the CPU player


    /** @brief CPU constructor
    @pre --
    @post La cpu has the Knowledge @p knowledge,reference to global chess @p chess,
    profundity for searching possibles movements @p profundity and his color is @p color.
     */
    public Cpu(Knowledge knowledge,Chess chess,int profundity,PieceColor color){

        _knowledge=knowledge;
        _chess=chess;
        _profundity=profundity;
        _color=color;
    }

    /** @brief Makes a movement
    @pre --
        @return Returns a pair which indicates the origin position and final position of the pice movmement choose.
        If the knowledge its following a sequence and the @p anteriorMovement matches with the expected movement returns
        the next sequence movement. Otherwise if the state of @c _chess matches with one saved at knowledge returns the
        movement to do. On the other hand returns the best movement possible of all possible movements inside the profunidty
        @c _profundity. (In the actual version just returning minmax)
     */
    public Pair<Position,Position> doMovement(Pair<Position,Position> anteriorMovement){
        return minMax();    
    }

    /** @biref Returns the optimal movement to do.
    @pre --
    @return Returns the optimal movement to do inside the decision tree with profundity @c _profunidty.
     */
    private Pair<Position,Position> minMax(){ 
        Pair<Position,Position> movement = new Pair<Position,Position>(null,null);
        System.out.println("profunditat:"+_profundity);
        if(_color==PieceColor.White)System.out.println("cpu es blanca");
        else System.out.println("cpu es negra");
        Chess copia = _chess.copy(_chess);
        //copia.applyMovement(new Position(1,0),new Position(2,0),null);
        System.out.println(_chess.showBoard());
        System.out.println(copia.showBoard());
        i_minMax(0,0,0,movement,Integer.MIN_VALUE,Integer.MAX_VALUE,copia);
        System.out.println("best movement: i:"+movement.first.toString() + " d:" + movement.second.toString() );
        return movement;
    }
    /** @brief Immersion function for minMaxAlgorsim
    @pre --
    @return Returns the puntuation choosen for the @p playerType of the actual profunity.
     */
    private int i_minMax(int score,int profundity,int playerType,Pair<Position,Position> movement,int biggestAnterior,int smallerAnterior,Chess tauler){
        if(profundity==_profundity)return score;
        else if(playerType==0){
            Integer max = Integer.MIN_VALUE;
            List<Pair<Position,Piece>> pieces;
            if(_color==PieceColor.White)pieces=tauler.pListWhite();
            else pieces=tauler.pListBlack();
            /*Iterator<Pair<Position,Piece>> itPieces1 = pieces.iterator();
            System.out.println("------------------");
            while(itPieces1.hasNext()){
                Pair<Position,Piece> piece1 = itPieces1.next();
                System.out.println("color:"+piece1.second.color());
            }
            System.out.println("------------------");*/
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            int i=0;
            while(itPieces.hasNext()){  // FOR EACH PIECE
                i++;
                Pair<Position,Piece> piece = itPieces.next();
                System.out.println("(cpu.java 70)profunitat:"+profundity+"peçes provades    :"+i+" color peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" TAULER ACTUAL:\n");
                //System.out.println(_chess.showBoard());
                List<Pair<Position,Integer>> destinyWithScores = tauler.destinyWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext()){// FOR EACH MOVEMENT
                    Chess taulerCopia = tauler.copy(tauler);
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result=pieceMovement.second + score;
                    System.out.println("(cpu.java 77)Moviment possible peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" DESTI:"+pieceMovement.first.toString());
                    if(pieceMovement.second>0)tauler.applyMovement(piece.first,pieceMovement.first,pieceMovement.first);//aplicar
                    else tauler.applyMovement(piece.first,pieceMovement.first,null);
                    System.out.println("(cpu.java 80) tauler despres d'aplicar moviment:"+taulerCopia.showBoard());
                    result = i_minMax(result,profundity+1,1,movement,biggestAnterior,smallerAnterior,tauler);
                    //_chess = originalChess.copy(originalChess);
                    tauler = taulerCopia;
                    System.out.println("(cpu.java 84) tauler despres de desfer moviment  peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol()+tauler.showBoard());

                    if(result>max){
                        biggestAnterior=result;
                        max=result;
                        movement.first=piece.first;
                        movement.second=pieceMovement.first;
                        //System.out.println("(cpu.java 91)new best movement: i:"+movement.first.toString() + " d:" + movement.second.toString() );
                    }
                    if(smallerAnterior<=biggestAnterior)break;
                }
            }
            return max;
        }
        else{
            Integer min = Integer.MAX_VALUE;
            List<Pair<Position,Piece>> pieces;
            if(_color==PieceColor.Black)pieces=tauler.pListWhite();
            else pieces=tauler.pListBlack();
            /*Iterator<Pair<Position,Piece>> itPieces1 = pieces.iterator();
            System.out.println("------------------");
            while(itPieces1.hasNext()){
                Pair<Position,Piece> piece1 = itPieces1.next();
                System.out.println("color:"+piece1.second.color());
            }
            System.out.println("------------------");*/
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){  //FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                System.out.println("(cpu.java 107)profunitat:"+profundity+" color peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" TAULER ACTUAL:\n");
                System.out.println("(cpu.java 108)"+tauler.showBoard());
                List<Pair<Position,Integer>> destinyWithScores = tauler.destinyWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext()){ //FOR EACH MOVEMENT
                    Chess taulerCopia = tauler.copy(tauler);
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result= -pieceMovement.second + score;
                    //System.out.println("(cpu.java 132)"+ta.showBoard());
                    //System.out.println("(cpu.java 133)Moviment possible peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" DESTI:"+pieceMovement.first.toString());
                    if(pieceMovement.second>0)tauler.applyMovement(piece.first,pieceMovement.first,pieceMovement.first);//aplicar
                    else tauler.applyMovement(piece.first,pieceMovement.first,null);
                    System.out.println("(cpu.java 117) tauler despres d'aplicar moviment:"+tauler.showBoard());
                    result = i_minMax(result,profundity+1,0,movement,biggestAnterior,smallerAnterior,tauler);
                    //_chess = originalChess.copy(originalChess);
                    tauler = taulerCopia;
                    System.out.println("(cpu.java 121) tauler despres de desfer moviment  peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol()+tauler.showBoard());

                    if(result<min){
                        smallerAnterior=result;
                        min=result;
                        //movement.first=piece.first;
                        //movement.second=pieceMovement.first;
                        //System.out.println("(cpu.java 128)new best movement: i:"+movement.first.toString() + " d:" + movement.second.toString() );
                    }
                    if(biggestAnterior>=smallerAnterior)break;
                }
            }
            return min;
        }
    }
}