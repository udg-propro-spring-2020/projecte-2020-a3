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
    /// @throws JSONParseFormatException If some of the file content is not in the correct format
    ///         or there is an incoherence 
    public static Chess buildChess(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        Scanner in = new Scanner(new File(fileLocation));
        
        /// Skip first {
        in.nextLine();
        int nRows = getInt(in.nextLine());
        if (nRows < 4 || nRows > 16) {
            throw new JSONParseFormatException(
                "El fitxer conté un nombre no vàlid de files",
                JSONParseFormatException.ExceptionType.ILLEGAL_NUMBER
            );
        }
        int nCols = getInt(in.nextLine());
        if (nCols < 4 || nCols > 16) {
            throw new JSONParseFormatException(
                "El fitxer conté un nombre no vàlid de columnes",
                JSONParseFormatException.ExceptionType.ILLEGAL_NUMBER
            );
        }

        /// Skip two lines
        in.nextLine();
        in.nextLine();
        List<PieceType> typeList = getListPieceTypes(in);

        /// Next two lines
        in.nextLine();
        String temp = getString(in.nextLine());
        List<String> initialPos = new ArrayList<>();

        if (!temp.equals("[]")) {
            initialPos = getListStrings(in);
        } else {
            throw new JSONParseFormatException(
                "El llista de posicions incials no pot estar buit.", 
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }
        
        if (illegalPieceName(initialPos, typeList)) {
            throw new JSONParseFormatException(
                "Les posicions inicials contenen una peça que no es troba al llistat de tipus de peces.",
                JSONParseFormatException.ExceptionType.ILLEGAL_TYPE
            );
        }

        int chessLimits = getInt(in.nextLine());
        if (chessLimits < 0) {
            System.err.println("En nombre límit d'escacs ha de ser superior a 0.");
            System.err.println("S'ha agafat el valor per defecte [3].");
        }
        int inactiveLimits = getInt(in.nextLine());
        if (inactiveLimits < 0) {
            System.err.println("En nombre límit de torns inactius ha de ser superior a 0.");
            System.err.println("S'ha agafat el valor per defecte [3].");    
        }

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
    /// @throws JSONParseFormatException If some of the file content is not in the correct format
    ///         or there is an incoherence
    public static Chess buildSavedChessGame(String fileLocation) throws FileNotFoundException, JSONParseFormatException {
        Scanner mainSc = new Scanner(new File(fileLocation));
        /// Skip first {
        mainSc.nextLine();
        /// Get the file location
        String configFileName = getString(mainSc.nextLine());
        if (configFileName.isEmpty()) {
            throw new JSONParseFormatException(
                "El fitxer de configuració no pot ser buit",
                JSONParseFormatException.ExceptionType.ILLEGAL_NAME
            );
        }

        /// LOADING CHESS CONFIGURATION FROM FILE
        Chess chess = buildChess(configFileName);

        /// INITIAL POSITIONS
        /// Read initial white positions
        List<Pair<Position, Piece>> whiteInitPos = new ArrayList<>();
        if (!getString(mainSc.nextLine()).equals("[]")) {
            whiteInitPos = getInitialPositionList(mainSc, chess.typeList(), PieceColor.White);
        } else {
            throw new JSONParseFormatException(
                "El llistat de peces blanques not pot ser buit.",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }
        /// Skip ],
        mainSc.nextLine();
        
        List<Pair<Position, Piece>> blackInitPos = new ArrayList<>();
        if (!getString(mainSc.nextLine()).equals("[]")) {
            whiteInitPos = getInitialPositionList(mainSc, chess.typeList(), PieceColor.Black);
        } else {
            throw new JSONParseFormatException(
                "El llistat de peces negres not pot ser buit.",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }
        /// Skip ],
        mainSc.nextLine();

        /// Check if last line has a result (which means that the match) has ended
        /// Skip lines
        /// Next turn
        mainSc.nextLine();
        /// Turn list - if there's, skip it
        if (!getString(mainSc.nextLine()).equals("[]")) {
            getTurnList(mainSc);
        } 

        /// Skip ],
        String finalResult = getString(mainSc.nextLine());
        if (finalResult.contains("GUANYEN")) {
            /// Game has ended
            throw new JSONParseFormatException(
                "La partida carregada ja ha finalitzat.",
                JSONParseFormatException.ExceptionType.END_OF_GAME
            );
        }

        /// Close scanner
        mainSc.close();

        /// Create Chess
        return new Chess(chess, whiteInitPos, blackInitPos);
    }

    /// @biref Gets the game developement
    /// @pre ---
    /// @post Reads the game developement from the file. Returns a pair containing a 
    ///       list of turns and the winning piece color. If @param forKnowledge is true,
    ///       returns the winning color. Otherwise, returns null.
    /// @throws JSONParseFormatException If the file contains an empty turn list
    public static Pair<List<Turn>, PieceColor> matchInformation(String fileLocation, boolean forKnowledge) 
        throws FileNotFoundException, JSONParseFormatException {
        Scanner in = new Scanner(new File(fileLocation));

        /// Skip 167 lines
        for (int i = 0; i < 167; i++) {
            in.nextLine();
        }

        /// Read turns
        List<Turn> turnList = new ArrayList<>();

        if (!getString(in.nextLine()).equals("[]")) {
            turnList = getTurnList(in);

            /// Skip ],
            in.nextLine();
        } else {
            throw new JSONParseFormatException(
                "La partida de la qual s'està intentant llegir informació no conté torns.",
                JSONParseFormatException.ExceptionType.EMPTY_LIST
            );
        }

        PieceColor tempColor = null;
        if (forKnowledge) {
            String s = getString(in.nextLine());
            tempColor = s.contains("BLANQUES") 
                ? PieceColor.White
                : (s.contains("NEGRES"))
                    ? PieceColor.Black
                    : null;
            if (tempColor == null) {
                throw new JSONParseFormatException(
                    "El color del guanyador no és vàlid. Ha de ser \"BLANQUES\" o \"NEGRES\".",
                    JSONParseFormatException.ExceptionType.ILLEGAL_COLOR
                );
            }
        }

        /// Close scanner
        in.close();
        
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
        return values.length < 2 ? "" : values[1].trim();
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
                System.err.println("El segon moviment queda exclòs.");
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
    /// @throws JSONParseFormatException If there's a piece in the list that does not exist as a piece type
    private static List<Pair<Position, Piece>> getInitialPositionList(Scanner fr, List<PieceType> pTypes,
            PieceColor color) throws JSONParseFormatException {
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
                throw new JSONParseFormatException(
                    "Una peça del llistat de posicions inicials no existeix als tipus de peça.",
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

            /// Validate turn result value
            switch (result) {
                case "ESCAC":
                case "ESCAC I MAT":
                case "TAULES PER REI OFEGAT":
                case "TAULES PER ESCAC CONTINU":
                case "TAULES PER INACCIÓ":
                case "TAULES SOL·LICITADES":
                case "TAULES ACCEPTADES":
                case "RENDICIÓ":
                case "AJORNAMENT":
                    /// End of game
                    break;
                default: {
                    if (result.contains("PROMOCIÓ")) {
                        /// TODO: Handle promotion

                    } else if (result.contains("ENROC")) {
                        /// TODO: Handle castling
                    } else {
                        System.err.println("El resultat d'un moviment no és vàlid. No es tindrà en compte.");
                        System.err.println("Resultat: " + result);
                        result = "";
                    }
                }
            }

            turnList.add(new Turn(color, move, result));
            s = fr.nextLine().trim();
        }

        return turnList;
    }

    /// @brief Checks if the name corresponds to a piece type
    /// @pre ---
    /// @post Returns true if the name does not exist in the @param types list
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
    ///       the @param temp movement
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
