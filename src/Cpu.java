/** @file Cpu.java
    @brief Un jugador automàtic.
 */

/**
    @class Cpu
    @brief Jugador automàtic amb coneixement sobre partides guanyadores.
 */

public class Cpu{

    private Coneixament _coneixament;   ///< Coneixament de sequencies de partides guanyadores
    private Escacs _escacs;             ///< Referencia al objecte Chess del joc
    private int _profunditat;           ///< Nivell de profunditat de cerca en l'arbre de possibiles moviments

    /** @brief Crea la cpu
    @pre --
    @post La cpu té el coneixament \p coneixament,una referencia al joc d'escacs \p escacs
    i una profunidtat de busqueda de moviments \p profunditat.
     */
    public Cpu(Coneixament coneixament,Escacs escacs,int profunditat){
        _coneixament=coneixament;
        _escacs=escacs;
        _profunditat=profunditat;
    }

    /** @brief Fa una tirada
    @pre --
    @return Retorna parella de la tirada indiciant la posicio de origen i la posicio desti d'una fitxa. Si el coneixament ja segueix una sequencia
    i la \p tiradaAnterior concideix amb la tirada esperada la retorna la tirada. Altrament Si estat de _escacs concideix amb un guardat a _coneixament
    retorna retorna la tirada. D'altre banda retorna la millor tirada possible de totes les possibles fins la profunidat _profunditat. 
     */
    public Pair<Posicio,Posicio> ferTirada(Pair<Posicio,Posicio> tiradaAnterior){
        /**
        logica seria:
        Pair<Posicio,Posicio> tirada = _coneixament.tiradaSeguent(tiradaAnterior);
        if(tirada == null) tirada = _coneixament.buscarConeixament(_escacs);
        if(tirada == null) tirada = ferBacktracking(_escacs,_profunditat...);
         */
    }
}