/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cacer
 */
public class Escacs {
    private int _files;        ///< Alçada del tauler
    private int _columnes;     ///< Amplada del tauler
    private Pesa[][] _tauler;
    
    /** @brief Crea el tauler
	@pre  4 <= \p files <= 16 i 4 <= \p columnes <= 16
	@post El tauler de joc té dimensió \p files x \p columnes, i
	les fitxes estan en la seva situació inicial.
    */
    public Escacs(/*JSON PATH*/) {
        /*
        Aqui es va llegint tot el JSON i es van guadant les dades com files i columnes
        i es van creant peces amb moviments.
        Quan s'arriba al "posicio inicial" del JSON es crea el tauler
        */
        //PecesEnPosicioInicial [] <-- El json l'emplena
        if (_files < 4 || _columnes < 4 || _columnes > 16 || _files > 16)
                throw new RuntimeException("El nombre de files i columnes ha de ser entre 4 i 16");
        _tauler = new Pesa[_files][_columnes];
        for (int i = 0; i < _files; ++i) {
            for (int j = 0; j < _columnes; ++j) {
                _tauler[i][j]=null;
            }
        }
        /*
        Primer es fa un calcul sobre a quina casella es comensarà a emplenar el tauler.
        S'ha de tenir en compte el nombre de caselles i el nombre de peces. A partir de 
        aixo ja es poden fer els bucles correctament i posar cada peça al seu lloc
        */
        //int act=0; //Pos inici de l'array, si es vol comensar pels peons comencem pel final
        //filaInici = calcul...
        //columnaInici = calcul...
        //for (int i = filaInici; i < _files; ++i) {
            //for (int j = columnaInici ; j < _columnes; ++j) {
                /*
                S'ha de recorrer la sortida de JSON que haurem guardat com a
                posicionsInicials i posar cada fitxa a la seva casella corresponent
                */
                //_tauler[i][j]= PecesEnPosicioInicial[act];
            //}
        //}
    }
    /** @brief Diu quantes files té el tauler */
    public int files() {
        return _files;
    }

    /** @brief Diu quantes columnes té el tauler */
    public int columnes() {
        return _columnes;
    }

    /** @brief Retorna la peça d'una casella
	@pre 0 <= f < files() i 0 <= c < columnes()
	@return La peça de la posició (f,c); null si no hi ha cap fitxa
	en aquesta posició.
    */
    public Pesa peça(int f, int c) {
        //f ha sigut transformada en int abans de passar per parametre
	return _tauler[f][c];
    }
    public int partidaAcabada(/*int limitEscacs, int limitTorns, bool taules, bool reiMort*/){
        int acabat=0;
        /*
        Aqui es van comprovant si els valors que es passen per parametre (escacs
        o torns sense capturar cap peça) han arribat al seu limit, si s'ha decidit
        quedar en taules o si un rei ha mort en aquell torn.
        Podriem retornar un int o un altre depenent de quin hagi sigut el final del joc
        0: No finalitzat
        1: limitEscacs
        2: limitTorns
        3: taules
        4: rei negre
        5: rei blanc 
        
        Podem saber quin rei a mort a partir del jugador que ha fet la tirada actual
        
        */
        return acabat;
    }
    /** @brief Guarda en JSON la partida en el torn actual */
    private void guardarTorn(){
        /*
        Ha de escriure un JSON amb la informació del torn actual seguint el model de JSON del moodle.
        Servira per si es vol fer i refer la jugada
        */
    }
    public void refer(){
        /*
        Es crida el contructor Escacs amb el json que li pertoqui de l'array 
        */
    }
    public void desfer(){
        /*
        Podem cridar el constructor Escacs amb el fitxer json de l'ultim torn de l'array.
        Necesitem un contador que ens digui per quina posicio d'aquest array anem per si refem o desfem mes.
        */
    }
    private void guardarPartida(){
        /*
        Ha de escriure un JSON amb la informació final de la partida seguint el model de JSON del moodle.
        Servira per si es vol agregar la partida a Coneixement i tenir un registre de tot el joc.
        */
    }
    
    
    /** @brief Diu si un moviment és vàlid, i quina fitxa es mata
	@pre \p origen i \p desti són posicions vàlides del tauler.
	@return Parella indicant si el moviment d'una fitxa de \p
	origen a \p desti és possible, i la posició de la fitxa a matar
	si s'escau.
    */
    public Pair<Boolean,Posicio> moviment(Posicio origen, Posicio desti) {
        Pair<Boolean,Posicio> r = new Pair<>(false,null);
        int x0 = origen.fila;
        int y0 = origen.columna;
        int x1 = desti.fila;
        int y1 = desti.columna;
        Pesa p = _tauler[x0][y0];
        /*
        Seguint la idea de les dames retornariem si es possible fer el moviment i la
        posicio de la fitxa a matar (Varia depenent de si pot caçar saltant).
        Per validar el moviment:
        1. Es demana l'array de moviments de la peça que hi ha en la posició origen.
        2. Es comproba que el moviment que es vol fer (l'hem de calcular) està dins d'aquest array.
        3. Es comproba que la peça no surt de tauler
        4. Apliquem moviment o demanem nou moviment
        */
        
        return r;
    }
    /** @brief Aplica un moviment
	@pre \p origen i \p desti són posicions vàlides del tauler;
	si \p matar no és null, aleshores és una posició vàlida del
	tauler.
	@post La fitxa de la posició \p origen s'ha mogut a la posició
	\p destí, i si \p matar no és null, s'ha eliminat la fitxa
	d'aquesta posició.
    */
    public void aplicaMoviment(Posicio origen, Posicio desti, Posicio matar) {
        //Podem reutilitzar l'aplicarMoviment de les dames
        _tauler[desti.fila][desti.columna] = _tauler[origen.fila][origen.columna];
        _tauler[origen.fila][origen.columna] = null;
        if (matar != null)
                _tauler[matar.fila][matar.columna] = null;
    }

    
}
