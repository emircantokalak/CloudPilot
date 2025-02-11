import java.io.IOException;
import java.sql.Connection;
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
 * The CreateProfileController class handles the creation of new employee profiles.
 * It verifies input data, checks for existing records, and inserts new profiles into the database.
 */
public class CreateProfileController {
    private long employeeId; // This value should be dynamically set based on the logged-in user.

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField employeeIdField;

    @FXML
    private Button createButton;

    @FXML
    private Button exitbutton;

    private Connection connection;

    /**
     * Initializes the controller with the database connection.
     */
    public void initData(Connection connection) {
        this.connection = connection;
    }

    /**
     * Handles the action to go back to the main page.
     */
    @FXML
    void handlegobackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        loadFXML("Main.fxml");
    }

    /**
     * Handles the action to create a new profile.
     * Validates input data, checks for existing records, and inserts a new profile into the database.
     */
    @FXML
    void handleCreateButton() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String role = roleField.getText();
        String password = passwordField.getText();
        String employeeIdStr = employeeIdField.getText();

        // Validate input data
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || role.isEmpty() || password.isEmpty() || employeeIdStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required. Please fill in all fields.");
        } else if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format. Please include an '@' symbol.");
        } else if (isEmailRegistered(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email is already registered. Please use a different email.");
        } else {
            try {
                long employeeId = Long.parseLong(employeeIdStr); // Convert employeeId to long

                if (employeeIdStr.length() != 11) {
                    throw new SQLException("Invalid Employee_ID: " + employeeIdStr + ". Employee_ID must be 11 digits.");
                }

                if (isEmployeeIdRegistered(employeeId)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Employee ID is already used. Please use a different ID.");
                } else {
                    // Insert new employee into the database
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO employee (Employee_ID, E_Name_First, E_Name_Last, Email, Role, Password) VALUES (?, ?, ?, ?, ?, ?)");
                    statement.setLong(1, employeeId);
                    statement.setString(2, firstName);
                    statement.setString(3, lastName);
                    statement.setString(4, email);
                    statement.setString(5, role);
                    statement.setString(6, password);
                    statement.executeUpdate();

                    // Inform user about successful profile creation
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Profile created successfully!");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Employee ID. Please enter a valid number.");
            } catch (SQLException e) {
                if (e.getMessage().contains("Invalid Employee_ID")) {
                    showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                } else {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create profile.");
                }
            }
        }
    }

    /**
     * Checks if the given email is already registered in the database.
     */
    private boolean isEmailRegistered(String email) {
        try {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM employee WHERE Email = ?");
            checkStatement.setString(1, email);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to check email.");
        }
        return false;
    }

    /**
     * Checks if the given employee ID is already registered in the database.
     */
    private boolean isEmployeeIdRegistered(long employeeId) {
        try {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM employee WHERE Employee_ID = ?");
            checkStatement.setLong(1, employeeId);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to check employee ID.");
        }
        return false;
    }

    /**
     * Validates the email format.
     */
    private boolean isValidEmail(String email) {
        return email.contains("@");
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

    /**
     * Loads the specified FXML file and shows it in a new stage.
     */
    private void loadFXML(String fxmlFileName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFileName));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
