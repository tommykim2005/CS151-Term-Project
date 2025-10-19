package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;

public class AddStudentController {

    // 2.1 Basic Information
    @FXML private TextField fullNameField;                                // required
    @FXML private ComboBox<String> academicStatusCombo;                   // required [Freshman, Sophomore, Junior, Senior, Graduate]
    @FXML private RadioButton employedRadio;                              // Employed
    @FXML private RadioButton notEmployedRadio;                           // Not Employed
    @FXML private ToggleGroup jobStatusGroup;                             // injected in FXML <fx:define>
    @FXML private TextField jobDetailsField;                              // required if employed

    @FXML private Button saveButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        academicStatusCombo.setItems(FXCollections.observableArrayList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));

        // Enable/disable Job Details based on job status
        jobStatusGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            boolean employed = employedRadio.isSelected();
            jobDetailsField.setDisable(!employed);
            if (!employed) jobDetailsField.clear();
        });

        // Safety net if ToggleGroup didn’t inject for any reason
        if (jobStatusGroup == null) {
            jobStatusGroup = new ToggleGroup();
            employedRadio.setToggleGroup(jobStatusGroup);
            notEmployedRadio.setToggleGroup(jobStatusGroup);
        }
    }

    @FXML
    private void saveStudent() {
        String name = Student.normalizeName(fullNameField.getText());
        if (name.isEmpty()) { alert("Validation", "Full Name is required."); return; }

        String academic = valueOf(academicStatusCombo);
        if (academic.isEmpty()) { alert("Validation", "Academic Status is required."); return; }

        String jobStatus = employedRadio.isSelected() ? "Employed" : "Not Employed";
        String jobDetails = jobDetailsField.getText() == null ? "" : jobDetailsField.getText().trim();
        if ("Employed".equals(jobStatus) && jobDetails.isEmpty()) {
            alert("Validation", "Job Details is required when Employed."); return;
        }

        // Duplicate prevention (trimmed full name, case-insensitive)
        StudentRepository.loadAll();
        if (StudentRepository.existsByName(name)) {
            alert("Duplicate", "A student with the same name already exists."); return;
        }

        Student s = new Student(name, academic, jobStatus, jobDetails);
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
    }

    private String valueOf(ComboBox<String> cb) {
        String v = cb.getValue();
        return v == null ? "" : v.trim();
    }

    private void alert(String head, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(head);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }
}
