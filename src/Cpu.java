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
        i_minMax(0,0,0,movement,Integer.MIN_VALUE,Integer.MAX_VALUE);
        return movement;
    }
    /** @brief Immersion function for minMaxAlgorsim
    @pre --
    @return Returns the puntuation choosen for the @p playerType of the actual profunity.
     */
    private int i_minMax(int score,int profundity,int playerType,Pair<Position,Position> movement,int biggestAnterior,int smallerAnterior){
        if(profundity==_profundity)return score;
        else if(playerType==0){
            Integer max = Integer.MIN_VALUE;
            List<Pair<Position,Piece>> pieces;
            if(_color==PieceColor.White)pieces=_chess.pListWhite();
            else pieces=_chess.pListBlack();
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){  // FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                System.out.println("tauler actual:"+_chess.showBoard());
                System.out.println("Posició de la peça actual provant:\n"+piece.first.toString());
                List<Pair<Position,Integer>> destinyWithScores = _chess.destinyWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext()){// FOR EACH MOVEMENT
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result=pieceMovement.second + score;
                    if(pieceMovement.second>0)_chess.applyMovement(piece.first,null,pieceMovement.first);//aplicar
                    else _chess.applyMovement(piece.first,pieceMovement.first,null);
                    result = i_minMax(result,profundity+1,1,movement,biggestAnterior,smallerAnterior);
                    _chess.undoMovement();
                    if(result>max){
                        biggestAnterior=result;
                        max=result;
                        movement.first=piece.first;
                        movement.second=pieceMovement.first;
                    }
                    if(smallerAnterior<=biggestAnterior)break;
                }
            }
            return max;
        }
        else{
            Integer min = Integer.MAX_VALUE;
            List<Pair<Position,Piece>> pieces;
            if(_color==PieceColor.Black)pieces=_chess.pListWhite();
            else pieces=_chess.pListBlack();
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){  //FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                System.out.println("tauler actual:"+_chess.showBoard());
                System.out.println("Posició de la peça actual provant:\n"+piece.first.toString());
                List<Pair<Position,Integer>> destinyWithScores = _chess.destinyWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext()){ //FOR EACH MOVEMENT
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result= -pieceMovement.second + score;
                    if(pieceMovement.second>0)_chess.applyMovement(piece.first,null,pieceMovement.first);//aplicar
                    else _chess.applyMovement(piece.first,pieceMovement.first,null);
                    result = i_minMax(result,profundity+1,0,movement,biggestAnterior,smallerAnterior);
                    _chess.undoMovement();
                    if(result<min){
                        smallerAnterior=result;
                        min=result;
                        movement.first=piece.first;
                        movement.second=pieceMovement.first;
                    }
                    if(biggestAnterior>=smallerAnterior)break;
                }
            }
            return min;
        }
    }
}