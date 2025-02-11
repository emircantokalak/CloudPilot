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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The CreateCustomerController class handles the creation of new customers.
 * It connects to the database, checks for existing records, and inserts new customer data.
 */
public class CreateCustomerController {
    private long employeeId; // This value should be dynamically set based on the logged-in user.

    /**
     * Sets the employee ID.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private DatePicker birthDatePicker;

    private Connection connection;

    /**
     * Initializes the controller class. Adds example data to the gender ComboBox.
     */
    @FXML
    void initialize() {
        // Adding example data to the ComboBox
        genderComboBox.getItems().addAll("Male", "Female", "Other");
    }

    /**
     * Handles the create button action. Collects input data, validates it, 
     * and inserts a new customer into the database.
     */
    @FXML
    void handleCreateButton(ActionEvent event) {
        // Get the content of the TextFields
        String id = idField.getText();
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String gender = genderComboBox.getValue();
        String birthDate = birthDatePicker.getValue() != null ? birthDatePicker.getValue().toString() : "";
        String phone = phoneField.getText();

        // Check for empty fields
        if (id.isEmpty() || name.isEmpty() || lastName.isEmpty() || gender == null || birthDate.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled out.");
            return;
        }

        // Connect to the MySQL database
        try {
            connection = DriverManager.getConnection("Your URL", "Your Username", "Your Password");

            // Check if the phone number already exists
            if (isPhoneNumberExists(phone)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Phone number already exists.");
                return;
            }

            // Check if the ID already exists
            if (isIdExists(id)) {
                showAlert(Alert.AlertType.ERROR, "Error", "ID number already exists.");
                return;
            }

            // Insert a new customer into the database
            PreparedStatement statement = connection.prepareStatement("INSERT INTO passenger (Passenger_ID, P_Name_First, P_Name_Last, Gender, Birth_Date, Phone_Number) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, lastName);
            statement.setString(4, gender);
            statement.setString(5, birthDate);
            statement.setString(6, phone);

            // Execute the query and check the number of affected rows
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer created successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer.");
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer.");
        }
    }

    /**
     * Checks if the given phone number already exists in the database.
     */
    private boolean isPhoneNumberExists(String phone) throws SQLException {
        PreparedStatement checkPhoneStatement = connection.prepareStatement("SELECT COUNT(*) FROM passenger WHERE Phone_Number = ?");
        checkPhoneStatement.setString(1, phone);
        ResultSet resultSet = checkPhoneStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }

    /**
     * Checks if the given ID already exists in the database.
     */
    private boolean isIdExists(String id) throws SQLException {
        PreparedStatement checkIdStatement = connection.prepareStatement("SELECT COUNT(*) FROM passenger WHERE Passenger_ID = ?");
        checkIdStatement.setString(1, id);
        ResultSet resultSet = checkIdStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
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
        FXMLLoader loader = loadFXML("MainPage.fxml");
        if (loader != null) {
            MainPageController mainPageController = loader.getController();
            mainPageController.setEmployeeId(employeeId);
        }
    }

    /**
     * Loads the specified FXML file and returns the FXMLLoader.
     */
    private FXMLLoader loadFXML(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
