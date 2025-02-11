import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The MainPageController class handles navigation between different pages of the application.
 * It uses the employee ID to maintain user context across different scenes.
 */
public class MainPageController {

    private long employeeId; // This value should be dynamically set based on the logged-in user.

    /**
     * Sets the employee ID.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Navigates to the Check-In page.
     */
    @FXML
    void goToCheckIn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = loadFXML("CheckIn.fxml");
        if (loader != null) {
            CheckinController controller = loader.getController();
            controller.setEmployeeId(employeeId);
        }
    }

    /**
     * Navigates to the Flights page.
     */
    @FXML
    void goToFlights(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = loadFXML("Flights.fxml");
        if (loader != null) {
            FlightsController controller = loader.getController();
            controller.setEmployeeId(employeeId);
        }
    }

    /**
     * Navigates to the Services page.
     */
    @FXML
    void goToServices(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = loadFXML("Services.fxml");
        if (loader != null) {
            servicesController controller = loader.getController();
            controller.setEmployeeId(employeeId);
        }
    }

    /**
     * Navigates to the Customer page.
     */
    @FXML
    void goToCustomer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = loadFXML("customer.fxml");
        if (loader != null) {
            CustomerController controller = loader.getController();
            controller.setEmployeeId(employeeId);
        }
    }

    /**
     * Navigates to the Profile page.
     */
    @FXML
    void goToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the profile.fxml file
            EditProfileController editProfileController = loader.getController();
            editProfileController.setEmployeeId(employeeId); // Set the employee ID

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();

            // Close the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the Create Customer page.
     */
    @FXML
    void goToCreateCustomer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = loadFXML("createcustomer.fxml");
        if (loader != null) {
            CreateCustomerController controller = loader.getController();
            controller.setEmployeeId(employeeId);
        }
    }

    /**
     * Exits the current page and navigates to the main page.
     */
    @FXML
    void exitpage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = loadFXML("Main.fxml");
        if (loader != null) {
            MainController controller = loader.getController();
            controller.setEmployeeId(employeeId);
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
}
