/** @file Cpu.java
    @brief Un jugador automàtic.
 */

/**
    @class Cpu
    @brief Jugador automàtic amb coneixement sobre partides guanyadores.
 */

public class Cpu{

    private Coneixament _knowledge;     ///< Coneixament de sequencies de partides guanyadores
    private Escacs _escacs;             ///< Referencia al objecte Chess del joc
    private int _profunditat;           ///< Nivell de profunditat de cerca en l'arbre de possibiles moviments
    private string _color;              ///< Color de les peçes de la CPU

    /** @brief Crea la cpu
    @pre --
    @post La cpu té el coneixament \p coneixament,una referencia al joc d'escacs \p escacs
    i una profunidtat de busqueda de moviments \p profunditat.
     */
    public Cpu(Knowledge knowledge,Escacs escacs,int profunditat,string color){
        _knowledge=knowledge;
        _escacs=escacs;
        _profunditat=profunditat;
        _color=color;
    }

    /** @brief Fa una tirada
    @pre --
    @return Retorna parella de la tirada indiciant la posicio de origen i la posicio desti d'una fitxa. Si el coneixament ja segueix una sequencia
    i la \p tiradaAnterior concideix amb la tirada esperada la retorna la tirada. Altrament Si estat de _escacs concideix amb un guardat a _knowledge
    retorna retorna la tirada. D'altre banda retorna la millor tirada possible de totes les possibles fins la profunidat _profunditat. 
     */
    public Pair<Posicio,Posicio> ferTirada(Pair<Posicio,Posicio> tiradaAnterior){
        /**
        logica seria:
        Pair<Posicio,Posicio> tirada = _knowledge.tiradaSeguent(tiradaAnterior);
        if(tirada == null) tirada = _knowledge.buscarConeixament(_escacs);
        if(tirada == null) tirada = ferBacktracking(_escacs,_profunditat...);
         */
    }

    /** @biref Pesudocodi recrusiva algoritma MinMax sense podar de moment
    @pre --
    @return  
     */
    private int minMax(int profunditat,int tipusJugador){
        /*if nivell == 0 o fi partida retornar valor d'aquest
        else
        if tipusJugador = 0 (maxim)
            max=-infinity
            per cada fitxa del jugador
                per cada moviment de la fitxa
                    valor = minMax(profunditat+1,1)
                    si mes gran que max actualitzar max
                    retornar max
            return max
        else tipusJugador = 1
            min=+infity
            per cada fitxa del jugador
                per cada moviment de la fitxa
                valor = minMax(profunditat)
                si mes gran que min actualitzar
                retornar min
        */

    }   
}