package salesmanagement.salesmanagement.scenecontrollers;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import salesmanagement.salesmanagement.SQLConnection;

public abstract class SceneController {
    SQLConnection sqlConnection;
    Stage stage;

    public void setSqlConnection(SQLConnection sqlConnection, Stage stage) {
        this.sqlConnection = sqlConnection;
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    @FXML
    protected void closeStage() {
        if (sqlConnection != null)
            sqlConnection.addClosingWork();
        System.exit(0);
    }

    @FXML
    protected void minimizeStage(MouseEvent mouseEvent) {
        ((Stage) ((JFXButton) mouseEvent.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    protected void maximumStage(MouseEvent mouseEvent) {

    }

    ;

    protected Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    ProgressIndicator progressIndicator;

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void setProgressIndicatorStatus(Task<?> databaseConnectionTask, Node bannedArea) {
        progressIndicator.visibleProperty().bind(databaseConnectionTask.runningProperty());
        bannedArea.disableProperty().bind(databaseConnectionTask.runningProperty());
    }

    public void showProgressIndicator(Node bannedPane) {
        progressIndicator.setVisible(true);
        bannedPane.setDisable(true);
    }

    public void hideProgressIndicator(Node bannedPane) {
        progressIndicator.setVisible(false);
        bannedPane.setDisable(false);
    }

    /**
     * This runTask() function is a useful utility method that allows for the execution
     * of a background task while updating the UI with progress and disabling certain
     * UI elements. It is well-designed and easy to use, with the parameters clearly
     * indicating what functionality will be provided by the method. The code is concise
     * and uses JavaFX 's Task class to handle background processing and UI updates.
     * Overall, this function promotes good coding practices and can help improve
     * the user experience in JavaFX applications.
     *
     * @author ThanhAn
     * @since 1.3
     */
    static public void runTask(Runnable taskFunction, Runnable finishFunction, ProgressIndicator progressIndicator, Node bannedArea) {
        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() {
                taskFunction.run();
                return null;
            }
        };
        if (progressIndicator != null) {
            progressIndicator.visibleProperty().bind(task.runningProperty());
        }
        if (bannedArea != null) {
            bannedArea.disableProperty().bind(task.runningProperty());
        }
        if (finishFunction != null) {
            task.setOnSucceeded(workerStateEvent -> {
                finishFunction.run();
            });
        }
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    /**
     * Runs a task in a separate thread and shows a progress indicator during the task.
     * Disables a specified pane or node during the task.
     * Executes a specified finish function upon completion of the task.
     *
     * @param taskFunction   the runnable task to execute in the separate thread.
     * @param finishFunction the optional runnable function to execute upon completion of the task.
     * @param bannedArea     the pane or node to disable during the task.
     */
    public static void runTask(Runnable taskFunction, Runnable finishFunction, Pane bannedArea) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                taskFunction.run();
                return null;
            }
        };
        ProgressIndicator progressIndicator = new ProgressIndicator();
        if (bannedArea != null) {
            bannedArea.getChildren().add(progressIndicator);
            bannedArea.getChildren().get(0).disableProperty().bind(task.runningProperty());
        }
        progressIndicator.visibleProperty().bind(task.runningProperty());

        if (finishFunction != null) {
            task.setOnSucceeded(workerStateEvent -> {
                finishFunction.run();
            });
        }
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }
}
