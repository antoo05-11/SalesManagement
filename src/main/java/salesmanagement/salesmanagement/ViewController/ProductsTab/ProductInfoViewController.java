package salesmanagement.salesmanagement.ViewController.ProductsTab;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import salesmanagement.salesmanagement.SalesComponent.Product;
import salesmanagement.salesmanagement.Utils.NotificationCode;
import salesmanagement.salesmanagement.Utils.NotificationSystem;
import salesmanagement.salesmanagement.ViewController.InfoViewController;

import static salesmanagement.salesmanagement.SceneController.SceneController.runTask;

public class ProductInfoViewController extends InfoViewController<Product> implements ProductsTabController {
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

    @FXML
    protected void save() {
        runTask(() -> {
                    close();
                    String query = String.format("UPDATE products SET productName = '%s', productLine = '%s', productVendor = '%s', productDescription = '%s', quantityInStock = %d, buyPrice = %s, sellPrice = %s WHERE productCode = '%s'",
                            productNameTextField.getText(), productLineTextField.getText(), productVendorTextField.getText(), descriptionTextField.getText(),
                            Integer.parseInt(inStockTextField.getText()), buyPriceTextField.getText().replaceAll(",","."), sellPriceTextField.getText().replaceAll(",","."), productCodeTextField.getText());
                    sqlConnection.updateQuery(query);
                }, () -> {
                    parentController.show();
                    NotificationSystem.throwNotification(NotificationCode.SUCCEED_CREATE_PRODUCT, stage);
                },
                loadingIndicator, null);
    }

    @FXML
    protected void add() {
        runTask(() -> {
                    close();
                    String query = String.format("insert into products(productCode, productName, productLine, productVendor, productDescription, quantityInStock, buyPrice, sellPrice) " +
                                    "VALUES ('%s', '%s', '%s', '%s', '%s', %d, %s, %s);",
                            productCodeTextField.getText(), productNameTextField.getText(),
                            productLineTextField.getText(), productVendorTextField.getText(), descriptionTextField.getText(),
                            Integer.parseInt(inStockTextField.getText()), buyPriceTextField.getText().replaceAll(",","."), sellPriceTextField.getText().replaceAll(",","."));
                    sqlConnection.updateQuery(query);
                }, () -> {
                    parentController.show();
                    NotificationSystem.throwNotification(NotificationCode.SUCCEED_CREATE_PRODUCT, stage);
                },
                loadingIndicator, null);
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
        addButton.setVisible(true);
        productCodeTextField.setText("");
        productNameTextField.setText("");
        productVendorTextField.setText("");
        productLineTextField.setText("");
        descriptionTextField.setText("");
        inStockTextField.setText("");
        sellPriceTextField.setText("");
        buyPriceTextField.setText("");
    }
}
