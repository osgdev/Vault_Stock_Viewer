package uk.gov.dvla.osg.vault.main;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
public class AlertSample extends Application {
 
    AtomicInteger taskExecution = new AtomicInteger(0);
 
    @Override
    public void start(Stage stage) throws Exception {
        Button runProcess = new Button("Run Process");
        Label processResult = new Label();
        runProcess.setOnAction(e -> {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Operation in progress",
                    ButtonType.CANCEL
            );
            alert.setTitle("Running Operation");
            alert.setHeaderText("Please wait... ");
            ProgressIndicator progressIndicator = new ProgressIndicator();
            alert.setGraphic(progressIndicator);
 
            Task<Void> task = new Task<Void>() {
                final int N_ITERATIONS = 15;
 
                {
                    setOnFailed(a -> {
                        alert.close();
                        updateMessage("Failed");
                    });
                    setOnSucceeded(a -> {
                        alert.close();
                        updateMessage("Succeeded");
                    });
                    setOnCancelled(a -> {
                        alert.close();
                        updateMessage("Cancelled");
                    });
                }
 
                @Override
                protected Void call() throws Exception {
                    updateMessage("Processing");
 
                    int i;
                    for (i = 0; i < N_ITERATIONS; i++) {
                        if (isCancelled()) {
                            break;
                        }
 
                        updateProgress(i, N_ITERATIONS);
 
                        try {
                            Thread.sleep(1_000);
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }
                    }
 
                    if (!isCancelled()) {
                        updateProgress(i, N_ITERATIONS);
                    }
 
                    return null;
                }
            };
 
            progressIndicator.progressProperty().bind(task.progressProperty());
            processResult.textProperty().unbind();
            processResult.textProperty().bind(task.messageProperty());
 
            Thread taskThread = new Thread(
                    task,
                    "task-thread-" + taskExecution.getAndIncrement()
            );
            taskThread.start();
 
            alert.initOwner(stage);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL && task.isRunning()) {
                task.cancel();
            }
        });
 
        VBox layout = new VBox(runProcess, processResult);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setPrefSize(800, 600);
 
        stage.setScene(new Scene(layout));
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}