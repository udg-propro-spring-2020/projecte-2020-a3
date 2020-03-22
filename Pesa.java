/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cacer
 */
public class Pesa {
    String nom;
    String simbol;
    String imatgePeça;
    int valor;
    boolean promocionable;
    boolean invulnerable;
    Moviment[] moviments; //Hi ha string i int com a moviments.
    //([Mov X, Mov Y, Captura, Salt])
    //En cas de lletra, poden avançar varies caselles
    Moviment[] movimentsInicials;
    private class Moviment {
    
        public int MovX;   
        public int MovY;   
        public int captura; 
        public int salt;    
    
    }

    Pesa(/*Fragment del JSON que conté la peça*/){
        /*
        Aqui es tracta el fragment JSON i es guarden els atributs de la peça esmentats adalt.
        Per guardar els moviments possibles d'una peça en cas de string "a", que significa
        que pot avançar varies caselles, ho transformem en un int que ho indiqui, per exemple 69,
        per facilitar les comprobacions de moviment.
        */
    }
    boolean esInvulnerable(){
        return invulnerable;
    }
    
    boolean esPromocionable(){
        return promocionable;
    }
    
    Moviment[] movimentsPeça(){
        return moviments;
    }
    
    Moviment[] movimentsInicialsPeça(){
        return movimentsInicials;
    }
    
}
