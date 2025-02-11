import java.sql.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Boarding class represents a boarding record for a passenger.
 * It includes information about the passenger, flight details, and additional services.
 */
public class Boarding {
    private final IntegerProperty boardingID;
    private final IntegerProperty passengerID;
    private final StringProperty seatNumber;
    private final IntegerProperty baggageAllowance;
    private final ObjectProperty<Date> departure;
    private final ObjectProperty<Date> arrival;
    private final StringProperty flightNumber;

    // Added service features
    private final BooleanProperty extraDrink;
    private final BooleanProperty extraMeal;
    private final BooleanProperty suitCenter;
    private final IntegerProperty extraLuggage;
    private final StringProperty serviceDescription;

    /**
     * Constructor to initialize all properties of the boarding.
     */
    public Boarding(int boardingID, int passengerID, String seatNumber, int baggageAllowance, Date departure, Date arrival, String flightNumber) {
        this.boardingID = new SimpleIntegerProperty(boardingID);
        this.passengerID = new SimpleIntegerProperty(passengerID);
        this.seatNumber = new SimpleStringProperty(seatNumber);
        this.baggageAllowance = new SimpleIntegerProperty(baggageAllowance);
        this.departure = new SimpleObjectProperty<>(departure);
        this.arrival = new SimpleObjectProperty<>(arrival);
        this.flightNumber = new SimpleStringProperty(flightNumber);

        // Initialize service features
        this.extraDrink = new SimpleBooleanProperty(false);
        this.extraMeal = new SimpleBooleanProperty(false);
        this.suitCenter = new SimpleBooleanProperty(false);
        this.extraLuggage = new SimpleIntegerProperty(0);
        this.serviceDescription = new SimpleStringProperty("");
    }

    /**
     * Overloaded constructor with default values for baggage allowance, departure, and arrival.
     */
    public Boarding(int boardingID, int passengerID, String seatNumber, String flightNumber) {
        this(boardingID, passengerID, seatNumber, 0, null, null, flightNumber);
    }

    private int employeeId;

    /**
     * Sets the ID of the employee handling the boarding.
     */
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Gets the boarding ID.
     */
    public int getBoardingID() {
        return boardingID.get();
    }

    public IntegerProperty boardingIDProperty() {
        return boardingID;
    }

    public void setBoardingID(int boardingID) {
        this.boardingID.set(boardingID);
    }

    /**
     * Gets the passenger ID.
     */
    public int getPassengerID() {
        return passengerID.get();
    }

    public IntegerProperty passengerIDProperty() {
        return passengerID;
    }

    public void setPassengerID(int passengerID) {
        this.passengerID.set(passengerID);
    }

    /**
     * Gets the seat number.
     */
    public String getSeatNumber() {
        return seatNumber.get();
    }

    public StringProperty seatNumberProperty() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber.set(seatNumber);
    }

    /**
     * Gets the baggage allowance.
     */
    public int getBaggageAllowance() {
        return baggageAllowance.get();
    }

    public IntegerProperty baggageAllowanceProperty() {
        return baggageAllowance;
    }

    public void setBaggageAllowance(int baggageAllowance) {
        this.baggageAllowance.set(baggageAllowance);
    }

    /**
     * Gets the departure date and time.
     */
    public Date getDeparture() {
        return departure.get();
    }

    public ObjectProperty<Date> departureProperty() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure.set(departure);
    }

    /**
     * Gets the arrival date and time.
     */
    public Date getArrival() {
        return arrival.get();
    }

    public ObjectProperty<Date> arrivalProperty() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival.set(arrival);
    }

    /**
     * Gets the flight number.
     */
    public String getFlightNumber() {
        return flightNumber.get();
    }

    public StringProperty flightNumberProperty() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber.set(flightNumber);
    }

    // Added getter and setter methods for the service features
    /**
     * Checks if extra drink service is included.
     */
    public boolean isExtraDrink() {
        return extraDrink.get();
    }

    public BooleanProperty extraDrinkProperty() {
        return extraDrink;
    }

    public void setExtraDrink(boolean extraDrink) {
        this.extraDrink.set(extraDrink);
    }

    /**
     * Checks if extra meal service is included.
     */
    public boolean isExtraMeal() {
        return extraMeal.get();
    }

    public BooleanProperty extraMealProperty() {
        return extraMeal;
    }

    public void setExtraMeal(boolean extraMeal) {
        this.extraMeal.set(extraMeal);
    }

    /**
     * Checks if suit center service is included.
     */
    public boolean isSuitCenter() {
        return suitCenter.get();
    }

    public BooleanProperty suitCenterProperty() {
        return suitCenter;
    }

    public void setSuitCenter(boolean suitCenter) {
        this.suitCenter.set(suitCenter);
    }

    /**
     * Gets the extra luggage amount.
     */
    public int getExtraLuggage() {
        return extraLuggage.get();
    }

    public IntegerProperty extraLuggageProperty() {
        return extraLuggage;
    }

    public void setExtraLuggage(int extraLuggage) {
        this.extraLuggage.set(extraLuggage);
    }

    /**
     * Gets the service description.
     */
    public String getServiceDescription() {
        return serviceDescription.get();
    }

    public StringProperty serviceDescriptionProperty() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription.set(serviceDescription);
    }
}
