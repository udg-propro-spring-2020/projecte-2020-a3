import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

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

    private Node _actual;                   ///< Node actual de la sequencia de tirades guanyadores que s'està seguin
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
    }

    /** @brief Crea el coneixement
    @pre --
    @post Es crea el coneixement.
     */
    public Knowledge(ArrayList<Pair<Position,Position>> tirades,int guanyador,Chess chess){
        /*_knowledge = new HashMap<Chess,Node>();
        _actual = null;


        Iterator<Pair<Position,Position>> it = tirades.Iterator();
        int i=0;
        Pair<Position,Position> tiradaGuanyadora;
        Pair<Position,Position> tiradaPerdedora;

        if(guanyador==1){ //si gunyen negres ens saltem el primer moviment
            tiradaPerdedora=reversePosition(it.next(),chess);
            chess.applyMovement(tiradaPerdedora.first,tiradaPerdedora.second,null);
        }

        ant = null;

        while(it.hasNext()){
            if(guanyador==0){//guanyen blanques
                tiradaGuanyadora=it.next();
                chess.applyMovement(tiradaGuanyadora.first,tiradaPerdedora.second,null);
                tiradaPerdedora=it.next();
                chess.applyMovement(tiradaPerdedora.first,tiradaPerdedora.second,null);
                Node n = new Node(tiradaGuanyadora,tiradaPerdedora); 
            }
            else{ //guanyen negres
                tiradaGuanyadora=reversePosition(it.next(),chess);
                chess.applyMovement(tiradaGuanyadora.first,tiradaPerdedora.second,null);
                tiradaPerdedora=reversePosition(it.next(),chess);
                chess.applyMovement(tiradaPerdedora.first,tiradaPerdedora.second,null);
                Node n = new Node(tiradaGuanyadora,tiradaPerdedora);
            }
            if(_knowledge.containsKey(chess)){
                    _knowledge[chess].add(n);
            }
            else{
                _knowledge[chess] = new ArrayList(){n};
            }
            if(ant!=null){
                ant.afegirNode(n);
            }
            ant = n;
        }*/
        
    }

    /** @brieF Busca una tirada de nodes que concordi amb la situació actual de Chess
    @pre --
    @return Retorna la jugada guanyadora del node que cocideix amb la situacio actual de \p Chess
    si la troba i actualitza _actual amb el valor del primer node. Altrament retorna null.
    */
    public Pair<Position,Position> buscarConeixament(Chess chess,int color){
        /*if(_knowledge.containsKey(chess)){
            Node _actual=_knowledge[chess].get(0);
            if(color==0)return n._tiradaGuanyadora;
            else return reversePosition(n._tiradaGuanyadora,chess);
        }
        else */return null;
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
       /* Pair<Position,Position> t;
            t.first=Position(chess.rows()-1-t.first.row(),chess.columns()-1-t.first.columns());
            t.second=Position(chess.rows()-1-t.second.row(),chess.columns()-1-t.second.columns());
        return t;*/
        return null;
    }
    
}