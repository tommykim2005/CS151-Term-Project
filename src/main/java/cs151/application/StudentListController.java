package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.ResourceBundle;

public class StudentListController implements Initializable {
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

    private static final Path FILE = Paths.get("data", "student_profiles.csv");
    private final ObservableList<Student> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        full_Name.setCellValueFactory(new PropertyValueFactory<>("full_Name"));
        academic_Status.setCellValueFactory(new PropertyValueFactory<>("academic_Status"));
        current_Job_Status.setCellValueFactory(new PropertyValueFactory<>("current_Job_Status"));
        job_Details.setCellValueFactory(new PropertyValueFactory<>("job_Details"));
        programming_Languages.setCellValueFactory(new PropertyValueFactory<>("programming_Languages"));
        databases.setCellValueFactory(new PropertyValueFactory<>("databases"));
        preferred_Role.setCellValueFactory(new PropertyValueFactory<>("preferred_Role"));
        comments.setCellValueFactory(new PropertyValueFactory<>("comments"));

        whitelist.setCellValueFactory(new PropertyValueFactory<>("whitelist"));
        whitelist.setCellFactory(CheckBoxTableCell.forTableColumn(whitelist));
        whitelist.setEditable(false);

        blacklist.setCellValueFactory(new PropertyValueFactory<>("blacklist"));
        blacklist.setCellFactory(CheckBoxTableCell.forTableColumn(blacklist));
        blacklist.setEditable(false);

        table.setItems(items);

        load();
        FXCollections.sort(items, Comparator.comparing(s -> s.getFull_Name().toLowerCase()));
    }

    private void load() {
        items.clear();
        if (!Files.exists(FILE)) return;

        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);

                String full_Name = safe(fields, 0);
                String Academic_Status = safe(fields, 1);
                String Current_Job_Status = safe(fields, 2);
                String Job_Details = safe(fields, 3);
                String Programming_Languages = safe(fields, 4);
                String Databases = safe(fields, 5);
                String Preferred_Role = safe(fields, 6);
                String Comments = safe(fields, 7);
                boolean Whitelist = "TRUE".equalsIgnoreCase(safe(fields, 8)) || "true".equalsIgnoreCase(safe(fields, 8));
                boolean Blacklist = "TRUE".equalsIgnoreCase(safe(fields, 9)) || "true".equalsIgnoreCase(safe(fields, 9));

                Student s = new Student(full_Name, Academic_Status, Current_Job_Status, Job_Details,
                        Programming_Languages, Databases, Preferred_Role, Comments, Whitelist, Blacklist);
                items.add(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String safe(String[] a, int i) { return (i < a.length && a[i] != null) ? a[i] : ""; }
}
