import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    /// Default values
    private static final double MAX_BTN_WIDTH = 300.0;

    /// Game Start Options
    private Stage window;
    private Button defaultGameButton;
    private Button configuredGameButton;
    private Button loadGameButton;
    private Button exitGameButton;

    /// @brief Launches the application
    /// @pre ---
    /// @post Starts the GUI application
    public static void main(String[] args) {
        launch(args);
    }

    /// @brief Simulates a scene changing
    /// @pre ---
    /// @post Changes the scene title and content
    private void nextScene(String title) {
        window.setTitle(title);

        Text text = new Text(title);
        
        Button close = new Button();
        ItemBuilder.buildButton(close, "Go Back", null, ItemBuilder.BtnType.EXIT);
        close.setOnAction(e -> {
            buildMainScene();
        });

        VBox layout = new VBox();
        Collection<Node> children = new ArrayList<>();
        children.add(text);
        children.add(close);

        ItemBuilder.buildVBox(layout, 125.0, children);        

        Scene scene = new Scene(layout);
        window.setScene(scene);        
        window.show();
    }

    /// @brief Buils the main scene displaying a menu
    /// @pre ---
    /// @post Displays the 4 buttons of the menu
    private void buildMainScene() {
        VBox layout = new VBox();
        Collection<Node> children = new ArrayList<>();
        children.add(defaultGameButton);
        children.add(configuredGameButton);
        children.add(loadGameButton);
        children.add(exitGameButton);

        ItemBuilder.buildVBox(layout, 12.0, children);

        Scene mainScene = new Scene(layout);
        mainScene.getStylesheets().add("style.css");
        window.setScene(mainScene);
        window.show();
    }
    
    /// @brief Function to build the main menu buttons content
    /// @pre ---
    /// @post Sets the main menu buttons functionality and style
    private void setButtons() {
        defaultGameButton = new Button();
        ItemBuilder.buildButton(
            defaultGameButton,
            "Iniciar partida",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        defaultGameButton.setOnAction(e -> {
            nextScene("INICIA PARTIDA NORMAL");
        });

        configuredGameButton = new Button();
        ItemBuilder.buildButton(
            configuredGameButton,
            "Preconfigura una partida",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        configuredGameButton.setOnAction(e -> {
            nextScene("PARTIDA PRE-CONFIGURADA");
        });

        loadGameButton = new Button();
        ItemBuilder.buildButton(
            loadGameButton,
            "Carrega una partida",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.PRIMARY
        );
        loadGameButton.setOnAction(e -> {
            nextScene("PARTIDA CARREGADA");
        });

        exitGameButton = new Button();
        ItemBuilder.buildButton(
            exitGameButton,
            "Surt",
            MAX_BTN_WIDTH,
            ItemBuilder.BtnType.EXIT
        );
        exitGameButton.setOnAction(e -> {
            window.close();
        });

        /// Set CSS
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        window.setTitle("ESCACS");
        window.setWidth(600.0);
        window.setHeight(250.0);
        setButtons();

        buildMainScene();
    }
}