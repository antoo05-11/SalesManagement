package salesmanagement.salesmanagement;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.burningwave.core.assembler.StaticComponentContainer;
import salesmanagement.salesmanagement.SceneController.LoginSceneController;
import salesmanagement.salesmanagement.SceneController.MainSceneController;
import salesmanagement.salesmanagement.Utils.ImageController;
import salesmanagement.salesmanagement.DataAccess.SQLConnection;

import java.io.IOException;

import static salesmanagement.salesmanagement.SceneController.SceneController.runTask;

public class AppController {
    private static AppController appController = null;
    private final Stage stage;
    private Scene loginScene;
    private Scene mainScene;
    private SQLConnection sqlConnection;

    private LoginSceneController loginSceneController;
    private MainSceneController mainSceneController;

    private AppController(Stage stage) {
        this.stage = stage;
    }

    public static AppController getAppController(Stage stage) {
        synchronized (AppController.class) {
            if (appController == null) {
                appController = new AppController(stage);
            }
        }
        return appController;
    }

    public synchronized void run() {
        StaticComponentContainer.JVMInfo.getVersion();

        FXMLLoader loginFXMLLoader = new FXMLLoader(SalesManagement.class.getResource("fxml-scene/login-scene.fxml"));
        try {
            this.loginScene = new Scene(loginFXMLLoader.load());
        } catch (IOException e) {
            System.exit(0);
        }
        loginSceneController = loginFXMLLoader.getController();

        FXMLLoader mainFXMLLoader = new FXMLLoader(SalesManagement.class.getResource("fxml-scene/main-scene.fxml"));
        try {
            this.mainScene = new Scene(mainFXMLLoader.load());
        } catch (IOException e) {
            System.exit(0);
        }

        mainSceneController = mainFXMLLoader.getController();
        mainSceneController.setScene(this.mainScene);

        //Set up stage config.
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("SAMA");
        stage.setScene(this.loginScene);
        this.loginScene.setFill(Color.TRANSPARENT);
        stage.getIcons().add(ImageController.getImage("app_icon.png"));
        stage.show();

        stage.getScene().setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        stage.getScene().setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        var password = "M3hB6j3RXWrqpQssMdSq";
        var user = "ur3nnlrz6fwe487r";
        var url = "jdbc:mysql://bro797wh6lkgffqmjr7x-mysql.services.clever-cloud.com:3306/bro797wh6lkgffqmjr7x";

        sqlConnection = new SQLConnection(url, user, password);
        loginSceneController.setSqlConnection(sqlConnection, stage);

        runTask(() -> sqlConnection.connectServer(), null, loginSceneController.getProgressIndicator(), loginSceneController.getLoginPane());
        mainSceneController.loginDataListener.start();

        BooleanProperty reLogin = new SimpleBooleanProperty(false);
        reLogin.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                reLogin.set(false);
                FXMLLoader loader = new FXMLLoader(SalesManagement.class.getResource("fxml-scene/main-scene.fxml"));
                try {
                    this.mainScene = new Scene(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
                mainSceneController = loader.getController();
                mainSceneController.setScene(this.mainScene);
                mainSceneController.loginDataListener.start();
            }
        });

        AnimationTimer logOutListener = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (mainSceneController.beClosed()) {
                    mainSceneController.close();
                    MainSceneController.loggerID = -1;
                    reLogin.set(true);
                    stage.setScene(AppController.this.loginScene);
                    loginSceneController.resetData();
                }
            }
        };
        logOutListener.start();
    }

    double xOffset;
    double yOffset;
}