package salesmanagement.salesmanagement.SalesComponent;

import javafx.scene.control.Label;
import salesmanagement.salesmanagement.DataAccess.SQLConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Action implements SalesComponent {
    private int actionID;
    private LocalDateTime time;
    private String description;
    private Label result;

    private ComponentModified componentModified;
    private ActionCode actionCode;
    private ResultCode resultCode;
    private String componentModifiedID;

    public Action(ResultSet actionsSet) throws SQLException {
        actionID = actionsSet.getInt("actionID");
        time = actionsSet.getTimestamp("time").toLocalDateTime();
        description = actionsSet.getString("description");
        result = new Label(actionsSet.getString("result"));
        componentModifiedID = actionsSet.getString("componentModifiedID");
        if (result.getText().equals("SUCCESSFUL"))
            result.setStyle("-fx-text-fill: WHITE; -fx-alignment: center; -fx-font-weight: bold; -fx-background-color: rgba(117,217,117,0.91); -fx-pref-width: 120;");
        else if (result.getText().equals("FAILED"))
            result.setStyle("-fx-text-fill: WHITE; -fx-alignment: center; -fx-font-weight: bold; -fx-background-color: #d92c2c;-fx-pref-width: 120;");
    }

    public Action(String componentModifiedID, ComponentModified componentModified, ActionCode actionCode, ResultCode resultCode) {
        this.time = LocalDateTime.now();
        this.componentModifiedID = componentModifiedID;
        this.componentModified = componentModified;
        this.actionCode = actionCode;
        this.resultCode = resultCode;
    }

    public enum ActionCode {
        CREATE_NEW,
        EDIT,
        REMOVE
    }

    public enum ComponentModified {
        ORDER,
        PRODUCT,
        EMPLOYEE,
        PRODUCTLINES,
        CUSTOMER
    }


    public enum ResultCode {
        SUCCESSFUL,
        FAILED
    }

    public void pushAction(SQLConnection connection) {
        packActionMessage();
        String query = String.format("insert into activityLog (time, result, userID, description, componentModifiedID) " +
                        "VALUES ('%s', '%s', '%d', %s, %s)",
                time.toString(), resultCode.name(), connection.getUserID(),
                (description == null) ? "null" : "'" + description + "'",
                (componentModifiedID == null) ? "null" : "'" + componentModifiedID + "'");

        try {
            connection.updateQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void packActionMessage() {
        String prefix = null, postfix = null;
        if (actionCode == ActionCode.CREATE_NEW) {
            if (componentModified == ComponentModified.ORDER)
                prefix = "Create new ";
            else prefix = "Add new ";
        } else if (actionCode == ActionCode.EDIT) prefix = "Edit ";
        else if (actionCode == ActionCode.REMOVE) prefix = "Remove ";

        if (componentModified == ComponentModified.CUSTOMER) postfix = "Customer";
        else if (componentModified == ComponentModified.EMPLOYEE) postfix = "Employee";
        else if (componentModified == ComponentModified.PRODUCT) postfix = "Product";
        else if (componentModified == ComponentModified.PRODUCTLINES) postfix = "Productlines";
        else if (componentModified == ComponentModified.ORDER) postfix = "Order";

        if (actionCode == ActionCode.EDIT) postfix += "\\'s information";

        description = prefix + postfix;
    }

    public String getComponentModifiedID() {
        return componentModifiedID;
    }

    public void setComponentModifiedID(String componentModifiedID) {
        this.componentModifiedID = componentModifiedID;
    }

    public int getActionID() {
        return actionID;
    }

    public void setActionID(int actionID) {
        this.actionID = actionID;
    }

    public String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return time.format(formatter);
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Label getResult() {
        return result;
    }

    public void setResult(Label result) {
        this.result = result;
    }
}
