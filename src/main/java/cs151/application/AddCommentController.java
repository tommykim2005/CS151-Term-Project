package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddCommentController {

    @FXML
    private Button exitButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextField name;

    @FXML
    private TextArea input;

    @FXML
    private TableView<comment> table;

    @FXML
    private TableColumn<comment, StringProperty> dateAdded;

    @FXML
    private TableColumn<comment, StringProperty> commentText;

    private static final Path FILE = Paths.get("data", "student_data_test.csv");
    private final ObservableList<comment> items = FXCollections.observableArrayList();

    public void initialize (){
        dateAdded.setCellValueFactory(new PropertyValueFactory<>("date"));
        commentText.setCellValueFactory(new PropertyValueFactory<>("comment"));

        table.setItems(items);
    }

    public void exit(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentSearch.fxml"));
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setTitle("MentorLink - Student Search");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        String comment = input.getText();
        writeComment(name.getText(), comment);

        input.clear();
    }

    public void preset(Student x){
        initialize();

        //adds name of student
        name.setText(x.getFull_Name());

        //loads existing comments
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;

            while ((line = r.readLine()) != null) {

                    String[] fields = line.split(",", -1);

                    if(fields[0].contains(x.getFull_Name())) {
                        String comment = fields[9];
                        String[] temp;
                        fields = comment.split("\\|\\|\\|", 0);

                        for(int i = 1; i < fields.length; i++){
                            temp = fields[i].split(":: ");
                            //comment c = new comment(temp[0], temp[1]);
                            comment c = new comment(temp[0],temp[1]);
                            items.add(c);
                        }
                    }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void writeComment(String name, String comment) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length > 0 && cols[0].trim().equalsIgnoreCase(name.trim())) {
                    if (cols.length < 10) cols = Arrays.copyOf(cols, 10);
                    String existing = cols[9] == null ? "" : cols[9];
                    cols[9] = existing + "|||" + LocalDateTime.now().format(fmt) + ":: " + comment;
                    line = String.join(",", cols);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter bw = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        comment c = new comment(LocalDateTime.now().format(fmt),comment);
        items.add(c);
    }

    public class comment {
        private final StringProperty date;
        private final StringProperty comment;

        public comment(String date, String comment) {
            this.date = new SimpleStringProperty(date);
            this.comment = new SimpleStringProperty(comment);
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public StringProperty dateProperty() {
            return date;
        }

        public String getComment() {
            return comment.get();
        }

        public void setComment(String comment) {
            this.comment.set(comment);
        }

        public StringProperty commentProperty() {
            return comment;
        }
    }


}
