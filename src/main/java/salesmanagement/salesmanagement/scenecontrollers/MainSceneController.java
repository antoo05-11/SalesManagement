package salesmanagement.salesmanagement.scenecontrollers;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.embed.swing.SwingFXUtils;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import salesmanagement.salesmanagement.Form;
import salesmanagement.salesmanagement.ImageController;
import salesmanagement.salesmanagement.SalesComponent.Employee;
import salesmanagement.salesmanagement.SalesComponent.Order;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.i18n.phonenumbers.Phonenumber;


public class MainSceneController extends SceneController {
    @FXML
    Text usernameText;
    @FXML
    TabPane tabPane;
    @FXML
    private Tab employeesOperationTab;
    @FXML
    private Tab createOrderTab;
    boolean createOrderInit = false;
    @FXML
    private Tab homeTab;
    @FXML
    private Tab settingTab;
    @FXML
    private Tab productsOperationTab;
    boolean productsInit = false;
    @FXML
    private Tab ordersTab;
    boolean ordersInit = false;

    @FXML
    JFXButton ordersTabButton;
    @FXML
    JFXButton newsTabButton;
    @FXML
    JFXButton settingsTabButton;
    @FXML
    JFXButton productsTabButton;
    @FXML
    JFXButton employeesTabButton;
    JFXButton currentTabButton;


    @FXML
    void goToCreateOrderTab() {
        tabPane.getSelectionModel().select(createOrderTab);
        initCreateOrder();
    }

    @FXML
    void goToOrdersTab() {
        initOrders();

        tabPane.getSelectionModel().select(ordersTab);
    }


    @FXML
    void goToProductsOperationTab() {
        initProducts();

        tabPane.getSelectionModel().select(productsOperationTab);
    }

    @FXML
    void goToSettingTab() {
        tabPane.getSelectionModel().select(settingTab);
    }

    public Node getMainScenePane() {
        return secondSplitPane;
    }

    //region DashBoard Tab
    @FXML
    Tab dashBoardTab;
    @FXML
    private BarChart barChart;

    @FXML
    public void displayDashBoardTab() {
        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("Popular programming languages rated by GitHub");

        dataSeries1.getData().add(new XYChart.Data("JavaScript", 2300));
        dataSeries1.getData().add(new XYChart.Data("Python", 1000));
        dataSeries1.getData().add(new XYChart.Data("Java", 986));
        dataSeries1.getData().add(new XYChart.Data("Ruby", 870));
        dataSeries1.getData().add(new XYChart.Data("C++", 413));
        dataSeries1.getData().add(new XYChart.Data("C#", 326));
        barChart.getData().add(dataSeries1);    }
    //endregion

    //region Employees Tab: list employees, employee's info: details, order, operations.
    @FXML
    TableView<Employee> employeeTable;
    @FXML
    private TableColumn<?, ?> emailColumn;
    @FXML
    private TableColumn<Employee, Integer> employeeNumberColumn;
    @FXML
    private TableColumn<?, ?> nameColumn;
    @FXML
    private TableColumn<?, ?> actionColumn;
    @FXML
    private TableColumn<?, ?> phoneColumn;
    @FXML
    private TableColumn<?, ?> employeeStatusColumn;
    @FXML
    AnchorPane employeeOperationPane;
    ArrayList<Employee> employees;

    @FXML
    void displayEmployeesTab() {
        tabPane.getSelectionModel().select(employeesOperationTab);

        employeeTable.setSelectionModel(null);
        ArrayList<Employee> employees = new ArrayList<>();
        runTask(() -> {
            ResultSet resultSet = null;
            String query = "SELECT * FROM employees";
            resultSet = sqlConnection.getDataQuery(query);
//            while (resultSet == null) {
//                System.out.println("reconnect...");
//                sqlConnection.connectServer();
//                resultSet = sqlConnection.getDataQuery(query);
//            }
            try {
                while (resultSet.next()) {
                    employees.add(new Employee(resultSet, MainSceneController.this));
                }
            } catch (SQLException ignored) {
            }
        }, () -> {
            ObservableList<Employee> employeeList = FXCollections.observableArrayList(employees);
            employeeTable.setItems(employeeList);
            this.employees = employees;
        }, progressIndicator, employeeOperationPane);
    }

    @FXML
    VBox employeeInfoBox;
    @FXML
    VBox detailsInfoBox;

    /**
     * When click on Name Text in Employee table, this function will be called to
     * display detail information of employee. It's called by an Employee object.
     * It hides employees table box and shows employee information box.
     */
    public void displayEmployeeInfoBox(Employee employee) {
        employeeInfoBox.toFront();
        employeeInfoBox.setVisible(true);
        employeeInfoBox.setDisable(false);

        employeeTableBox.toBack();
        employeeTableBox.setVisible(false);
        employeeTableBox.setDisable(true);
        fullNameLabel.setText(employee.getFullName());
        avatar.setImage(employee.getAvatar().getImage());
        lastNameTextField.setText(employee.getLastName());
        firstNameTextField.setText(employee.getFirstName());
        usernameTextField.setText(employee.getUsername());
        passwordField.setText(employee.getPassword());
        birthDatePicker.setValue(employee.getBirthDate());
        emailTextField.setText(employee.getEmail());
        phoneCodeBox.setValue(employee.getPhoneCode());
        joiningDatePicker.setValue(employee.getJoiningDate());
        lastWorkingDatePicker.setValue(employee.getLastWorkingDate());
        phoneNumberTextField.setText(employee.getPhone());
        statusBox.setValue(employee.getStatus().getText());
        accessibilityBox.setValue(employee.getJobTitle());
        if (Objects.equals(employee.getGender(), "male")) maleRadioButton.setSelected(true);
        else if (Objects.equals(employee.getGender(), "female")) femaleRadioButton.setSelected(false);
    }
    @FXML

    TableView orders_employeeTable;
    @FXML
    public void chooseOrders_employeeTable() {

    }

    /**
     * Called when clicking "BACK" button, return to employees list.
     * It shows employees table box and hides employee information box.
     * It relates to {@link  #displayEmployeeInfoBox(Employee)};
     */
    @FXML
    void goBackToEmployeeTableBox() {
        employeeInfoBox.toBack();
        employeeInfoBox.setVisible(false);
        employeeInfoBox.setDisable(true);

        employeeTableBox.toFront();
        employeeTableBox.setVisible(true);
        employeeTableBox.setDisable(false);

        maleRadioButton.setSelected(false);
        femaleRadioButton.setSelected(false);
    }

    @FXML
    void uploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            avatar.setImage(new Image(selectedFile.toURI().toString()));
            avatarAddress.setDisable(false);
            avatarAddress.setText(selectedFile.getAbsolutePath());
            avatarAddress.setDisable(true);
        }
    }

    @FXML
    ImageView avatar;
    @FXML
    Label fullNameLabel;
    @FXML
    JFXTextField lastNameTextField;
    @FXML
    JFXTextField firstNameTextField;
    @FXML
    DatePicker birthDatePicker;
    @FXML
    JFXRadioButton maleRadioButton;
    @FXML
    JFXRadioButton femaleRadioButton;
    @FXML
    JFXTextField avatarAddress;
    @FXML
    JFXComboBox<String> statusBox;
    @FXML
    JFXTextField emailTextField;
    @FXML
    JFXButton verifyButton;
    @FXML
    JFXComboBox<String> phoneCodeBox;
    @FXML
    JFXTextField phoneNumberTextField;
    @FXML
    JFXTextField usernameTextField;
    @FXML
    JFXPasswordField passwordField;
    @FXML
    JFXTextField employeeCodeTextField;
    @FXML
    JFXComboBox<String> accessibilityBox;
    @FXML
    JFXTextField supervisorTextField;
    @FXML
    DatePicker joiningDatePicker;
    @FXML
    DatePicker lastWorkingDatePicker;

    //endregion
    @FXML
    SplitPane firstSplitPane;
    @FXML
    SplitPane secondSplitPane;
    @FXML
    SplitPane thirdSplitPane;
    @FXML
    VBox employeeTableBox;
    @FXML
    HBox appName;
    @FXML
    ImageView smallAvatar;

    @FXML
    private void goToNewsTab() {
        tabPane.getSelectionModel().select(homeTab);
        newsTabButton.fire();
    }

    @Override
    protected void maximumStage(MouseEvent mouseEvent) {

    }

    @FXML
    StackPane menuPane;

    //region News Tab: display news, post news.
    @FXML
    WebView webPreview;
    @FXML
    HTMLEditor htmlEditor;
    String htmlText;
    MenuButton insertMenuButton;
    @FXML
    VBox newsPreviewBox;
    @FXML
    VBox newsCreatingBox;
    @FXML
    JFXTextField newsTitle;
    @FXML
    ImageView newsImageInserted;
    @FXML
    WebView newsDisplayedView;
    @FXML
    VBox newsPiecesBox;
    String mainContent = "";

    @FXML
    void goToCreateNews() {
        thirdSplitPane.setVisible(false);
        thirdSplitPane.setDisable(true);
        newsCreatingBox.setVisible(true);
        newsCreatingBox.setDisable(false);
    }

    @FXML
    public void previewNews() {
        newsPreviewBox.setVisible(true);
        newsPreviewBox.setDisable(false);
        newsCreatingBox.setVisible(false);
        htmlText = htmlEditor.getHtmlText();
        htmlText = htmlText.replace("<body contenteditable=\"true\">", "<body style='background : rgba(0,0,0,0);' contenteditable=\"false\";> " + "<h1 style=\"text-align:center; font-size:30px; font-weight:bold;\">" + newsTitle.getText() + "</h1>\n");
        webPreview.setPageFill(Color.TRANSPARENT);
        webPreview.getEngine().loadContent(htmlText);
    }

    @FXML
    public void backToNewsCreatingBox() {
        newsPreviewBox.setVisible(false);
        newsPreviewBox.setDisable(true);
        newsCreatingBox.setVisible(true);
    }

    @FXML
    public void postNews() throws SQLException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(newsImageInserted.getImage(), null), "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] imageBytes = outputStream.toByteArray();
        String postNewsQuery = "insert into notifications(employeeNumber, title, content, newsImage) values (?,?,?,?)";
        PreparedStatement statement = sqlConnection.getConnection().prepareStatement(postNewsQuery);
        statement.setInt(1, user.getEmployeeNumber());
        statement.setString(2, newsTitle.getText());
        statement.setString(3, htmlText);
        statement.setBytes(4, imageBytes);
        statement.executeUpdate();

        thirdSplitPane.setVisible(true);
        thirdSplitPane.setDisable(false);
        newsCreatingBox.setVisible(false);
        newsCreatingBox.setDisable(true);
    }

    private void uploadNotificationText() {
        runTask(() -> {
            String query = "SELECT * FROM notifications ORDER BY notifications.notificationID DESC LIMIT 6;";
            ResultSet resultSet = sqlConnection.getDataQuery(query);
            try {
                if (resultSet.next()) {
                    mainContent = resultSet.getString("content");
                }
                Platform.runLater(() -> {
                    newsDisplayedView.getEngine().loadContent(mainContent);
                });
                int newsPieceCount = 0;
                while (resultSet.next()) {
                    HBox newsPiece = ((HBox) newsPiecesBox.getChildren().get(newsPieceCount));
                    ((Text) newsPiece.getChildren().get(1)).setText(resultSet.getString("title"));
                    InputStream is = resultSet.getBinaryStream("newsImage");
                    Image image = new Image(is);
                    ((ImageView) newsPiece.getChildren().get(0)).setImage(image);
                    newsPieceCount++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, null, (Pane) thirdSplitPane.getItems().get(0));

    }

    @FXML
    void uploadImageNews(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose News Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            ((ImageView) event.getSource()).setImage(new Image(selectedFile.toURI().toString()));
        }
    }
    //endregion

    public void initialSetup() {
        // Load current UI.
        user = new Employee(sqlConnection, loggerID);
        usernameText.setText(user.getFullName());

        firstSplitPane.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
        ((StackPane) firstSplitPane.getItems().get(0)).setMinHeight(0.06 * Screen.getPrimary().getVisualBounds().getHeight());
        ((SplitPane) firstSplitPane.getItems().get(1)).setMinHeight(0.94 * Screen.getPrimary().getVisualBounds().getHeight());
        secondSplitPane.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth());
        ((AnchorPane) secondSplitPane.getItems().get(0)).setMinWidth(0.1667 * Screen.getPrimary().getVisualBounds().getWidth());
        ((TabPane) secondSplitPane.getItems().get(1)).setMinWidth(0.8333 * Screen.getPrimary().getVisualBounds().getWidth());

        thirdSplitPane.setMaxWidth(0.8333 * Screen.getPrimary().getVisualBounds().getWidth());
        ((Pane) thirdSplitPane.getItems().get(0)).setMinWidth(0.75 * thirdSplitPane.getMaxWidth());
        ((Pane) thirdSplitPane.getItems().get(1)).setMinWidth(0.25 * thirdSplitPane.getMaxWidth());
        Insets hboxMargin = new Insets(0, 0.8333 * Screen.getPrimary().getVisualBounds().getWidth(), 0, 0);
        StackPane.setMargin(appName, hboxMargin);

        double tableWidth = employeeTableBox.getWidth() * 0.95;
        employeeTable.setMaxWidth(tableWidth);
        employeeNumberColumn.setMinWidth(0.1 * tableWidth);
        nameColumn.setMinWidth(0.25 * tableWidth);
        phoneColumn.setMinWidth(0.15 * tableWidth);
        emailColumn.setMinWidth(0.2 * tableWidth);
        employeeStatusColumn.setMinWidth(0.1 * tableWidth);
        actionColumn.setMinWidth(0.2 * tableWidth);

        Circle clip = new Circle();
        clip.setRadius(35);
        clip.setCenterX(35);
        clip.setCenterY(35);
        smallAvatar.setClip(clip);

        currentTabButton = newsTabButton;
        goToNewsTab();

        insertMenuButton = new MenuButton("Insert...");
        ToolBar bar = null;
        Node node = htmlEditor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            bar = (ToolBar) node;
        }
        if (bar != null) {
            bar.getItems().add(insertMenuButton);
        }

        webPreview.setPageFill(Color.TRANSPARENT);
        newsDisplayedView.setPageFill(Color.TRANSPARENT);

        //TODO: test area

        // Load UI for others.
        runTask(() -> {
            // Load small avatar.
            try {
                PreparedStatement ps = sqlConnection.getConnection().prepareStatement("SELECT avatar FROM employees WHERE employeeNumber = ?");
                ps.setInt(1, loggerID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    InputStream is = rs.getBinaryStream("avatar");
                    Image image = new Image(is);
                    smallAvatar.setImage(image);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Prepare for employee table structure.
            employeeNumberColumn.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            employeeStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

            // Employees Tab Preparation.
            List<String> phoneCodes = new ArrayList<>();
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            for (String regionCode : phoneUtil.getSupportedRegions()) {
                Phonenumber.PhoneNumber exampleNumber = phoneUtil.getExampleNumber(regionCode);
                if (exampleNumber != null) {
                    int countryCode = exampleNumber.getCountryCode();
                    phoneCodes.add(String.format("+%d (%s)", countryCode, phoneUtil.getRegionCodeForCountryCode(countryCode)));
                }
            }
            phoneCodeBox.getItems().addAll(phoneCodes);

            List<String> accessibilitiesList = new ArrayList<>(Arrays.asList("HR", "Manager", "Employee", "Admin"));
            accessibilityBox.getItems().addAll(accessibilitiesList);

            List<String> statusList = new ArrayList<>(Arrays.asList("ACTIVE", "INACTIVE"));
            statusBox.getItems().addAll(statusList);
        }, null, null, null);
    }


    private Employee user;

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public static boolean haveJustOpened = false;
    public static int loggerID = -1;
    public static boolean haveChangeInEmployeesTab = false;
    public static boolean haveChangeInOrdersTab = false;
    public static boolean haveChangeInHomeTab = false;
    public static boolean haveChangeInSettingTab = false;
    public static boolean haveChangeInProductTab = false;
    public AnimationTimer loginDataListener = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if (MainSceneController.loggerID > 0) {
                runTask(() -> {
                    Platform.runLater(() -> {
                        stage.setScene(MainSceneController.this.scene);
                        stage.hide();
                        initialSetup();
                        stage.setX(0);
                        stage.setY(0);
                        stage.show();
                        uploadNotificationText();
                    });

                }, null, null, null);
                stop();
            }
        }
    };
    @FXML
    StackPane employeeInfoBoxContainer;
    Form employeeForm;
    @FXML
    JFXComboBox statusInput;

    public void initCreateOrder() {
        customerNameInput.clear();
        phoneNumberInput.clear();
        commentsInput.clear();
        statusInput.getItems().clear();
        statusInput.getItems().add("Cancelled");
        statusInput.getItems().add("Disputed");
        statusInput.getItems().add("In Process");
        statusInput.getItems().add("On Hold");
        statusInput.getItems().add("Resolved");
        statusInput.getItems().add("Shipped");

        productCodeOD.setCellValueFactory(new PropertyValueFactory<Order, String>("productCode"));
        quantityOD.setCellValueFactory(new PropertyValueFactory<Order, Integer>("quantityOrdered"));
        priceEachOD.setCellValueFactory(new PropertyValueFactory<Order, Double>("priceEach"));
        totalOD.setCellValueFactory(param -> {
            Order order = param.getValue();
            int quantity = order.getQuantityOrdered();
            double price = order.getPriceEach();
            double total = quantity * price;
            return new SimpleDoubleProperty(total).asObject();
        });

        orderDetailsTable.setItems(getList());

        orderDetailsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        orderDetailsTable.setEditable(true);
        productCodeOD.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityOD.setCellFactory(TextFieldTableCell.forTableColumn((new IntegerStringConverter())));
        priceEachOD.setCellFactory(TextFieldTableCell.forTableColumn((new DoubleStringConverter())));

        // Add a listener to the text property of the text field
        productCodeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // Update the suggestion list based on the current input
            if (newValue.equals("")) {
                suggestionList.setVisible(false);
            } else {
                if (newValue.length() > 2) {
                    List<String> suggestions = new ArrayList<>(); // Replace with your own function to retrieve suggestions

                    String sql = "SELECT productCode FROM products WHERE upper(productCode) LIKE upper('" + newValue + "%');";
                    try (ResultSet rs = sqlConnection.getDataQuery(sql)) {
                        // Add each suggestion to the list
                        while (rs.next()) {
                            suggestions.add(rs.getString("productCode"));
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    if (suggestions.isEmpty()) {
                        suggestionList.setVisible(false);
                    } else {
                        suggestionList.getItems().setAll(suggestions);
                        suggestionList.getSelectionModel().selectFirst();
                        suggestionList.setVisible(true);
                    }
                }
            }
        });


        // Add an event handler to the suggestion list
        suggestionList.setOnMouseClicked(event -> {
            String selectedValue = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedValue != null) {
                productCodeInput.setText(selectedValue);
                suggestionList.setVisible(false);
            }
        });

        productCodeInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selectedValue = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedValue != null) {
                    productCodeInput.setText(selectedValue);
                    suggestionList.setVisible(false);
                    productCodeInput.requestFocus();
                }
            } else if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.DOWN) {
                suggestionList.getSelectionModel().selectNext();
                event.consume();
                productCodeInput.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                suggestionList.getSelectionModel().selectPrevious();
                event.consume();
                productCodeInput.requestFocus();
            }
        });
    }

//    @FXML
//    public void search() {
//        String newValue = productCodeInput.getText();
//        if (newValue.equals("")) {
//            suggestionList.setVisible(false);
//        } else {
//            if (newValue.length() > 2) {
//                List<String> suggestions = new ArrayList<>(); // Replace with your own function to retrieve suggestions
//
//                String sql = "SELECT productCode FROM products WHERE upper(productCode) LIKE upper('" + newValue + "%');";
//                try (ResultSet rs = sqlConnection.getDataQuery(sql)) {
//                    // Add each suggestion to the list
//                    while (rs.next()) {
//                        suggestions.add(rs.getString("productCode"));
//                    }
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//
//                if (suggestions.isEmpty() || suggestions.get(0).equals(newValue)) {
//                    suggestionList.setVisible(false);
//                } else {
//                    suggestionList.getItems().setAll(suggestions);
//                    suggestionList.getSelectionModel().selectFirst();
//                    suggestionList.setVisible(true);
//                }
//            }
//        }
//    }
    public ObservableList<Order> getList() {
        ObservableList<Order> items = FXCollections.observableArrayList();
        return items;
    }

    public void addItem() {
        String productCode = productCodeInput.getText();
        int quantity = Integer.parseInt(quantityInput.getText());
        double priceEach = Double.parseDouble(priceEachInput.getText());

        // Check if an order with the same productCode already exists
        for (Order order : orderDetailsTable.getItems()) {
            if (order.getProductCode().equals(productCode)) {
                // Update the existing order
                order.setQuantityOrdered(quantity);
                order.setPriceEach(priceEach);
                orderDetailsTable.refresh();
                return;
            }
        }

        // If no existing order was found, create a new one and add it to the tableView
        Order order = new Order(productCode, quantity, priceEach);
        orderDetailsTable.getItems().add(order);

        productCodeInput.clear();
        quantityInput.clear();
        priceEachInput.clear();
    }

    public void removeItems() {
        ObservableList<Order> selectedRows, allItems;
        allItems = orderDetailsTable.getItems();

        selectedRows = orderDetailsTable.getSelectionModel().getSelectedItems();

        allItems.removeAll(selectedRows);
    }

    public void changeProductCode(TableColumn.CellEditEvent edittedCell) {
        Order selected = orderDetailsTable.getSelectionModel().getSelectedItem();
        selected.setProductCode(edittedCell.getNewValue().toString());
        orderDetailsTable.refresh();
    }

    public void changeQuantity(TableColumn.CellEditEvent edittedCell) {
        Order selected = orderDetailsTable.getSelectionModel().getSelectedItem();
        selected.setQuantityOrdered((int) edittedCell.getNewValue());
        orderDetailsTable.refresh();
    }

    public void changePriceEach(TableColumn.CellEditEvent edittedCell) {
        Order selected = orderDetailsTable.getSelectionModel().getSelectedItem();
        selected.setPriceEach((double) edittedCell.getNewValue());
        orderDetailsTable.refresh();
    }

    @FXML
    TableView<Order> orderDetailsTable;
    @FXML
    TableColumn<Order, String> productCodeOD;
    @FXML
    TableColumn<Order, Integer> quantityOD;
    @FXML
    TableColumn<Order, Double> priceEachOD;
    @FXML
    TableColumn<Order, Double> totalOD;
    @FXML
    JFXTextField customerNameInput;
    @FXML
    JFXTextField phoneNumberInput;
    @FXML
    JFXTextField productCodeInput;
    @FXML
    ListView<String> suggestionList;
    @FXML
    JFXTextField quantityInput;
    @FXML
    JFXTextField priceEachInput;
    @FXML
    JFXButton addButton;
    @FXML
    JFXButton removeButton;
    @FXML
    DatePicker orderDateInput;
    @FXML
    DatePicker requiredDateInput;
    @FXML
    DatePicker shippedDateInput;
    @FXML
    JFXTextField commentsInput;
    @FXML
    JFXButton submitOrderButton;
    @FXML
    JFXTextField customerNameInput;
    @FXML
    JFXTextField phoneNumberInput;


    public void createOrder() {
        runTask(() -> {
            String orderDate;
            if (orderDateInput.getValue() == null) {
                orderDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            } else {
                orderDate = orderDateInput.getValue().format(DateTimeFormatter.ISO_DATE);
            }
            String shippedDate;
            if (shippedDateInput.getValue() != null) {
                shippedDate = shippedDateInput.getValue().format(DateTimeFormatter.ISO_DATE);
                shippedDate = "'" + shippedDate;
                shippedDate += "'";
            } else {
                shippedDate = "null";
            }
            String check = "SELECT customerNumber FROM customers WHERE customerName = '" + customerNameInput.getText() + "' AND phone = '" + phoneNumberInput.getText() + "';" ;
            ResultSet result = sqlConnection.getDataQuery(check);
            int customerNumber = -1;
            try {
                if (result.next()) {
                    customerNumber = result.getInt("customerNumber");
                } else {
                    check = "INSERT INTO customers (customerName, phone) VALUES ('" + customerNameInput.getText() + "', '" + phoneNumberInput.getText() + "')";
                    sqlConnection.updateQuery(check);
                    check = "SELECT LAST_INSERT_ID() FROM customers;";
                    result = sqlConnection.getDataQuery(check);
                    if (result.next()) {
                        customerNumber = result.getInt(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String order = "insert into orders(orderDate, requiredDate, shippedDate, status, comments, customerNumber) values ('"
                    + orderDate + "','"
                    + requiredDateInput.getValue().format(DateTimeFormatter.ISO_DATE) + "',"
                    + shippedDate + ",'"
                    + statusInput.getValue() + "','"
                    + commentsInput.getText() + "',"
                    + customerNumber + ");";
            sqlConnection.updateQuery(order);
            result = sqlConnection.getDataQuery("SELECT LAST_INSERT_ID() FROM orders;");

            int orderNumber = 0;
            try {
                if (result.next()) {
                    orderNumber = result.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            StringBuilder orderdetails = new StringBuilder("insert into orderdetails values");
            ObservableList<Order> items = orderDetailsTable.getItems();

            for (Order item : items) {
                orderdetails.append("(").append(orderNumber)
                        .append(", '")
                        .append(item.getProductCode())
                        .append("',")
                        .append(item.getQuantityOrdered())
                        .append(",")
                        .append(item.getPriceEach())
                        .append("),");
            }
            orderdetails.deleteCharAt(orderdetails.length() - 1);
            orderdetails.append(';');
            sqlConnection.updateQuery(orderdetails.toString());
        }, null, progressIndicator, createOrderTab.getTabPane());

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order created successfully!", ButtonType.OK);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setGraphic(null);

        alert.showAndWait();
        goToOrdersTab();
    }

    public void editEmployees(Employee employee) {
        employeeInfoBoxContainer.setMouseTransparent(false);
        employeeForm.fillInForm(employee);
        employeeForm.show();
    }

    @FXML
    void tabSelectingEffect(Event event) {
        HBox hbox = (HBox) currentTabButton.getGraphic();
        Label label = (Label) hbox.getChildren().get(1);
        label.setTextFill(Color.valueOf("#7c8db5"));
        ImageView buttonIcon = (ImageView) hbox.getChildren().get(0);
        if (currentTabButton.equals(newsTabButton)) buttonIcon.setImage(ImageController.newsIcon);
        else if (currentTabButton.equals(ordersTabButton)) buttonIcon.setImage(ImageController.orderIcon);
        else if (currentTabButton.equals(productsTabButton)) buttonIcon.setImage(ImageController.productIcon);
        else if (currentTabButton.equals(employeesTabButton)) buttonIcon.setImage(ImageController.employeeIcon);
        else if (currentTabButton.equals(settingsTabButton)) buttonIcon.setImage(ImageController.settingsIcon);
        currentTabButton.setStyle("-fx-border-color: transparent;-fx-background-color :#ffffff; -fx-border-width: 0 0 0 4; -fx-border-radius: 0;");

        currentTabButton = (JFXButton) event.getSource();
        hbox = (HBox) currentTabButton.getGraphic();
        label = (Label) hbox.getChildren().get(1);
        label.setTextFill(Color.valueOf("#329cfe"));
        buttonIcon = (ImageView) hbox.getChildren().get(0);
        if (currentTabButton.equals(newsTabButton)) buttonIcon.setImage(ImageController.blueNewsIcon);
        else if (currentTabButton.equals(ordersTabButton)) buttonIcon.setImage(ImageController.blueOrderIcon);
        else if (currentTabButton.equals(productsTabButton)) buttonIcon.setImage(ImageController.blueProductIcon);
        else if (currentTabButton.equals(employeesTabButton)) buttonIcon.setImage(ImageController.blueEmployeeIcon);
        else if (currentTabButton.equals(settingsTabButton)) buttonIcon.setImage(ImageController.blueSettingsIcon);

        currentTabButton.setStyle("-fx-border-color: #60b1fd; -fx-background-color : #fafafa;-fx-border-width: 0 0 0 4; -fx-border-radius: 0;");

    }

    /**
     * Handle ORDERS tab.
     */
    @FXML
    TableView<ObservableList<Object>> ordersTable;
    @FXML
    TableColumn<ObservableList<Object>, Integer> orderNumberOrd;
    @FXML
    TableColumn<ObservableList<Object>, LocalDate> orderDateOrd;
    @FXML
    TableColumn<ObservableList<Object>, LocalDate> requiredDateOrd;
    @FXML
    TableColumn<ObservableList<Object>, LocalDate> shippedDateOrd;
    @FXML
    TableColumn<ObservableList<Object>, String> statusOrd;
    @FXML
    TableColumn<ObservableList<Object>, String> commentsOrd;
    @FXML
    TableColumn<ObservableList<Object>, Integer> customerNumberOrd;
    @FXML
    JFXButton createOrderButton;
    @FXML
    JFXButton removeOrderButton;

    void initOrders() {
        runTask(() -> {
            ordersTable.getItems().clear();
            orderNumberOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, Integer>, ObservableValue<Integer>>() {
                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<ObservableList<Object>, Integer> param) {
                    return new SimpleObjectProperty<Integer>((Integer) param.getValue().get(0));
                }
            });

            orderDateOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, LocalDate>, ObservableValue<LocalDate>>() {
                @Override
                public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<ObservableList<Object>, LocalDate> param) {
                    return new SimpleObjectProperty<LocalDate>((LocalDate) param.getValue().get(1));
                }
            });

            requiredDateOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, LocalDate>, ObservableValue<LocalDate>>() {
                @Override
                public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<ObservableList<Object>, LocalDate> param) {
                    return new SimpleObjectProperty<LocalDate>((LocalDate) param.getValue().get(2));
                }
            });

            shippedDateOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, LocalDate>, ObservableValue<LocalDate>>() {
                @Override
                public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<ObservableList<Object>, LocalDate> param) {
                    return new SimpleObjectProperty<LocalDate>((LocalDate) param.getValue().get(3));
                }
            });

            statusOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<Object>, String> param) {
                    return new SimpleStringProperty((String) param.getValue().get(4));
                }
            });

            commentsOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<Object>, String> param) {
                    return new SimpleStringProperty((String) param.getValue().get(5));
                }
            });

            customerNumberOrd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, Integer>, ObservableValue<Integer>>() {
                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<ObservableList<Object>, Integer> param) {
                    return new SimpleObjectProperty<Integer>((Integer) param.getValue().get(6));
                }
            });
            try {
                String query = "SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber FROM orders";
                ResultSet resultSet = sqlConnection.getDataQuery(query);
                while (resultSet.next()) {
                    ObservableList<Object> row = FXCollections.observableArrayList();
                    row.add(resultSet.getInt("orderNumber"));
                    row.add(resultSet.getDate("orderDate").toLocalDate());
                    row.add(resultSet.getDate("requiredDate").toLocalDate());
                    row.add(resultSet.getDate("shippedDate") != null ? resultSet.getDate("shippedDate").toLocalDate() : null);
                    row.add(resultSet.getString("status"));
                    row.add(resultSet.getString("comments"));
                    row.add(resultSet.getInt("customerNumber"));
                    ordersTable.getItems().add(row);
                }
                ordersTable.refresh();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, null, progressIndicator, ordersTab.getTabPane());
    }

    public void handleRemoveOrder() {
        // Get the selected row in the TableView
        ObservableList<Object> selectedRow = ordersTable.getSelectionModel().getSelectedItem();

        // If a row is selected, delete the corresponding order from the database and TableView
        if (selectedRow != null) {
            int orderNumber = (Integer) selectedRow.get(0);

            String deleteQuery = "DELETE FROM orders WHERE orderNumber = " + orderNumber;
            // Delete the order from the database
            sqlConnection.updateQuery(deleteQuery);

            // Delete the order from the TableView
            ordersTable.getItems().remove(selectedRow);

        }
    }

    @FXML
    TableView<ObservableList<Object>> productsTable;
    @FXML
    TableColumn<ObservableList<Object>, String> productCodeProd;
    @FXML
    TableColumn<ObservableList<Object>, String> productNameProd;
    @FXML
    TableColumn<ObservableList<Object>, String> productLineProd;
    @FXML
    Pane bgPane;
    @FXML
    AnchorPane productDetailsPane;
    @FXML
    JFXButton closeProductDetailsButton;

    void initProducts() {
        runTask(() -> {
            productsInit = true;
            productsTable.getItems().clear();
            productsTable.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2) {
                    ObservableList<Object> selected = productsTable.getSelectionModel().getSelectedItem();
                    initProductDetails(selected);
                    bgPane.setVisible(true);
                    productDetailsPane.setVisible(true);
                }
            });
            closeProductDetailsButton.setOnAction(event -> {
                bgPane.setVisible(false);
                productDetailsPane.setVisible(false);
            });
            productCodeProd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<Object>, String> param) {
                    return new SimpleObjectProperty<String>((String) param.getValue().get(0));
                }
            });

            productNameProd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<Object>, String> param) {
                    return new SimpleObjectProperty<String>((String) param.getValue().get(1));
                }
            });

            productLineProd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<Object>, String> param) {
                    return new SimpleObjectProperty<String>((String) param.getValue().get(2));
                }
            });


            try {
                String query = "SELECT productCode, productName, productLine FROM products";
                ResultSet resultSet = sqlConnection.getDataQuery(query);
                while (resultSet.next()) {
                    ObservableList<Object> row = FXCollections.observableArrayList();
                    row.add(resultSet.getString("productCode"));
                    row.add(resultSet.getString("productName"));
                    row.add(resultSet.getString("productLine"));
                    productsTable.getItems().add(row);
                }
                productsTable.refresh();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, null, progressIndicator, productsOperationTab.getTabPane());

    }

    @FXML
    JFXTextField productCodePDetails;
    @FXML
    JFXTextField productNamePDetails;
    @FXML
    JFXComboBox productLinePDetails;
    @FXML
    JFXTextField productScalePDetails;
    @FXML
    JFXTextField productVendorPDetails;
    @FXML
    JFXTextField inStockPDetails;
    @FXML
    JFXTextField buyPricePDetails;
    @FXML
    JFXTextField sellPricePDetails;
    @FXML
    JFXTextField productDescriptionPDetails;

    void initProductDetails(ObservableList<Object> selected) {
        try {
            productLinePDetails.getItems().clear();
            String query = "SELECT productLine FROM productlines";
            ResultSet resultSet = sqlConnection.getDataQuery(query);

            while (resultSet.next()) {
                String productLine = resultSet.getString("productLine");
                productLinePDetails.getItems().add(productLine);
            }
            query = "SELECT productCode, productName, productLine, productScale, productVendor, productDescription, quantityInStock, buyPrice, sellPrice FROM products WHERE productCode = '" + selected.get(0).toString() + "'";
            resultSet = sqlConnection.getDataQuery(query);
            if (resultSet.next()) {
                productCodePDetails.setText(resultSet.getString("productCode"));
                productNamePDetails.setText(resultSet.getString("productName"));
                productLinePDetails.setValue(resultSet.getString("productLine"));
                productScalePDetails.setText(resultSet.getString("productScale"));
                productVendorPDetails.setText(resultSet.getString("productVendor"));
                productDescriptionPDetails.setText(resultSet.getString("productDescription"));
                inStockPDetails.setText(resultSet.getString("quantityInStock"));
                buyPricePDetails.setText(resultSet.getString("buyPrice"));
                sellPricePDetails.setText(resultSet.getString("sellPrice"));
            }
        }, null, progressIndicator, null);
    }
}

