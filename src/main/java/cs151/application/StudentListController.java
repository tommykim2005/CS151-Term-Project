package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ResourceBundle;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;

public class StudentListController implements Initializable {

    @FXML
    private TableView<Student> table;

    @FXML
    private TableColumn<Student, String> full_Name;

    @FXML
    private TableColumn<Student, String> academic_Status;

    @FXML
    private TableColumn<Student, String> current_Job_Status;

    @FXML
    private TableColumn<Student, String> databases;

    @FXML
    private TableColumn<Student, String> job_Details;

    @FXML
    private TableColumn<Student,String> preferred_Role;

    @FXML
    private TableColumn<Student, String> programming_Languages;

    @FXML
    private TableColumn<Student, String> comments;

    @FXML
    private TableColumn<Student, Boolean> whitelist;

    @FXML
    private TableColumn<Student, Boolean> blacklist;

    private static final Path FILE = Paths.get("data", "student_data_test.csv");
    private final ObservableList<Student> items = FXCollections.observableArrayList();


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle){

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
    }

    private void load() {

        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {

            String line;

            while ((line = r.readLine()) != null) {
                String[] fields = line.split("\\|", -1);

                boolean Whitelist = fields[8].equalsIgnoreCase("TRUE");

                boolean Blacklist = fields[9].equalsIgnoreCase("TRUE");

                Student output = new Student(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], Whitelist, Blacklist);
                items.add(output);
            }

            Collections.sort(items);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //return out;
    }

}
