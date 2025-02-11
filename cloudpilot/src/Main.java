import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The Main class is the entry point of the JavaFX application.
 * It loads the FXML file, sets up the stage, and displays the main window.
 */
public class Main extends Application {

    /**
     * The start method is the main entry point for all JavaFX applications.
     * It sets up the primary stage and loads the initial scene.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();

            // Set the title and scene
            primaryStage.setTitle("CloudPilot");
            primaryStage.setScene(new Scene(root, 1540, 800));

            // Add the icon
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));

            // Show the window
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method is the entry point for the Java application.
     * It launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
