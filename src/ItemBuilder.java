import java.util.Collection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/// @brief Modular class to keep a constant style along the application
public class ItemBuilder {
    /// CONSTANTS
    private static final String CSS_LOCATION = "./data/style.css";

    /// CSS CLASSNAME CONSTANTS
    private static final String BTN_PRIMARY = "btn-primary";
    private static final String BTN_SECONDARY = "btn-secondary";
    private static final String BTN_EXIT = "btn-exit";
    private static final String BTN_ROUNDED = "btn-rounded";

    /// @brief Defines the possible button types
    public static enum BtnType {
        PRIMARY, SECONDARY, EXIT, ROUNDED
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
            case ROUNDED:
                btn.getStyleClass().add(BTN_ROUNDED);
        }

    }

    /// @brief Builds a default VBox
    /// @pre @p layout has been initialized
    /// @post Sets the VBox with the desired properties
    public static void buildVBox(VBox layout, Double spacing, Collection<? extends Node> children) {
        layout.setSpacing((spacing == null) ? 12.0 : spacing);
        layout.getStylesheets().add(CSS_LOCATION);
        layout.setPadding(new Insets(16.0, 48.0, 16.0, 48.0));
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(children);
    }

    /// @brief Builds a pop up window and shows it 
    /// @pre ---
    /// @post Creates a pop up window with the given title and a centered label. If has
    ///       a button, this button will close the pop up
    public static Stage buildPopUp(String title, String text, boolean hasButton) {
        Stage popUp = new Stage();

        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.setTitle("Error");
        Label errText = new Label("File location error");

        VBox layout = new VBox(24);
        layout.getChildren().add(errText);
        layout.setAlignment(Pos.CENTER);
        layout.getStylesheets().add(CSS_LOCATION);

        if (hasButton) {
            Button closeBtn = new Button();
            buildButton(closeBtn, "EXIT", 75.0, BtnType.EXIT);
            closeBtn.setOnAction(e -> {
                popUp.close();
            });

            layout.getChildren().add(closeBtn);
        }
        
        popUp.setScene(new Scene(layout, 200, 100));

        return popUp;
    }
}