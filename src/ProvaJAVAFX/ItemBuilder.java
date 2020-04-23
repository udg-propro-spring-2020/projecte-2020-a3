import java.util.Collection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/// @brief Modular class to keep a constant style along the application
public class ItemBuilder {
    /// CSS CLASSNAME CONSTANTS
    private static final String BTN_PRIMARY = "btn-primary";
    private static final String BTN_SECONDARY = "btn-secondary";
    private static final String BTN_EXIT = "btn-exit";

    /// @brief Defines the possible button types
    public static enum BtnType {
        PRIMARY, SECONDARY, EXIT,
    }

    /// @brief Builds a default button item
    /// @pre @p btn has been initialized
    /// @post Sets the button with the desired properties
    public static void buildButton(Button btn, String text, Double width, BtnType type) {
        btn.setText(text);
        btn.getStyleClass().add("btn");

        if (width != null) {
            btn.setMaxWidth(width);
        }
        /// If it is null, set it to default

        switch (type) {
            case PRIMARY:
                btn.getStyleClass().add(BTN_PRIMARY);
                break;
            case SECONDARY:
                btn.getStyleClass().add(BTN_SECONDARY);
                break;
            case EXIT:
                btn.getStyleClass().add(BTN_EXIT);
                break;
        }
    }

    /// @brief Builds a default VBox
    /// @pre @p layout has been initialized
    /// @post Sets the VBox with the desired properties
    public static void buildVBox(VBox layout, Double spacing, Collection<? extends Node> children) {
        layout.setSpacing((spacing == null) ? 12.0 : spacing);
        layout.getStylesheets().add("style.css");
        layout.setPadding(new Insets(16.0, 48.0, 16.0, 48.0));
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(children);
    }
}