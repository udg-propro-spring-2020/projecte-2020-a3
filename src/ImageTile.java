import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Miquel de Domingo i Giralt
 * @file ImageTile.java
 * @class ImageTile
 * @brief An image base tile of the chess game
 */
public class ImageTile extends ImageView {
    /**
     * @param img Tile image
     * @param width Tile width
     */
    ImageTile(Image img, int width) {
        setImage(img);
        setFitWidth(width);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
    }    
}