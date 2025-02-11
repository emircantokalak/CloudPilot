import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * The CheckinController class handles the check-in process for passengers. It
 * connects to the database, searches for passengers and flights, and manages
 * seat selection.
 */
public class CheckinController implements Initializable {
	private long employeeId;

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	@FXML
	private TextField flightNumberField;
	@FXML
	private TextField passengerIDField;
	@FXML
	private TextField fullNameField;
	@FXML
	private TextField passengerIDDisplay;
	@FXML
	private TextField flightNumberDisplay;
	@FXML
	private TextField boardingIDDisplay;
	@FXML
	private TextField gateDisplay;
	@FXML
	private TextField seatDisplay;
	@FXML
	private TextField departureInfoDisplay;
	@FXML
	private TextField LocationDisplay;

	@FXML
	private TextField arrivalInfoDisplay;

	@FXML
	private GridPane businessClassAnchorPane;

	@FXML
	private AnchorPane economyClassAnchorPane;

	private Connection connection;

	private Button lastSelectedButton = null;

	/**
	 * Initializes the controller class and sets up the database connection.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		connectToDatabase(); // Establish database connection

		// Adjust the AUTO_INCREMENT value correctly
		try {
			// Get the next auto-increment value
			String getMaxIdQuery = "SELECT MAX(Boarding_id) + 1 AS next_id FROM boarding";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(getMaxIdQuery);

			if (resultSet.next()) {
				int nextId = resultSet.getInt("next_id");

				// Set the auto-increment value
				String alterTableQuery = "ALTER TABLE boarding AUTO_INCREMENT = " + nextId;
				statement.execute(alterTableQuery);
			}

			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects to the database.
	 */
	private void connectToDatabase() {
		try {
			// Establish database connection
			String url = "Your URL";
			String username = "Your Username";
			String password = "Your Password";
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Database connection established.");
		} catch (SQLException e) {
			System.err.println("Error connecting to the database: " + e.getMessage());
		}
	}

	/**
	 * Loads the seat statuses for the given flight number.
	 */
	private void loadSeatStatuses(String flightNumber) {
		try {
			String query = "SELECT Seat_Number FROM boarding WHERE B_Flight_Number = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, flightNumber);

			ResultSet resultSet = preparedStatement.executeQuery();

			// Color all seats green first
			resetSeats();

			// Then color occupied seats red
			while (resultSet.next()) {
				String seatNumber = resultSet.getString("Seat_Number");
				Button seatButton = (Button) businessClassAnchorPane.lookup("#seat" + seatNumber);
				if (seatButton != null) {
					seatButton.setStyle("-fx-background-color: #F1948A; -fx-border-color: black;"); // Color red
					seatButton.setDisable(true); // Disable selection
				}
			}

			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets all seats to be green and selectable.
	 */
	private void resetSeats() {
		for (Node node : businessClassAnchorPane.getChildren()) {
			if (node instanceof Button) {
				Button seatButton = (Button) node;
				seatButton.setStyle("-fx-background-color: #58D68D; -fx-border-color: black;"); // Color green
				seatButton.setDisable(false); // Enable selection
			}
		}
	}

	/**
	 * Searches for a passenger based on the entered passenger ID.
	 */
	@FXML
	public void searchPassenger(ActionEvent event) {
		String passengerID = passengerIDField.getText().trim();
		try {
			String query = "SELECT * FROM passenger WHERE Passenger_ID = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, passengerID);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String name = resultSet.getString("P_Name_First");
				String surname = resultSet.getString("P_Name_Last");
				fullNameField.setText(name + " " + surname);
				passengerIDDisplay.setText(passengerID);
			} else {
				System.out.println("Passenger not found.");
			}

			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Searches for a flight based on the entered flight number and updates the UI
	 * with the flight details.
	 */
	@FXML
	void searchFlight(ActionEvent event) {
		String flightNumber = flightNumberField.getText().trim();
		String passengerID = passengerIDField.getText().trim();
		try {
			String query = "SELECT * FROM flight WHERE Flight_number = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, flightNumber);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				gateDisplay.setText(resultSet.getString("gate"));
				String departure = resultSet.getString("Departure_location");
				String destination = resultSet.getString("Arrival_Location");
				departureInfoDisplay.setText(resultSet.getString("F_Departure"));
				arrivalInfoDisplay.setText(resultSet.getString("F_Arrival"));

				flightNumberDisplay.setText(flightNumber);
				LocationDisplay.setText(departure + " - " + destination);
				boardingIDDisplay.setText(passengerID + "" + flightNumber.substring(2));

				// Load seat statuses when flight info is found
				loadSeatStatuses(flightNumber);

			} else {
				System.out.println("Flight not found.");
			}

			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new check-in record for the passenger.
	 */
	@FXML
	void createCheckIn(ActionEvent event) {
		String passengerID = passengerIDField.getText().trim();
		String flightNumber = flightNumberField.getText().trim();
		String seatNumber = seatDisplay.getText().trim();
		String departureInfo = departureInfoDisplay.getText().trim();
		String arrivalInfo = arrivalInfoDisplay.getText().trim();

		try {
			// Prevent multiple check-ins for the same passenger on the same flight
			String checkQuery = "SELECT * FROM boarding WHERE B_Passenger_ID = ? AND B_Flight_Number = ?";
			PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
			checkStatement.setString(1, passengerID);
			checkStatement.setString(2, flightNumber);

			ResultSet checkResult = checkStatement.executeQuery();

			if (checkResult.next()) {
				showAlert(Alert.AlertType.ERROR, "Check-In Error",
						"This passenger has already checked in for this flight.");
				return;
			}

			// Prevent multiple passengers from occupying the same seat on the same flight
			String seatCheckQuery = "SELECT * FROM boarding WHERE B_Flight_Number = ? AND Seat_Number = ?";
			PreparedStatement seatCheckStatement = connection.prepareStatement(seatCheckQuery);
			seatCheckStatement.setString(1, flightNumber);
			seatCheckStatement.setString(2, seatNumber);

			ResultSet seatCheckResult = seatCheckStatement.executeQuery();

			if (seatCheckResult.next()) {
				showAlert(Alert.AlertType.ERROR, "Check-In Error", "This seat is already taken for this flight.");
				return;
			}

			// Perform check-in
			String query = "INSERT INTO boarding (B_Passenger_ID, B_Flight_Number, Seat_Number, B_Departure, B_Arrival) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, passengerID);
			preparedStatement.setString(2, flightNumber);
			preparedStatement.setString(3, seatNumber);
			preparedStatement.setTimestamp(4, Timestamp.valueOf(departureInfo));
			preparedStatement.setTimestamp(5, Timestamp.valueOf(arrivalInfo));

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Check-In Success", "Check-in was successfully completed.");
				// Update seat statuses
				loadSeatStatuses(flightNumber);
			} else {
				showAlert(Alert.AlertType.ERROR, "Check-In Error", "Check-in failed. Please try again.");
			}

			preparedStatement.close();
			checkStatement.close();
			seatCheckStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the seat selection action. Highlights the selected seat and updates
	 * the seat display.
	 */
	@FXML
	void handleSeatSelection(ActionEvent event) {
		Button seatButton = (Button) event.getSource();
		String selectedSeat = seatButton.getText();

		// Reset the previously selected seat to its original color
		if (lastSelectedButton != null && !lastSelectedButton.isDisabled()) {
			lastSelectedButton.setStyle("-fx-background-color: #58D68D; -fx-border-color: black;"); // Reset to green
		}

		// Highlight the selected seat
		seatButton.setStyle("-fx-background-color: #FEB558; -fx-border-color: black;"); // Highlight in orange
		lastSelectedButton = seatButton;
		seatDisplay.setText(selectedSeat);
	}

	/**
	 * Handles the action to load seat statuses and highlight available seats.
	 */
	@FXML
	void handleSelect(ActionEvent event) {
		loadSeatStatuses(flightNumberField.getText().trim());
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

	/**
	 * Displays an alert with the specified type, title, and message.
	 */
	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
