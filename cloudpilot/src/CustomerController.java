import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * The CustomerController class handles the display, search, edit, and deletion of passengers.
 * It also manages the boarding information associated with each passenger.
 */
public class CustomerController {
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
    private TableView<Passenger> passengerTable;

    @FXML
    private TableView<Boarding> BoardingTable;

    @FXML
    private TableColumn<Passenger, Integer> passengerIDColumn;

    @FXML
    private TableColumn<Passenger, String> firstNameColumn;

    @FXML
    private TableColumn<Passenger, String> lastNameColumn;

    @FXML
    private TableColumn<Passenger, String> genderColumn;

    @FXML
    private TableColumn<Passenger, Date> birthDateColumn;

    @FXML
    private TableColumn<Passenger, String> phoneNumberColumn;

    @FXML
    private TableColumn<Boarding, Integer> B_PassengerIDColumn;

    @FXML
    private TableColumn<Boarding, Integer> BoardingIDColumn;

    @FXML
    private TableColumn<Boarding, String> Flight_numberColumn;

    @FXML
    private TableColumn<Boarding, String> seatNumberColumn;

    @FXML
    private TableColumn<Boarding, Boolean> extraDrinkColumn;

    @FXML
    private TableColumn<Boarding, Boolean> extraMealColumn;

    @FXML
    private TableColumn<Boarding, Boolean> suitCenterColumn;

    @FXML
    private TableColumn<Boarding, Integer> extraLuggageColumn;

    @FXML
    private TableColumn<Boarding, String> serviceDescriptionColumn;

    private Connection connection;

    /**
     * Initializes the controller class. Establishes a connection to the database and sets up the table columns.
     */
    @FXML   
    void initialize() {
        try {
            connection = DriverManager.getConnection("Your URL", "Your Username", "Your Password");

            // Set cell value factories to populate table columns
            passengerIDColumn.setCellValueFactory(cellData -> cellData.getValue().passengerIDProperty().asObject());
            firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
            lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
            genderColumn.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
            birthDateColumn.setCellValueFactory(cellData -> cellData.getValue().birthDateProperty());
            phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());

            B_PassengerIDColumn.setCellValueFactory(cellData -> cellData.getValue().passengerIDProperty().asObject());
            BoardingIDColumn.setCellValueFactory(cellData -> cellData.getValue().boardingIDProperty().asObject());
            Flight_numberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
            seatNumberColumn.setCellValueFactory(cellData -> cellData.getValue().seatNumberProperty());

            extraDrinkColumn.setCellValueFactory(cellData -> cellData.getValue().extraDrinkProperty());
            extraMealColumn.setCellValueFactory(cellData -> cellData.getValue().extraMealProperty());
            suitCenterColumn.setCellValueFactory(cellData -> cellData.getValue().suitCenterProperty());
            extraLuggageColumn.setCellValueFactory(cellData -> cellData.getValue().extraLuggageProperty().asObject());
            serviceDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().serviceDescriptionProperty());

            // Load passenger data into table
            loadPassengerData();
            loadBoardingData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for a passenger based on the entered ID in the search field.
     * If found, displays the passenger and their boarding details.
     */
    @FXML
    void search() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                String passengerQuery = "SELECT * FROM passenger WHERE Passenger_ID = ?";
                String boardingQuery = "SELECT * FROM boarding WHERE B_Passenger_ID = ?";
                PreparedStatement passengerStatement = connection.prepareStatement(passengerQuery);
                PreparedStatement boardingStatement = connection.prepareStatement(boardingQuery);
                passengerStatement.setInt(1, Integer.parseInt(searchText));
                boardingStatement.setInt(1, Integer.parseInt(searchText));
                ResultSet passengerResultSet = passengerStatement.executeQuery();
                ResultSet boardingResultSet = boardingStatement.executeQuery();

                ObservableList<Passenger> passengerList = FXCollections.observableArrayList();
                while (passengerResultSet.next()) {
                    Passenger passenger = new Passenger(
                            passengerResultSet.getInt("Passenger_ID"),
                            passengerResultSet.getString("P_Name_First"),
                            passengerResultSet.getString("P_Name_Last"),
                            passengerResultSet.getString("Gender"),
                            passengerResultSet.getDate("Birth_Date"),
                            passengerResultSet.getString("Phone_Number")
                    );
                    passengerList.add(passenger);
                }
                passengerTable.setItems(passengerList);

                ObservableList<Boarding> boardingList = FXCollections.observableArrayList();
                while (boardingResultSet.next()) {
                    Boarding boarding = new Boarding(
                            boardingResultSet.getInt("Boarding_id"),
                            boardingResultSet.getInt("B_Passenger_ID"),
                            boardingResultSet.getString("Seat_Number"),
                            boardingResultSet.getString("B_Flight_Number")
                    );
                    fetchServiceDetails(boarding);
                    boardingList.add(boarding);
                }
                BoardingTable.setItems(boardingList);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Passenger ID format. Please enter a valid number.");
            }
        } else {
            // If search field is empty, reload all data
            loadPassengerData();
            loadBoardingData();
        }
    }

    /**
     * Deletes the selected passenger and their associated records from the database.
     */
    @FXML
    void deletePassenger(ActionEvent event) {
        Passenger selectedPassenger = passengerTable.getSelectionModel().getSelectedItem();
        if (selectedPassenger != null) {
            try {
                // Retrieve the associated boarding IDs
                String selectBoardingQuery = "SELECT Boarding_id FROM boarding WHERE B_Passenger_ID = ?";
                PreparedStatement selectBoardingStatement = connection.prepareStatement(selectBoardingQuery);
                selectBoardingStatement.setInt(1, selectedPassenger.getPassengerID());
                ResultSet boardingResultSet = selectBoardingStatement.executeQuery();
                List<Integer> boardingIds = new ArrayList<>();
                while (boardingResultSet.next()) {
                    boardingIds.add(boardingResultSet.getInt("Boarding_id"));
                }

                // Delete service records associated with each boarding ID
                String deleteServicesQuery = "DELETE FROM services WHERE Boarding_ID = ?";
                PreparedStatement deleteServicesStatement = connection.prepareStatement(deleteServicesQuery);
                for (int boardingId : boardingIds) {
                    deleteServicesStatement.setInt(1, boardingId);
                    deleteServicesStatement.executeUpdate();
                }

                // Delete boarding records associated with the passenger
                String deleteBoardingQuery = "DELETE FROM boarding WHERE B_Passenger_ID = ?";
                PreparedStatement deleteBoardingStatement = connection.prepareStatement(deleteBoardingQuery);
                deleteBoardingStatement.setInt(1, selectedPassenger.getPassengerID());
                deleteBoardingStatement.executeUpdate();

                // Delete the passenger record
                String deletePassengerQuery = "DELETE FROM passenger WHERE Passenger_ID = ?";
                PreparedStatement deletePassengerStatement = connection.prepareStatement(deletePassengerQuery);
                deletePassengerStatement.setInt(1, selectedPassenger.getPassengerID());
                deletePassengerStatement.executeUpdate();

                // Reload data
                loadPassengerData();
                loadBoardingData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Edits the details of the selected passenger. Prompts the user for new information and updates the database.
     */
    @FXML
    void editPassenger(ActionEvent event) {
        Passenger selectedPassenger = passengerTable.getSelectionModel().getSelectedItem();
        if (selectedPassenger != null) {
            try {
                // Open dialog windows for new information
                TextInputDialog dialog = new TextInputDialog(selectedPassenger.getFirstName());
                dialog.setTitle("Edit Passenger");
                dialog.setHeaderText("Edit the details of the selected passenger");
                dialog.setContentText("First Name:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(firstName -> selectedPassenger.setFirstName(firstName));

                dialog = new TextInputDialog(selectedPassenger.getLastName());
                dialog.setContentText("Last Name:");
                result = dialog.showAndWait();
                result.ifPresent(lastName -> selectedPassenger.setLastName(lastName));

                dialog = new TextInputDialog(selectedPassenger.getGender());
                dialog.setContentText("Gender:");
                result = dialog.showAndWait();
                result.ifPresent(gender -> selectedPassenger.setGender(gender));

                dialog = new TextInputDialog(selectedPassenger.getBirthDate().toString());
                dialog.setContentText("Birth Date (YYYY-MM-DD):");
                result = dialog.showAndWait();
                result.ifPresent(birthDate -> selectedPassenger.setBirthDate(Date.valueOf(birthDate)));

                dialog = new TextInputDialog(selectedPassenger.getPhoneNumber());
                dialog.setContentText("Phone Number:");
                result = dialog.showAndWait();
                result.ifPresent(phoneNumber -> selectedPassenger.setPhoneNumber(phoneNumber));

                // Update the database with new information
                String updateQuery = "UPDATE passenger SET P_Name_First = ?, P_Name_Last = ?, Gender = ?, Birth_Date = ?, Phone_Number = ? WHERE Passenger_ID = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, selectedPassenger.getFirstName());
                updateStatement.setString(2, selectedPassenger.getLastName());
                updateStatement.setString(3, selectedPassenger.getGender());
                updateStatement.setDate(4, selectedPassenger.getBirthDate());
                updateStatement.setString(5, selectedPassenger.getPhoneNumber());
                updateStatement.setInt(6, selectedPassenger.getPassengerID());
                updateStatement.executeUpdate();

                // Reload data
                loadPassengerData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches the service details for the given boarding record.
     */
    private void fetchServiceDetails(Boarding boarding) throws SQLException {
        String serviceQuery = "SELECT * FROM services WHERE Boarding_ID = ?";
        PreparedStatement serviceStatement = connection.prepareStatement(serviceQuery);
        serviceStatement.setInt(1, boarding.getBoardingID());
        ResultSet serviceResultSet = serviceStatement.executeQuery();
        if (serviceResultSet.next()) {
            boarding.setExtraDrink(serviceResultSet.getBoolean("Extra_Drink"));
            boarding.setExtraMeal(serviceResultSet.getBoolean("Extra_Meal"));
            boarding.setSuitCenter(serviceResultSet.getBoolean("Suit_Center"));
            boarding.setExtraLuggage(serviceResultSet.getInt("Extra_Luggage"));
            boarding.setServiceDescription(serviceResultSet.getString("Service_Description"));
        } else {
            boarding.setExtraDrink(false);
            boarding.setExtraMeal(false);
            boarding.setSuitCenter(false);
            boarding.setExtraLuggage(0);
            boarding.setServiceDescription("");
        }
    }

    /**
     * Loads passenger data from the database into the passenger table.
     */
    private void loadPassengerData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM passenger");
            ObservableList<Passenger> passengerList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Passenger passenger = new Passenger(
                        resultSet.getInt("Passenger_ID"),
                        resultSet.getString("P_Name_First"),
                        resultSet.getString("P_Name_Last"),
                        resultSet.getString("Gender"),
                        resultSet.getDate("Birth_Date"),
                        resultSet.getString("Phone_Number")
                );
                passengerList.add(passenger);
            }
            passengerTable.setItems(passengerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads boarding data from the database into the boarding table.
     */
    private void loadBoardingData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM boarding");
            ObservableList<Boarding> boardingList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Boarding boarding = new Boarding(
                        resultSet.getInt("Boarding_id"),
                        resultSet.getInt("B_Passenger_ID"),
                        resultSet.getString("Seat_Number"),
                        resultSet.getString("B_Flight_Number")
                );
                fetchServiceDetails(boarding);
                boardingList.add(boarding);
            }
            BoardingTable.setItems(boardingList);
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
