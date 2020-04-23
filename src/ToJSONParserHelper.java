import java.util.List;

/**
 * @author Miquel de Domingo i Giralt
 * @file ToJSONParserHelper.java
 * @class ToJSONParserHelper
 * @brief Functional class to help parsing objects to JSON
 */
public class ToJSONParserHelper {
    /// CONSTANTS
    /// @brief JSON Style control constants
    private static final String NEXT_LINE = "\n";
    private static final String EOL = ",\n";
    private static final String EMPTY_LIST = "[]";
    private static final String QUOTE = "\"";
    private static final String SEPARATOR = ": ";

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

    /// @brief Saves the chess configuration in a JSON file
    /// @pre ---
    /// @post Configuration saved
    public static String saveChessConfiguration(Chess chess, String fileName) throws NullPointerException {
        if (chess == null) {
            throw new NullPointerException("saveChessConfiguration given chess cannot be null");
        } 
        StringBuilder s = new StringBuilder();

        s.append(ToJSONParserHelper.OBJ_START)
                .append(ToJSONParserHelper.propertyToJSON("nFiles", chess.rows(), false, true, "\t"))
                .append(ToJSONParserHelper.propertyToJSON("nCols", chess.cols(), false, true, "\t"))
                .append(ToJSONParserHelper.objectListToJSON("peces", chess.typeList(), false, "\t"))
                .append(ToJSONParserHelper.primitiveListToJSON("posInicial", chess.initialPositions(), "\t", true,
                        false))
                .append(ToJSONParserHelper.propertyToJSON("limitEscacsSeguits", chess.chessLimits(), false, true, "\t"))
                .append(ToJSONParserHelper.propertyToJSON("limitTornsInnaccio", chess.inactiveLimits(), false, true,
                        "\t"))
                .append(ToJSONParserHelper.objectListToJSON("enrocs", chess.castlings(), true, "\t"))
                .append(ToJSONParserHelper.OBJ_END);

        return s.toString();
    }

    /// @brief Parses a property to a JSON object property
    /// @pre ---
    /// @post Returns a object property String in JSON style ["propName": value],
    /// ending with a , and next line. If it is a String adds double quotes around
    /// it.
    /// @throws NullPointerException If @p value is null
    /// @throws IllegalArgumentException If @p identation does not
    public static String propertyToJSON(String propName, Object value, Boolean isString, Boolean trailingComa,
            String identation) throws NullPointerException, IllegalArgumentException {
        if (value == null) {
            throw new NullPointerException("PropertyToJSON null value given");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("PropertyToJSON identation can only be tabs or empty");
        } else {
            StringBuilder s = new StringBuilder();
            s.append(identation).append(valueToJSONString(propName)).append(SEPARATOR);

            if (isString) {
                s.append(valueToJSONString(value));
            } else {
                s.append(value);
            }

            if (trailingComa) {
                s.append(EOL);
            } else {
                s.append(NEXT_LINE);
            }

            return s.toString();
        }
    }

    /// @brief Parses the given value to a JSON string
    /// @pre ---
    /// @post Returns the given value between double quotes
    /// @throws NullPointerException If @p value is null
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
    /// @pre Objects must be indented
    /// @post Parses the given list to a String of a JSON style list
    public static String objectListToJSON(String listName, List<? extends JSON> list, Boolean lastItem,
            String identation) {
        if (listName.isEmpty() || listName == null) {
            throw new IllegalArgumentException("primitiveListToJSON listName value cannot be null nor empty");
        } else {
            StringBuilder s = new StringBuilder();
            s.append(identation).append(valueToJSONString(listName)).append(SEPARATOR);

            if (list.isEmpty() || list == null) {
                s.append(EMPTY_LIST);
            } else {
                s.append(LIST_START);
                for (int i = 0; i < list.size() - 1; i++) {
                    s.append(list.get(i).toJSON()).append(EOL);
                }
                s.append(list.get(list.size() - 1).toJSON()).append(NEXT_LINE).append(identation).append(LIST_END);
            }

            if (lastItem) {
                s.append(NEXT_LINE);
            } else {
                s.append(EOL);
            }

            return s.toString();
        }
    }

    /// @brieg Parses the given list to a JSON list
    /// @pre ---
    /// @post Parses the give list containing java primitive types to a String
    /// of a JSON style list. Objectes indented automatically, inner list elements
    /// tabbed one more.
    public static String primitiveListToJSON(String listName, List<? extends Object> list, String identation,
            Boolean inQuotes, Boolean lastItem) {
        if (listName.isEmpty() || listName == null) {
            throw new IllegalArgumentException("primitiveListToJSON listName value cannot be null nor empty");
        } else {
            StringBuilder s = new StringBuilder();
            s.append(identation).append(valueToJSONString(listName)).append(SEPARATOR);

            if (list.isEmpty() || list == null) {
                s.append(EMPTY_LIST);
            } else {
                s.append(LIST_START);
                for (int i = 0; i < list.size() - 1; i++) {
                    s.append(identation).append(ONE_TAB).append(inQuotes ? valueToJSONString(list.get(i)) : list.get(i))
                            .append(EOL);
                }

                s.append(identation).append(ONE_TAB)
                        .append(inQuotes ? valueToJSONString(list.get(list.size() - 1)) : list.get(list.size() - 1))
                        .append(NEXT_LINE).append(identation).append(LIST_END);
            }

            if (lastItem) {
                s.append(NEXT_LINE);
            } else {
                s.append(EOL);
            }

            return s.toString();
        }
    }
}