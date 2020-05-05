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

    private HashMap<String,Pair<Position,Position>> _knowledge;

    /** @brief Crea el coneixement
    @pre --
    @post Es crea el coneixement.
     */
    public Knowledge(List<Pair<List<Turn>, PieceColor>> games,Chess chess){
        
        _knowledge = new HashMap<String,Pair<Position,Position>>();
        games.forEach((game)->{
            int movementCounter = 0;
            PieceColor winner = game.second;

            Iterator<Turn> itTurns = game.first.iterator();
            Pair<Position,Position> actualTurn = null;

            if(winner==PieceColor.Black){
                actualTurn = itTurns.next().moveAsPair();
                actualTurn = reversePosition(actualTurn,chess);
                chess.applyMovement(actualTurn.first,actualTurn.second,null);
                movementCounter++;
            }
            while(itTurns.hasNext()){
                actualTurn = itTurns.next().moveAsPair();
                if(winner==PieceColor.White){
                    if(!_knowledge.containsKey(chess)){
                        _knowledge.put(chess.chessStringView(winner),actualTurn);
                    }
                    chess.applyMovement(actualTurn.first,actualTurn.second,null);
                    movementCounter++;
                    if(itTurns.hasNext()){
                        actualTurn = itTurns.next().moveAsPair();
                        chess.applyMovement(actualTurn.first,actualTurn.second,null);
                        movementCounter++;
                    }
                }
                else{
                    actualTurn = reversePosition(actualTurn,chess);
                    if(!_knowledge.containsKey(chess)){
                        _knowledge.put(chess.chessStringView(winner),actualTurn);
                    }
                    chess.applyMovement(actualTurn.first,actualTurn.second,null);
                    movementCounter++;
                    if(itTurns.hasNext()){
                        actualTurn = itTurns.next().moveAsPair();
                        actualTurn = reversePosition(actualTurn,chess);
                        chess.applyMovement(actualTurn.first,actualTurn.second,null);
                        movementCounter++;
                    }
                }
            }
            while(movementCounter>0){chess.undoMovement();movementCounter--;}
        }); 
    }

    /** @brieF Busca una tirada de nodes que concordi amb la situació actual de Chess
    @pre --
    @return Retorna la jugada guanyadora del node que cocideix amb la situacio actual de \p Chess
    si la troba i actualitza _actual amb el valor del primer node. Altrament retorna null.
    */
    public Pair<Position,Position> buscarConeixament(Chess chess,PieceColor color){
        if(_knowledge.containsKey(chess.chessStringView(color))){
            if(color==PieceColor.White)return _knowledge.get(chess.chessStringView(color));
            else return reversePosition(_knowledge.get(chess.chessStringView(color)),chess);
        }
        else return null;
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