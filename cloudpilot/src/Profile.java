import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Profile class represents an employee profile with details such as
 * employee ID, first name, last name, email, role, and password.
 */
public class Profile {
    private final StringProperty employeeID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final StringProperty role;
    private final StringProperty password;

    /**
     * Constructs a new Profile with the specified details.
     */
    public Profile(String employeeID, String firstName, String lastName, String email, String role, String password) {
        this.employeeID = new SimpleStringProperty(employeeID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
        this.password = new SimpleStringProperty(password);
    }

    // Getter and setter methods

    /**
     * Gets the employee ID.
     */
    public String getEmployeeID() {
        return employeeID.get();
    }

    /**
     * Sets the employee ID.
     */
    public void setEmployeeID(String employeeID) {
        this.employeeID.set(employeeID);
    }

    /**
     * Returns the employee ID property.
     */
    public StringProperty employeeIDProperty() {
        return employeeID;
    }

    /**
     * Gets the first name.
     */
    public String getFirstName() {
        return firstName.get();
    }

    /**
     * Sets the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    /**
     * Returns the first name property.
     */
    public StringProperty firstNameProperty() {
        return firstName;
    }

    /**
     * Gets the last name.
     */
    public String getLastName() {
        return lastName.get();
    }

    /**
     * Sets the last name.
     */
    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    /**
     * Returns the last name property.
     */
    public StringProperty lastNameProperty() {
        return lastName;
    }

    /**
     * Gets the email.
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Sets the email.
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Returns the email property.
     */
    public StringProperty emailProperty() {
        return email;
    }

    /**
     * Gets the role.
     */
    public String getRole() {
        return role.get();
    }

    /**
     * Sets the role.
     */
    public void setRole(String role) {
        this.role.set(role);
    }

    /**
     * Returns the role property.
     */
    public StringProperty roleProperty() {
        return role;
    }

    /**
     * Gets the password.
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Sets the password.
     */
    public void setPassword(String password) {
        this.password.set(password);
    }

    /**
     * Returns the password property.
     */
    public StringProperty passwordProperty() {
        return password;
    }
}
