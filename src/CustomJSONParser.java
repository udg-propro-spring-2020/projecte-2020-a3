import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CustomJSONParser {
    /// @pre Last char read was {
    /// @post Returns a map of the object read 
    public static Map<Object, Object> parseToMap(Scanner fr) {
        if (fr.hasNextLine()) {
            // Beginning of object
            Map<Object, Object> map = new HashMap<>();
            String aux = fr.nextLine().trim();
            if (aux.endsWith(",")) {
                aux = aux.replace(",", "");
            }
            
            while (!aux.equals("}")) {
                String[] values = aux.split(":");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                
                Object v;
                if (values[1].equals("[")) {
                    /// An array
                    v = parseArray(fr);
                } else if (values[1].equals("{")) {
                    v = parseToMap(fr);
                } else {
                    /// Not an array
                    if (!values[1].startsWith("\"")) {
                        v = Integer.valueOf(values[1]);
                    } else {
                        v = values[1].replace("\"", "");
                    }
                }
                
                map.put(values[0].replace("\"", ""), v);
                
                aux = fr.nextLine().trim();
                if (aux.endsWith(",")) {
                    aux = aux.replace(",", "");
                }
            }

            return map;
        }
        return null;
    }

    /// @pre Last char read was [
    /// @post Returns a list of the array read 
    public static List<Object> parseArray(Scanner fr) {
        if (fr.hasNextLine()) {
            String aux = fr.nextLine().trim().replace(",", "");
            
            List<Object> array = new ArrayList<>();

            while (!aux.equals("]")) {
                System.out.println(aux);
                /// While not end of array
                if (aux.equals("{")) {
                    array.add(parseToMap(fr));
                } else {
                    if (!aux.startsWith("\"")) {
                        /// Integer value
                        array.add(aux);
                    } else {
                        array.add(aux.replace("\"", ""));
                    }

                    aux = fr.nextLine().trim();
                    if (aux.endsWith(",")) {
                        aux = aux.replace(",", "");
                    }
                }
            }

            return array;
        }
        return null;
    }
}
