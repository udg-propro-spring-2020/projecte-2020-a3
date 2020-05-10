import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/** @file Knowledge.java
    @brief Coneixement de partides guanyadores.
 */

/**
    @class Knowledge
    @brief Implementació d'una estructura per guardar per diferents (regles de moment no) 
    de joc les situacions dels taulers conjuntament amb les seves diferents
    tirades guanyadores.
 */

public class Knowledge{

    /*private Node _actual;                   ///< Node actual de la sequencia de tirades guanyadores que s'està seguin
    private HashMap<Chess,ArrayList<Node>> _knowledge;    ///< Map per guardar per cada situacio del tauler la seva sequencia de tirades guanyadores
    //private Set<Node> nodes;                ///< Set per guardar tots els nodes que tenim
    
    private class Node{
        private Pair<Position,Position> _tiradaGuanyadora;    ///< Tirada guanyadora
        private Pair<Position,Position> _tiradaPerdedora;     //<  Tirada perdadora
        private ArrayList<Node> _nodesSeguents;             ///< Node seguent

        Node(Pair<Position,Position> tiradaGuanyadora,Pair<Position,Position> TiradaPerdedora){
            _tiradaGuanyadora = tiradaGuanyadora;
            _tiradaPerdedora = TiradaPerdedora;
        }
        void afegirNode(Node seg){
            _nodesSeguents.add(seg);
        }
    }*/

    private HashMap<String,Pair<Position,Position>> _knowledgeWhite;
    private HashMap<String,Pair<Position,Position>> _knowledgeBlack;

    /** @brief Crea el coneixement
    @pre --
    @post Es crea el coneixement.
     */
    public Knowledge(List<Pair<List<Turn>, PieceColor>> games,Chess chess){
        
        _knowledgeWhite = new HashMap<String,Pair<Position,Position>>();
        _knowledgeBlack = new HashMap<String,Pair<Position,Position>>();
        
            games.forEach((game)->{
            PieceColor winner = game.second;
            Chess chessCopy = (Chess)chess.clone();
            
            Iterator<Turn> itTurns = game.first.iterator();
            Pair<Position,Position> actualTurn = null;

            if(winner==PieceColor.Black){
                //System.out.println("guanyen negres");
                actualTurn = itTurns.next().moveAsPair();
                //actualTurn = reversePosition(actualTurn,chessCopy);
                chessCopy.applyMovement(actualTurn.first,actualTurn.second,null);
            }
            while(itTurns.hasNext()){
                actualTurn = itTurns.next().moveAsPair();
                if(winner==PieceColor.White){
                    if(!_knowledgeWhite.containsKey(chessCopy.chessStringView(winner))){
                        _knowledgeWhite.put(chessCopy.chessStringView(winner),actualTurn);
                    }
                    chessCopy.applyMovement(actualTurn.first,actualTurn.second,null);
                    if(itTurns.hasNext()){
                        actualTurn = itTurns.next().moveAsPair();
                        chessCopy.applyMovement(actualTurn.first,actualTurn.second,null);
                    }
                }
                else{
                    //System.out.println("situacio:"+chessCopy.chessStringView(winner));
                    //System.out.println("Posicio guardada: o:"+actualTurn.first.toString()+" d:"+actualTurn.second.toString());
                    //actualTurn = reversePosition(actualTurn,chessCopy);
                    //System.out.println("Posicio guardada: o:"+actualTurn.first.toString()+" d:"+actualTurn.second.toString());
                    if(!_knowledgeBlack.containsKey(chessCopy.chessStringView(winner))){
                        _knowledgeBlack.put(chessCopy.chessStringView(winner),actualTurn);
                    }
                    chessCopy.applyMovement(actualTurn.first,actualTurn.second,null);
                    if(itTurns.hasNext()){
                        actualTurn = itTurns.next().moveAsPair();
                        //actualTurn = reversePosition(actualTurn,chessCopy);
                        chessCopy.applyMovement(actualTurn.first,actualTurn.second,null);
                    }
                }
            }
        }); 
    }

    /** @brieF Busca una tirada de nodes que concordi amb la situació actual de Chess
    @pre --
    @return Retorna la jugada guanyadora del node que cocideix amb la situacio actual de \p Chess
    si la troba i actualitza _actual amb el valor del primer node. Altrament retorna null.
    */
    public Pair<Position,Position> buscarConeixament(Chess chess,PieceColor color){
        System.out.println("buscant:"+chess.chessStringView(color));
        /*if(_knowledge.containsKey(chess.chessStringView(color))){
            if(color==PieceColor.White)return _knowledge.get(chess.chessStringView(color));
            else return reversePosition(_knowledge.get(chess.chessStringView(color)),chess);
        }*/
        if(color==PieceColor.White){
            if(_knowledgeWhite.containsKey(chess.chessStringView(color)))return _knowledgeWhite.get(chess.chessStringView(color));
            else return null;
        }
        else{
            if(_knowledgeBlack.containsKey(chess.chessStringView(color)))return _knowledgeBlack.get(chess.chessStringView(color));
            else return null;
        }
    }

    /** @brief Comprova la tirada anteriror i retorna la jugada seguent a fer
    @pre --
    @return Retorna null si _actual = null o si la jugadaAnterior no concideix amb la
    jugadaPerdadora del _actual.Altrament carrega el node seguent de _actual a _actual i
    retorna la seva jugadaGuanyadora.
     */
    public Pair<Position,Position> tiradaSeguent(Pair<Position,Position> tiradaAnterior,int color,Chess chess){
        /*if(_actual==null)return null;
        else{
            if(color==0 && _actual._tiradaPerdedora.first==tiradaAnterior.first && _actual._tiradaPerdedora.second==tiradaAnterior.second){
                _actual=_actual._nodesSeguents.get(0);
                return _actual._tiradaGuanyadora;
            }
            else if(color==1 && reversePosition(_actual._tiradaPerdedora,chess).first==tiradaAnterior.first && reversePosition(_actual._tiradaPerdedora,chess).second==tiradaAnterior.second){
                _actual=_actual._nodesSeguents.get(0);
                return _actual._tiradaGuanyadora;
            }
            else return null;
        }*/
        return null;
    }

    /** @brief Cambia les positionnes de la tirada com si fosin de l'altre color
    @pre --
    @return Retorna el pair /p tirades modificat com si ho hagués fet l'altre color
     */
    private Pair<Position,Position> reversePosition(Pair<Position,Position> tirada,Chess chess){
        Pair<Position,Position> t =new Pair<Position,Position>(
            new Position(chess.rows()-1-tirada.first.row(),chess.cols()-1-tirada.first.col()),
            new Position(chess.rows()-1-tirada.second.row(),chess.cols()-1-tirada.second.col())
            );
        return t;
    }
    
}