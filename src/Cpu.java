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
    private double minim=Double.MAX_VALUE;
    private int total=0;
    private double suma=0;


    /** @brief Default CPU constructor
    @pre chess is not null, profundity > 0 and color is not null
    @param knowledge Reference to the knowledge
    @param chess Reference to the Chess
     */
    public Cpu(Knowledge knowledge,Chess chess,int profundity,PieceColor color){
        System.out.println("Soc nivell:"+profundity + " color:"+color.toString());
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
        int punt =i_minMax(0,0,0,movement,Integer.MIN_VALUE,Integer.MAX_VALUE,taulerCopia);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("(cpu.java:63)Moviment escollit del minMax: orig:"+movement.first.toString() + " dest:" + movement.second.toString() +" puntuacio:"+punt);
        double seconds = (double)duration / 1_000_000_000.0;
        if(seconds>maxim)maxim=seconds;
        if(seconds<minim)minim=seconds;
        total++;
        suma+=seconds;
        System.out.println("Maxim temps:"+maxim);
        System.out.println("Minim temps:"+minim);
        System.out.println("Mitjana temps:"+suma/total);
        System.out.println("Total de moviments fets:"+total);
        return movement;
    }

    /** @brief Immersion function for minMaxAlgorsim
    @pre --
    @return Returns the puntuation choosen for the @p playerType of the actual profunity.
     */
    private int i_minMax(Integer score,int profundity,int playerType,Pair<Position,Position> movement,int biggestAnterior,int smallerAnterior,Chess tauler){
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
            Boolean follow = true;
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext() && follow){//for each peice
                Pair<Position,Piece> piece = itPieces.next();
                List<Pair<Position,Integer>> destinyWithScores= tauler.allPiecesDestiniesWithValues(piece.first);//take all possibles movements for this piece which the socre asssociated at this movement

                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext() && follow){//for each movement
                    
                    Chess taulerCopia = (Chess)tauler.clone();//copy the actual chess because after applying this movememnt and al the minmax alogirthm we need the chess as now
                    
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    Integer result=pieceMovement.second + score;//add actul + score for actual movement into result
                    //System.out.println(taulerCopia.showBoard());
                    //System.out.println("socre of thjis:"+pieceMovement.second+ " Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString());
                    //System.out.println(taulerCopia.showBoard());
                    //if(profundity==0)
                    //System.out.println("crido jugador amb nivell:"+profundity+" moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score:"+result);
                    /*if(pieceMovement.second>=100){
                        
                        System.out.println("crido jugador amb nivell:"+profundity+" moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score:"+result);
                        System.out.println(taulerCopia.showBoard());
                        System.out.println("PUNTUACIO JUGADA:"+pieceMovement.second);
                    }*/
                    Pair<List<MoveAction>,List<Position>> check= taulerCopia.checkMovement(piece.first,pieceMovement.first);//necessary for the chgit ess, it needs to know the pieces which will die and(list of positions), the list of moveAction is for Console/Visual game class 
                    
                    List<MoveAction> actions = taulerCopia.applyMovement(piece.first,pieceMovement.first,check.second,false);//we apply this movement with the returnend parameters on the checkMovement
                    if(_color==PieceColor.White){
                        piecesContrincant=tauler.pListBlack();
                    }
                    else {
                        piecesContrincant=tauler.pListWhite();
                    }
                    if(!taulerCopia.isCheck(piecesContrincant)){
                        //if(profundity==0)System.out.println("no és escac");
                        actions.forEach((action)->{
                            if(action==MoveAction.Promote){
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
                        //System.out.println("result actual:"+result+" max actual:"+max+ "  profunity:"+profundity);
                        if(result>=max){
                                //if(profundity==0)System.out.println(result==Integer.MIN_VALUE);
                                if(profundity==0 && result > max){
                                    //System.out.println("nou valor");
                                    equealMovementsFirstLevel.clear();
                                    equealMovementsFirstLevel.add(new Pair<Position,Position>( (Position) piece.first.clone(),(Position) pieceMovement.first.clone()));
                                }
                                else if(profundity==0 && result==max){
                                        //System.out.println("valor igual");
                                        equealMovementsFirstLevel.add(new Pair<Position,Position>((Position) piece.first.clone(),(Position) pieceMovement.first.clone()));
                                }
                                if(result>biggestAnterior)biggestAnterior=result;
                                max=result;
                        }
                        /*If the new biggest is bigger than smallest on
                        the anterior node (because anterior will choose the samllest)
                        we dont have to continue inspecinting this branch so we cut it.
                        */
                        if(smallerAnterior<biggestAnterior){/*System.out.println("tallo desde CPU result:"+result+" nivell:"+profundity);*/follow=false;}
                    }
                    else {taulerCopia.undoMovement();}
                }
            }
            if(profundity==0){
                if(equealMovementsFirstLevel.isEmpty()){
                    itPieces = pieces.iterator();
                    while(itPieces.hasNext()){
                        Pair<Position,Piece> piece = itPieces.next();
                        List<Pair<Position,Integer>> destinyWithScores= tauler.allPiecesDestiniesWithValues(piece.first);
                        Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                        while(itMoviments.hasNext()){
                            Pair<Position,Integer> pieceMovement = itMoviments.next();
                            System.out.println("moviment possible Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString());
                        }
                    }
                    
                }
                else{
                    Random rand = new Random();
                    int n = rand.nextInt(equealMovementsFirstLevel.size());
                    Pair<Position,Position> choosed = equealMovementsFirstLevel.get(n);
                    movement.first = choosed.first;
                    movement.second = choosed.second;
                }
            }
            return max;
        }
        else{ /*Here we will choose the lowest because we want to minamize our negative score
                the socre here will be negative because the positive for the other player is negative for the cpu*/
            //codi del contrincant
            Integer min = Integer.MAX_VALUE;
            List<Pair<Position,Piece>> pieces,piecesContrincant;
            if(_color==PieceColor.Black){
                pieces=tauler.pListWhite();//peçes del contrincant
                piecesContrincant=tauler.pListBlack();//peçes del contricant del contricanmt (cpu)
            }
            else {
                pieces=tauler.pListBlack();
                piecesContrincant=tauler.pListWhite();
            }
            Boolean follow = true;
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext() && follow){  //FOR EACH PIECE
                Pair<Position,Piece> piece = itPieces.next();
                List<Pair<Position,Integer>> destinyWithScores=tauler.allPiecesDestiniesWithValues(piece.first);
                Iterator<Pair<Position,Integer>> itMoviments = destinyWithScores.iterator();
                while(itMoviments.hasNext() && follow){ //FOR EACH MOVEMENT
                    Chess taulerCopia = (Chess)tauler.clone();
                    Pair<Position,Integer> pieceMovement = itMoviments.next();
                    //System.out.println("socre of thjis:"+pieceMovement.second+ " Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString());
                    Integer result= -pieceMovement.second + score;
                    //System.out.println(taulerCopia.showBoard());
                    //if(profundity==0)
                    //System.out.println("SOC contrincant nivell:"+profundity+" trio moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score actual: "+result);
                    /*if(pieceMovement.second>=100){
                        
                        System.out.println("crido jugador amb nivell:"+profundity+" moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score:"+result);
                        System.out.println(taulerCopia.showBoard());
                        System.out.println("PUNTUACIO JUGADA:"+pieceMovement.second);
                    }*/
                    Pair<List<MoveAction>,List<Position>> check= taulerCopia.checkMovement(piece.first,pieceMovement.first);
                    List<MoveAction> actions=taulerCopia.applyMovement(piece.first,pieceMovement.first,check.second,false);
                    if(_color==PieceColor.Black){
                        //pieces=tauler.pListWhite();//peçes del contrincant
                        piecesContrincant=taulerCopia.pListBlack();//peçes del contricant del contricanmt (cpu)
                    }
                    else {
                        //pieces=tauler.pListBlack();
                        piecesContrincant=taulerCopia.pListWhite();
                    }
                    
                    if(!taulerCopia.isCheck(piecesContrincant)){
                        //if(profundity==0)System.out.println("no és escac");
                        actions.forEach((action)->{
                            if(action==MoveAction.Promote){
                                //System.out.println("hello");
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
                        //System.out.println("SOC el contrincant trio moviment Origen:"+piece.first.toString()+" desti:"+pieceMovement.first.toString()+" score actual: "+result);
                        result = i_minMax(result,profundity+1,0,movement,biggestAnterior,smallerAnterior,taulerCopia);
                        //System.out.println("min:"+min+" score"+result);
                        if(result<=min){
                            if(result<smallerAnterior)smallerAnterior=result;
                            min=result;
                        }
                        /*If the new smallest is smaller than biggest on
                        the anterior node (because anterior will choose the bigger)
                        we dont have to continue inspecinting this branch so we cut it.
                        */
                        if(biggestAnterior>smallerAnterior){/*System.out.println("tallo desde CONTRINCANT result:"+result+" nivell:"+profundity);*/follow=false;}
                    }
                    else {
                        //System.out.println(x);
                        /* System.out.println(taulerCopia.showBoard());
                        System.out.println("hi ha escac");*/
                        taulerCopia.undoMovement();
                        
                    }
                }
                    
            }
            //System.out.println("escolleixo:"+min);
            if(min==Integer.MIN_VALUE)return -100+score;
            else return min;
        }
    }
}