package salesmanagement.salesmanagement.scenecontrollers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import salesmanagement.salesmanagement.ImageController;
import salesmanagement.salesmanagement.NotificationCode;
import salesmanagement.salesmanagement.NotificationSystem;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;

public class LoginSceneController extends SceneController implements Initializable {
    @FXML
    JFXTextField username;
    @FXML
    JFXPasswordField password;

    @FXML
    private VBox loginPane;
    @FXML
    private Pagination pagination;
    @FXML
    private StackPane loginRoot;

    public VBox getLoginPane() {
        return loginPane;
    }

    @FXML
    public void checkAccount(Event event) {
        if (event instanceof KeyEvent)
            if (((KeyEvent) event).getCode() != KeyCode.ENTER) return;
        String password = this.password.getCharacters().toString();
        String username = this.username.getText();

        Task<Boolean> checkAccountTask = new Task<>() {
            @Override
            protected Boolean call() throws SQLException {
                String query = "select employeeNumber, username, password from employees where username = '" + username + "' and password = '" + password + "'";
                ResultSet resultSet = sqlConnection.getDataQuery(query);
                if (resultSet.next()) {
                    MainSceneController.loggerID = resultSet.getInt("employeeNumber");
                    MainSceneController.haveJustOpened = true;
                    return true;
                }
                return false;
            }
        };

        checkAccountTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                NotificationSystem.throwNotification(NotificationCode.INVALID_LOGIN, stage);
            }
        });

        new Thread(checkAccountTask).start();
        setProgressIndicatorStatus(checkAccountTask, loginPane);
    }

    public void setProgressIndicatorStatus(Task<?> databaseConnectionTask) {
        super.setProgressIndicatorStatus(databaseConnectionTask, loginPane);
    }


    public void setProgressIndicatorStatus(boolean loading) {
        if (loading) {
            showProgressIndicator();
        } else {

        }
    }

    public void showProgressIndicator() {
        super.showProgressIndicator(loginPane);
    }

    public void hideProgressIndicator() {
        super.hideProgressIndicator(loginPane);
    }

    ImageView[] imageViews = new ImageView[5];
    Timeline autoPaging = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
        int nextPage = (pagination.getCurrentPageIndex() + 1) % pagination.getPageCount();
        pagination.setCurrentPageIndex(nextPage);
    }));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Rectangle rect = new Rectangle(loginRoot.getPrefWidth(), loginRoot.getPrefHeight());
        rect.setArcHeight(15.0);
        rect.setArcWidth(15.0);
        loginRoot.setClip(rect);

        for (int i = 0; i < 5; i++) {
            imageViews[i] = new ImageView(ImageController.getImage("app_intro/app_intro_" + (i + 1) + ".png"));
            imageViews[i].setFitWidth(pagination.getPrefWidth());
            imageViews[i].setFitHeight(pagination.getPrefHeight() - 50);
        }
        pagination.setPageFactory((pageIndex) -> imageViews[pageIndex]);



        autoPaging.setCycleCount(Timeline.INDEFINITE);
        autoPaging.play();
    }
}
