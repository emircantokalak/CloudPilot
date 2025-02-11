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
 * The EditProfileController class handles the editing of employee profiles.
 * It loads profile data from the database, allows the user to edit the details, and saves the changes back to the database.
 */
public class EditProfileController {

    @FXML
    private TextField profileId;

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
    private Button loadDataButton;

    private Profile profile;

    private Connection connection;

    private long employeeId;

    /**
     * Sets the employee ID and loads the profile data from the database.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
        profileId.setText(String.valueOf(employeeId)); // Set Profile ID to the employee ID
        profileId.setEditable(false); // Make the Employee ID field non-editable
        loadDataFromDatabase(); // Load the profile data from the database
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        // Initialize method is left empty as data will be loaded in setEmployeeId method
    }

    /**
     * Loads the profile data from the database when the loadDataButton is clicked.
     */
    @FXML
    void loadDataFromDatabase(ActionEvent event) {
        loadDataFromDatabase();
    }

    /**
     * Loads the profile data from the database based on the employee ID.
     */
    private void loadDataFromDatabase() {
        try {
            // Connect to the MySQL database
            connection = DriverManager.getConnection("Your URL", "Your Username", "Your Password");
            System.out.println("Connection established");

            if (connection != null) {
                // Get the employee ID from the profileId field
                long inputEmployeeId = Long.parseLong(profileId.getText());
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM employee WHERE Employee_ID = ?");
                statement.setLong(1, inputEmployeeId);
                System.out.println("Query prepared: SELECT * FROM employee WHERE Employee_ID = " + inputEmployeeId);
                ResultSet resultSet = statement.executeQuery();
                System.out.println("Query executed");

                if (resultSet.next()) {
                    System.out.println("Profile found");

                    // Create a Profile object with the data from the database
                    profile = new Profile(
                            resultSet.getString("Employee_ID"),
                            resultSet.getString("E_Name_First"),
                            resultSet.getString("E_Name_Last"),
                            resultSet.getString("Email"),
                            resultSet.getString("Role"),
                            resultSet.getString("Password")
                    );

                    // Set the data to the fields
                    profileId.setText(profile.getEmployeeID());
                    firstNameField.setText(profile.getFirstName());
                    lastNameField.setText(profile.getLastName());
                    emailField.setText(profile.getEmail());
                    roleField.setText(profile.getRole());
                    passwordField.setText(profile.getPassword());
                    System.out.println("Profile data set to fields");
                } else {
                    System.out.println("Profile not found");
                }

                // Close the connection
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }

    /**
     * Handles the action to edit the profile. Validates the input data and updates the profile in the database.
     */
    @FXML
    void handleEditButton(ActionEvent event) {
        // Get the edited data from the fields
        try {
            // New Employee ID entered
            String newEmployeeId = profileId.getText();

            if (!newEmployeeId.equals(String.valueOf(employeeId))) {
                showAlert(Alert.AlertType.ERROR, "Error", "You cannot change the Employee ID.");
                profileId.setText(String.valueOf(employeeId)); // Reset Employee ID to the original value
                return;
            }

            // Update the profile in the database
            connection = DriverManager.getConnection("Your URL", "Your Username", "Your Password");
            PreparedStatement statement = connection.prepareStatement("UPDATE employee SET E_Name_First = ?, E_Name_Last = ?, Email = ?, Role = ?, Password = ? WHERE Employee_ID = ?");
            statement.setString(1, firstNameField.getText());
            statement.setString(2, lastNameField.getText());
            statement.setString(3, emailField.getText());
            statement.setString(4, roleField.getText());
            statement.setString(5, passwordField.getText());
            statement.setLong(6, employeeId);
            int rowsAffected = statement.executeUpdate();

            // Check the number of affected rows
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile edited successfully!");
                // Update the Profile object
                profile = new Profile(
                        newEmployeeId,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailField.getText(),
                        roleField.getText(),
                        passwordField.getText()
                );
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to edit profile.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to edit profile.");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    /**
     * Exits the current page and loads the main page.
     */
    @FXML
    void exitpage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        loadFXML("MainPage.fxml");
    }

    /**
     * Loads the specified FXML file and shows it in a new stage.
     */
    private void loadFXML(String fxmlFileName) {
        // Load the specified FXML file and show it in a new stage
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();

            // Get the controller
            MainPageController mainPageController = loader.getController();
            mainPageController.setEmployeeId(employeeId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
