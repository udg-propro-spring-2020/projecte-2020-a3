import java.util.ArrayList;
import java.util.Collection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/// @author Miquel de Domingo 
/// @file ItemBuilder.java
/// @class ItemBuilder
/// @brief Modular class to keep a constant style along the application
public class ItemBuilder {
    /// CONSTANTS
    private static final String CSS_LOCATION = "./data/styles/style.css";      ///< Default CSS file location

    /// CSS CLASSNAME CONSTANTS
    private static final String BTN = "btn";                            ///< Default button class name
    private static final String BTN_PRIMARY = "btn-primary";            ///< Primary button class name
    private static final String BTN_SECONDARY = "btn-secondary";        ///< Secondary button class name
    private static final String BTN_ACCENT = "btn-accent";              ///< Secondary button class name
    private static final String BTN_EXIT = "btn-exit";                  ///< Exit button class name
    private static final String BTN_SHADOWLESS = "btn-shadowless";                      ///< Pane class name
    private static final String TEXT_TITLE = "title";                   ///< Title class name
    private static final String PANE = "pane";                          ///< Pane class name
    private static final String PANE_BG = "pane_with_bg";               ///< Pane class name
    private static final String SPACER = "spacer";                      ///< Spacer class name

    /// @brief Defines the possible button types
    public static enum BtnType {
        PRIMARY, SECONDARY, EXIT, ACCENT
    }

    /// @brief Builds a title
    /// @pre @p text cannot be null
    /// @post Returns a text item with the title CSS styling of title
    public static Text buildTitle(String text) {
        Text l = new Text(text);
        l.getStyleClass().add(TEXT_TITLE);

        return l;
    }

    /// @brief Builds a default button item
    /// @pre @p btn has been initialized
    /// @post Sets the button with the desired properties
    public static void buildButton(Button btn, String text, Double width, BtnType type) {
        btn.setText(text);
        btn.getStyleClass().add(BTN);

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
            case ACCENT:
                btn.getStyleClass().add(BTN_ACCENT);
                break;
            case EXIT:
                btn.getStyleClass().add(BTN_EXIT);
                break;
        }

    }

    /// @brief Builds a default VBox
    /// @pre @p layout has been initialized
    /// @post Sets the VBox with the desired properties. If @p hasBackground is true
    ///       adds the pane class
    public static VBox buildVBox(Double spacing, Collection<? extends Node> children, boolean hasBackground) {
        VBox layout = new VBox();
        
        layout.getStyleClass().add(PANE);
        if (hasBackground) {
            layout.getStyleClass().add(PANE_BG);
        }
        layout.setSpacing((spacing == null) ? 24.0 : spacing);
        layout.setPadding(new Insets(16.0, 12.0, 16.0, 12.0));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(children);

        return layout;
    }

    /// @brief Builds a default HBox
    /// @pre ---
    /// @post Returns a HBox with the desired properties. If @p hasBackground is true
    ///       adds the pane class
    public static HBox buildHBox(Double spacing, Collection<? extends Node> children, boolean hasBackground) {
        HBox layout = new HBox();

        layout.getStyleClass().add(PANE);
        if (hasBackground) {
            layout.getStyleClass().add(PANE_BG);
        }
        layout.setSpacing((spacing == null) ? 24.0 : spacing);
        layout.setPadding(new Insets(16.0, 48.0, 16.0, 48.0));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(children);

        return layout;
    }

    /// @brief Builds a spacer with the given/default height
    /// @pre @p spacing > 0
    /// @post Returns a Region as an empty spacer
    public static Region buildSpacer(Double spacing) {
        Region spacer = new Region();
        
        spacer.setMinHeight((spacing == null) ? 150 : spacing);
        spacer.getStyleClass().add(SPACER);

        return spacer;
    }
    
    /// @brief Builds a BorderPane with the given nodes to its correspondent position
    /// @pre ---
    /// @post Returns a BorderPane with the nodes to its correspondent positions. Adds the
    ///       default CSS class to the pane
    public static BorderPane buildBorderPane(Node center, Node top, Node left, Node right, Node bottom) {
        BorderPane bp = new BorderPane(center, top, right, bottom, left);
        bp.getStylesheets().add(CSS_LOCATION);

        return bp;
    }

    /// @brief Builds a default Scene
    /// @pre @p layout has been initialized
    /// @post Returns a scene of the current layout and with the default
    ///       CSS styles
    public static Scene buildScene(Pane layout) {
        Scene scene = new Scene(layout);
        scene.getStylesheets().add(CSS_LOCATION);
        
        return scene;
    }

    /// @brief Builds a pop up window 
    /// @pre ---
    /// @post Creates a pop up window with the given title and a centered label. If has
    ///       a button, this button will close the pop up
    public static Stage buildPopUp(String title, String text, boolean hasButton) {
        Stage popUp = new Stage();
        Collection<Node> list = new ArrayList<>();

        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.setTitle(title);
        Label errText = new Label(text);
        errText.setTextAlignment(TextAlignment.CENTER);
        list.add(errText);

        if (hasButton) {
            Button closeBtn = new Button();
            buildButton(closeBtn, "CLOSE", 125.0, BtnType.EXIT);
            closeBtn.setOnAction(e -> {
                popUp.close();
            });

            list.add(closeBtn);
        }
        
        VBox layout = buildVBox(12.0, list, false);
        layout.getStylesheets().add(CSS_LOCATION);
        popUp.setScene(new Scene(layout));
        popUp.setWidth(300.0);

        return popUp;
    }

    /// @brief Builds a yes/no pop up window
    /// @pre ---
    /// @post Creates a pop up window with the given title, a centered label and 
    ///       the button collection
    public static boolean buildConfirmationPopUp(String title, String text) {
        Stage popUp = new Stage();
        Collection<Node> buttons = new ArrayList<>();
        Collection<Node> list = new ArrayList<>();

        // Set title
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.setTitle(title);

        // Set text
        Label errText = new Label(text);
        errText.setTextAlignment(TextAlignment.CENTER);
        list.add(errText);

        Button yesBtn = new Button();
        // Create Yes / No buttons
        ItemBuilder.buildButton(
            yesBtn, 
            "YES", 
            75.0, 
            ItemBuilder.BtnType.SECONDARY
        );
        yesBtn.setOnAction(e -> {
            yesBtn.setUserData(true);
            popUp.close();
        });
        yesBtn.getStyleClass().add(BTN_SHADOWLESS);
        buttons.add(yesBtn);
            
        Button noBtn = new Button();
        ItemBuilder.buildButton(
            noBtn, 
            "NO", 
            75.0, 
            ItemBuilder.BtnType.EXIT
        );
        noBtn.setOnAction(e -> {
            noBtn.setUserData(false);
            popUp.close();
        });
        noBtn.getStyleClass().add(BTN_SHADOWLESS);
        buttons.add(noBtn);
        
        // Button layout
        HBox buttonsBox = buildHBox(12.0, buttons, false);
        list.add(buttonsBox);
        
        // Pop up layout
        VBox layout = buildVBox(12.0, list, false);
        layout.getStylesheets().add(CSS_LOCATION);
        popUp.setScene(new Scene(layout));
        
        // Show and wait
        popUp.showAndWait();
        // Return pressed value
        if (yesBtn.getUserData() != null) {
            return true;
        } else {
            return false;
        }
    }
}