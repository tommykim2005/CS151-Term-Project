    package cs151.application;

    import cs151.application.DefineLanguagesApp;


    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
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

        //opens a new scene for that asks the user to define a programming language

    @FXML
    private void programming_language_click() {
        Stage plStage = cs151.application.DefineLanguagesApp.open();
        define_programming_language.setDisable(true);
        plStage.setOnHidden(e -> define_programming_language.setDisable(false));
    }

@FXML
private void switchToStudentList() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentList.fxml"));
    Scene scene = new Scene(loader.load());
    Stage stage = (Stage) menu_studentList.getScene().getWindow();
    stage.setScene(scene);
    stage.show();
}



@FXML
private void openAddStudentPage() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("add_student.fxml"));
    Stage stage = new Stage();
    stage.setScene(new Scene(loader.load()));
    stage.setTitle("Add Student");
    stage.show();
}








    }