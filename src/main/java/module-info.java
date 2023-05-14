module salesmanagement.salesmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires org.controlsfx.controls;

    requires java.mail;
    requires javafx.swing;
    requires javafx.web;

    requires libphonenumber;
    requires jasperreports;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.burningwave.core;
    requires org.apache.commons.net;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.media;
    opens salesmanagement.salesmanagement to javafx.fxml;
    exports salesmanagement.salesmanagement;
    exports salesmanagement.salesmanagement.SalesComponent;
    opens salesmanagement.salesmanagement.SalesComponent to javafx.fxml;
    exports salesmanagement.salesmanagement.SceneController;
    opens salesmanagement.salesmanagement.SceneController to javafx.fxml;
    exports salesmanagement.salesmanagement.ViewController;
    opens salesmanagement.salesmanagement.ViewController to javafx.fxml;
    exports salesmanagement.salesmanagement.ViewController.CustomersTab;
    opens salesmanagement.salesmanagement.ViewController.CustomersTab to javafx.fxml;
    exports salesmanagement.salesmanagement.ViewController.EmployeesTab;
    opens salesmanagement.salesmanagement.ViewController.EmployeesTab to javafx.fxml;
    exports salesmanagement.salesmanagement.ViewController.SettingsTab;
    opens salesmanagement.salesmanagement.ViewController.SettingsTab to javafx.fxml;
    exports salesmanagement.salesmanagement.Utils;
    opens salesmanagement.salesmanagement.Utils to javafx.fxml;
    exports salesmanagement.salesmanagement.DataAccess;
    opens salesmanagement.salesmanagement.DataAccess to javafx.fxml;
}