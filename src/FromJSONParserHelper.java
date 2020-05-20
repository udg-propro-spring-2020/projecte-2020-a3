import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/// @author Miquel de Domingo i Giralt 
/// @file FromJSONParserHelper.java
/// @class FromJSONParserHelper
/// @brief Parses the given file with the chess configuration to a chess object
public class FromJSONParserHelper {
    /// @brief Function that returns the name of the game configuration
    /// @pre ---
    /// @post Returns the name of the file containing the configuration
    public static String getConfigurationFileName(String fileLocation) throws FileNotFoundException {
        Scanner mainSc = new Scanner(new File(fileLocation));
        // Skip first {
        mainSc.nextLine();
        // Get the file location
        String configFileName = getString(mainSc.nextLine());
        // Closing file scanner
        mainSc.close();

        return configFileName;
    }

    /// @brief Builds a chess with the given configuration file
    /// @pre ---
    /// @post Builds a chess with the given configuration file
    /// @throws JSONParseFormatException If some of the file content is not in the
    ///         correct format or there is an incoherence
    public static Chess buildChess(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        Scanner in = new Scanner(new File(fileLocation));

        // Skip first {
        in.nextLine();
        int nRows = getInt(in.nextLine());
        if (nRows < 4 || nRows > 16) {
            throw new JSONParseFormatException(
                "The file contains a number of rows not valid",
                JSONParseFormatException.ExceptionType.ILLEGAL_NUMBER
            );
        }
        int nCols = getInt(in.nextLine());
        if (nCols < 4 || nCols > 16) {
            throw new JSONParseFormatException(
                "The file contains a number of columns not valid",
                JSONParseFormatException.ExceptionType.ILLEGAL_NUMBER
            );
        }

        // Skip two lines
        in.nextLine();
        String temp = getString(in.nextLine());
        List<PieceType> typeList = null;

        if (!temp.equals("[]")) {
            typeList = getListPieceTypes(in);
        } else {
            throw new JSONParseFormatException(
                "Piece type list cannot be empty",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }
        Map<String, PieceType> typeMap = mapFromTypes(typeList);

        // Next two lines
        in.nextLine();
        temp = getString(in.nextLine());
        List<String> initialPos = new ArrayList<>();

        if (!temp.equals("[]")) {
            initialPos = getListStrings(in);
        } else {
            throw new JSONParseFormatException(
                "Initial positions list cannot be empty",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }

        if (illegalPieceName(initialPos, typeMap)) {
            throw new JSONParseFormatException(
                "Initial positions contains a piece that is not a piece type",
                JSONParseFormatException.ExceptionType.ILLEGAL_TYPE
            );
        }

        int chessLimits = getInt(in.nextLine());
        if (chessLimits < 0) {
            System.err.println("Chess limit must be greater than 0.");
            System.err.println("Default value [3] will be used.");
        }
        int inactiveLimits = getInt(in.nextLine());
        if (inactiveLimits < 0) {
            System.err.println("Inactive limit must be greater than 0..");
            System.err.println("Default value [3] will be used.");
        }

        /// Castlings
        List<Castling> castlings = new ArrayList<>();
        if (!getString(in.nextLine()).equals("[]")) {
            /// If castlings list is not empty
            castlings = getListCastlings(in, typeMap);
        }

        /// Close scanner
        in.close();

        return new Chess(nRows, nCols, typeList, initialPos, chessLimits, inactiveLimits, castlings);
    }

    /// @brief Builds a chess with the given configuration and game developement
    /// @pre ---
    /// @post Creates a chess game with the given configuration and game
    /// developement form the JSON file
    /// @throws JSONParseFormatException If some of the file content is not in the
    ///         correct format or if there is an incoherence
    public static Chess buildSavedChessGame(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        Scanner mainSc = new Scanner(new File(fileLocation));
        /// Skip first {
        mainSc.nextLine();
        /// Get the file location
        String configFileName = getString(mainSc.nextLine());
        if (configFileName.isEmpty()) {
            throw new JSONParseFormatException(
                "Configuration file cannot be empty",
                JSONParseFormatException.ExceptionType.ILLEGAL_NAME
            );
        }

        // LOADING CHESS CONFIGURATION FROM FILE
        Chess chess = buildChess(configFileName);

        // INITIAL POSITIONS
        // Read initial white positions
        List<Pair<Position, Piece>> whiteInitPos = new ArrayList<>();
        if (!getString(mainSc.nextLine()).equals("[]")) {
            whiteInitPos = getInitialPositionList(mainSc, chess.typeList(), PieceColor.White);
        } else {
            throw new JSONParseFormatException(
                "White piece list cannot be null",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }
        // Skip ],
        mainSc.nextLine();

        List<Pair<Position, Piece>> blackInitPos = new ArrayList<>();
        if (!getString(mainSc.nextLine()).equals("[]")) {
            blackInitPos = getInitialPositionList(mainSc, chess.typeList(), PieceColor.Black);
        } else {
            throw new JSONParseFormatException(
                "Black piece list cannot be null",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }
        // Skip ],
        mainSc.nextLine();

        // Check if last line has a result (which means that the match) has ended
        // Skip lines
        // Next turn
        mainSc.nextLine();
        // Turn list - if there's, skip it
        if (!getString(mainSc.nextLine()).equals("[]")) {
            getTurnList(mainSc, mapFromTypes(chess.typeList()));
        }

        // Skip ],
        String finalResult = getString(mainSc.nextLine());
        if (finalResult.contains("GUANYEN") || finalResult.contains("TAULES")) {
            /// Game has ended
            throw new JSONParseFormatException(
                "Saved game has already finished",
                JSONParseFormatException.ExceptionType.END_OF_GAME
            );
        }

        // Close scanner
        mainSc.close();

        // Create Chess
        return new Chess(chess, whiteInitPos, blackInitPos);
    }

    /// @biref Gets the game developement
    /// @pre ---
    /// @post Reads the game developement from the file. Returns a pair containing a
    ///       list of turns and the winning piece color. If @p forKnowledge is true,
    ///       returns the winning color. Otherwise, returns null.
    /// @throws JSONParseFormatException If the file contains an empty turn list
    public static Pair<List<Turn>, PieceColor> matchInformation(String fileLocation, Map<String, PieceType> types, boolean forKnowledge)
            throws FileNotFoundException, JSONParseFormatException {
        Scanner in = new Scanner(new File(fileLocation));

        // Skip 167 lines
        for (int i = 0; i < 167; i++) {
            in.nextLine();
        }

        // Read turns
        List<Turn> turnList = new ArrayList<>();

        if (!getString(in.nextLine()).equals("[]")) {
            turnList = getTurnList(in, types);

            // Skip ],
            in.nextLine();
        } else {
            throw new JSONParseFormatException(
                "The match does not have any turns",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }

        PieceColor tempColor = null;
        if (forKnowledge) {
            String s = getString(in.nextLine());
            tempColor = s.contains("BLANQUES") ? PieceColor.White : (s.contains("NEGRES")) ? PieceColor.Black : null;
            if (tempColor == null) {
                throw new JSONParseFormatException(
                    "Winning color is not valid. Must be \"BLANQUES\" or \"NEGRES\".",
                    JSONParseFormatException.ExceptionType.ILLEGAL_COLOR
                );
            }
        }

        // Close scanner
        in.close();

        return new Pair<List<Turn>, PieceColor>(turnList, tempColor);
    }

    /// @brief Returns the int from a JSON property
    /// @pre s == "x": y
    /// @post Returns the y value as an integer
    private static int getInt(String s) {
        // Remove comas and "
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return Integer.valueOf(values[1].trim());
    }

    /// @brief Returns the string from a JSON property
    /// @pre s == "x": "y"
    /// @post Returns the y value as a String without the double quotes, commas and
    ///       trimmed
    private static String getString(String s) {
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        if (values.length < 2) {
            return "";
        } else if (values.length == 2) {
            return values[1].trim();
        } else {
            // This case is when there is a castling or a promotion

            return values[1].trim() + ": " + values[2].trim();
        }
    }

    /// @brief Gets the movement list from the file
    /// @pre Scanner poiting at first line of the list
    /// @post Returns the JSON movements list and the scanner poiting at the end of
    ///       the line where the list ends.
    private static List<Movement> getListMovements(Scanner fr) {
        String s = fr.nextLine().trim();
        List<Movement> mList = new ArrayList<>();
        while (!s.equals("]")) {
            if (s.equals("],")) {
                fr.nextLine();
                s = fr.nextLine().trim();
            }

            // X
            String aux = s.replace("\"", "").replace(",", "");
            int x = aux.equals("a") ? 50 : (aux.equals("-a") ? -50 : Integer.parseInt(aux));
            // Y
            aux = fr.nextLine().trim().replace("\"", "").replace(",", "");
            int y = aux.equals("a") ? 50 : (aux.equals("-a") ? -50 : Integer.parseInt(aux));
            // Can capture
            int capture = Integer.parseInt(fr.nextLine().trim().replace(",", ""));
            // Can jump
            int jump = Integer.parseInt(fr.nextLine().trim().replace(",", ""));

            // Check if movement already exists
            Movement temp = new Movement(x, y, capture, jump);
            if (illegalMovement(temp, mList)) {
                System.err.println("Two movements have the same displacement vector.");
                System.err.println("The second movement won't be added.");
            } else {
                mList.add(temp);
            }
            s = fr.nextLine().trim();
        }
        return mList;
    }

    /// @brief Gets the string (positions) list from the file
    /// @pre The JSON list is not empty
    /// @post Returns the JSON positions list and the scanner poiting at the end of
    /// the line where the list ends.
    private static List<String> getListStrings(Scanner fr) {
        List<String> posList = new ArrayList<>();
        String s = fr.nextLine().trim();
        while (!s.equals("],")) {
            posList.add(s.replace("\"", "").replace(",", ""));
            s = fr.nextLine().trim();
        }

        return posList;
    }

    /// @brief Gets the piece type list from the file
    /// @pre The JSON list is not empty
    /// @post Returns the JSON pieces list and the scanner pointing at the end of
    /// the line where the list ends.
    private static List<PieceType> getListPieceTypes(Scanner fr) {
        List<PieceType> pList = new ArrayList<>();
        String s = fr.nextLine().trim();

        while (!s.equals("}")) { /// While not last object
            if (s.equals("},")) {
                // Check if },
                // And skip {
                fr.nextLine();
                s = fr.nextLine().trim();
            }

            // Name
            String name = getString(s);
            // Symbol
            String symbol = getString(fr.nextLine());
            // WhiteImage
            String wImage = getString(fr.nextLine());
            // BlackImage
            String bImage = getString(fr.nextLine());
            // Value
            int value = getInt(fr.nextLine());

            // Movements
            // Skip 2 lines
            fr.nextLine();
            fr.nextLine();
            List<Movement> movements = getListMovements(fr);

            // Initial Movements
            // Skip ],
            fr.nextLine();
            List<Movement> initMovements = new ArrayList<>();
            if (!getString(fr.nextLine()).equals("[]")) {
                // If list is not empty
                // Skip [
                fr.nextLine();
                initMovements = getListMovements(fr);
                // Skip ],
                fr.nextLine();
            }

            // Promotable
            boolean promotable = getString(fr.nextLine()).equals("true") ? true : false;
            // Invulnerable
            boolean invulnerable = getString(fr.nextLine()).equals("true") ? true : false;

            pList.add(
                new PieceType(name, symbol, wImage, bImage, value, promotable, invulnerable, movements, initMovements)
            );

            s = fr.nextLine().trim();
        }
        return pList;
    }

    /// @brief Gets the castling list from the file
    /// @pre ---
    /// @post Returns the JSON castling list which can be empty
    private static List<Castling> getListCastlings(Scanner fr, Map<String, PieceType> types) {
        // Skip {
        fr.nextLine();
        List<Castling> cList = new ArrayList<>();
        String s = fr.nextLine().trim();
        while (!s.equals("}")) {
            if (s.equals("},")) {
                fr.nextLine();
                s = fr.nextLine().trim();
            }

            String aPiece = getString(s);
            String bPiece = getString(fr.nextLine());
            boolean stand = getString(fr.nextLine()).equals("true") ? true : false;
            boolean emptyMiddle = getString(fr.nextLine()).equals("true") ? true : false;

            if (illegalType(aPiece, types) || illegalType(bPiece, types)) {
                System.err.println("A castling contains a piece that does not exist in the piece type - Skipped.");
            } else {
                cList.add(new Castling(aPiece, bPiece, stand, emptyMiddle));
            }

            s = fr.nextLine().trim();
        }
        return cList;
    }

    /// @pre Gets the initial positios list fro the file
    /// @pre Scanner pointing at {
    /// @post Returns a list of paris like Pair<A, B> where A is the positions and B
    ///       the piece type
    /// @throws JSONParseFormatException If there's a piece in the list that does
    ///         not exist as a piece type
    private static List<Pair<Position, Piece>> getInitialPositionList(Scanner fr, List<PieceType> pTypes,
            PieceColor color) throws JSONParseFormatException {
        // Skip {
        fr.nextLine();
        List<Pair<Position, Piece>> pList = new ArrayList<>();
        String s = fr.nextLine().trim();
        while (!s.equals("}")) {
            if (s.equals("},")) {
                fr.nextLine();
                s = fr.nextLine().trim();
            }

            Position pos = new Position(getString(s));
            String ptName = getString(fr.nextLine());
            PieceType type = null;

            for (PieceType pt : pTypes) {
                // Search for the type
                if (pt.ptName().equals(ptName)) {
                    type = pt;
                    break;
                }
            }

            if (type == null) {
                throw new JSONParseFormatException(
                    "A piece from the initial positions list does not exits.",
                    JSONParseFormatException.ExceptionType.ILLEGAL_TYPE
                );
            }

            boolean moved = getString(fr.nextLine()).equals("true") ? true : false;

            pList.add(new Pair<Position, Piece>(pos, new Piece(type, moved, color)));
            s = fr.nextLine().trim();
        }

        return pList;
    }

    /// @brief Gets the turn list from the file
    /// @pre Scanner pointing at {
    /// @post Returns the list of turns which can't be empty
    /// @throws JSONParseFormatException if there's a promotion turn which contains
    ///         an invalid piece type
    private static List<Turn> getTurnList(Scanner fr, Map<String, PieceType> types) throws JSONParseFormatException {
        // Skip {
        fr.nextLine();
        List<Turn> turnList = new ArrayList<>();
        String s = fr.nextLine().trim();
        while (!s.equals("}")) {
            if (s.equals("},")) {
                fr.nextLine();
                s = fr.nextLine().trim();
            }

            PieceColor color = getString(s).equals("BLANQUES") ? PieceColor.White : PieceColor.Black;
            String origin = getString(fr.nextLine());
            String dest = getString(fr.nextLine());

            s = fr.nextLine();
            String result = getString(s);

            // Validate turn result value
            switch (result) {
                case "ESCAC I MAT":
                case "TAULES PER REI OFEGAT":
                case "TAULES PER ESCAC CONTINU":
                case "TAULES PER INACCIÓ":
                case "TAULES ACCEPTADES":
                case "RENDICIÓ":
                case "AJORNAMENT":
                    // End of game
                    break;
                case "TAULES SOL·LICITADES":
                    // Empty turn
                    turnList.add(
                        new Turn(color, result)
                    );
                    break;
                case "ESCAC":
                case "": {
                    // Add new turn
                    Pair<String, String> move = new Pair<String, String>(origin, dest);
                    turnList.add(new Turn(color, move, result));
                }
                default: {
                    if (result.contains("PROMOCIÓ")) {
                        Pair<String, String> promotion = extractPromotionTurn(result);
                        if (illegalType(promotion.first, types) || illegalType(promotion.second, types)) {
                            throw new JSONParseFormatException(
                                "Promoted turn contains a piece that is not a piece type",
                                JSONParseFormatException.ExceptionType.ILLEGAL_TYPE
                            );
                        } else {
                            turnList.add(
                                new Turn(color, types.get(promotion.first), types.get(promotion.second))
                            );
                        }
                    } else if (result.contains("ENROC")) {
                        turnList.add(
                            new Turn(
                                color,
                                new Pair<String, String>(
                                    origin,
                                    dest
                                )
                            )
                        );
                    } else if (!result.isEmpty()) {
                        System.err.println("Movement result is not valid. It ill not be taken into account.");
                        System.err.println("Error result: " + result);
                        result = "";
                    }
                }
            }

            s = fr.nextLine().trim();
        }

        return turnList;
    }

    /// @brief Extracts the promotion from a turn result
    /// @pre ---
    /// @post From a promotion result [PROMOCIO: ORIGINAL-PROMOTED] returns the original
    ///       and the promoted as a Strings pair
    private static Pair<String, String> extractPromotionTurn(String result) {
        // Separate by the :
        String[] partOne = result.split(":");
        // Separate by the -
        String[] partTwo = partOne[1].trim().split("-");

        return new Pair<String, String>(partTwo[0], partTwo[1]);
    }

    /// @brief Crates a Map from the list of pieces
    /// @pre ---
    /// @brief Returns a Map of the list of pieces in which the piece name is the key
    public static Map<String, PieceType> mapFromTypes(List<PieceType> types) {
        Map<String, PieceType> map = new HashMap<>();
        for (PieceType type : types) {
            // Each type name is unique
            map.put(type.ptName(), type);
        }
        return map;
    }

    /// @brief Checks if the name corresponds to a piece type
    /// @pre ---
    /// @post Returns true if the name does not exist in the @p types list
    private static boolean illegalType(String name, Map<String, PieceType> types) {
        // Contains is O(1)
        return !types.containsKey(name);
    }

    /// @brief Checks if there's a piece name that is not a pieceType
    /// @pre ---
    /// @post Returns true if there's a piece name that is not a pieceType
    private static boolean illegalPieceName(List<String> initPositions, Map<String, PieceType> types) {
        if (initPositions != null && types == null || (!initPositions.isEmpty() && types.isEmpty())) {
            return true;
        } else if ((initPositions == null && types == null) || (initPositions.isEmpty() && types.isEmpty())) {
            return true;
        } else {
            // List of false
            List<Boolean> exists = new ArrayList<>();
            for (int i = 0; i < initPositions.size(); i++) {
                exists.add(false);
            }

            for (int i = 0; i < initPositions.size(); i++) {
                String name = initPositions.get(i);
                if (!illegalType(name, types)) {
                    exists.set(i, true);
                }
            }

            // If does not contain false, all piece names exist and are correct
            return exists.contains(false);
        }
    }

    /// @brief Checks if there's a movement which contains a moving vector
    ///        equal as one already existent
    /// @pre ---
    /// @post Returns true if there's a movement with the same moving vector as
    ///       the @p temp movement
    private static boolean illegalMovement(Movement temp, List<Movement> list) {
        boolean existent = false;
        int i = 0;
        while (!existent && i < list.size()) {
            Movement m = list.get(i);
            if (m.movX() == temp.movX() && m.movY() == temp.movY()) {
                existent = true;
            }
            i++;
        }

        return existent;
    }
}
