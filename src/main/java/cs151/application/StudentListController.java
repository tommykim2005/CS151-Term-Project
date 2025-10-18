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

        Collections.sort(items);

        table.setItems(items);

        load();
    }

    private void load() {

        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {

            String line; String[] cols;

            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);

                String full_Name = fields[0];
                String Academic_Status = fields[1];
                String Current_Job_Status = fields[2];
                String Job_Details = fields[3];
                String Programming_Languages = fields[4];
                String Databases = fields[5];
                String Preferred_Role = fields[6];
                String Comments = fields[7];

                boolean Whitelist;
                Whitelist = fields[8].equalsIgnoreCase("TRUE");

                boolean Blacklist;
                Blacklist = fields[9].equalsIgnoreCase("TRUE");

                Student output = new Student(full_Name,Academic_Status,Current_Job_Status,Job_Details,Programming_Languages,Databases,Preferred_Role,Comments,Whitelist,Blacklist);
                items.add(output);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //return out;
    }

}
