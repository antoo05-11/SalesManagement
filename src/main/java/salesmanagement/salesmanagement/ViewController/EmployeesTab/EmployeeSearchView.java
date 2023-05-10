package salesmanagement.salesmanagement.ViewController.EmployeesTab;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import salesmanagement.salesmanagement.ViewController.ViewController;

public class EmployeeSearchView extends ViewController {

    @FXML
    private ComboBox<?> accessibilityComboBox;

    @FXML
    private TextField emailTextField;

    @FXML
    private VBox employeeFilterBox;

    @FXML
    private TableView<?> employeesTable;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private ComboBox<?> statusComboBox;

    @FXML
    void clearAll(MouseEvent event) {

    }

    @FXML
    void search(MouseEvent event) {

    }

}
