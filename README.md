# JavaFX Project

## Requirements
- **JavaFX** must be installed and properly configured.
- **JDBC Connector JAR** is required for database connectivity.
- Correct **VM arguments** must be set for JavaFX to work.

## Database Setup
- The project does not include a pre-configured database.
- Users need to create and set up their own database.
- Ensure that the correct database schema and necessary tables are created before running the project.

## How to Run
1. Add the **JDBC Connector JAR** to your project's classpath.
2. Ensure JavaFX libraries are correctly referenced.
3. Set the required **VM arguments**:
   ```
   --module-path "path-to-javafx-lib" --add-modules javafx.controls,javafx.fxml
   ```
4. Create and configure the database as needed.
5. Run the project from your IDE or command line.

## Notes
- If you encounter any issues with database connectivity, verify your JDBC URL, username, and password.
- Make sure the JavaFX runtime is properly installed.

Enjoy working with this JavaFX project!
