import java.util.List;

/**
 * @author Miquel de Domingo i Giralt
 * @file JSONParserHelper.java
 * @class JSONParserHelper
 * @brief Functional class to help parsing objects to JSON
 */
public class JSONParserHelper {
    /// CONSTANTS
    /// @brief JSON Style control constants
    private static final String NEXT_LINE = "\n";
    private static final String EOL = ",\n";
    private static final String EMPTY_LIST = "[]";
    private static final String QUOTE = "\"";

    /// PUBLIC UTIL CONSTANTS
    /// @brief Util constants to avoid code repetition and meaningless strings
    public static final String ONE_TAB = "\t";
    public static final String TWO_TABS = "\t\t";
    public static final String THREE_TABS = "\t\t\t";
    public static final String FOUR_TABS = "\t\t\t\t";
    public static final String FIVE_TABS = "\t\t\t\t\t";
    public static final String LIST_START = "[\n";
    public static final String LIST_END = "]";
    public static final String OBJ_START = "{\n";
    public static final String OBJ_END = "}";

    /// @brief Parses a property to a JSON object property
    /// @pre ---
    /// @post Returns a object property String in JSON style ["propName": value],
    /// ending with a , and next line. If it is a String adds double quotes around
    /// it.
    /// @throws NullPointerException If @param value is null
    /// @throws IllegalArgumentException If @param identation does not
    public static String propertyToJSON(String propName, Object value, Boolean isString, String identation)
            throws NullPointerException, IllegalArgumentException {
        if (value == null) {
            throw new NullPointerException("PropertyToJSON null value given");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("PropertyToJSON identation can only be tabs or empty");
        } else {
            StringBuilder s = new StringBuilder();
            s.append(identation).append(valueToJSONString(propName)).append(": ");

            if (isString) {
                s.append(valueToJSONString(value));
            } else {
                s.append(value);
            }
            s.append(EOL);

            return s.toString();
        }
    }

    /// @brief Parses the given value to a JSON string
    /// @pre ---
    /// @post Returns the given value between double quotes
    /// @throws NullPointerException If @param value is null
    public static String valueToJSONString(Object value) throws NullPointerException {
        if (value == null) {
            throw new NullPointerException("ValueToJSONString null value given.");
        } else {
            StringBuilder s = new StringBuilder();
            s.append(QUOTE).append(value).append(QUOTE);
            return s.toString();
        }
    }

    /// @brief Parses the given list to a JSON list
    /// @pre ---
    /// @post Parses the given list to a String of a JSON style list
    public static String listToJSON(String listName, List<? extends JSON> list, String identation) {
        StringBuilder s = new StringBuilder();
        s.append(identation).append(valueToJSONString(listName)).append(": ");

        if (list.isEmpty() || list == null) {
            s.append(EMPTY_LIST).append(EOL);
        } else {
            s.append(LIST_START);
            for (int i = 0; i < list.size() - 1; i++) {
                s.append(list.get(i).toJSON()).append(EOL);
            }
            s.append(list.get(list.size() - 1).toJSON()).append(NEXT_LINE).append(identation).append(LIST_END)
                    .append(EOL);
        }

        return s.toString();
    }
}