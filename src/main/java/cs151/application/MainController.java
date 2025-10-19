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
    private Label menu_search;

    @FXML
    private Button menu_studentList;

    @FXML
    private Label menu_studentProfile;

    @FXML
    private Label menu_report;

    @FXML
    private Button define_programming_language;

    @FXML
    private Button create_student;

    @FXML
    private Button Student_List;

    // opens a new scene for that asks the user to define a programming language
    @FXML
    private void programming_language_click() {
        Stage plStage = cs151.application.DefineLanguagesApp.open();
        define_programming_language.setDisable(true);
        plStage.setOnHidden(e -> define_programming_language.setDisable(false));
    }

    // Switch scene
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToStudentList(ActionEvent event) throws IOException {
//        root = FXMLLoader.load(getClass().getResource("StudentList.fxml"));
//        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        stage.setTitle("Student List");
//        scene = new Scene(root);
//
//        stage.setScene(scene);
//        stage.show();

        Stage SLstage = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("StudentList.fxml"));
        SLstage.setTitle("Student List");
        SLstage.setScene(new Scene(root.load()));
        SLstage.show();

        menu_studentList.setDisable(true);
        SLstage.setOnHidden(e -> menu_studentList.setDisable(false));
    }

    @FXML
    public void openAddStudentPage(javafx.event.ActionEvent e) throws java.io.IOException {
        javafx.fxml.FXMLLoader fxml = new javafx.fxml.FXMLLoader(getClass().getResource("add_student.fxml"));
        javafx.scene.Scene scene = new javafx.scene.Scene(fxml.load());
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        //System.out.println("openAddStudentPage fired");

    }

}