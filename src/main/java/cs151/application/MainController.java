    package cs151.application;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
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

        //opens a new scene for that asks the user to define a programming language

    @FXML
    private void programming_language_click() {
        Stage plStage = cs151.application.DefineLanguagesApp.open();
        define_programming_language.setDisable(true);
        plStage.setOnHidden(e -> define_programming_language.setDisable(false));
    }

        @FXML
        private void switchToStudentList(ActionEvent event) throws IOException{
            Stage SLstage = new Stage();
            FXMLLoader root = new FXMLLoader(getClass().getResource("StudentList.fxml"));
            SLstage.setTitle("Student List");
            SLstage.setScene(new Scene(root.load()));
            SLstage.show();

            menu_studentList.setDisable(true);
            SLstage.setOnHidden(e -> menu_studentList.setDisable(false));
        }



    }