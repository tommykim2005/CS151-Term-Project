package cs151.application;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DefineLanguagesApp extends Application {
    private TextField nameField = new TextField();
    private static List<String> languages = new ArrayList<>();

    private TableView<Language> table = new TableView<>();
    private final ObservableList<Language> data = FXCollections.observableArrayList();

    private static final Path FILE = Paths.get("data", "languages.csv");

    static {
        try {
            List<String> loaded = loadStatic();
            if (loaded == null || loaded.size() != 3) {
                languages = new ArrayList<>();
                languages.add("Java");
                languages.add("Python");
                languages.add("C++");
            } else {
                languages = new ArrayList<>(loaded);
            }
        } catch (Exception e) {
            languages = new ArrayList<>();
            languages.add("Java");
            languages.add("Python");
            languages.add("C++");
        }
    }

    @Override
    public void start(Stage stage) {
        ToolBar topBar = new ToolBar();
        Label title = new Label("Define Programming Languages");
        Button loadBtn = new Button("Load");
        Button saveBtn = new Button("Save");
        Button exitBtn = new Button("Exit");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getItems().addAll(title, spacer, loadBtn, saveBtn, exitBtn);

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(12));
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);

        Button addBtn = new Button("Add");
        Button deleteBtn = new Button("Delete");
        HBox actions = new HBox(10, addBtn, deleteBtn);

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
        deleteBtn.setOnAction(e -> deleteLanguage());
        loadBtn.setOnAction(e -> {
            List<Language> loaded = load();
            data.setAll(loaded);
            List<String> ls = loaded.stream().map(l -> l.name.get()).collect(Collectors.toList());
            if (ls.size() != 3) {
                info("Note", "Exactly 3 languages are required. Resetting to Java, Python, C++.");
                setDefaultThree();
                data.setAll(languages.stream().map(Language::new).collect(Collectors.toList()));
            } else {
                languages = new ArrayList<>(ls);
            }
            table.refresh();
        });
        saveBtn.setOnAction(e -> save(new ArrayList<>(data)));
        exitBtn.setOnAction(e -> stage.close());

        data.setAll(languages.stream().map(Language::new).collect(Collectors.toList()));

        stage.setTitle("Define Languages");
        stage.setScene(new Scene(root, 480, 520));
        stage.show();
    }

    public static Stage open() {
        Stage stage = new Stage();
        try {
            // start(...) is already on the JavaFX Application Thread when called from a
            // controller event
            new DefineLanguagesApp().start(stage); // your start() should set a Scene and call stage.show()
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stage;
    }

    private void addLanguage() {
        String n = trimOrEmpty(nameField.getText());
        if (n.isEmpty()) {
            info("Validation", "Name is required");
            return;
        }
        data.add(new Language(n));
        languages = data.stream().map(l -> l.name.get()).collect(Collectors.toList());
        clearForm();
    }

    private void deleteLanguage() {
        Language sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Select a row", "Please select a language to delete.");
            return;
        }
        data.remove(sel);
        languages = data.stream().map(l -> l.name.get()).collect(Collectors.toList());
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

    private void save(List<Language> langs) {
        try {
            if (!Files.exists(FILE.getParent()))
                Files.createDirectories(FILE.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Language l : langs) {
                    w.write(csv(l.name.get()));
                    w.newLine();
                }
            }
            languages = langs.stream().map(l -> l.name.get()).collect(Collectors.toList());
            if (languages.size() != 3) {
                setDefaultThree();
            }
            info("Saved", "Languages saved successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
            info("Error", "Failed to save: " + ex.getMessage());
        }
    }

    private static List<String> loadStatic() {
        if (!Files.exists(FILE))
            return null;
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            List<String> out = new ArrayList<>();
            String line;
            while ((line = r.readLine()) != null) {
                String name = trimOrEmpty(parseCsv(line)[0]);
                if (!name.isEmpty())
                    out.add(name);
            }
            return out;
        } catch (IOException ex) {
            return null;
        }
    }

    private List<Language> load() {
        List<Language> out = new ArrayList<>();
        List<String> loaded = loadStatic();
        if (loaded == null || loaded.isEmpty()) {
            setDefaultThree();
            loaded = new ArrayList<>(languages);
        }
        for (String n : loaded)
            out.add(new Language(n));
        Collections.sort(out);
        return out;
    }

    private static void setDefaultThree() {
        languages.clear();
        languages.add("Java");
        languages.add("Python");
        languages.add("C++");
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static String csv(String... cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            String c = cols[i] == null ? "" : cols[i];
            if (c.contains(",") || c.contains("\"")) {
                c = c.replace("\"", "\"\"");
                c = "\"" + c + "\"";
            }
            sb.append(c);
            if (i < cols.length - 1)
                sb.append(',');
        }
        return sb.toString();
    }

    private static String[] parseCsv(String line) {
        List<String> out = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '\"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        cur.append('\"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(ch);
                }
            } else {
                if (ch == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else if (ch == '\"') {
                    inQuotes = true;
                } else {
                    cur.append(ch);
                }
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    public static class Language implements Comparable<Language> {
        public final SimpleStringProperty name = new SimpleStringProperty("");

        public Language(String n) {
            this.name.set(n == null ? "" : n);
        }

        @Override
        public int compareTo(Language o) {
            return this.name.get().compareToIgnoreCase(o.name.get());
        }
    }

    public static List<String> getLanguageList() {
        if (languages == null || languages.size() != 3) {
            setDefaultThree();
        }
        return new ArrayList<>(languages);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
