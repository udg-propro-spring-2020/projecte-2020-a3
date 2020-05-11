import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * @author Miquel de Domingo i Giralt
 * @file UIPiece.java
 * @class UIPiece
 * @brief Holds the information of a Piece in the UI mode
 * @details The piece will control all the events which allow it to move
 *          via mouse events
 */
public class UIPiece extends StackPane{
    private Piece piece;            ///< Color of the piece
    private int width;              ///< Tile width
    private double mouseX;          ///< X Coordinates of the mouse (pixels)
    private double oldX;            ///< X Old coordinate (pixels)
    private double mouseY;          ///< Y Coordinates of the mouse (pixels)
    private double oldY;            ///< Y Old coordinate (pixels)

    private static final String DEF_IMG_LOCATION = "./data/img/";       ///< Default image location

    UIPiece(Piece piece, int width, int x, int y) {
        this.piece = piece;
        this.width = width;
        
        /// Move the piece
        move(x, y);

        /// Add the piece image
        ImageView img = new ImageView(getImage());
        getChildren().add(img);

        /// Handle events
    }

    /// @brief Moves the piece to the center of the tile
    /// @pre 0 <= @param x && 0 <= @param y
    /// @post Moves the piece to the center of the tile
    public void move(int x, int y) {
        this.oldX = width * x;
        this.oldY = width * y;
        relocate(oldX, oldY);
    }

    /// @brief Returns the piece to its old position
    /// @pre ---
    /// @post Returns the piece to its old position
    public void cancelMove() {
        relocate(oldX, oldY);
    }

    /// @brief Returns the image of the piece
    /// @pre ---
    /// @post Returns the image of the piece
    private Image getImage() throws NullPointerException {
        Image img = null;

        try {
            img = new Image(
                new FileInputStream(
                    DEF_IMG_LOCATION + this.piece.type().colorImageLocation(
                        this.piece.color()
                    )
                )
            );
        } catch (FileNotFoundException e) {
            throw new NullPointerException("Error on finding the image");
        }

        return img;
    }
}