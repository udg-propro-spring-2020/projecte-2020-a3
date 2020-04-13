/** @file Cpu.java
    @brief Un jugador automàtic.
 */

/**
    @class Cpu
    @brief Jugador automàtic amb coneixement sobre partides guanyadores.
 */

public class Cpu{

    private Coneixament _knowledge;     ///< Coneixament de sequencies de partides guanyadores
    private Chess _chess;             ///< Referencia al objecte Chess del joc
    private int _profunditat;           ///< Nivell de profunditat de cerca en l'arbre de possibiles moviments
    private string _color;              ///< Color de les peçes de la CPU

    /** @brief Crea la cpu
    @pre --
    @post La cpu té el coneixament \p coneixament,una referencia al joc d'escacs \p escacs
    i una profunidtat de busqueda de moviments \p profunditat.
     */
    public Cpu(Knowledge knowledge,Chess chess,int profunditat,int color){
        _knowledge=knowledge;
        _chess=chess;
        _profunditat=profunditat;
        _color=color;
    }

    /** @brief Fa una tirada
    @pre --
    @return Retorna parella de la tirada indiciant la Position de origen i la Position desti d'una fitxa. Si el coneixament ja segueix una sequencia
    i la \p tiradaAnterior concideix amb la tirada esperada la retorna la tirada. Altrament Si estat de _escacs concideix amb un guardat a _knowledge
    retorna retorna la tirada. D'altre banda retorna la millor tirada possible de totes les possibles fins la profunidat _profunditat. 
     */
    public Pair<Position,Position> ferTirada(Pair<Position,Position> tiradaAnterior){
        /**
        logica seria:
        Pair<Position,Position> tirada = _knowledge.tiradaSeguent(tiradaAnterior);
        if(tirada == null) tirada = _knowledge.buscarConeixament(_escacs);
        if(tirada == null) tirada = ferBacktracking(_escacs,_profunditat...);
         */
         //potser mes d'una opcio
        
    }

    /** @biref Pesudocodi recrusiva algoritma MinMax sense podar de moment
    @pre --
    @return  
     */
    private Pair<Position,Position> minMax(){ 
        Pair<Position,Position> moviment = new Pair<Position,Position>(null,null);
        return i_minMax(0,0,moviment,Integer.MIN_VALUE,Integer.MAX_VALUE);

    }

    private int i_minMax(int puntuacio,int tipusJugador,Pair<Position,Position> moviment,int mesGranAnterior,int mesPetitAnterior){
        if(profunditat==_profunditat)return puntuacio;
        else if(tipusJugador==0){
            Integer max = Integer.MIN_VALUE;
            List<Pair<Position,Piece>> pieces;
            if(_color==0)pieces=_chess.pListWhite();
            else pieces=_chess.pListBlack();
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){//per cada fitxa
                List<Pair<Position,Integer>> destinyWithScores = _chess.destinyWithValues(itPieces.second);
                Iterator<Pair<Position,Integer>> itMoviments;
                while(itMoviments.hasNext()){//per cada moviment
                    Pair<Position,Integer> moviment = itMoviments.next();
                    Integer score=moviment.second + puntuacio;
                    if(moviment.second>0)_chess.applyMovement(itPieces.second,null,moviment.first);//aplicar
                    else _chess.applyMovement(itPieces.second,moviment.first,null);
                    score = minMax(score,profunditat+1,1,moviment);
                    //desfer !!!
                    if(score>max){
                        mesGranAnterior=score;
                        max=score;
                        moviment.first=itPieces.second,
                        movimient.second=moviment.second;
                    }
                    if(mesPetitAnterior<=mesGranAnterior)break;
                }
            }
            return max;
        }
        else{
            Integer min = Integer.MAX_VALUE;
            List<Pair<Position,Piece>> pieces;
            if(_color==1)pieces=_chess.pListWhite();
            else pieces=_chess.pListBlack();
            Iterator<Pair<Position,Piece>> itPieces = pieces.iterator();
            while(itPieces.hasNext()){//per cada fitxa
                List<Pair<Position,Integer>> destinyWithScores = _chess.destinyWithValues(itPieces.second);
                Iterator<Pair<Position,Integer>> itMoviments;
                while(itMoviments.hasNext()){//per cada moviment
                    Pair<Position,Integer> moviment = itMoviments.next();
                    Integer score= -moviment.second + puntuacio;
                    if(moviment.second>0)_chess.applyMovement(itPieces.second,null,moviment.first);//aplicar
                    else _chess.applyMovement(itPieces.second,moviment.first,null);
                    score = minMax(score,profunditat+1,0,moviment);
                    //desfer !!!
                    if(score>max){
                        mesGranAnterior=score;
                        max=score;
                        moviment.first=itPieces.second,
                        movimient.second=moviment.second;
                    }
                    if(mesGranAnterior>=mesPetitAnterior)break;
                    }
                }
            return min;
        }
    }
}