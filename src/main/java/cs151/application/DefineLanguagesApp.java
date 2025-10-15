package cs151.application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefineLanguagesApp extends Application {
    private TextField nameField = new TextField();
    private TextArea notesField = new TextArea();

    private TableView<Language> table = new TableView<>();
    private final ObservableList<Language> data = FXCollections.observableArrayList();

    private static final Path FILE = Paths.get("data", "languages.csv");

    @Override
    public void start(Stage stage) {
        // Top toolbar
        ToolBar topBar = new ToolBar();
        Label title = new Label("Define Programming Languages");
        Button loadBtn = new Button("Load");
        Button saveBtn = new Button("Save");
        Button exitBtn = new Button("Exit");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getItems().addAll(title, spacer, loadBtn, saveBtn, exitBtn);

        // Form
        notesField.setPrefRowCount(3);
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Language Name:"), 0, 0);
        form.add(nameField, 1, 0);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(75);
        form.getColumnConstraints().addAll(c1, c2);

        // Buttons
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        HBox actions = new HBox(10, addBtn, updateBtn, deleteBtn);

        // Table
        TableColumn<Language, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().name);
        nameCol.setPrefWidth(250);

        table.getColumns().addAll(nameCol);
        table.setItems(data);
        table.setPrefHeight(360);

        VBox center = new VBox(12, form, actions, table);
        center.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(center);

        addBtn.setOnAction(e -> addLanguage());
        updateBtn.setOnAction(e -> updateLanguage());
        deleteBtn.setOnAction(e -> deleteLanguage());
        loadBtn.setOnAction(e -> {
            data.setAll(load());
            table.refresh();
        });
        saveBtn.setOnAction(e -> save(new ArrayList<>(data)));

        exitBtn.setOnAction(e -> stage.close());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) return;
            nameField.setText(sel.name.get());
        });

        stage.setScene(new Scene(root, 800, 540));
        stage.setTitle("MentorLink - Define Programming Languages");
        stage.show();
    }

    private void addLanguage() {
        String name = trimOrEmpty(nameField.getText());
        if (name.isEmpty()) {
            info("Validation", "Language name is required.");
            return;
        }
        data.add(new Language(name));

        Collections.sort(data);

        clearForm();
    }

    public static Stage open() {
    Stage stage = new Stage();
    javafx.application.Platform.runLater(() -> {
        try {
            new DefineLanguagesApp().start(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });
    return stage;
    }

    //test

    private void updateLanguage() {
        Language sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Selection", "Select a language to update.");
            return;
        }
        String name = trimOrEmpty(nameField.getText());
        if (name.isEmpty()) {
            info("Validation", "Language name is required.");
            return;
        }
        sel.name.set(name);
        table.refresh();
        clearForm();
    }

    private void deleteLanguage() {
        Language sel = table.getSelectionModel().getSelectedItem();
        if (sel != null) data.remove(sel);
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void info(String head, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(head);
        a.showAndWait();
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    private void save(List<Language> langs) {
        try {
            if (!Files.exists(FILE.getParent())) Files.createDirectories(FILE.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                w.write("name,level,type,notes\n");

                for (Language l : langs) {
                    w.write(csv(l.name.get()));
                    w.write("\n");
                }
            }
            info("Saved", "Saved to " + FILE.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            info("Error", "Could not save file.");
        }
    }

    private ObservableList<Language> load() {
        ObservableList<Language> out = FXCollections.observableArrayList();
        if (!Files.exists(FILE)) return out;
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {

            String line; boolean header = true; String[] cols;

            while ((line = r.readLine()) != null) {
                if (header) { header = false; continue; }
                cols = parseCsv(line, 4);
                out.add(new Language(cols[0]));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            info("Error", "Could not load file.");
        }
        return out;
    }

    private static String csv(String s) {
        if (s == null) s = "";
        boolean quote = s.contains(",") || s.contains("\"") || s.contains("\n");
        String t = s.replace("\"", "\"\"");
        return quote ? "\"" + t + "\"" : t;
    }

    private static String[] parseCsv(String line, int expected) {
        String[] result = new String[expected];
        int idx = 0, i = 0;
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { sb.append('"'); i++; }
                    else inQuotes = false;
                } else sb.append(c);
            } else {
                if (c == ',') { result[idx++] = sb.toString(); sb.setLength(0); }
                else if (c == '"') inQuotes = true;
                else sb.append(c);
            }
            i++;
        }
        result[idx] = sb.toString();
        for (int k = 0; k < expected; k++) if (result[k] == null) result[k] = "";
        return result;
    }

    public static class Language implements Comparable<Language>{
        public final SimpleStringProperty name = new SimpleStringProperty("");

        public Language(String n) {
            this.name.set(n == null ? "" : n);
        }

        @Override
        public int compareTo(Language o) {
            return this.name.get().compareTo(o.name.get());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
