/** @author Joan Plaja
    @file Pair.java
    @class Pair
    @brief Un parell genèric
*/

/** @class Pair
    @brief Parell genèric
 */
public class Pair<S,T> implements Cloneable {
    public S first;
    public T second;
    
    // @brief Create a pair of two given objects
	public Pair(S first, T second) {
        this.first = first; 
        this.second = second;
    }

    @Override
    public Object clone() {
        Pair<S,T> cloned = null;

        try {
            cloned = (Pair<S,T>) super.clone();
        } catch (CloneNotSupportedException c) {
            System.err.println("Pair clone exception");
        }
        return cloned;
    }

}
