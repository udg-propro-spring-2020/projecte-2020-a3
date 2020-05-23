import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/// @author Miquel de Domingo i Giralt
/// @file UIPiece.java
/// @class UIPiece
/// @brief Holds the information of a Piece in the UI mode
/// @details The piece will control all the events which allow it to move
///          via mouse events
public class UIPiece extends StackPane {
    private Piece _piece;               ///< Color of the piece
    private int _width;                 ///< Tile width
    private double _mouseX;             ///< X Coordinates of the mouse (pixels)
    private double _oldX;               ///< X Old coordinate (pixels)
    private double _mouseY;             ///< Y Coordinates of the mouse (pixels)
    private double _oldY;               ///< Y Old coordinate (pixels)

    private static final String DEF_IMG_LOCATION = "data/img/";       ///< Default image location

    /// @brief Default piece constructor
    /// @param piece Piece it will display
    /// @param width Width of the piece
    /// @param x X position  
    /// @param y Y position 
    UIPiece(Piece piece, int width, int x, int y) {
        this._piece = piece;
        this._width = width;
        
        // Move the piece
        move(x, y);

        // Add the piece image
        ImageView img = new ImageView(getImage());
        getChildren().add(img);
    }

    /// @brief Sets a double to the mouse X property
    /// @pre ---
    /// @post Sets a double to the mouse X property
    public void setMouseX(double value) {
        _mouseX = value;
    }

    /// @brief Sets a double to the mouse Y property
    /// @pre ---
    /// @post Sets a double to the mouse Y property
    public void setMouseY(double value) {
        _mouseY = value;
    }

    /// @brief Returns the old X value of the piece
    /// @pre ---
    /// @post Returns the old X value of the piece
    public double oldX() {
        return this._oldX;
    }

    /// @brief Returns the old Y value of the piece
    /// @pre ---
    /// @post Returns the old Y value of the piece
    public double oldY() {
        return this._oldY;
    }

    /// @brief Moves the piece to the center of the tile
    /// @pre 0 <= @p x && 0 <= @p y
    /// @post Moves the piece to the center of the tile
    public void move(int x, int y) {
        this._oldX = _width * x;
        this._oldY = _width * y;
        relocate(_oldX, _oldY);
    }

    /// @brief Returns the piece to its old position
    /// @pre ---
    /// @post Returns the piece to its old position
    public void cancelMove() {
        relocate(_oldX, _oldY);
    }

    /// @brief Returns the piece color
    /// @pre ---
    /// @post Returns the piece color
    public PieceColor color() {
        return this._piece.color();
    }

    /// @brief Returns the piece held by this
    /// @pre ---
    /// @post Returns the piece held by this
    public Piece piece() {
        return this._piece;
    }

    /// @brief Promotes the piece to the given one
    /// @pre ---
    /// @post Promotes the piece to the given one
    public void promoteType(Piece promoted) {
        this._piece = promoted;

        // Add the piece image
        ImageView img = new ImageView(getImage());
        // Remove last image
        getChildren().remove(getChildren().size() - 1);
        getChildren().add(img);
    }

    /// @brief Returns the image of the piece
    /// @pre ---
    /// @post Returns the image of the piece
    private Image getImage() throws NullPointerException {
        Image img = null;

        try {
            img = new Image(
                new FileInputStream(
                    DEF_IMG_LOCATION + this._piece.type().colorImageLocation(
                        this._piece.color()
                    )
                )
            );
        } catch (FileNotFoundException e) {
            throw new NullPointerException("Error on finding the image");
        }

        return img;
    }
}