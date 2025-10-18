package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddStudentController {

    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private TextField emailField;
    @FXML private TextField majorField;
    @FXML private ListView<String> languageList;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private ObservableList<String> availableLanguages = FXCollections.observableArrayList();

@FXML
public void initialize() {
    try {
        availableLanguages.addAll(DefineLanguagesApp.getLanguageList());
    } catch (Exception e) {
        System.err.println("Warning: Could not load languages from DefineLanguagesApp.");
    }

    languageList.setItems(availableLanguages);
    languageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
}

@FXML
    private void saveStudent() {
        String name = nameField.getText();
        String id = idField.getText();
        String email = emailField.getText();
        String major = majorField.getText();
        List<String> selectedLanguages = languageList.getSelectionModel().getSelectedItems();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            showAlert("Missing Fields", "Please fill in all required fields.");
            return;
        }

        // Append to local file
        try (FileWriter writer = new FileWriter("data/student_profiles.csv", true)) {
            writer.write(String.join(",", id, name, email, major, String.join("|", selectedLanguages)) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        showAlert("Success", "Student profile saved successfully!");
        clearFields();
    }
@FXML
    private void clearFields() {
        nameField.clear();
        idField.clear();
        emailField.clear();
        majorField.clear();
        languageList.getSelectionModel().clearSelection();
    }
@FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
@FXML
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
