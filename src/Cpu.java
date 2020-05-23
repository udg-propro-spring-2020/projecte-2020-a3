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
    private double maxim=0;


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
                System.out.println("(cpu.java:47)Moviment escollit del coneixament: orig:"+movement.first.toString()+" dest:"+movement.second.toString());
                return movement;
            }
            else return minMax();//If we dont find any stored movement we choose from the minmax algorithm
        }
        else return minMax();//If we dont have knowledge we choose from the minmax
    }

    /** @biref Returns the optimal movement to do.
    @pre --
    @return Returns the optimal movement to do inside the decision tree with profundity @c _profunidty.
     */
    private Pair<Position,Position> minMax(){ 
        Pair<Position,Position> movement = new Pair<Position,Position>(null,null);
        Chess taulerCopia = (Chess)_chess.clone();//not necessary (take it out)
        long startTime = System.nanoTime();
        i_minMax(0,0,0,movement,Integer.MIN_VALUE,Integer.MAX_VALUE,taulerCopia);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("(cpu.java:63)Moviment escollit del minMax: orig:"+movement.first.toString() + " dest:" + movement.second.toString() );
        double seconds = (double)duration / 1_000_000_000.0;
        if(seconds>maxim)maxim=seconds;
        System.out.println("Maxim temps:"+maxim);
        return movement;
    }

    /** @brief Immersion function for minMaxAlgorsim
    @pre --
    @return Returns the puntuation choosen for the @p playerType of the actual profunity.
     */
    private int i_minMax(int score,int profundity,int playerType,Pair<Position,Position> movement,int biggestAnterior,int smallerAnterior,Chess tauler){
        if(profundity==_profundity)return score;
        else if(playerType==0){ //Turn of the cpu player (we will maximize score here)
            Integer max = Integer.MIN_VALUE;
            List<Pair<Position,Position>> equealMovementsFirstLevel = new ArrayList<Pair<Position,Position>>();
            List<Pair<Position,Piece>> pieces,piecesContrincant;
            if(_color==PieceColor.White){
                pieces=tauler.pListWhite();//take the cpu pieces
                piecesContrincant=tauler.pListBlack();
            }
            else {
                pieces=tauler.pListBlack();
                piecesContrincant=tauler.pListWhite();
            }
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            Boolean follow = true;
            while(itPieces.hasNext() && follow){//for each peice
                Pair<Position,Piece> piece = itPieces.next();
                List<Pair<Position,Integer>> destinyWithScores= tauler.allPiecesDestiniesWithValues(piece.first);//take all possibles movements for this piece which the socre asssociated at this movement

                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext() && follow){//for each movement
                    
                    Chess taulerCopia = (Chess)tauler.clone();//copy the actual chess because after applying this movememnt and al the minmax alogirthm we need the chess as now
                    
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result=pieceMovement.second + score;//add actul + score for actual movement into result
                    //System.out.println("crido jugador amb moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString());
                    //taulerCopia.pintarLlistes();
                    //if(piece.first.toString().equals("b5") && pieceMovement.first.toString().equals("c7"))System.out.println(taulerCopia.showBoard());
                    Pair<List<MoveAction>,List<Position>> check= taulerCopia.checkMovement(piece.first,pieceMovement.first);//necessary for the chgit ess, it needs to know the pieces which will die and(list of positions), the list of moveAction is for Console/Visual game class 
                    
                    List<MoveAction> actions = taulerCopia.applyMovement(piece.first,pieceMovement.first,check.second,false);//we apply this movement with the returnend parameters on the checkMovement
                    if(!taulerCopia.isCheck(piecesContrincant)){
                        actions.forEach((action)->{
                            //System.out.println("action "+action.toString());
                            //System.out.println(taulerCopia.showBoard());
                            if(action==MoveAction.Promote){
                                //System.out.println("hello");
                                List<PieceType> typePieces = taulerCopia.typeList();
                                Iterator<PieceType> itTypePieces = typePieces.iterator();
                                PieceType piecetype = itTypePieces.next();
                                while(itTypePieces.hasNext()){
                                    PieceType nextPieceType = itTypePieces.next();
                                    if(nextPieceType.ptValue()>piecetype.ptValue())piecetype=nextPieceType;
                                }
                                //System.out.println("--------------------------aplicar promocionar");
                                taulerCopia.promotePiece(pieceMovement.first,piecetype);
                                //System.out.println(taulerCopia.showBoard());       
                            }
                        });
                        //System.out.println("SOC cpu nivell:"+profundity+"max:"+max+"smallestAnterior:"+smallerAnterior+" trio moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score actual: "+result);
                        //if(profundity==0)System.out.println("Max actual:"+max);
                        result = i_minMax(result,profundity+1,1,movement,biggestAnterior,smallerAnterior,taulerCopia); //recursive call minMax with playerType = 1 to make the optimal simulation for the other plyer 

                        if(result>max){
                                biggestAnterior=result;
                                max=result;
                                if(profundity==0){
                                    equealMovementsFirstLevel.clear();
                                    equealMovementsFirstLevel.add(new Pair<Position,Position>( (Position) piece.first.clone(),(Position) pieceMovement.first.clone()));
                                }
                            }
                        else if(result==max && profundity == 0){
                            equealMovementsFirstLevel.add(new Pair<Position,Position>((Position) piece.first.clone(),(Position) pieceMovement.first.clone()));
                        }

                        /*if(result>max){
                            if(profundity==0){
                                //System.out.println("actualitzant el moviment score:"+result);
                                //System.out.println("moviment anterior: o:"+movement.first+" d:"+movement.second);
                                //System.out.println("nou moviment: o:"+piece.first+" d:"+pieceMovement.first);
                                movement.first=piece.first;
                                movement.second=pieceMovement.first;
                            }
                            biggestAnterior=result;
                            max=result;
                        }*/
                        /*If the new biggest is bigger than smallest on
                        the anterior node (because anterior will choose the samllest)
                        we dont have to continue inspecinting this branch so we cut it.
                        */
                        if(smallerAnterior<=biggestAnterior){/*System.out.println("tallo desde CPU");*/follow=false;}
                    }
                    else taulerCopia.undoMovement();
                }
            }
            //System.out.println("CPU nivell:"+profundity+" score returnant:"+max);
            if(profundity==0){
                Random rand = new Random();
                int n = rand.nextInt(equealMovementsFirstLevel.size());
                Pair<Position,Position> choosed = equealMovementsFirstLevel.get(n);
                movement.first = choosed.first;
                movement.second = choosed.second;
                //System.out.println("Total de moviments iguals:"+equealMovementsFirstLevel.size());
            }
            return max;
        }
        else{ /*Here we will choose the lowest because we want to minamize our negative score
                the socre here will be negative because the positive for the other player is negative for the cpu*/
            
            Integer min = Integer.MAX_VALUE;
            List<Pair<Position,Piece>> pieces,piecesContrincant;
            if(_color==PieceColor.Black){
                pieces=tauler.pListWhite();//take the cpu pieces
                piecesContrincant=tauler.pListBlack();
            }
            else {
                pieces=tauler.pListBlack();
                piecesContrincant=tauler.pListWhite();
            }
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            Boolean follow = true;
            while(itPieces.hasNext() && follow){  //FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                List<Pair<Position,Integer>> destinyWithScores=tauler.allPiecesDestiniesWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext() && follow){ //FOR EACH MOVEMENT
                    Chess taulerCopia = (Chess)tauler.clone();
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result= -pieceMovement.second + score;
                    //System.out.println("SOC contrincant nivell:"+profundity+"min:"+min+"biggestAnterior:"+biggestAnterior+" trio moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score actual: "+result);
                    //taulerCopia.pintarLlistes();
                    Pair<List<MoveAction>,List<Position>> check= taulerCopia.checkMovement(piece.first,pieceMovement.first);
                    List<MoveAction> actions=taulerCopia.applyMovement(piece.first,pieceMovement.first,check.second,false);
                    if(!taulerCopia.isCheck(piecesContrincant)){
                        actions.forEach((action)->{
                            //System.out.println("action "+action.toString());
                            //System.out.println(taulerCopia.showBoard());
                            if(action==MoveAction.Promote){
                                //System.out.println("hello");
                                List<PieceType> typePieces = taulerCopia.typeList();
                                Iterator<PieceType> itTypePieces = typePieces.iterator();
                                PieceType piecetype = itTypePieces.next();
                                while(itTypePieces.hasNext()){
                                    PieceType nextPieceType = itTypePieces.next();
                                    if(nextPieceType.ptValue()>piecetype.ptValue())piecetype=nextPieceType;
                                }
                                //System.out.println("--------------------------aplicar promocionar");
                                taulerCopia.promotePiece(pieceMovement.first,piecetype);
                                //System.out.println(taulerCopia.showBoard());                 
                                }
                        });
                        //System.out.println("SOC el contrincant trio moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score actual: "+result);
                        result = i_minMax(result,profundity+1,0,movement,biggestAnterior,smallerAnterior,taulerCopia);
                        if(result<min){
                            smallerAnterior=result;
                            min=result;
                        }
                        /*If the new smallest is smaller than biggest on
                        the anterior node (because anterior will choose the bigger)
                        we dont have to continue inspecinting this branch so we cut it.
                        */
                        if(biggestAnterior>=smallerAnterior){/*System.out.println("tallo desde CONTRINCANT");*/follow=false;}
                    }
                    else taulerCopia.undoMovement();
                }
                    
            }
            //System.out.println("CONTRINCANT nivell:"+profundity+" score returnant:"+min);
            return min;
        }
    }
}