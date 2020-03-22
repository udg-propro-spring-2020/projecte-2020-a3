/** @file Posicio.java
    @brief Una posició d'un tauler.
 */

/** @class Posicio
    @brief Parell fila, columna
 */
public class Posicio {
    public int fila;
    public int columna;
    
    public Posicio(int f, int c) {
	   fila = f; columna = c; 
    }

    @Override
    public String toString() {
	   return "(" + fila + "," + columna + ")";
    }
}
