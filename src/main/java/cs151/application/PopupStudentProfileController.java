package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PopupStudentProfileController {

    // --- FXML TextField ---
    @FXML private TextField full_name;
    @FXML private TextField academicStatus;
    @FXML private TextField jobStatus;
    @FXML private TextField jobDetail;
    @FXML private TextField languages;
    @FXML private TextField databases;
    @FXML private TextField role;

    // Table Elements
    @FXML private TableView<comment> commentsTable;
    @FXML private TableColumn<comment, String> dateAdded;
    @FXML private TableColumn<comment, String> commentText;

    private static final Path FILE = Paths.get("data", "student_data_test.csv");
    private final ObservableList<comment> comments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        dateAdded.setCellValueFactory(new PropertyValueFactory<>("date"));
        commentText.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentsTable.setItems(comments);

        // Enable resizing of table columns
        commentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Set Proportions of columns:
        // Date gets 25% of width, Comment gets 75%
        dateAdded.prefWidthProperty().bind(commentsTable.widthProperty().multiply(0.25));
        commentText.prefWidthProperty().bind(commentsTable.widthProperty().multiply(0.75));

        dateAdded.setMinWidth(100);
        commentText.setMaxWidth(500);

        commentText.setCellFactory(tc -> new TableCell<comment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    // A. Set the text
                    setText(item);

                    // B. FORCE the ellipsis (...)
                    setTextOverrun(OverrunStyle.ELLIPSIS);
                    setWrapText(false); // Ensure it stays on one line

                }
            }
        });

        commentsTable.setRowFactory(tv -> {
            TableRow<comment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() ) {
                    comment selected = row.getItem();
                    Alert a = new Alert(Alert.AlertType.NONE, selected.getComment(), ButtonType.CLOSE);
                    a.setResizable(true);
                    a.setHeight(300);
                    a.setHeaderText(selected.getDate());
                    a.showAndWait();
                }
            });
            return row;
        });

    }

    public void loadStudentData(String studentName) {
        comments.clear();

        loadProfileFromCSV(studentName);
        loadCommentsFromCSV(studentName);
    }

    private void loadProfileFromCSV(String studentName) {
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (fields.length > 1 && fields[0].trim().equalsIgnoreCase(studentName)) {

                    full_name.setText(fields[0]);
                    academicStatus.setText(fields[1]);
                    jobStatus.setText(fields[2]);
                    jobDetail.setText(fields[3]);
                    languages.setText(fields[4]);
                    databases.setText(fields[5]);
                    role.setText(fields[6]);

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCommentsFromCSV(String studentName) {
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (fields.length > 0 && fields[0].contains(studentName)) {
                    // Safety check: ensure line has enough columns
                    if (fields.length > 9) {
                        String commentBlock = fields[9];
                        commentBlock = commentBlock.replace("///", ",");

                        // Splitting custom format "date:: comment|||date:: comment"
                        String[] entries = commentBlock.split("\\|\\|\\|");

                        // Adjusted loop to start at 0 (unless string starts with |||)
                        for (String entry : entries) {
                            if (entry.contains(":: ")) {
                                String[] parts = entry.split(":: ");
                                if (parts.length >= 2) {
                                    comment c = new comment(parts[0], parts[1]);
                                    comments.add(c);
                                }
                            }
                        }
                    }
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
