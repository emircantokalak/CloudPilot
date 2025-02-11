import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Flight class represents a flight with details such as flight number,
 * departure and arrival locations, times, and gate information.
 */
public class Flight {
    private StringProperty flightNumber;
    private StringProperty departureLocation;
    private StringProperty arrivalLocation;
    private ObjectProperty<LocalDateTime> departureTime;
    private ObjectProperty<LocalDateTime> arrivalTime;
    private StringProperty gate;

    /**
     * Default constructor initializing properties with empty values.
     */
    public Flight() {
        this.flightNumber = new SimpleStringProperty();
        this.departureLocation = new SimpleStringProperty();
        this.arrivalLocation = new SimpleStringProperty();
        this.departureTime = new SimpleObjectProperty<>();
        this.arrivalTime = new SimpleObjectProperty<>();
        this.gate = new SimpleStringProperty();
    }

    // Getter and setter methods

    /**
     * Gets the flight number property.
     */
    public StringProperty flightNumberProperty() {
        return flightNumber;
    }

    /**
     * Gets the flight number.
     */
    public String getFlightNumber() {
        return flightNumber.get();
    }

    /**
     * Sets the flight number.
     */
    public void setFlightNumber(String flightNumber) {
        this.flightNumber.set(flightNumber);
    }

    /**
     * Gets the departure location property.
     */
    public StringProperty departureLocationProperty() {
        return departureLocation;
    }

    /**
     * Gets the departure location.
     */
    public String getDepartureLocation() {
        return departureLocation.get();
    }

    /**
     * Sets the departure location.
     */
    public void setDepartureLocation(String departureLocation) {
        this.departureLocation.set(departureLocation);
    }

    /**
     * Gets the arrival location property.
     */
    public StringProperty arrivalLocationProperty() {
        return arrivalLocation;
    }

    /**
     * Gets the arrival location.
     */
    public String getArrivalLocation() {
        return arrivalLocation.get();
    }

    /**
     * Sets the arrival location.
     */
    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation.set(arrivalLocation);
    }

    /**
     * Gets the departure time property.
     */
    public ObjectProperty<LocalDateTime> departureTimeProperty() {
        return departureTime;
    }

    /**
     * Gets the departure time.
     */
    public LocalDateTime getDepartureTime() {
        return departureTime.get();
    }

    /**
     * Sets the departure time.
     */
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime.set(departureTime);
    }

    /**
     * Gets the arrival time property.
     */
    public ObjectProperty<LocalDateTime> arrivalTimeProperty() {
        return arrivalTime;
    }

    /**
     * Gets the arrival time.
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime.get();
    }

    /**
     * Sets the arrival time.
     */
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime.set(arrivalTime);
    }

    /**
     * Gets the gate property.
     */
    public StringProperty gateProperty() {
        return gate;
    }

    /**
     * Gets the gate.
     */
    public String getGate() {
        return gate.get();
    }

    /**
     * Sets the gate.
     */
    public void setGate(String gate) {
        this.gate.set(gate);
    }
}
