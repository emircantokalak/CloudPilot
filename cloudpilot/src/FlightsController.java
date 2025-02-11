import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * The FlightsController class handles the display, search, edit, and deletion of flights.
 * It also manages the navigation to other pages such as creating flights.
 */
public class FlightsController {
    private long employeeId; // This value should be dynamically set based on the logged-in user.

    /**
     * Sets the employee ID.
     */
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Flight> flightTable;

    @FXML
    private TableColumn<Flight, String> flightNumberColumn;

    @FXML
    private TableColumn<Flight, String> departureLocationColumn;

    @FXML
    private TableColumn<Flight, LocalDateTime> departureTimeColumn;

    @FXML
    private TableColumn<Flight, String> arrivalLocationColumn;

    @FXML
    private TableColumn<Flight, LocalDateTime> arrivalTimeColumn;

    @FXML
    private TableColumn<Flight, String> gateColumn;

    private Connection connection;

    /**
     * Initializes the controller class. Establishes a connection to the database and sets up the table columns.
     */
    @FXML
    void initialize() {
        try {
            connection = DriverManager.getConnection("Your URL", "Your Username", "Your Password");

            // Set cell value factories to populate table columns
            flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
            departureLocationColumn.setCellValueFactory(cellData -> cellData.getValue().departureLocationProperty());
            departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
            arrivalLocationColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalLocationProperty());
            arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
            gateColumn.setCellValueFactory(cellData -> cellData.getValue().gateProperty());

            // Load flight data into table
            loadFlightData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for flights based on the text entered in the search field.
     */
    @FXML
    void searchFlight() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                String query = "SELECT * FROM flight WHERE Flight_number LIKE ? OR Departure_Location LIKE ? OR Arrival_Location LIKE ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, "%" + searchText + "%");
                statement.setString(2, "%" + searchText + "%");
                statement.setString(3, "%" + searchText + "%");
                ResultSet resultSet = statement.executeQuery();
                ObservableList<Flight> flightList = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    Flight flight = new Flight();
                    flight.setFlightNumber(resultSet.getString("Flight_number"));
                    flight.setDepartureLocation(resultSet.getString("Departure_Location"));
                    flight.setDepartureTime(resultSet.getTimestamp("F_Departure").toLocalDateTime());
                    flight.setArrivalLocation(resultSet.getString("Arrival_Location"));
                    flight.setArrivalTime(resultSet.getTimestamp("F_Arrival").toLocalDateTime());
                    flight.setGate(resultSet.getString("gate"));
                    flightList.add(flight);
                }
                flightTable.setItems(flightList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // If search field is empty, reload all flights
            loadFlightData();
        }
    }

    /**
     * Loads flight data from the database into the flight table.
     */
    private void loadFlightData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM flight");
            ObservableList<Flight> flightList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Flight flight = new Flight();
                flight.setFlightNumber(resultSet.getString("Flight_number"));
                flight.setDepartureLocation(resultSet.getString("Departure_Location"));
                flight.setDepartureTime(resultSet.getTimestamp("F_Departure").toLocalDateTime());
                flight.setArrivalLocation(resultSet.getString("Arrival_Location"));
                flight.setArrivalTime(resultSet.getTimestamp("F_Arrival").toLocalDateTime());
                flight.setGate(resultSet.getString("gate"));
                flightList.add(flight);
            }
            flightTable.setItems(flightList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
     * Navigates to the create flights page.
     */
    @FXML
    void gotocreateflights(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        loadFXML("createflights.fxml");
    }

    /**
     * Deletes the selected flight from the database.
     */
    @FXML
    void deleteFlight(ActionEvent event) {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                // Delete related rows from the boarding table
                String deleteBoardingQuery = "DELETE FROM boarding WHERE B_Flight_Number = ?";
                PreparedStatement deleteBoardingStatement = connection.prepareStatement(deleteBoardingQuery);
                deleteBoardingStatement.setString(1, selectedFlight.getFlightNumber());
                deleteBoardingStatement.executeUpdate();

                // Delete the flight from the flight table
                String deleteFlightQuery = "DELETE FROM flight WHERE Flight_number = ?";
                PreparedStatement deleteFlightStatement = connection.prepareStatement(deleteFlightQuery);
                deleteFlightStatement.setString(1, selectedFlight.getFlightNumber());
                deleteFlightStatement.executeUpdate();

                // Reload data
                loadFlightData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Edits the details of the selected flight. Prompts the user for new information and updates the database.
     */
    @FXML
    void editFlight(ActionEvent event) {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                // Open dialog windows for new information
                TextInputDialog dialog = new TextInputDialog(selectedFlight.getFlightNumber());
                dialog.setTitle("Edit Flight");
                dialog.setHeaderText("Edit the details of the selected flight");
                dialog.setContentText("Flight Number:");
                Optional<String> result = dialog.showAndWait();
                if (!result.isPresent() || result.get().trim().isEmpty()) {
                    showAlert("Error", "Flight Number cannot be empty.");
                    return;
                }
                String newFlightNumber = result.get();

                dialog = new TextInputDialog(selectedFlight.getDepartureLocation());
                dialog.setContentText("Departure Location:");
                result = dialog.showAndWait();
                if (!result.isPresent() || result.get().trim().isEmpty()) {
                    showAlert("Error", "Departure Location cannot be empty.");
                    return;
                }
                String newDepartureLocation = result.get();

                dialog = new TextInputDialog(selectedFlight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                dialog.setContentText("Departure Time (YYYY-MM-DD HH:MM:SS):");
                result = dialog.showAndWait();
                if (!result.isPresent() || result.get().trim().isEmpty()) {
                    showAlert("Error", "Departure Time cannot be empty.");
                    return;
                }
                LocalDateTime newDepartureTime = LocalDateTime.parse(result.get(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                dialog = new TextInputDialog(selectedFlight.getArrivalLocation());
                dialog.setContentText("Arrival Location:");
                result = dialog.showAndWait();
                if (!result.isPresent() || result.get().trim().isEmpty()) {
                    showAlert("Error", "Arrival Location cannot be empty.");
                    return;
                }
                String newArrivalLocation = result.get();

                dialog = new TextInputDialog(selectedFlight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                dialog.setContentText("Arrival Time (YYYY-MM-DD HH:MM:SS):");
                result = dialog.showAndWait();
                if (!result.isPresent() || result.get().trim().isEmpty()) {
                    showAlert("Error", "Arrival Time cannot be empty.");
                    return;
                }
                LocalDateTime newArrivalTime = LocalDateTime.parse(result.get(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                dialog = new TextInputDialog(selectedFlight.getGate());
                dialog.setContentText("Gate:");
                result = dialog.showAndWait();
                if (!result.isPresent() || result.get().trim().isEmpty()) {
                    showAlert("Error", "Gate cannot be empty.");
                    return;
                }
                String newGate = result.get();

                // Update the flight number in the boarding table
                String updateBoardingQuery = "UPDATE boarding SET B_Flight_Number = ? WHERE B_Flight_Number = ?";
                PreparedStatement updateBoardingStatement = connection.prepareStatement(updateBoardingQuery);
                updateBoardingStatement.setString(1, newFlightNumber);
                updateBoardingStatement.setString(2, selectedFlight.getFlightNumber());
                updateBoardingStatement.executeUpdate();

                // Update the existing record in the flight table
                String updateFlightQuery = "UPDATE flight SET Flight_number = ?, Departure_Location = ?, F_Departure = ?, Arrival_Location = ?, F_Arrival = ?, gate = ? WHERE Flight_number = ?";
                PreparedStatement updateFlightStatement = connection.prepareStatement(updateFlightQuery);
                updateFlightStatement.setString(1, newFlightNumber);
                updateFlightStatement.setString(2, newDepartureLocation);
                updateFlightStatement.setTimestamp(3, java.sql.Timestamp.valueOf(newDepartureTime));
                updateFlightStatement.setString(4, newArrivalLocation);
                updateFlightStatement.setTimestamp(5, java.sql.Timestamp.valueOf(newArrivalTime));
                updateFlightStatement.setString(6, newGate);
                updateFlightStatement.setString(7, selectedFlight.getFlightNumber());
                updateFlightStatement.executeUpdate();

                // Reload data
                loadFlightData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays an alert with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
