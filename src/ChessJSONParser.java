import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @class ChessJSONParser
 * @brief Parses the given file with the chess configuration to a chess object
 */
public class ChessJSONParser {
    /// @pre ---
    /// @post Creates a chess game with the given configuration
    public static Chess buildChess(String fileLocation) throws FileNotFoundException {
        Scanner fr = new Scanner(new File(fileLocation));

        /// Skip first {
        fr.nextLine();
        int nRows = getInt(fr.nextLine());
        int nCols = getInt(fr.nextLine());

        /// Skip two lines
        fr.nextLine();
        fr.nextLine();
        ArrayList<Piece> pList = getListPieces(fr);

        /// Next two lines
        fr.nextLine();
        fr.nextLine();
        ArrayList<String> initialPos = getListPositions(fr);

        int chessLimits = getInt(fr.nextLine());
        int inactiveLimits = getInt(fr.nextLine());

        /// Castlings
        ArrayList<Castling> castlings = new ArrayList<>();
        if (!getString(fr.nextLine()).equals("[]")) {
            /// If castlings list is not empty
            castlings = getListCastlings(fr);
        }

        return new Chess(nRows, nCols, chessLimits, inactiveLimits, pList, initialPos, castlings);
    }

    /// @pre s == "x": y
    /// @post Returns the y value as an integer
    private static int getInt(String s) {
        /// Remove comas and "
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return Integer.valueOf(values[1].trim());
    }

    /// @pre s == "x": "y"
    /// @post Returns the y value as a String without the double quotes
    private static String getString(String s) {
        String[] values = s.replace(",", "").replace("\"", "").trim().split(":");
        return values[1].trim();
    }

    /// @pre Last line read == "["
    /// @post Returns the JSON movements list and the scanner poiting at the end of
    /// the line where the list ends.
    private static ArrayList<Movement> getListMovements(Scanner fr) {
        String s = fr.nextLine().trim();
        ArrayList<Movement> mList = new ArrayList<>();
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
    /// the line where the list ends.
    private static ArrayList<String> getListPositions(Scanner fr) {
        ArrayList<String> posList = new ArrayList<>();
        String s = fr.nextLine().trim();
        while (!s.equals("],")) {
            posList.add(s.replace("\"", "").replace(",", ""));
            s = fr.nextLine().trim();
        }

        return posList;
    }

    /// @pre The JSON list is not empty
    /// @post Returns the JSON pieces list and the scanner pointing at the end of
    /// the line where the list ends.
    private static ArrayList<Piece> getListPieces(Scanner fr) {
        ArrayList<Piece> pList = new ArrayList<>();
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
            ArrayList<Movement> movements = getListMovements(fr);

            /// Initial Movements
            /// Skip ],
            fr.nextLine();
            ArrayList<Movement> initMovements = new ArrayList<>();
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

            pList.add(
                    new Piece(name, symbol, wImage, bImage, value, promotable, invulnerable, movements, initMovements));

            s = fr.nextLine().trim();
        }
        return pList;
    }

    private static ArrayList<Castling> getListCastlings(Scanner fr) {
        /// Skip {
        fr.nextLine();
        ArrayList<Castling> cList = new ArrayList<>();
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
}
