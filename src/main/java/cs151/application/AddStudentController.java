package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class AddStudentController {

    // 2.1 Basic Information
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> academicStatusCombo;
    @FXML private RadioButton employedRadio;
    @FXML private RadioButton notEmployedRadio;
    @FXML private ToggleGroup jobStatusGroup;
    @FXML private TextField jobDetailsField;

    // 2.2 Skills & Interests
    @FXML private ListView<String> languagesList;
    @FXML private ListView<String> databasesList;
    @FXML private ComboBox<String> roleCombo;

    // 2.3 Faculty Evaluation
    @FXML private TextArea commentsArea;

    // 2.4 Future Services Flags
    @FXML private CheckBox whitelistCheck;
    @FXML private CheckBox blacklistCheck;

    // Buttons
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private static final Path FILE = Paths.get("data", "languages.csv");

    @FXML
    public void initialize() {
        // Academic Status
        academicStatusCombo.setItems(FXCollections.observableArrayList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));

        jobStatusGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean employed = employedRadio.isSelected();
            jobDetailsField.setDisable(!employed);
            if (!employed) jobDetailsField.clear();
        });

        List<String> langs = new ArrayList<>();

        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                langs.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            info("Error", "Could not load file.");
        }

        languagesList.setItems(FXCollections.observableArrayList(langs));
        languagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        databasesList.setItems(FXCollections.observableArrayList(
                "MySQL", "Postgres", "MongoDB"
        ));
        databasesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        roleCombo.setItems(FXCollections.observableArrayList(
                "Front-End", "Back-End", "Full-Stack", "Data", "Other"
        ));

        whitelistCheck.selectedProperty().addListener((o, ov, nv) -> { if (nv) blacklistCheck.setSelected(false); });
        blacklistCheck.selectedProperty().addListener((o, ov, nv) -> { if (nv) whitelistCheck.setSelected(false); });

        if (jobStatusGroup == null) {
            jobStatusGroup = new ToggleGroup();
            employedRadio.setToggleGroup(jobStatusGroup);
            notEmployedRadio.setToggleGroup(jobStatusGroup);
        }
    }

    @FXML
    private void saveStudent() {
        String name = normalize(fullNameField.getText());
        if (name.isEmpty()) { alert("Validation", "Full Name is required."); return; }

        String academic = vOf(academicStatusCombo);
        if (academic.isEmpty()) { alert("Validation", "Academic Status is required."); return; }

        String jobStatus = employedRadio.isSelected() ? "Employed" : "Not Employed";
        String jobDetails = normalize(jobDetailsField.getText());
        if ("Employed".equals(jobStatus) && jobDetails.isEmpty()) {
            alert("Validation", "Job Details is required when Employed."); return;
        }

        List<String> selectedLangs = new ArrayList<>(languagesList.getSelectionModel().getSelectedItems());
        if (selectedLangs.isEmpty()) { alert("Validation", "Select at least one Programming Language."); return; }

        List<String> selectedDBs = new ArrayList<>(databasesList.getSelectionModel().getSelectedItems());
        if (selectedDBs.isEmpty()) { alert("Validation", "Select at least one Database."); return; }

        String role = vOf(roleCombo);
        if (role.isEmpty()) { alert("Validation", "Preferred Professional Role is required."); return; }

        boolean white = whitelistCheck.isSelected();
        boolean black = blacklistCheck.isSelected();
        if (white && black) { alert("Validation", "Whitelist and Blacklist are mutually exclusive."); return; }

        if (StudentRepository.existsByName(name)) {
            alert("Duplicate", "A student with the same name already exists."); return;
        }
    //test
        String comments = normalize(commentsArea.getText());
        if (!comments.isEmpty()) {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            comments = today + ": " + comments;
        }

        StudentRepository.appendRow(
                name,
                academic,
                jobStatus,
                jobDetails,
                String.join(";", selectedLangs),
                String.join(";", selectedDBs),
                role,
                comments,
                white,
                black
        );

        alert("Saved", "Student profile saved.");
        clearForm();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helpers
    private static String normalize(String s) { return s == null ? "" : s.trim(); }
    private static String vOf(ComboBox<String> cb) { String v = cb.getValue(); return v == null ? "" : v.trim(); }

    private void clearForm() {
        fullNameField.clear();
        academicStatusCombo.getSelectionModel().clearSelection();
        notEmployedRadio.setSelected(true);
        jobDetailsField.clear();
        languagesList.getSelectionModel().clearSelection();
        databasesList.getSelectionModel().clearSelection();
        roleCombo.getSelectionModel().clearSelection();
        commentsArea.clear();
        whitelistCheck.setSelected(false);
        blacklistCheck.setSelected(false);
    }

    private void alert(String head, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(head);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void info(String head, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(head);
        a.showAndWait();
    }
}