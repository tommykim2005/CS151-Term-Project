package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddStudentController {

    // 2.1 Basic Information
    @FXML private TextField fullNameField;                                // required
    @FXML private ComboBox<String> academicStatusCombo;                   // required [Freshman, Sophomore, Junior, Senior, Graduate]
    @FXML private RadioButton employedRadio;                              // Employed
    @FXML private RadioButton notEmployedRadio;                           // Not Employed
    @FXML private ToggleGroup jobStatusGroup;                             // group
    @FXML private TextField jobDetailsField;                              // required if employed

    // 2.2 Skills & Interests
    @FXML private ListView<String> languagesList;                         // multi-select from DefineLanguagesApp
    @FXML private ListView<String> databasesList;                         // multi-select hard-coded
    @FXML private ComboBox<String> roleCombo;                             // required [Front-End, Back-End, Full-Stack, Data, Other]

    // 2.3 Faculty Evaluation
    @FXML private TextArea commentsArea;                                  // multi-line, can append later

    // 2.4 Future Services Flags (mutually exclusive)
    @FXML private CheckBox whitelistCheck;
    @FXML private CheckBox blacklistCheck;

    @FXML private Button saveButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        // Academic status
        academicStatusCombo.setItems(FXCollections.observableArrayList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));

        // Job status radios in a ToggleGroup (set in FXML) + job details enable/disable
        jobStatusGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            boolean employed = employedRadio.isSelected();
            jobDetailsField.setDisable(!employed);
            if (!employed) jobDetailsField.clear();
        });

        // Languages (exactly the 3 defined)
        List<String> langs;
        try {
            langs = DefineLanguagesApp.getLanguageList();
        } catch (Exception e) {
            langs = Arrays.asList("Java", "Python", "C++");
        }
        languagesList.setItems(FXCollections.observableArrayList(langs));
        languagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Databases (hard-coded, can add more)
        databasesList.setItems(FXCollections.observableArrayList(
                "MySQL", "Postgres", "MongoDB"
        ));
        databasesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Roles
        roleCombo.setItems(FXCollections.observableArrayList(
                "Front-End", "Back-End", "Full-Stack", "Data", "Other"
        ));

        // Mutual exclusivity
        whitelistCheck.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) blacklistCheck.setSelected(false);
        });
        blacklistCheck.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) whitelistCheck.setSelected(false);
        });
    }

    @FXML
    private void saveStudent() {
        // Required validations
        String name = Student.normalizeName(fullNameField.getText());
        if (name.isEmpty()) { alert("Validation", "Full Name is required."); return; }

        String academic = valueOf(academicStatusCombo);
        if (academic.isEmpty()) { alert("Validation", "Academic Status is required."); return; }

        String jobStatus = employedRadio.isSelected() ? "Employed" : "Not Employed";
        String jobDetails = jobDetailsField.getText() == null ? "" : jobDetailsField.getText().trim();
        if ("Employed".equals(jobStatus) && jobDetails.isEmpty()) {
            alert("Validation", "Job Details is required when Employed.");
            return;
        }

        List<String> selectedLangs = new ArrayList<>(languagesList.getSelectionModel().getSelectedItems());
        if (selectedLangs.isEmpty()) { alert("Validation", "Select at least one Programming Language."); return; }

        List<String> selectedDBs = new ArrayList<>(databasesList.getSelectionModel().getSelectedItems());
        if (selectedDBs.isEmpty()) { alert("Validation", "Select at least one Database."); return; }

        String role = valueOf(roleCombo);
        if (role.isEmpty()) { alert("Validation", "Preferred Professional Role is required."); return; }

        boolean white = whitelistCheck.isSelected();
        boolean black = blacklistCheck.isSelected();
        if (white && black) { alert("Validation", "Whitelist and Blacklist are mutually exclusive."); return; }

        // Duplicate prevention by trimmed full name
        StudentRepository.loadAll(); // ensure cache is fresh
        if (StudentRepository.existsByName(name)) {
            alert("Duplicate", "A student with the same name already exists.");
            return;
        }

        Student s = new Student(
                name,
                academic,
                jobStatus,
                jobDetails,
                String.join(";", selectedLangs),
                String.join(";", selectedDBs),
                role,
                commentsArea.getText() == null ? "" : commentsArea.getText().trim(),
                white,
                black
        );

        StudentRepository.add(s);
        alert("Saved", "Student profile saved.");
        clearForm();
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

    private String valueOf(ComboBox<String> cb) {
        String v = cb.getValue();
        return v == null ? "" : v.trim();
    }

    // CHANGED: return type is void; no return value
    private void alert(String head, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(head);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }
}
