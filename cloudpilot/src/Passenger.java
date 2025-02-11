import java.sql.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Passenger class represents a passenger with details such as
 * passenger ID, first name, last name, gender, birth date, and phone number.
 * It also includes a reference to a Boarding instance.
 */
public class Passenger {
    private final IntegerProperty passengerID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty gender;
    private final ObjectProperty<Date> birthDate;
    private final StringProperty phoneNumber;

    private Boarding boarding;

    /**
     * Constructs a new Passenger with the specified details.
     */
    public Passenger(int passengerID, String firstName, String lastName, String gender, Date birthDate, String phoneNumber) {
        this.passengerID = new SimpleIntegerProperty(passengerID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.gender = new SimpleStringProperty(gender);
        this.birthDate = new SimpleObjectProperty<>(birthDate);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    /**
     * Gets the passenger ID.
     */
    public int getPassengerID() {
        return passengerID.get();
    }

    /**
     * Returns the passenger ID property.
     */
    public IntegerProperty passengerIDProperty() {
        return passengerID;
    }

    /**
     * Sets the passenger ID.
     */
    public void setPassengerID(int passengerID) {
        this.passengerID.set(passengerID);
    }

    /**
     * Gets the first name.
     */
    public String getFirstName() {
        return firstName.get();
    }

    /**
     * Returns the first name property.
     */
    public StringProperty firstNameProperty() {
        return firstName;
    }

    /**
     * Sets the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    /**
     * Gets the last name.
     */
    public String getLastName() {
        return lastName.get();
    }

    /**
     * Returns the last name property.
     */
    public StringProperty lastNameProperty() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender.get();
    }

    /**
     * Returns the gender property.
     *
     * @return the gender property
     */
    public StringProperty genderProperty() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender.set(gender);
    }

    /**
     * Gets the birth date.
     *
     * @return the birth date
     */
    public Date getBirthDate() {
        return birthDate.get();
    }

    /**
     * Returns the birth date property.
     */
    public ObjectProperty<Date> birthDateProperty() {
        return birthDate;
    }

    /**
     * Sets the birth date.
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate.set(birthDate);
    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    /**
     * Returns the phone number property.
     *
     * @return the phone number property
     */
    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    /**
     * Sets the boarding details.
     */
    public void setBoarding(Boarding boarding) {
        this.boarding = boarding;
    }

    /**
     * Gets the boarding details.
     */
    public Boarding getBoarding() {
        return boarding;
    }
}
