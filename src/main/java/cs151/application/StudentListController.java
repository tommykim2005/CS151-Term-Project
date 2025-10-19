package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class StudentListController implements Initializable {
    // Table
    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, String> full_Name;
    @FXML private TableColumn<Student, String> academic_Status;
    @FXML private TableColumn<Student, String> current_Job_Status;
    @FXML private TableColumn<Student, String> job_Details;
    @FXML private TableColumn<Student, String> programming_Languages;
    @FXML private TableColumn<Student, String> databases;
    @FXML private TableColumn<Student, String> preferred_Role;
    @FXML private TableColumn<Student, String> comments;
    @FXML private TableColumn<Student, Boolean> whitelist;
    @FXML private TableColumn<Student, Boolean> blacklist;

    // Search controls (all case-insensitive)
    @FXML private TextField nameSearch;
    @FXML private ComboBox<String> statusSearch;      // Any + statuses
    @FXML private TextField langSearch;               // comma separated tokens
    @FXML private TextField dbSearch;                 // comma separated tokens
    @FXML private ComboBox<String> roleSearch;        // Any + roles
    @FXML private Button filterBtn;
    @FXML private Button resetBtn;

    // Comment append + delete/edit
    @FXML private TextArea addCommentArea;
    @FXML private Button addCommentBtn;
    @FXML private Button deleteBtn;

    private final ObservableList<Student> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup table columns
        table.setEditable(true);

        full_Name.setCellValueFactory(c -> c.getValue().full_NameProperty());
        academic_Status.setCellValueFactory(c -> c.getValue().academic_StatusProperty());
        current_Job_Status.setCellValueFactory(c -> c.getValue().current_Job_StatusProperty());
        job_Details.setCellValueFactory(c -> c.getValue().job_DetailsProperty());
        programming_Languages.setCellValueFactory(c -> c.getValue().programming_LanguagesProperty());
        databases.setCellValueFactory(c -> c.getValue().databasesProperty());
        preferred_Role.setCellValueFactory(c -> c.getValue().preferred_RoleProperty());
        comments.setCellValueFactory(c -> c.getValue().commentsProperty());
        whitelist.setCellValueFactory(c -> c.getValue().whitelistProperty());
        blacklist.setCellValueFactory(c -> c.getValue().blacklistProperty());

        // Editable cells
        full_Name.setCellFactory(TextFieldTableCell.forTableColumn());
        full_Name.setOnEditCommit(e -> {
            String newName = Student.normalizeName(e.getNewValue());
            if (newName.isEmpty()) { table.refresh(); return; }
            // prevent duplicate names
            if (StudentRepository.getAll().stream()
                    .anyMatch(s -> s != e.getRowValue() &&
                                   Student.normalizeName(s.getFull_Name()).equalsIgnoreCase(newName))) {
                info("Duplicate", "Another student already has that name.");
                table.refresh(); return;
            }
            e.getRowValue().setFull_Name(newName);
            StudentRepository.sortByName();
            StudentRepository.saveAll();
            refreshAll();
        });

        academic_Status.setCellFactory(ComboBoxTableCell.forTableColumn(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"));
        academic_Status.setOnEditCommit(e -> {
            e.getRowValue().setAcademic_Status(e.getNewValue());
            StudentRepository.saveAll();
            refreshAll();
        });

        current_Job_Status.setCellFactory(ComboBoxTableCell.forTableColumn("Employed", "Not Employed"));
        current_Job_Status.setOnEditCommit(e -> {
            String v = e.getNewValue();
            Student s = e.getRowValue();
            s.setCurrent_Job_Status(v);
            if ("Employed".equalsIgnoreCase(v) && (s.getJob_Details() == null || s.getJob_Details().trim().isEmpty())) {
                info("Validation", "Job Details is required when Employed.");
            }
            StudentRepository.saveAll();
            refreshAll();
        });

        job_Details.setCellFactory(TextFieldTableCell.forTableColumn());
        job_Details.setOnEditCommit(e -> {
            e.getRowValue().setJob_Details(e.getNewValue() == null ? "" : e.getNewValue().trim());
            StudentRepository.saveAll();
            refreshAll();
        });

        programming_Languages.setCellFactory(TextFieldTableCell.forTableColumn());
        programming_Languages.setOnEditCommit(e -> {
            e.getRowValue().setProgramming_Languages(normListString(e.getNewValue()));
            StudentRepository.saveAll();
            refreshAll();
        });

        databases.setCellFactory(TextFieldTableCell.forTableColumn());
        databases.setOnEditCommit(e -> {
            e.getRowValue().setDatabases(normListString(e.getNewValue()));
            StudentRepository.saveAll();
            refreshAll();
        });

        preferred_Role.setCellFactory(ComboBoxTableCell.forTableColumn(
                "Front-End", "Back-End", "Full-Stack", "Data", "Other"));
        preferred_Role.setOnEditCommit(e -> {
            e.getRowValue().setPreferred_Role(e.getNewValue());
            StudentRepository.saveAll();
            refreshAll();
        });

        comments.setCellFactory(TextFieldTableCell.forTableColumn());
        comments.setOnEditCommit(e -> {
            e.getRowValue().setComments(e.getNewValue() == null ? "" : e.getNewValue().trim());
            StudentRepository.saveAll();
            refreshAll();
        });

        whitelist.setCellFactory(CheckBoxTableCell.forTableColumn(whitelist));
        blacklist.setCellFactory(CheckBoxTableCell.forTableColumn(blacklist));
        whitelist.setEditable(true);
        blacklist.setEditable(true);

        // keep mutual exclusivity on checkbox edit
        whitelist.setOnEditCommit(e -> {
            Student s = e.getRowValue();
            s.setWhitelist(e.getNewValue());
            if (e.getNewValue()) s.setBlacklist(false);
            StudentRepository.saveAll();
            refreshAll();
        });
        blacklist.setOnEditCommit(e -> {
            Student s = e.getRowValue();
            s.setBlacklist(e.getNewValue());
            if (e.getNewValue()) s.setWhitelist(false);
            StudentRepository.saveAll();
            refreshAll();
        });

        // Load data and attach to table
        StudentRepository.loadAll();
        items.setAll(StudentRepository.getAll());
        items.sort(Comparator.naturalOrder());
        table.setItems(items);

        // Search dropdowns
        statusSearch.setItems(FXCollections.observableArrayList(
                "Any", "Freshman", "Sophomore", "Junior", "Senior", "Graduate"));
        statusSearch.getSelectionModel().select("Any");

        roleSearch.setItems(FXCollections.observableArrayList(
                "Any", "Front-End", "Back-End", "Full-Stack", "Data", "Other"));
        roleSearch.getSelectionModel().select("Any");

        // Buttons
        filterBtn.setOnAction(e -> applyFilters());
        resetBtn.setOnAction(e -> resetFilters());
        addCommentBtn.setOnAction(e -> doAddComment());
        deleteBtn.setOnAction(e -> doDelete());
    }

    private void refreshAll() {
        items.setAll(StudentRepository.getAll());
        items.sort(Comparator.naturalOrder());
        table.refresh();
    }

    private void applyFilters() {
        String nameQ = val(nameSearch);
        String statusQ = opt(statusSearch);
        String roleQ = opt(roleSearch);
        List<String> langs = tokens(val(langSearch));
        List<String> dbs = tokens(val(dbSearch));
        List<Student> filtered = StudentRepository.search(nameQ, statusQ, langs, dbs, roleQ);
        items.setAll(filtered);
        table.refresh();
    }

    private void resetFilters() {
        nameSearch.clear();
        langSearch.clear();
        dbSearch.clear();
        statusSearch.getSelectionModel().select("Any");
        roleSearch.getSelectionModel().select("Any");
        refreshAll();
    }

    private void doAddComment() {
        Student s = table.getSelectionModel().getSelectedItem();
        if (s == null) { info("Select", "Select a student first."); return; }
        String text = val(addCommentArea);
        if (text.isEmpty()) { info("Validation", "Write a comment first."); return; }
        s.addComment(text);
        StudentRepository.saveAll();
        addCommentArea.clear();
        refreshAll();
    }

    private void doDelete() {
        Student s = table.getSelectionModel().getSelectedItem();
        if (s == null) { info("Select", "Select a student to delete."); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + s.getFull_Name() + "?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Confirm Delete");
        a.showAndWait();
        if (a.getResult() == ButtonType.OK) {
            StudentRepository.delete(s);
            refreshAll();
        }
    }

    // helpers
    private String normListString(String v) {
        if (v == null) return "";
        // normalize comma/semicolon separated lists to semicolons
        List<String> toks = Arrays.stream(v.split("[;,]"))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        return String.join(";", toks);
    }

    private String val(TextInputControl c) { return c == null ? "" : c.getText().trim(); }
    private String val(TextArea c) { return c == null ? "" : c.getText().trim(); }
    private String opt(ComboBox<String> cb) {
        String v = cb.getValue();
        return v == null ? "Any" : v.trim();
    }
    private List<String> tokens(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(s.split("[,;]")).map(String::trim).filter(t -> !t.isEmpty()).collect(Collectors.toList());
    }

    private void info(String head, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(head);
        a.setContentText(msg);
        a.showAndWait();
    }
}
