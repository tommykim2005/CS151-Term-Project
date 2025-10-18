package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage home) throws IOException {
        FXMLLoader root = new FXMLLoader(getClass().getResource("main.fxml"));
        home.setTitle("MentorLink - Home Page");
        home.setScene(new Scene(root.load()));
        home.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}