package salesmanagement.salesmanagement.ViewController.ProductsTab;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import salesmanagement.salesmanagement.SalesComponent.Action;
import salesmanagement.salesmanagement.SalesComponent.Product;
import salesmanagement.salesmanagement.Utils.InputErrorCode;
import salesmanagement.salesmanagement.Utils.NotificationCode;
import salesmanagement.salesmanagement.Utils.NotificationSystem;
import salesmanagement.salesmanagement.ViewController.InfoView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static salesmanagement.salesmanagement.SceneController.SceneController.runTask;
import static salesmanagement.salesmanagement.Utils.InputErrorCode.getInputErrorLabel;
import static salesmanagement.salesmanagement.Utils.Utils.shake;

public class ProductInfoView extends InfoView<Product> implements ProductsTab {
    @FXML
    private TextField buyPriceTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField inStockTextField;
    @FXML
    private TextField productCodeTextField;
    @FXML
    private TextField productLineTextField;
    @FXML
    private TextField productNameTextField;
    @FXML
    private TextField productVendorTextField;
    @FXML
    private TextField sellPriceTextField;

    private List<String> productLines;

    @FXML
    protected void figureSave() {
        runTask(() -> {
                    close();
                    String query = String.format("UPDATE products SET productName = '%s', productLine = '%s', productVendor = '%s', productDescription = '%s', quantityInStock = %d, buyPrice = %s, sellPrice = %s WHERE productCode = '%s'",
                            productNameTextField.getText(), productLineTextField.getText(), productVendorTextField.getText(), descriptionTextField.getText(),
                            Integer.parseInt(inStockTextField.getText()), buyPriceTextField.getText().replaceAll(",", "."), sellPriceTextField.getText().replaceAll(",", "."), productCodeTextField.getText());

                    sqlConnection.updateQuery(query, productCodeTextField.getText(),
                            Action.ComponentModified.PRODUCT, Action.ActionCode.EDIT);
                }, () -> {
                    parentController.show();
                    NotificationSystem.throwNotification(NotificationCode.SUCCEED_CREATE_PRODUCT, stage);
                },
                loadingIndicator, null);
    }

    @FXML
    protected void figureAdd() {
        runTask(() -> {
                    close();
                    String query = String.format("insert into products(productCode, productName, productLine, productVendor, productDescription, quantityInStock, buyPrice, sellPrice) " +
                                    "VALUES ('%s', '%s', '%s', '%s', '%s', %d, %s, %s);",
                            productCodeTextField.getText(), productNameTextField.getText(),
                            productLineTextField.getText(), productVendorTextField.getText(), descriptionTextField.getText(),
                            Integer.parseInt(inStockTextField.getText()),
                            buyPriceTextField.getText().replaceAll(",", "."),
                            sellPriceTextField.getText().replaceAll(",", "."));

                    Action action;
                    try {
                        sqlConnection.updateQuery(query);
                        action = new Action(productCodeTextField.getText(),
                                Action.ComponentModified.PRODUCT,
                                Action.ActionCode.CREATE_NEW,
                                Action.ResultCode.SUCCESSFUL);
                    } catch (SQLException e) {
                        action = new Action(null,
                                Action.ComponentModified.PRODUCT,
                                Action.ActionCode.CREATE_NEW,
                                Action.ResultCode.FAILED);
                    }
                    action.pushAction(sqlConnection);
                }, () -> {
                    parentController.show();
                    NotificationSystem.throwNotification(NotificationCode.SUCCEED_CREATE_PRODUCT, stage);
                },
                loadingIndicator, null);
    }

    public void loadProductLine() {
        productLines = new ArrayList<>();
        String query = "select distinct productLine from productlines";
        ResultSet resultSet = sqlConnection.getDataQuery(query);
        try {
            while (resultSet.next()) {
                productLines.add(resultSet.getString("productLine"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TextFields.bindAutoCompletion(productLineTextField, productLines);
    }

    @Override
    protected void show(Product selectedSalesComponent) {
        super.show();
        saveButton.setVisible(true);
        productCodeTextField.setText(selectedSalesComponent.getProductCode());
        productNameTextField.setText(selectedSalesComponent.getProductName());
        productVendorTextField.setText(selectedSalesComponent.getProductVendor());
        productLineTextField.setText(selectedSalesComponent.getProductLine());
        descriptionTextField.setText(selectedSalesComponent.getProductDescription());

        inStockTextField.setText(Integer.toString(selectedSalesComponent.getQuantityInStock()));
        sellPriceTextField.setText(Double.toString(selectedSalesComponent.getSellPrice()));
        buyPriceTextField.setText(Double.toString(selectedSalesComponent.getBuyPrice()));
    }

    @Override
    public void show() {
        super.show();
        loadProductLine();
        addButton.setVisible(true);
    }

    @Override
    public void addRegexChecker() {
        List<TextField> textFields = new ArrayList<>(Arrays.asList(productCodeTextField,
                productNameTextField, productVendorTextField, descriptionTextField));
        for (TextField textField : textFields) {
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    VBox container = (VBox) textField.getParent();

                    if (!textField.getText().matches("^.+$")) {
                        textField.setStyle("-fx-border-color: #f35050");
                        if (!(container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                            container.getChildren().add(getInputErrorLabel(InputErrorCode.INVALID_INPUT));
                        }
                        shake(textField);
                    } else {
                        textField.setStyle("-fx-border-color: transparent");
                        if ((container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                            container.getChildren().remove(container.getChildren().size() - 1);
                        }
                    }
                }
            });
        }
        productLineTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                VBox container = (VBox) productLineTextField.getParent();

                if (!productLines.contains(productLineTextField.getText())) {
                    productLineTextField.setStyle("-fx-border-color: #f35050");
                    if (!(container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().add(getInputErrorLabel(InputErrorCode.INVALID_INPUT));
                    }
                    shake(productLineTextField);
                } else {
                    productLineTextField.setStyle("-fx-border-color: transparent");
                    if ((container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().remove(container.getChildren().size() - 1);
                    }
                }
            }
        });

        buyPriceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                VBox container = (VBox) buyPriceTextField.getParent();
                try {
                    Double.parseDouble(buyPriceTextField.getText());
                    buyPriceTextField.setStyle("-fx-border-color: transparent");
                    if ((container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().remove(container.getChildren().size() - 1);
                    }
                } catch (Exception e) {
                    buyPriceTextField.setStyle("-fx-border-color: #f35050");
                    if (!(container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().add(getInputErrorLabel(InputErrorCode.INVALID_INPUT));
                    }
                    shake(buyPriceTextField);
                }
            }
        });

        sellPriceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                VBox container = (VBox) sellPriceTextField.getParent();
                try {
                    Double.parseDouble(sellPriceTextField.getText());
                    sellPriceTextField.setStyle("-fx-border-color: transparent");
                    if ((container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().remove(container.getChildren().size() - 1);
                    }
                } catch (Exception e) {
                    sellPriceTextField.setStyle("-fx-border-color: #f35050");
                    if (!(container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().add(getInputErrorLabel(InputErrorCode.INVALID_INPUT));
                    }
                    shake(sellPriceTextField);
                }
            }
        });

        inStockTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                VBox container = (VBox) inStockTextField.getParent();
                try {
                    Integer.parseInt(inStockTextField.getText());
                    inStockTextField.setStyle("-fx-border-color: transparent");
                    if ((container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().remove(container.getChildren().size() - 1);
                    }
                } catch (Exception e) {
                    inStockTextField.setStyle("-fx-border-color: #f35050");
                    if (!(container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                        container.getChildren().add(getInputErrorLabel(InputErrorCode.INVALID_INPUT));
                    }
                    shake(inStockTextField);
                }
            }
        });
    }

    @Override
    public boolean validInput() {
        List<TextField> textFields = new ArrayList<>(Arrays.asList(productCodeTextField,
                productNameTextField, productVendorTextField, productLineTextField, descriptionTextField));
        for (TextField textField : textFields) if (textField.getText().equals("")) return false;

        if (!productLines.contains(productLineTextField.getText())) return false;

        try {
            Double.parseDouble(buyPriceTextField.getText());
        } catch (Exception e) {
            return false;
        }

        try {
            Double.parseDouble(sellPriceTextField.getText());
        } catch (Exception e) {
            return false;
        }

        try {
            Integer.parseInt(inStockTextField.getText());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void removeInvalidAlert() {
        List<TextField> textFields = new ArrayList<>(Arrays.asList(productCodeTextField,
                productNameTextField, productVendorTextField, productLineTextField,
                descriptionTextField, sellPriceTextField, buyPriceTextField, inStockTextField));
        for (TextField textField : textFields) {
            VBox container = (VBox) textField.getParent();
            textField.setStyle("-fx-border-color: transparent");
            if ((container.getChildren().get(container.getChildren().size() - 1) instanceof Label)) {
                container.getChildren().remove(container.getChildren().size() - 1);
            }
        }
    }
}