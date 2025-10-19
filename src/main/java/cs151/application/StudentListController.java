package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class StudentListController implements Initializable {
    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, String> full_Name;
    @FXML private TableColumn<Student, String> academic_Status;
    @FXML private TableColumn<Student, String> current_Job_Status;
    @FXML private TableColumn<Student, String> job_Details;

    private final ObservableList<Student> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        full_Name.setCellValueFactory(new PropertyValueFactory<>("full_Name"));
        academic_Status.setCellValueFactory(new PropertyValueFactory<>("academic_Status"));
        current_Job_Status.setCellValueFactory(new PropertyValueFactory<>("current_Job_Status"));
        job_Details.setCellValueFactory(new PropertyValueFactory<>("job_Details"));

        StudentRepository.loadAll();
        items.setAll(StudentRepository.getAll());
        items.sort(Comparator.naturalOrder()); // case-insensitive by full name (via Comparable)
        table.setItems(items);
    }
}
