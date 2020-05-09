/*
 * @author Miquel de Domingo i Giralt
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @file FromJSONParserHelper.java
 * @class FromJSONParserHelper
 * @brief Parses the given file with the chess configuration to a chess object
 */
public class FromJSONParserHelper {
    /// @brief Function that returns the name of the game configuration
    /// @pre ---
    /// @post Returns the name of the file containing the configuration
    public static String getConfigurationFileName(String fileLocation) throws FileNotFoundException {
        Scanner mainSc = new Scanner(new File(fileLocation));
        /// Skip first {
        mainSc.nextLine();
        /// Get the file location
        String configFileName = getString(mainSc.nextLine());
        /// Closing file scanner
        mainSc.close();

        return configFileName;
    }

    /// @brief Builds a chess with the given configuration file
    /// @pre ---
    /// @post Builds a chess with the given configuration file
    /// @throws Exception If some of the file content is not in the correct format
    ///         or there is an incoherence 
    public static Chess buildChess(String fileLocation) throws FileNotFoundException, Exception {
        Scanner in = new Scanner(new File(fileLocation));
        
        /// Skip first {
        in.nextLine();
        int nRows = getInt(in.nextLine());
        if (nRows < 4 || nRows > 16) {
            throw new Exception("El fitxer conté un nombre no vàlid de files");
        }
        int nCols = getInt(in.nextLine());
        if (nCols < 4 || nCols > 16) {
            throw new Exception("El fitxer conté un nombre no vàlid de columnes");
        }

        /// Skip two lines
        in.nextLine();
        in.nextLine();
        List<PieceType> typeList = getListPieceTypes(in);

        /// Next two lines
        in.nextLine();
        in.nextLine();
        List<String> initialPos = getListStrings(in); // ? Needed
        if (illegalPieceName(initialPos, typeList)) {
            throw new Exception("Les posicions inicials contenen una peça que no es troba al llistat de tipus de peces.");
        }

        int chessLimits = getInt(in.nextLine());
        int inactiveLimits = getInt(in.nextLine());

        /// Castlings
        List<Castling> castlings = new ArrayList<>();
        if (!getString(in.nextLine()).equals("[]")) {
            /// If castlings list is not empty
            castlings = getListCastlings(in, typeList);
        }
        
        /// Close scanner
        in.close();

        return new Chess(nRows, nCols, typeList, initialPos, chessLimits, inactiveLimits, castlings);
    }

    /// @brief Builds a chess with the given configuration and game developement
    /// @pre ---
    /// @post Creates a chess game with the given configuration and game
    ///       developement form the JSON file
    /// @throws Exception If some of the file content is not in the correct format
    ///         or there is an incoherence
    public static Chess buildSavedChessGame(String fileLocation) throws FileNotFoundException, Exception {
        Scanner mainSc = new Scanner(new File(fileLocation));
        /// Skip first {
        mainSc.nextLine();
        /// Get the file location
        String configFileName = getString(mainSc.nextLine());

        /// LOADING CHESS CONFIGURATION FROM FILE
        Chess chess = buildChess(configFileName);

        /// INITIAL POSITIONS
        /// Read initial white positions
        List<Pair<Position, Piece>> whiteInitPos = !getString(mainSc.nextLine()).equals("[]")
            ? getInitialPositionList(mainSc, chess.typeList(), PieceColor.White)    /// If not empty, read the list
            : new ArrayList<>();                                                    /// If empty, create an empty list
        /// Skip ],
        mainSc.nextLine();

        /// Read initial white positions
        List<Pair<Position, Piece>> blackInitPos = !getString(mainSc.nextLine()).equals("[]")
            ? getInitialPositionList(mainSc, chess.typeList(), PieceColor.Black)    /// If not empty, read the list
            : new ArrayList<>();                                                    /// If empty, create an empty list
        /// Skip ],
        mainSc.nextLine();

        /// Next lines are not necessary
        /// Close scanner
        mainSc.close();

        /// Create Chess
        return new Chess(chess, whiteInitPos, blackInitPos);
    }

    /// @biref Gets the game developement
    /// @pre ---
    /// @post Reads the game developement from the file. Returns a pair containing a 
    ///       list of turns and the winning piece color.
    public static Pair<List<Turn>, PieceColor> matchInformation(String fileLocation) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileLocation));

        /// Skip 167 lines
        for (int i = 0; i < 167; i++) {
            in.nextLine();
        }

        /// Read turns
        boolean hasTurns = !getString(in.nextLine()).equals("[]");

        List<Turn> turnList = hasTurns
            ? getTurnList(in)                                               /// If not empty, read the list
            : new ArrayList<>();                                            /// If empty, create an empty list
        if (hasTurns) {
            /// Skip ],
            in.nextLine();
        }

        String s = getString(in.nextLine());
        PieceColor tempColor = s.contains("BLANQUES") 
            ? PieceColor.White
            : PieceColor.Black;
        
        return new Pair<List<Turn>, PieceColor>(
            turnList,
            tempColor
        );
    }

    /// @brief Returns the int from a JSON property
    /// @pre s == "x": y
    /// @post Returns the y value as an integer
    private static int getInt(String s) {
        /// Remove comas and "
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return Integer.valueOf(values[1].trim());
    }

    /// @brief Returns the string from a JSON property
    /// @pre s == "x": "y"
    /// @post Returns the y value as a String without the double quotes, commas and
    ///       trimmed
    private static String getString(String s) {
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return values[1].isEmpty() ? "" : values[1].trim();
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

            /// X
            String aux = s.replace("\"", "").replace(",", "");
            int x = aux.equals("a") ? 50 : (aux.equals("-a") ? -50 : Integer.parseInt(aux));
            /// Y
            aux = fr.nextLine().trim().replace("\"", "").replace(",", "");
            int y = aux.equals("a") ? 50 : (aux.equals("-a") ? -50 : Integer.parseInt(aux));
            /// Can capture
            int capture = Integer.parseInt(fr.nextLine().trim().replace(",", ""));
            /// Can jump
            int jump = Integer.parseInt(fr.nextLine().trim().replace(",", ""));

            /// Check if movement already exists
            Movement temp = new Movement(x, y, capture, jump);
            if (illegalMovement(temp, mList)) {
                System.err.println("Dos moviments tenen el mateix vector de desplaçament.");
                System.out.println("El segon moviment queda exclòs.");
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
    ///       the line where the list ends.
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
    ///       the line where the list ends.
    private static List<PieceType> getListPieceTypes(Scanner fr) {
        List<PieceType> pList = new ArrayList<>();
        String s = fr.nextLine().trim();

        while (!s.equals("}")) { /// While not last object
            if (s.equals("},")) {
                /// Check if },
                /// And skip {
                fr.nextLine();
                s = fr.nextLine().trim();
            }

            /// Name
            String name = getString(s);
            /// Symbol
            String symbol = getString(fr.nextLine());
            /// WhiteImage
            String wImage = getString(fr.nextLine());
            /// BlackImage
            String bImage = getString(fr.nextLine());
            /// Value
            int value = getInt(fr.nextLine());

            /// Movements
            /// Skip 2 lines
            fr.nextLine();
            fr.nextLine();
            List<Movement> movements = getListMovements(fr);

            /// Initial Movements
            /// Skip ],
            fr.nextLine();
            List<Movement> initMovements = new ArrayList<>();
            if (!getString(fr.nextLine()).equals("[]")) {
                /// If list is not empty
                /// Skip [
                fr.nextLine();
                initMovements = getListMovements(fr);
                /// Skip ],
                fr.nextLine();
            }

            /// Promotable
            boolean promotable = getString(fr.nextLine()).equals("true") ? true : false;
            /// Invulnerable
            boolean invulnerable = getString(fr.nextLine()).equals("true") ? true : false;

            pList.add(new PieceType(name, symbol, wImage, bImage, value, promotable, invulnerable, movements,
                    initMovements));

            s = fr.nextLine().trim();
        }
        return pList;
    }

    /// @brief Gets the castling list from the file
    /// @pre ---
    /// @post Returns the JSON castling list which can be empty
    private static List<Castling> getListCastlings(Scanner fr, List<PieceType> types) {
        /// Skip {
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
                System.err.println("Un enroc conté una peça que no es troba a la llista de tipus peça - Exclòs.");
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
    /// @throws Exception If there's a piece in the list that does not exist as a piece type
    private static List<Pair<Position, Piece>> getInitialPositionList(Scanner fr, List<PieceType> pTypes,
            PieceColor color) throws Exception {
        /// Skip {
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
                /// Search for the type
                if (pt.ptName().equals(ptName)) {
                    type = pt;
                    break;
                }
            }

            if (type == null) {
                throw new Exception("Una peça del llistat de posicions inicials no existeix als tipus de peça.");
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
    private static List<Turn> getTurnList(Scanner fr) {
        /// Skip {
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
            Pair<String, String> move = new Pair<String, String>(origin, dest);

            s = fr.nextLine();
            String result = s.trim().endsWith("\"\"") ? "" : getString(s);

            turnList.add(new Turn(color, move, result));
            s = fr.nextLine().trim();
        }

        return turnList;
    }

    /// @brief Checks if the name corresponds to a piece type
    /// @pre ---
    /// @post Returns true if the name does not exist in the @p types list
    private static boolean illegalType(String name, List<PieceType> types) {
        for (PieceType type : types) {
            if (type.ptName().equals(name)) { 
                return false;
            }
        }

        /// If it reaches here, means the name is not valid
        return true;
    }

    /// @brief Checks if there's a piece name that is not a pieceType
    /// @pre ---
    /// @post Returns true if there's a piece name that is not a pieceType
    private static boolean illegalPieceName(List<String> initPositions, List<PieceType> types) {
        if (initPositions != null && types == null|| (!initPositions.isEmpty() && types.isEmpty())) {
            return true;
        } else if ((initPositions == null && types == null) || (initPositions.isEmpty() && types.isEmpty())) {
            return true;
        } else {
            /// List of false
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

            /// If does not contain false, all piece names exist and are correct
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
