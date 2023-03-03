package salesmanagement.salesmanagement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @since 1.0
 */
public class AppController {
    private static AppController appController = null;
    private Stage stage;
    private Scene loginScene;

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
        FXMLLoader fxmlLoader = new FXMLLoader(SalesManagement.class.getResource("login_scene.fxml"));
        try {
            loginScene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Sales Management");
        stage.setScene(loginScene);
        stage.getIcons().add(new Image("/app_icon.jpg"));
        stage.show();
    }
}
