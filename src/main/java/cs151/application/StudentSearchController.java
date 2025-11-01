package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class StudentSearchController implements Initializable {
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

    @FXML
    private Button searchButton;

    @FXML
    private Button editButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField searchField;

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
    }

    @FXML
    public void exitSearchPage()  {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void searchStudent(){
            String search = searchField.getText().toLowerCase();
            table.getItems().clear();

            try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                String line;

                while ((line = r.readLine()) != null) {
                    if(line.toLowerCase().contains(search)) {

                        String[] fields = line.split(",", -1);

                        boolean Whitelist = fields[8].equalsIgnoreCase("TRUE");

                        boolean Blacklist = fields[9].equalsIgnoreCase("TRUE");

                        Student output = new Student(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], Whitelist, Blacklist);
                        items.add(output);
                    }
                }

                Collections.sort(items);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    @FXML
    public void deleteStudent(){
        Student target = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(target);

        StudentRepository.deleteStudent(target.getFull_Name());
    }

    @FXML
    public void editStudent() throws IOException{
        //open edit student profile
        Student target = table.getSelectionModel().getSelectedItem();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("edit_student.fxml"));
        Scene scene = new Scene(fxml.load());
        Stage stage = (Stage) (editButton).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        EditStudentController editor = fxml.getController();
        editor.preset(target);
    }


}
