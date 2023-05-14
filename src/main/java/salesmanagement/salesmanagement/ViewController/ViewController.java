package salesmanagement.salesmanagement.ViewController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import salesmanagement.salesmanagement.DataAccess.SQLConnection;
import salesmanagement.salesmanagement.Utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ViewController implements Initializable {
    @FXML
    protected StackPane root;
    @FXML
    protected ProgressIndicator loadingIndicator;

    protected static SQLConnection sqlConnection;
    protected Stage stage;
    protected ViewController parentController;
    protected static UserRight userRight;
    protected boolean rightSet = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setVisible(false);
    }

    @FXML
    public void close() {
        root.setVisible(false);
    }

    @FXML
    public void show() {
        if (stage == null) {
            stage = (Stage) root.getScene().getWindow();
        }
        root.setVisible(true);
    }

    public static void setSqlConnection(SQLConnection sqlConnection) {
        if (ViewController.sqlConnection == null)
            ViewController.sqlConnection = sqlConnection;
    }

    public static void setUserRight(String userRight) {
        if (userRight.equalsIgnoreCase("employee")) ViewController.userRight = UserRight.EMPLOYEE;
        else if (userRight.equalsIgnoreCase("manager")) ViewController.userRight = UserRight.MANAGER;
        else if (userRight.equalsIgnoreCase("hr")) ViewController.userRight = UserRight.HR;
    }

    public static UserRight getUserRight() {
        return userRight;
    }

    final public void setParentController(ViewController parentController) {
        this.parentController = parentController;
    }

    final public ProgressIndicator getLoadingIndicator() {
        return loadingIndicator;
    }

    final public StackPane getRoot() {
        return root;
    }

    @FXML
    protected void resetData() {
        for (Node node : Utils.getAllNodes(root)) {
            if (node instanceof TextField) ((TextField) node).setText("");
            if (node instanceof ComboBox<?>) {
                ((ComboBox<String>) node).setValue("All");
            }
            if (node instanceof DatePicker) ((DatePicker) node).setValue(null);
        }
    }

    @FXML
    protected void lockData(boolean lock) {
        for (Node node : Utils.getAllNodes(root)) {
            if (!node.isDisable()) {
                if (node instanceof TextField) node.setDisable(lock);
                if (node instanceof ComboBox<?>) node.setDisable(lock);
                if (node instanceof DatePicker) node.setDisable(lock);
            }
        }
    }
}
