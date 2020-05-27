import java.util.List;
import java.util.Iterator;
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.Random;

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
    private int _profundity;        ///< Profunidty level for search the possibilities movements tree.
    private PieceColor _color;      ///< Color of the CPU player

    /** @brief Default CPU constructor
    @pre chess is not null, profundity > 0 and color is not null
    @param knowledge Reference to the knowledge
    @param chess Reference to the Chess
     */
    public Cpu(Knowledge knowledge,Chess chess,int profundity,PieceColor color){
        if(chess==null)throw new NullPointerException("chess given argument cannot be null");
        else if(color==null)throw new NullPointerException("color given argument cannot be null");
        else if(profundity<=0)throw new IllegalArgumentException("profunidty argument cannot be less or equal to zero");
        else{
            _knowledge=knowledge;
            _chess=chess;
            _profundity=profundity;
            _color=color;
        }
    }

    /** @brief Makes a movement
    @pre --
    @return Returns a pair which indicates the origin position and final position of the pice movmement choose.
     */
    public Pair<Position,Position> doMovement(){
        if(_knowledge!=null){ //If we have knowledge
            Pair<Position,Position> movement = _knowledge.buscarConeixament(_chess,_color);
            if(movement!=null){ //If we find knowledge saved we choose the movement to do from the knowledge
                return movement;
            }
            else return minMax();//If we dont find any stored movement we choose from the minmax algorithm
        }
        else return minMax();//If we dont have knowledge we choose from the minmax
    }

    /** @biref Returns the optimal movement to do.
    @pre    Cpu can do a movement
    @return Returns the optimal movement to do inside the decision tree with profundity @c _profunidty.
     */
    private Pair<Position,Position> minMax(){ 
        Pair<Position,Position> movement = new Pair<Position,Position>(null,null);
        i_minMax(0,0,0,movement,Integer.MIN_VALUE,Integer.MAX_VALUE,_chess);
        return movement;
    }

    /** @brief Immersion function for minMaxAlgorsim
    @pre --
    @return Returns the puntuation choosen for the @p playerType of this @p profundity.
     */
    private int i_minMax(Integer score,int profundity,int playerType,Pair<Position,Position> movement,int biggestAnterior,int smallerAnterior,Chess tauler){
        if(profundity==_profundity)return score;
        else if(playerType==0){ //Turn of the cpu player (we will maximize score here)
            
            Integer max = Integer.MIN_VALUE;
            List<Pair<Position,Position>> equealMovementsFirstLevel = new ArrayList<Pair<Position,Position>>();//list to save movements with same puntuation at profunity 1
            List<Pair<Position,Piece>> pieces,piecesContrincant;
            if(_color==PieceColor.White)pieces=tauler.pListWhite();
            else pieces=tauler.pListBlack();
            Boolean follow = true;
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();

            while(itPieces.hasNext() && follow){//for each peice

                Pair<Position,Piece> piece = itPieces.next();
                List<Pair<Position,Integer>> destinyWithScores= tauler.allPiecesDestiniesWithValues(piece.first);//take all possibles movements for this piece which the socre asssociated at this movement
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();

                while(itMoviments.hasNext() && follow){//for each movement

                    Chess taulerCopia = (Chess)tauler.clone();//We copy the chess and we will work with that copy for not affecting next movements
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result=pieceMovement.second + score;//add actul + score for actual movement into result
                    Pair<List<MoveAction>,List<Position>> check= taulerCopia.checkMovement(piece.first,pieceMovement.first);//necessary for the chess class, it needs to know the pieces which will die and(list of positions), the list of moveAction is for Console/Visual game class 
                    List<MoveAction> actions = taulerCopia.applyMovement(piece.first,pieceMovement.first,check.second,false);//we apply this movement with the returnend parameters on the checkMovement
                    
                    if(_color==PieceColor.White)piecesContrincant=taulerCopia.pListBlack();//take contrincant pieces
                    else piecesContrincant=taulerCopia.pListWhite();
                    
                    if(!taulerCopia.isCheck(piecesContrincant)){//we look if this movement will cause check if it does we omit it
                        actions.forEach((action)->{
                            if(action==MoveAction.Promote){//if this movement cause a promotion we choose the biggest puntuation piece to promote
                                List<PieceType> typePieces = taulerCopia.typeList();
                                Iterator<PieceType> itTypePieces = typePieces.iterator();
                                PieceType piecetype = itTypePieces.next();
                                while(itTypePieces.hasNext()){
                                    PieceType nextPieceType = itTypePieces.next();
                                    if(nextPieceType.ptValue()>piecetype.ptValue())piecetype=nextPieceType;
                                }
                                taulerCopia.promotePiece(pieceMovement.first,piecetype);    
                            }
                        });

                        result = i_minMax(result,profundity+1,1,movement,biggestAnterior,smallerAnterior,taulerCopia); //recursive call minMax with playerType = 1 to make the optimal simulation for the other plyer 
                        
                        if(result>=max){
                                if(profundity==0 && result > max){
                                    equealMovementsFirstLevel.clear();
                                    equealMovementsFirstLevel.add(new Pair<Position,Position>( (Position) piece.first.clone(),(Position) pieceMovement.first.clone()));
                                }
                                else if(profundity==0 && result==max)equealMovementsFirstLevel.add(new Pair<Position,Position>((Position) piece.first.clone(),(Position) pieceMovement.first.clone()));
                                if(result>biggestAnterior)biggestAnterior=result;
                                max=result;
                        }
                        /*If the new biggest is bigger than smallest on
                        the anterior node (because anterior will choose the samllest)
                        we dont have to continue inspecinting this branch so we cut it.
                        */
                        if(smallerAnterior<biggestAnterior){follow=false;}
                    }
                }
            }
            if(profundity==0){/*At first level for CPU player we need to choose the movement if there's more than one with same puntuation we will choose one random. With that we cant be sure that cpu vs cpu
            will not always choose same movements */
                Random rand = new Random();
                int n = rand.nextInt(equealMovementsFirstLevel.size());
                Pair<Position,Position> choosed = equealMovementsFirstLevel.get(n);
                movement.first = choosed.first;
                movement.second = choosed.second;
            }
            if(max==Integer.MIN_VALUE)return -100+score;//if the max == MIN_VALUE it means that all movements possibles of CPU puts himself in check(loose game) so we return a value of -100(king value) + preuvious score
            else return max;//else we return the max value possible
        }
        else{ /*Here we will choose the lowest because we want to minamize our negative score
                the socre here will be negative because the positive for the other player is negative for the cpu*/
            //codi del contrincant

            Integer min = Integer.MAX_VALUE;
            List<Pair<Position,Piece>> pieces,piecesContrincant;
            if(_color==PieceColor.Black)pieces=tauler.pListWhite();//peçes del contrincant
            else pieces=tauler.pListBlack();
            Boolean follow = true;
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();

            while(itPieces.hasNext() && follow){//FOR EACH PIECE

                Pair<Position,Piece> piece = itPieces.next();
                List<Pair<Position,Integer>> destinyWithScores=tauler.allPiecesDestiniesWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();

                while(itMoviments.hasNext() && follow){//FOR EACH MOVEMENT

                    Chess taulerCopia = (Chess)tauler.clone();//We copy the chess and we will work with that copy for not affecting next movements
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result= -pieceMovement.second + score;
                    Pair<List<MoveAction>,List<Position>> check= taulerCopia.checkMovement(piece.first,pieceMovement.first);
                    List<MoveAction> actions=taulerCopia.applyMovement(piece.first,pieceMovement.first,check.second,false);
                    
                    if(_color==PieceColor.Black)piecesContrincant=taulerCopia.pListBlack();//take contrincant pieces
                    else piecesContrincant=taulerCopia.pListWhite();
                    
                    if(!taulerCopia.isCheck(piecesContrincant)){//we look if this movement will cause check if it does we omit it
                        actions.forEach((action)->{
                            if(action==MoveAction.Promote){//if this movement cause a promotion we choose the biggest puntuation piece to promote
                                List<PieceType> typePieces = taulerCopia.typeList();
                                Iterator<PieceType> itTypePieces = typePieces.iterator();
                                PieceType piecetype = itTypePieces.next();
                                while(itTypePieces.hasNext()){
                                    PieceType nextPieceType = itTypePieces.next();
                                    if(nextPieceType.ptValue()>piecetype.ptValue())piecetype=nextPieceType;
                                }
                                taulerCopia.promotePiece(pieceMovement.first,piecetype);               
                                }
                        });
                        result = i_minMax(result,profundity+1,0,movement,biggestAnterior,smallerAnterior,taulerCopia);//recrusive call
                        if(result<=min){//if the result is less than the actual min we update it
                            if(result<smallerAnterior)smallerAnterior=result;//if its smaller than the anteirorSmaller we also updatate this one
                            min=result;
                        }
                        /*If the new smallest is smaller than biggest on
                        the anterior node (because anterior will choose the bigger)
                        we dont have to continue inspecinting this branch so we cut it.
                        */
                        if(biggestAnterior>smallerAnterior){follow=false;}
                    }
                }
                    
            }
            if(min==Integer.MAX_VALUE)return 100+score;//if contricant cant choose any movement then it means that all of his movements put himself into check so he cant do anythink (he loose)
            else return min;//else we return the minimum value possible
        }
    }
}