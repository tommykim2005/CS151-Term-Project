package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AddStudentController {

    @FXML private TextField fullNameField;
    @FXML private TextField academicStatusField;
    @FXML private TextField currentJobStatusField;
    @FXML private TextField jobDetailsField;
    @FXML private ListView<String> languageList;
    @FXML private TextField databasesField;
    @FXML private TextField preferredRoleField;
    @FXML private TextArea commentsArea;
    @FXML private CheckBox whitelistCheck;
    @FXML private CheckBox blacklistCheck;

    @FXML private Button saveButton;
    @FXML private Button backButton;

    private final ObservableList<String> availableLanguages = FXCollections.observableArrayList();
    private static final Path FILE = Paths.get("data", "student_profiles.csv");

    @FXML
    public void initialize() {
        try {
            availableLanguages.setAll(DefineLanguagesApp.getLanguageList());
        } catch (Exception e) {
            availableLanguages.setAll("Java", "Python", "C++");
        }
        languageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        languageList.setItems(availableLanguages);
    }

    @FXML
    private void saveStudent() {
        String name = text(fullNameField);
        String academic = text(academicStatusField);
        String currentJob = text(currentJobStatusField);
        String jobDetails = text(jobDetailsField);
        String languages = String.join(";", languageList.getSelectionModel().getSelectedItems());
        String databases = text(databasesField);
        String preferredRole = text(preferredRoleField);
        String comments = text(commentsArea);
        boolean whitelist = whitelistCheck.isSelected();
        boolean blacklist = blacklistCheck.isSelected();

        if (name.isEmpty() || languages.isEmpty()) {
            showAlert("Missing Fields", "Full name and at least one language are required.");
            return;
        }

        try {
            if (!Files.exists(FILE.getParent())) Files.createDirectories(FILE.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(csv(name, academic, currentJob, jobDetails, languages, databases, preferredRole, comments,
                        Boolean.toString(whitelist), Boolean.toString(blacklist)));
                w.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save: " + e.getMessage());
            return;
        }

        showAlert("Success", "Student profile saved.");
        clearFields();
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

    private void clearFields() {
        fullNameField.clear();
        academicStatusField.clear();
        currentJobStatusField.clear();
        jobDetailsField.clear();
        databasesField.clear();
        preferredRoleField.clear();
        commentsArea.clear();
        whitelistCheck.setSelected(false);
        blacklistCheck.setSelected(false);
        languageList.getSelectionModel().clearSelection();
    }

    private static String text(TextInputControl c) { return c == null ? "" : c.getText().trim(); }
    private static String text(TextArea c) { return c == null ? "" : c.getText().trim(); }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static String csv(String... cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            String c = cols[i] == null ? "" : cols[i];
            if (c.contains(",") || c.contains("\"")) {
                c = c.replace("\"", "\"\"");
                c = "\"" + c + "\"";
            }
            sb.append(c);
            if (i < cols.length - 1) sb.append(',');
        }
        return sb.toString();
    }
}
