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
        i_minMax(0,0,0,movement,Integer.MIN_VALUE,Integer.MAX_VALUE);
        System.out.println("best movement: i:"+movement.first.toString() + " d:" + movement.second.toString() );
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
            Iterator<Pair<Position,Piece>> itPieces1 = pieces.iterator();
            System.out.println("------------------");
            while(itPieces1.hasNext()){
                Pair<Position,Piece> piece1 = itPieces1.next();
                System.out.println("color:"+piece1.second.color());
            }
            System.out.println("------------------");
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){  // FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                Position initialPosition = new Position(piece.first.row(),piece.first.col());
                //System.out.println("(cpu.java 70)profunitat:"+profundity+" color peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" TAULER ACTUAL:\n");
                //System.out.println(_chess.showBoard());
                List<Pair<Position,Integer>> destinyWithScores = _chess.destinyWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext()){// FOR EACH MOVEMENT
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result=pieceMovement.second + score;
                    //System.out.println("(cpu.java 77)Moviment possible peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" DESTI:"+pieceMovement.first.toString());
                    if(pieceMovement.second>0)_chess.applyMovement(piece.first,pieceMovement.first,pieceMovement.first);//aplicar
                    else _chess.applyMovement(piece.first,pieceMovement.first,null);
                    //System.out.println("(cpu.java 80) tauler despres d'aplicar moviment:"+_chess.showBoard());
                    result = i_minMax(result,profundity+1,1,movement,biggestAnterior,smallerAnterior);
                    _chess.undoMovement();
                    piece.first=initialPosition;
                    //System.out.println("(cpu.java 84) tauler despres de desfer moviment  peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol()+_chess.showBoard());

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
            if(_color==PieceColor.Black)pieces=_chess.pListWhite();
            else pieces=_chess.pListBlack();
            Iterator<Pair<Position,Piece>> itPieces1 = pieces.iterator();
            System.out.println("------------------");
            while(itPieces1.hasNext()){
                Pair<Position,Piece> piece1 = itPieces1.next();
                System.out.println("color:"+piece1.second.color());
            }
            System.out.println("------------------");
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){  //FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                Position initialPosition = new Position(piece.first.row(),piece.first.col());
                //System.out.println("(cpu.java 107)profunitat:"+profundity+" color peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" TAULER ACTUAL:\n");
                //System.out.println("(cpu.java 108)"+_chess.showBoard());
                List<Pair<Position,Integer>> destinyWithScores = _chess.destinyWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext()){ //FOR EACH MOVEMENT
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result= -pieceMovement.second + score;
                    //System.out.println("(cpu.java 114)Moviment possible peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol() + "  Posició de la peça actual provant:"+piece.first.toString()+" DESTI:"+pieceMovement.first.toString());
                    if(pieceMovement.second>0)_chess.applyMovement(piece.first,pieceMovement.first,pieceMovement.first);//aplicar
                    else _chess.applyMovement(piece.first,pieceMovement.first,null);
                    //System.out.println("(cpu.java 117) tauler despres d'aplicar moviment:"+_chess.showBoard());
                    result = i_minMax(result,profundity+1,0,movement,biggestAnterior,smallerAnterior);
                    _chess.undoMovement();
                    piece.first=initialPosition;
                    //System.out.println("(cpu.java 121) tauler despres de desfer moviment  peça:"+piece.second.color() + "  color simbol:"+piece.second.symbol()+_chess.showBoard());

                    if(result<min){
                        smallerAnterior=result;
                        min=result;
                        movement.first=piece.first;
                        movement.second=pieceMovement.first;
                        //System.out.println("(cpu.java 128)new best movement: i:"+movement.first.toString() + " d:" + movement.second.toString() );
                    }
                    if(biggestAnterior>=smallerAnterior)break;
                }
            }
            return min;
        }
    }
}