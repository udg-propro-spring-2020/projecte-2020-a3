import java.util.List;

/// @author Miquel de Domingo i Giralt
/// @file ToJSONParserHelper.java
/// @class ToJSONParserHelper
/// @brief Functional class to help parsing objects to JSON
public class ToJSONParserHelper {
    // CONSTANTS
    /// @brief JSON Style control constants
    private static final String NEXT_LINE  = "\n";              ///< Next line 
    private static final String EOL        = ",\n";             ///< Coma & next line 
    private static final String EMPTY_LIST = "[]";              ///< JSON Empty list 
    private static final String QUOTE      = "\"";              ///< Scaped quote
    private static final String SEPARATOR  = ": ";              ///< JSON Separator

    // PUBLIC UTIL CONSTANTS
    /// @brief Util constants to avoid code repetition and meaningless strings
    public static final String ONE_TAB    = "\t";               ///< Simulates one tab
    public static final String TWO_TABS   = "\t\t";             ///< Simulates two tabs
    public static final String THREE_TABS = "\t\t\t";           ///< Simulates three tabs
    public static final String FOUR_TABS  = "\t\t\t\t";         ///< Simulates four tabs
    public static final String FIVE_TABS  = "\t\t\t\t\t";       ///< Simulates five tabs
    public static final String LIST_START = "[\n";              ///< JSON List start
    public static final String LIST_END   = "]";                ///< JSON List end
    public static final String OBJ_START  = "{\n";              ///< JSON Object start
    public static final String OBJ_END    = "}";                ///< JSON Object end

    /// @brief Creates a string containing the chess configuration
    /// @pre ---
    /// @post Returns a string with the chess configuration in JSON style
    /// @throws NullPointerException If the given chess is null
    public static String saveChessConfigToJSON(Chess chess) throws NullPointerException {
        if (chess == null) {
            throw new NullPointerException("saveChessConfiguration given chess cannot be null");
        } 
        StringBuilder s = new StringBuilder();

        s.append(OBJ_START)
            .append(propertyToJSON("nFiles", chess.rows(), false, true, ONE_TAB))
            .append(propertyToJSON("nCols", chess.cols(), false, true, ONE_TAB))
            .append(objectListToJSON("peces", chess.typeList(), false, ONE_TAB))
            .append(primitiveListToJSON("posInicial", chess.initialPositions(), ONE_TAB, true, false))
            .append(propertyToJSON("limitEscacsSeguits", chess.checkLimits(), false, true, ONE_TAB))
            .append(propertyToJSON("limitTornsInaccio", chess.inactiveLimits(), false, true,ONE_TAB))
            .append(objectListToJSON("enrocs", chess.castlings(), true, ONE_TAB))
            .append(OBJ_END);

        return s.toString();
    }

    /// @brief Creates a string containing the game data
    /// @pre ---
    /// @post Returns a string with the game data in JSON style
    /// @throw NullPointerException If the given chess is null
    public static String saveGameToJSON(Chess chess, String configurationFile, PieceColor nextTurn, List<Turn> turns, String finalResult) {
        if (chess == null) {
            throw new NullPointerException("saveGame given chess cannot be null");
        }

        StringBuilder s = new StringBuilder();
        configurationFile = configurationFile.replace("\\", "/");

        s.append(OBJ_START)
            .append(propertyToJSON("fitxerRegles", configurationFile, true, true, ONE_TAB))
            .append(initPosListToJSON(chess.whiteInitPos(), "posIniBlanques", ONE_TAB, false))
            .append(initPosListToJSON(chess.blackInitPos(), "posIniNegres", ONE_TAB, false))
            .append(propertyToJSON("proper_torn", nextTurn.toString(), true, true, ONE_TAB))
            .append(objectListToJSON("tirades", turns, false, ONE_TAB))
            .append(propertyToJSON("resultat_final", finalResult, true, false, ONE_TAB))
            .append(OBJ_END);
        
        return s.toString();
    }

    /// @brief Parses a property to a JSON object property
    /// @pre ---
    /// @post Returns a object property String in JSON style ["propName": value],
    ///       ending with a , and next line. If it is a String adds double quotes around
    ///       it
    /// @throws IllegalArgumentException If @p identation does no contain spaces or tabs
    public static String propertyToJSON(String propName, Object value, Boolean isString, Boolean trailingComa,
            String identation) {
        if (value == null) {
            throw new NullPointerException("PropertyToJSON null value given");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("PropertyToJSON identation can only be tabs or empty");
        }

        StringBuilder s = new StringBuilder();
        s.append(identation)
            .append(valueToJSONString(propName))
            .append(SEPARATOR);

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

    /// @brief Parses the given value to JSON (adding a trailing coma) identated
    /// @pre ---
    /// @post Returns the given value as a JSON value identated
    /// @throw NullPointerException If @p value is null
    /// @throw IllegalArgumentException If @p identation does not contain only spaces or tabs
    public static String valueToJSON(Object value, String identation, Boolean trailingComa) {
        if (value == null) {
            throw new NullPointerException("valueToJSON null value given");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("valueToJSON identation can only be tabs or empty");
        }

        StringBuilder s = new StringBuilder();
        s.append(identation)
            .append(value);

        if (trailingComa) {
            s.append(EOL);
        } else {
            s.append(NEXT_LINE);
        }
        return s.toString();
    }

    /// @brief Parses the given value to a JSON string
    /// @pre ---
    /// @post Returns the given value between double quotes
    /// @throws NullPointerException If @p value is null
    public static String valueToJSONString(Object value) {
        if (value == null) {
            throw new NullPointerException("ValueToJSONString null value given.");
        }

        StringBuilder s = new StringBuilder();
        s.append(QUOTE)
            .append(value)
            .append(QUOTE);
        return s.toString();
    }

    /// @brief Parses the given list to a JSON list
    /// @pre Objects must be indented
    /// @post Parses the given list to a String of a JSON style list
    /// @throw NullPointerException If the @p list is null or contains a null item
    /// @thwo IllegalArgumentException If the @p identation does not contain only spaces or tabs
    public static String objectListToJSON(String listName, List<? extends JSON> list, Boolean lastItem, String identation)
            throws NullPointerException, IllegalArgumentException {
        if (listName.isEmpty() || listName == null) {
            throw new NullPointerException("objectListToJSON listName value cannot be null nor empty");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("objectListToJSON identation can only be tabs or empty");
        }

        StringBuilder s = new StringBuilder();
        s.append(identation)
            .append(valueToJSONString(listName))
            .append(SEPARATOR);

        if (list.isEmpty() || list == null) {
            s.append(EMPTY_LIST);
        } else {
            s.append(LIST_START);
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i) == null) {
                    throw new NullPointerException("objectListToJSON list contains a null value at " + i);
                }

                s.append(list.get(i).toJSON())
                    .append(EOL);
            }

            if (list.get(list.size() - 1) == null) {
                throw new NullPointerException("objectListToJSON list contains a null value at last item");
            }

            s.append(list.get(list.size() - 1).toJSON())
                .append(NEXT_LINE)
                .append(identation)
                .append(LIST_END);
        }

        if (lastItem) {
            s.append(NEXT_LINE);
        } else {
            s.append(EOL);
        }

        return s.toString();
    }

    /// @brief Parses the given list to a JSON list
    /// @pre ---
    /// @post Parses the give list containing java primitive types to a String
    ///       of a JSON style list. Objectes indented automatically, inner list elements
    ///       tabbed one more.
    /// @throw NullPointerException If the @p list is null or contains a null item
    /// @throw IllegalArgumentException If the @p identation does not contain only spaces or tabs
    public static String primitiveListToJSON(String listName, List<? extends Object> list, String identation, Boolean inQuotes, Boolean lastItem) 
            throws NullPointerException, IllegalArgumentException {
        if (listName.isEmpty() || listName == null) {
            throw new NullPointerException("primitiveListToJSON listName value cannot be null nor empty");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("primitiveListToJSON identation can only be tabs or empty");
        }

        StringBuilder s = new StringBuilder();
        s.append(identation)
            .append(valueToJSONString(listName))
            .append(SEPARATOR);

        if (list.isEmpty() || list == null) {
            s.append(EMPTY_LIST);
        } else {
            s.append(LIST_START);
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i) == null) {
                    s.append(identation)
                        .append(ONE_TAB)
                        .append(valueToJSONString(""))
                        .append(EOL);   
                } else {
                    s.append(identation)
                        .append(ONE_TAB)
                        .append(inQuotes ? valueToJSONString(list.get(i)) : list.get(i))
                        .append(EOL);
                }
            }
            
            if (list.get(list.size() - 1) == null) {
                s.append(identation)
                    .append(ONE_TAB)
                    .append(valueToJSONString(""))
                    .append(NEXT_LINE)
                    .append(identation)
                    .append(LIST_END);
            } else {
                s.append(identation)
                .append(ONE_TAB)
                .append(inQuotes ? valueToJSONString(list.get(list.size() - 1)) : list.get(list.size() - 1))
                .append(NEXT_LINE)
                .append(identation)
                .append(LIST_END);
            }
        }

        if (lastItem) {
            s.append(NEXT_LINE);
        } else {
            s.append(EOL);
        }

        return s.toString();
    }

    /// @brief Parses a list of pair containing Position & Piece
    /// @pre ---
    /// @post Parses the pair list as a JSON object writing the first the position and then the piece
    /// @throw NullPointerException If the @p list is null or contains a null element
    /// @throw IllegalArgumentException If the @p identation does not contain only spaces or tabs
    private static String initPosListToJSON(List<Pair<Position, Piece>> list, String listName, String identation, Boolean lastItem) throws NullPointerException {
        if (list == null) {
            throw new NullPointerException("initPosListToJSON null value given");
        } else if (!identation.trim().isEmpty()) {
            throw new IllegalArgumentException("PropertyToJSON identation can only be tabs or empty");
        }

        StringBuilder s = new StringBuilder();
        s.append(identation)
            .append(valueToJSONString(listName))
            .append(SEPARATOR);
        
        if (list.isEmpty()) {
            s.append(EMPTY_LIST);
        } else {
            s.append(LIST_START);

            for (int i = 0; i < list.size() - 1; i++) {
                Pair<Position, Piece> item = list.get(i);
                if (item == null || item.first == null || item.second == null) {
                    // Continue
                    continue;
                }

                s.append(identation + ONE_TAB)
                    .append(OBJ_START)
                    .append(item.first.toJSON())
                    .append(item.second.toJSON())
                    .append(identation + ONE_TAB)
                    .append(OBJ_END)
                    .append(EOL);
            }

            Pair<Position, Piece> item = list.get(list.size() - 1);
            if (!(item == null || item.first == null || item.second == null)) {    
                s.append(identation + ONE_TAB)
                    .append(OBJ_START)
                    .append(item.first.toJSON())
                    .append(item.second.toJSON())
                    .append(identation + ONE_TAB)
                    .append(OBJ_END)
                    .append(NEXT_LINE)
                    .append(identation)
                    .append(LIST_END);                
            }
        }

        if (lastItem) {
            s.append(NEXT_LINE);
        } else {
            s.append(EOL);
        }

        return s.toString();
    }
}