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
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * The servicesController class handles fetching and saving services related to a boarding ID.
 * It connects to a MySQL database to perform these operations.
 */
public class servicesController {
    private long employeeId; // This value should be dynamically set based on the logged-in user.

    /**
     * Sets the employee ID.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private TextField boardingIdField;

    @FXML
    private RadioButton extraDrinkRadioButton;

    @FXML
    private RadioButton extraMealRadioButton;

    @FXML
    private RadioButton extraLuggageRadioButton;

    @FXML
    private RadioButton suitCenterRadioButton;

    @FXML
    private TextArea serviceDescriptionField;

    @FXML
    private Button fetchBoardingsButton;

    @FXML
    private Button saveButton;

    /**
     * Establishes a connection to the MySQL database.
     */
    private Connection connect() {
        String url = "Your URL";
        String user = "Your Username";
        String password = "Your Password";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the action of fetching services based on the boarding ID entered.
     */
    @FXML
    private void handleFetchBoardingsButtonAction() {
        String boardingId = boardingIdField.getText().trim();

        if (!boardingId.isEmpty()) {
            String servicesQuery = "SELECT * FROM services WHERE Boarding_ID = ?";

            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(servicesQuery)) {
                pstmt.setString(1, boardingId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    extraDrinkRadioButton.setSelected(rs.getBoolean("Extra_Drink"));
                    extraMealRadioButton.setSelected(rs.getBoolean("Extra_Meal"));
                    suitCenterRadioButton.setSelected(rs.getBoolean("Suit_Center"));
                    extraLuggageRadioButton.setSelected(rs.getInt("Extra_Luggage") == 2);
                    serviceDescriptionField.setText(rs.getString("Service_Description"));
                } else {
                    clearFields();
                    showAlert("No Services Found", "No services found for the entered Boarding ID.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Input Error", "Please enter a valid Boarding ID.");
        }
    }

    /**
     * Handles the action of saving services based on the entered details.
     */
    @FXML
    private void handleSaveButtonAction() {
        String boardingId = boardingIdField.getText().trim();
        boolean extraDrink = extraDrinkRadioButton.isSelected();
        boolean extraMeal = extraMealRadioButton.isSelected();
        boolean suitCenter = suitCenterRadioButton.isSelected();
        int extraLuggage = extraLuggageRadioButton.isSelected() ? 2 : 1;
        String serviceDescription = serviceDescriptionField.getText();

        if (!boardingId.isEmpty()) {
            String query = "INSERT INTO services (Boarding_ID, Extra_Drink, Extra_Meal, Suit_Center, Extra_Luggage, Service_Description) VALUES (?, ?, ?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE Extra_Drink = VALUES(Extra_Drink), Extra_Meal = VALUES(Extra_Meal), Suit_Center = VALUES(Suit_Center), Extra_Luggage = VALUES(Extra_Luggage), Service_Description = VALUES(Service_Description)";

            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, boardingId);
                pstmt.setBoolean(2, extraDrink);
                pstmt.setBoolean(3, extraMeal);
                pstmt.setBoolean(4, suitCenter);
                pstmt.setInt(5, extraLuggage);
                pstmt.setString(6, serviceDescription);

                pstmt.executeUpdate();

                showAlert("Success", "Services updated successfully.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Input Error", "Please enter a valid Boarding ID.");
        }
    }

    /**
     * Exits the current page and navigates to the main page.
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
     * Loads the specified FXML file and shows it in a new stage.
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

    /**
     * Displays an alert with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears all the input fields.
     */
    private void clearFields() {
        extraDrinkRadioButton.setSelected(false);
        extraMealRadioButton.setSelected(false);
        suitCenterRadioButton.setSelected(false);
        extraLuggageRadioButton.setSelected(false);
        serviceDescriptionField.clear();
    }
}
