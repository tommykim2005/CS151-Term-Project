package cs151.application;

import cs151.application.DefineLanguagesApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Label logo;

    @FXML
    private Button menu_search;

    @FXML
    private Button menu_studentList;

    @FXML
    private Button menu_report;

    @FXML
    private Button define_programming_language;

    @FXML
    private Button create_student;

    // opens a new scene for that asks the user to define a programming language
    @FXML
    private void programming_language_click(ActionEvent e) {
        cs151.application.DefineLanguagesApp.open(e);
    }

    public void openStudentListPage() throws IOException {
        Stage SLstage = (Stage) menu_studentList.getScene().getWindow();
        FXMLLoader root = new FXMLLoader(getClass().getResource("StudentList.fxml"));
        SLstage.setTitle("MentorLink - Student List");
        SLstage.setScene(new Scene(root.load()));
        SLstage.show();
    }

    @FXML
    public void openAddStudentPage() throws IOException {
        Stage stage = (Stage) create_student.getScene().getWindow();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("add_student.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setTitle("MentorLink - Add Student Page");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openSearchPage() throws IOException{
        Stage stage = (Stage) menu_search.getScene().getWindow();
        FXMLLoader root = new FXMLLoader(getClass().getResource("studentSearch.fxml"));
        stage.setTitle("MentorLink - Student Search");
        stage.setScene(new Scene(root.load()));
        stage.show();

        menu_search.setDisable(true);
        stage.setOnHidden(e -> menu_search.setDisable(false));
    }

    @FXML
    public void openReportPage(){

    }

}