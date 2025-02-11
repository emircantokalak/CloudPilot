import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The MainController class handles user login and navigation to the profile creation page.
 * It connects to a MySQL database to authenticate users.
 */
public class MainController {
    private long employeeId; // This value should be dynamically set based on the logged-in user.

    /**
     * Sets the employee ID.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createProfileButton;

    // MySQL connection parameters
    private final String url = "Your URL";
    private final String username = "Your Username";
    private final String password = "Your Password";

    private Connection connection;

    /**
     * Initializes the controller class and establishes a connection to the MySQL database.
     */
    @FXML
    void initialize() {
        try {
            // Connect to MySQL database
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the login action. Authenticates the user and navigates to the main page if successful.
     */
    @FXML
    void handleLoginButton(ActionEvent event) {
        String email = usernameField.getText();
        String password = passwordField.getText();

        // Show alert if email or password is empty
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Email and password must not be empty.");
            return;
        }

        try {
            // Query the database for the user
            PreparedStatement statement = connection.prepareStatement("SELECT Employee_ID FROM employee WHERE email = ? AND password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Login successful
                long employeeId = resultSet.getLong("Employee_ID"); // Change getInt to getLong

                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
                Parent root = loader.load();

                // Get the controller associated with the MainPage.fxml file
                MainPageController mainPageController = loader.getController();
                mainPageController.setEmployeeId(employeeId);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("MainPage");
                stage.show();
            } else {
                // Login failed
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action to navigate to the profile creation page.
     */
    @FXML
    void handleCreateProfileButton(ActionEvent event) {
        try {
            // Load the CreateProfile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateProfile.fxml"));
            Parent root = loader.load();
            Stage stage1 = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage1.close();

            // Get the controller associated with the CreateProfile.fxml file
            CreateProfileController controller = loader.getController();

            // Set the stage
            Stage stage2 = new Stage();
            stage2.setScene(new Scene(root));
            stage2.setTitle("Create Profile");

            // Pass the database connection to the CreateProfileController
            controller.initData(connection);

            // Show the stage
            stage2.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert with the specified type, title, and message.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
