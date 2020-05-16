import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

/** 
    @author Joan plaja
    @file Knowledge.java
    @class Knowledge
    @brief Knowledge of winner sequences for white and black players
 */
public class Knowledge{

    private HashMap<String,Pair<Position,Position>> _knowledgeWhite;    //< Map storing chess table situation with the movement to do in that situation for white pieces.//< Map storing chess table situation with the movement to do in that situation for white pieces.
    private HashMap<String,Pair<Position,Position>> _knowledgeBlack;    //< Map storing chess table situation with the movement to do in that situation for white pieces.

    /** @brief Default knowledge cosnstructor
    @pre Chess is not null
    @param games List of games with the list of turns and the winner color.
    @param chess Reference to the actual Chess
     */
    public Knowledge(List<Pair<List<Turn>, PieceColor>> games,Chess chess){
        
        if(chess == null){
            throw new NullPointerException("chess given argument cannot be null");
        }
        else{
            _knowledgeWhite = new HashMap<String,Pair<Position,Position>>();
            _knowledgeBlack = new HashMap<String,Pair<Position,Position>>();
        
            games.forEach((game)->{
                PieceColor winner = game.second;
                Chess chessCopy = (Chess)chess.clone();
                
                Iterator<Turn> itTurns = game.first.iterator();
                Pair<Position,Position> actualTurn = null;

                if(winner==PieceColor.Black){
                    actualTurn = itTurns.next().moveAsPair();
                    chessCopy.applyMovement(actualTurn.first,actualTurn.second,null,false);
                }
                while(itTurns.hasNext()){
                    actualTurn = itTurns.next().moveAsPair();
                    if(winner==PieceColor.White){
                        if(!_knowledgeWhite.containsKey(chessCopy.chessStringView(winner))){
                            _knowledgeWhite.put(chessCopy.chessStringView(winner),actualTurn);
                        }
                        chessCopy.applyMovement(actualTurn.first,actualTurn.second,null,false);
                        if(itTurns.hasNext()){
                            actualTurn = itTurns.next().moveAsPair();
                            chessCopy.applyMovement(actualTurn.first,actualTurn.second,null,false);
                        }
                    }
                    else{
                        if(!_knowledgeBlack.containsKey(chessCopy.chessStringView(winner))){
                            _knowledgeBlack.put(chessCopy.chessStringView(winner),actualTurn);
                        }
                        chessCopy.applyMovement(actualTurn.first,actualTurn.second,null,false);
                        if(itTurns.hasNext()){
                            actualTurn = itTurns.next().moveAsPair();
                            chessCopy.applyMovement(actualTurn.first,actualTurn.second,null,false);
                        }
                    }
                }
            }); 
        }
        
    }

    /** @brief Returns a movement from the knowledge
    @pre chess and color cannot be null
    @post Returns a movement
    */
    public Pair<Position,Position> buscarConeixament(Chess chess,PieceColor color){
        if(chess==null)throw new NullPointerException("chess given argument cannot be null");
        else if(color == null)throw new NullPointerException("color given argument cannot be null");
        else{
            if(color==PieceColor.White){
                if(_knowledgeWhite.containsKey(chess.chessStringView(color)))return _knowledgeWhite.get(chess.chessStringView(color));
                else return null;
            }
            else{
                if(_knowledgeBlack.containsKey(chess.chessStringView(color)))return _knowledgeBlack.get(chess.chessStringView(color));
                else return null;
            }
        }
    }
}