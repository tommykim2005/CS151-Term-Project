package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

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
    private TableColumn<Student, Boolean> whitelist;

    @FXML
    private TableColumn<Student, Boolean> blacklist;

    @FXML
    private Button exitButton;

    @FXML
    private RadioButton WhiteList;

    @FXML
    private RadioButton BlackList;

    @FXML
    private ToggleGroup serviceFlag;

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

        whitelist.setCellValueFactory(new PropertyValueFactory<>("whitelist"));
        whitelist.setCellFactory(CheckBoxTableCell.forTableColumn(whitelist));
        whitelist.setEditable(false);

        blacklist.setCellValueFactory(new PropertyValueFactory<>("blacklist"));
        blacklist.setCellFactory(CheckBoxTableCell.forTableColumn(blacklist));
        blacklist.setEditable(false);

        if (serviceFlag == null) {
            serviceFlag = new ToggleGroup();
            WhiteList.setToggleGroup(serviceFlag);
            BlackList.setToggleGroup(serviceFlag);
        }

        serviceFlag.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean white = WhiteList.isSelected();
            boolean black = BlackList.isSelected();
            switchList(white, black);
        });

        table.setItems(items);

        load();

        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Check for double-click AND ensure the row is not empty
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Student selectedStudent = row.getItem();
                    openPopup(selectedStudent);
                }
            });
            return row;
        });
    }

    private void load() {

        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {

            String line;

            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);

                boolean Whitelist = fields[7].equalsIgnoreCase("TRUE");

                boolean Blacklist = fields[8].equalsIgnoreCase("TRUE");

                Student output = new Student(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], Whitelist, Blacklist, fields[9]);
                items.add(output);
            }

            Collections.sort(items);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void switchList(boolean checkWhite, boolean checkBlack){
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            items.clear();

            String line; Student output;

            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);

                boolean Whitelist = fields[7].equalsIgnoreCase("TRUE");

                boolean Blacklist = fields[8].equalsIgnoreCase("TRUE");


                if(Whitelist==checkWhite && Blacklist==checkBlack) {
                    output = new Student(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], Whitelist, Blacklist, fields[9]);
                    items.add(output);
                } 
            }

            Collections.sort(items);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void exit() throws IOException{
        Stage stage = (Stage) exitButton.getScene().getWindow();
        FXMLLoader root = new FXMLLoader(getClass().getResource("main.fxml"));
        stage.setTitle("MentorLink - Home Page");
        stage.setScene(new Scene(root.load()));
        stage.show();
    }

    private void openPopup(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("popup_student_profile.fxml"));
            Parent root = loader.load();

            PopupStudentProfileController controller = loader.getController();

            controller.loadStudentData(student.getFull_Name());

            Stage stage = new Stage();
            if (table.getScene() != null) {
                stage.initOwner(table.getScene().getWindow());
            }

            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle("Student Profile: " + student.getFull_Name());
            stage.setScene(new Scene(root));

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
