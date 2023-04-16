package salesmanagement.salesmanagement.SalesComponent;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import salesmanagement.salesmanagement.ImageController;
import salesmanagement.salesmanagement.SQLConnection;
import salesmanagement.salesmanagement.scenecontrollers.MainSceneController;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * @since 1.3
 */
public class Employee {
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private Text status;
    private String phone;
    private Text name;
    private String email;
    private String officeCode;
    private int reportsTo;
    private String jobTitle;
    private String username;
    private String password;
    private ImageView avatar = new ImageView();
    private static SQLConnection sqlConnection;
    private MainSceneController mainSceneController;
    private HBox action = new HBox();
    private String gender;
    private LocalDate birthDate;
    private boolean mailVerified;
private String phoneCode;
private LocalDate lastWorkingDate;
private LocalDate joiningDate;
    /**
     * This constructor is used to create an employee object
     * for content storage purposes only,
     * without affecting database connection variables.
     */
    public Employee(ResultSet employeeRecord, MainSceneController mainSceneController) {
        try {
            this.mainSceneController = mainSceneController;

            employeeNumber = employeeRecord.getInt("employeeNumber");
            lastName = employeeRecord.getString("lastName");
            firstName = employeeRecord.getString("firstName");
            email = employeeRecord.getString("email");
            officeCode = employeeRecord.getString("officeCode");
            reportsTo = employeeRecord.getInt("reportsTo");
            jobTitle = employeeRecord.getString("jobTitle");
            name = new Text(lastName + " " + firstName);
            status = new Text(employeeRecord.getString("status"));
            phone = employeeRecord.getString("phone");
            username = employeeRecord.getString("username");
            password = employeeRecord.getString("password");
            gender = employeeRecord.getString("gender");
            mailVerified = employeeRecord.getBoolean("mailVerified");
            phoneCode = employeeRecord.getString("phoneCode");
            joiningDate = employeeRecord.getDate("joiningDate").toLocalDate();
            lastWorkingDate = employeeRecord.getDate("lastWorkingDate").toLocalDate();
            try {
                birthDate = employeeRecord.getDate("birthDate").toLocalDate();
            }
            catch (SQLException e) {

            }
            status.setStyle("-fx-background-color: GREEN;");
            status.setFill(Color.WHITE);

            name.setOnMouseClicked(event -> {
                mainSceneController.displayEmployeeInfoBox(this);
            });
            name.setFill(Color.web("#329cfe"));
            name.setCursor(Cursor.HAND);

            try {
                ResultSet rs = sqlConnection.getDataQuery("SELECT avatar FROM employees WHERE employeeNumber = " + employeeNumber);
                if (rs.next()) {
                    InputStream is = rs.getBinaryStream("avatar");
                    Image image;
                    if (is == null) image = ImageController.getImage("sample_avatar.jpg");
                    else image = new Image(is);
                    avatar.setImage(image);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Operation is a hbox include
        JFXButton removeButton = new JFXButton();
        ImageView removeImg = new ImageView(ImageController.getImage("remove.png"));
        removeImg.setFitWidth(20);
        removeImg.setFitHeight(20);
        removeButton.setGraphic(removeImg);

        JFXButton editButton = new JFXButton();
        ImageView editImg = new ImageView(ImageController.getImage("edit.png"));
        editImg.setFitWidth(20);
        editImg.setFitHeight(20);
        editButton.setGraphic(editImg);

        action.getChildren().add(editButton);
        action.getChildren().add(removeButton);

        removeButton.setOnMouseClicked(event -> removeEmployee());

        editButton.setOnMouseClicked(event -> mainSceneController.editEmployees(this));
    }

    public void editEmployeeInfo() {

    }

    /**
     * This function appears to be removing an employee from a database,
     * and it does so by showing a confirmation dialog box to the user.
     * The dialog box contains a label, a text field, and a message
     * asking the user if they are sure they want to delete the employee.
     * If the user confirms that they want to delete the employee,
     * the function will remove the employee from a list and then
     * execute a SQL command to delete the employee from the database.
     * <p>
     * Overall, the code appears to be well-written and organized.
     * The use of a dialog box to confirm the deletion of the employee
     * adds an extra layer of safety, ensuring that the user does not
     * accidentally delete an employee. However, without knowing the
     * context in which this code is being used, it is difficult
     * to fully assess the quality of the code.
     */
    private void removeEmployee() {

        Dialog dialog = new Dialog<>();
        //  dialog.getDialogPane(mainSceneController.getEmployeeOperationPane());

        VBox vBox = new VBox();
        Label a = new Label("abc");
        JFXTextField b = new JFXTextField("abc");
        vBox.getChildren().addAll(Arrays.asList(a, b));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setGraphic(vBox);
        alert.setTitle("Modifying Confirmation!");
        alert.setContentText(this.toString());
        alert.setHeaderText("You are deleting this employee permanently: ");
        alert.initOwner(mainSceneController.getStage());
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);

        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                mainSceneController.getEmployees().remove(this);
                MainSceneController.haveChangeInEmployeesTab = true;
                sqlConnection.updateQuery("DELETE FROM employees WHERE employeeNumber = " + employeeNumber);
            }
        });
    }

    /**
     * This Employee's initializer allows initialization when the user logs into the application.
     * This also means that when entering the main screen of the application, this class is only
     * used once. It is reused when logging out to the login page. The connection to the data
     * storage server is saved as private static within this class to use the connection variable
     * for other employee objects. This means that employees created by other initialization methods
     * use this connection and do not create new connections.
     */
    public Employee(SQLConnection sqlConnection, int employeeNumber) {
        if (sqlConnection != null) Employee.sqlConnection = sqlConnection;
        this.employeeNumber = employeeNumber;
        String query = "select * from employees where employeeNumber = " + employeeNumber;
        ResultSet resultSet = Employee.sqlConnection.getDataQuery(query);
        try {
            if (resultSet.next()) {
                lastName = resultSet.getString("lastName");
                firstName = resultSet.getString("firstName");
                email = resultSet.getString("email");
                officeCode = resultSet.getString("officeCode");
                reportsTo = resultSet.getInt("reportsTo");
                jobTitle = resultSet.getString("jobTitle");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Employee(int employeeNumber) {
        this(null, employeeNumber);
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }

    /**
     * This code defines a toString() method to return a string
     * representation of the current Employee object. Specifically,
     * the method uses the String class to create a string that
     * combines the properties of the Employee object, including
     * the employee number, full name, email address, and office code.
     * Finally, the method returns this string as the result.
     * Defining a toString() method is very useful for displaying
     * object information in a readable and user-friendly way.
     */
    @Override
    public String toString() {
        String employeeRecord = String.format("Employee: %d \nFull Name: %s \nEmail: %s \nOffice Code: %s", employeeNumber, getFullName(), email, officeCode);
        return employeeRecord;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public int getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(int reportsTo) {
        this.reportsTo = reportsTo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public HBox getAction() {
        return action;
    }

    public void setAction(HBox action) {
        this.action = action;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }

    public Text getStatus() {
        return status;
    }

    public void setStatus(Text status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isMailVerified() {
        return mailVerified;
    }

    public void setMailVerified(boolean mailVerified) {
        this.mailVerified = mailVerified;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public LocalDate getLastWorkingDate() {
        return lastWorkingDate;
    }

    public void setLastWorkingDate(LocalDate lastWorkingDate) {
        this.lastWorkingDate = lastWorkingDate;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
}
