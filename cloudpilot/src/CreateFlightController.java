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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The CreateFlightController class handles the creation of new flights.
 * It connects to the database, validates input data, and inserts new flight records.
 */
public class CreateFlightController {
    private long employeeId; // This value should be dynamically set based on the logged-in user.

    /**
     * Sets the employee ID.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private TextField flightNumberField;

    @FXML
    private TextField originField;

    @FXML
    private TextField destinationField;

    @FXML
    private TextField departureTimeField;

    @FXML
    private TextField arrivalTimeField;

    @FXML
    private TextField gateNumberField;

    @FXML
    private Button createFlightButton;

    @FXML
    private Button cancelButton;

    private final String url = "Your URL";
    private final String user = "Your Username";
    private final String password = "Your Password";

    /**
     * Handles the create flight action. Collects input data, validates it, 
     * and inserts a new flight record into the database.
     */
    @FXML
    private void createFlight() {
        String flightNumber = flightNumberField.getText();
        String origin = originField.getText();
        String destination = destinationField.getText();
        String departureTime = departureTimeField.getText();
        String arrivalTime = arrivalTimeField.getText();
        String gateNumber = gateNumberField.getText();

        // Check for empty fields
        if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty() || departureTime.isEmpty() || arrivalTime.isEmpty() || gateNumber.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
        } else {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Perform assertion to check for conflicting gate
                if (hasConflictingGate(conn, departureTime, gateNumber)) {
                    showAlert("Error", "Conflict: Another flight with the same departure time already exists at gate " + gateNumber);
                    return;
                }

                // Connect to the database and insert the flight data
                String query = "INSERT INTO flight (Flight_number, F_Departure, F_Arrival, Departure_Location, Arrival_Location, gate) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, flightNumber);
                    pstmt.setString(2, departureTime);
                    pstmt.setString(3, arrivalTime);
                    pstmt.setString(4, origin);
                    pstmt.setString(5, destination);
                    pstmt.setString(6, gateNumber);
                    pstmt.executeUpdate();

                    showAlert("Success", "Flight created successfully.");
                    clearFields();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to create flight. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to create flight. Please try again.");
            }
        }
    }

    /**
     * Handles the cancel action. Clears all input fields.
     */
    @FXML
    private void cancel() {
        clearFields();
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        flightNumberField.clear();
        originField.clear();
        destinationField.clear();
        departureTimeField.clear();
        arrivalTimeField.clear();
        gateNumberField.clear();
    }

    /**
     * Displays an alert with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Exits the current page and loads the flights page.
     */
    @FXML
    void exitpage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        loadFXML("Flights.fxml");
    }

    /**
     * Loads the specified FXML file and returns the FXMLLoader.
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

    /**
     * Checks if there are any conflicting gates for the given departure time.
     */
    private boolean hasConflictingGate(Connection conn, String departureTime, String gateNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM flight WHERE F_Departure = ? AND gate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, departureTime);
            pstmt.setString(2, gateNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        }
        return false;
    }
}
