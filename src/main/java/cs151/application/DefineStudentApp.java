package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DefineStudentApp extends Application {

    public void start(Stage home) throws IOException {

    }






    public static Stage open() {
        Stage stage = new Stage();
        javafx.application.Platform.runLater(() -> {
            try {
                new DefineStudentApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return stage;
    }

}
