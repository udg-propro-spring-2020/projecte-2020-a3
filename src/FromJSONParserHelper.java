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

    /// @brief Builds a chess with the given configuration
    /// @pre ---
    /// @post Creates a chess game with the given configuration form the JSON file
    public static Chess buildChess(String fileLocation) throws FileNotFoundException {
        Scanner mainSc = new Scanner(new File(fileLocation));
        /// Skip first {
        mainSc.nextLine();
        /// Get the file location
        String configFileName = getString(mainSc.nextLine());

        /// LOADING FILE CONFIGURATION FROM FILE
        Scanner configSc = new Scanner(new File(configFileName));
        /// Skip first {
        configSc.nextLine();
        int nRows = getInt(configSc.nextLine());
        int nCols = getInt(configSc.nextLine());

        /// Skip two lines
        configSc.nextLine();
        configSc.nextLine();
        List<PieceType> typeList = getListPieceTypes(configSc);

        /// Next two lines
        configSc.nextLine();
        configSc.nextLine();
        List<String> initialPos = getListStrings(configSc); // ? Needed

        int chessLimits = getInt(configSc.nextLine());
        int inactiveLimits = getInt(configSc.nextLine());

        /// Castlings
        List<Castling> castlings = new ArrayList<>();
        if (!getString(configSc.nextLine()).equals("[]")) {
            /// If castlings list is not empty
            castlings = getListCastlings(configSc);
        }
        
        /// Close scanner
        configSc.close();
        /// END OF FILE CONFIGURATION

        /// INITIAL POSITIONS
        /// Read initial white positions
        List<Pair<Position, Piece>> whiteInitPos = !getString(mainSc.nextLine()).equals("[]")
            ? getInitialPositionList(mainSc, typeList, PieceColor.White)    /// If not empty, read the list
            : new ArrayList<>();                                            /// If empty, create an empty list
        /// Skip ],
        mainSc.nextLine();

        /// Read initial white positions
        List<Pair<Position, Piece>> blackInitPos = !getString(mainSc.nextLine()).equals("[]")
            ? getInitialPositionList(mainSc, typeList, PieceColor.Black)    /// If not empty, read the list
            : new ArrayList<>();                                            /// If empty, create an empty list
        /// Skip ],
        mainSc.nextLine();

        /// Next turn
        PieceColor nextTurnColor = getString(mainSc.nextLine()).toLowerCase().equals("blanques") 
            ? PieceColor.White
            : PieceColor.Black;

        /// Read turns
        List<Turn> turnList = !getString(mainSc.nextLine()).equals("[]") 
            ? getTurnList(mainSc)                                           /// If not empty, read the list
            : new ArrayList<>();                                            /// If empty, create an empty list

        /// Next lines are not necessaruy
        /// Close scanner
        mainSc.close();

        /// Create Chess
        return new Chess(nRows, nCols, chessLimits, inactiveLimits, typeList, initialPos, castlings, whiteInitPos,
                blackInitPos, nextTurnColor, turnList);
    }

    /// @pre s == "x": y
    /// @post Returns the y value as an integer
    private static int getInt(String s) {
        /// Remove comas and "
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return Integer.valueOf(values[1].trim());
    }

    /// @pre s == "x": "y"
    /// @post Returns the y value as a String without the double quotes, commas and
    ///       trimmed
    private static String getString(String s) {
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return values[1].isEmpty() ? "" : values[1].trim();
    }

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

            mList.add(new Movement(x, y, capture, jump));
            s = fr.nextLine().trim();
        }
        return mList;
    }

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

    /// @pre ---
    /// @post Returns the JSON castling list which can be empty
    private static List<Castling> getListCastlings(Scanner fr) {
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

            cList.add(new Castling(aPiece, bPiece, stand, emptyMiddle));
            s = fr.nextLine().trim();
        }
        return cList;
    }

    /// @pre Scanner pointing at {
    /// @post Returns a list of paris like Pair<A, B> where A is the positions and B
    ///       the piece type.
    private static List<Pair<Position, Piece>> getInitialPositionList(Scanner fr, List<PieceType> pTypes,
            PieceColor color) {
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

            boolean moved = getString(fr.nextLine()).equals("true") ? true : false;

            pList.add(new Pair<Position, Piece>(pos, new Piece(type, moved, color)));
            s = fr.nextLine().trim();
        }

        return pList;
    }

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
}
