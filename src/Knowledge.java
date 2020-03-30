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
    private Map<Escacs,Node> _knowledge;  ///< Map per guardar per cada situacio del tauler la seva sequencia de tirades guanyadores

    private class Node{
        private Pair<Posicio,Posicio> _tiradaGuanyadora;    ///< Tirada guanyadora
        private Pair<Posicio,Posicio> _tiradaPerdedora;     //<  Tirada perdadora
        private Node _seg;                                  ///< Node seguent

        Node(Pair<Posicio,Posicio> tiradaGuanyadora,Pair<Posicio,Posicio> TiradaPerdedora,Node seg){
            _tiradaGuanyadora = tiradaGuanyadora;
            _tiradaPerdedora = TiradaPerdedora;
            _seg = seg;
        }
    }

    /** @brief Crea el coneixement
    @pre --
    @post Es crea el coneixement.
     */
    public Knowledge(/* JSON PATH */){
        _knowledge = new HashMap<Escacs,Node>();
        _actual = null;
        /** Es tracta el coneixament i es guarda */
    }

    /** @brieF Busca una tirada de nodes que concordi amb la situació actual de Escacs
    @pre --
    @return Retorna la jugada guanyadora del node que cocideix amb la situacio actual de \p escacs
    si la troba i actualitza _actual amb el valor del primer node. Altrament retorna null.
    */
    public Pair<Posicio,Posicio> buscarConeixament(Escacs escacs){

    }

    /** @brief Comprova la tirada anteriror i retorna la jugada seguent a fer
    @pre --
    @return Retorna null si _actual = null o si la jugadaAnterior no concideix amb la
    jugadaPerdadora del _actual.Altrament carrega el node seguent de _actual a _actual i
    retorna la seva jugadaGuanyadora.
     */
    public Pair<Posicio,Posicio> tiradaSeguent(Pair<Posicio,Posicio> tiradaAnterior){

    }
    
}