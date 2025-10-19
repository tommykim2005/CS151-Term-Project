package cs151.application;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Student implements Comparable<Student> {
    // Properties
    private final StringProperty full_Name = new SimpleStringProperty("");
    private final StringProperty academic_Status = new SimpleStringProperty(""); // Freshman, Sophomore, Junior, Senior, Graduate
    private final StringProperty current_Job_Status = new SimpleStringProperty(""); // Employed, Not Employed
    private final StringProperty job_Details = new SimpleStringProperty("");
    private final StringProperty programming_Languages = new SimpleStringProperty(""); // semicolon-separated
    private final StringProperty databases = new SimpleStringProperty("");             // semicolon-separated
    private final StringProperty preferred_Role = new SimpleStringProperty("");        // Front-End, Back-End, Full-Stack, Data, Other
    private final StringProperty comments = new SimpleStringProperty("");              // multiple entries appended with delimiter
    private final BooleanProperty whitelist = new SimpleBooleanProperty(false);
    private final BooleanProperty blacklist = new SimpleBooleanProperty(false);

    public static final String COMMENT_DELIM = "|||";
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public Student() {}

    public Student(String fullName, String academic, String currentJob, String jobDetails,
                   String langs, String dbs, String role, String comm, boolean white, boolean black) {
        setFull_Name(fullName);
        setAcademic_Status(academic);
        setCurrent_Job_Status(currentJob);
        setJob_Details(jobDetails);
        setProgramming_Languages(langs);
        setDatabases(dbs);
        setPreferred_Role(role);
        setComments(comm);
        setWhitelist(white);
        setBlacklist(black);
    }

    // Getters/Setters & properties
    public String getFull_Name() { return full_Name.get(); }
    public void setFull_Name(String v) { full_Name.set(v); }
    public StringProperty full_NameProperty() { return full_Name; }

    public String getAcademic_Status() { return academic_Status.get(); }
    public void setAcademic_Status(String v) { academic_Status.set(v); }
    public StringProperty academic_StatusProperty() { return academic_Status; }

    public String getCurrent_Job_Status() { return current_Job_Status.get(); }
    public void setCurrent_Job_Status(String v) { current_Job_Status.set(v); }
    public StringProperty current_Job_StatusProperty() { return current_Job_Status; }

    public String getJob_Details() { return job_Details.get(); }
    public void setJob_Details(String v) { job_Details.set(v); }
    public StringProperty job_DetailsProperty() { return job_Details; }

    public String getProgramming_Languages() { return programming_Languages.get(); }
    public void setProgramming_Languages(String v) { programming_Languages.set(v); }
    public StringProperty programming_LanguagesProperty() { return programming_Languages; }

    public String getDatabases() { return databases.get(); }
    public void setDatabases(String v) { databases.set(v); }
    public StringProperty databasesProperty() { return databases; }

    public String getPreferred_Role() { return preferred_Role.get(); }
    public void setPreferred_Role(String v) { preferred_Role.set(v); }
    public StringProperty preferred_RoleProperty() { return preferred_Role; }

    public String getComments() { return comments.get(); }
    public void setComments(String v) { comments.set(v); }
    public StringProperty commentsProperty() { return comments; }

    public boolean getWhitelist() { return whitelist.get(); }
    public void setWhitelist(boolean v) { whitelist.set(v); }
    public BooleanProperty whitelistProperty() { return whitelist; }

    public boolean getBlacklist() { return blacklist.get(); }
    public void setBlacklist(boolean v) { blacklist.set(v); }
    public BooleanProperty blacklistProperty() { return blacklist; }

    // Business rules helpers
    public static String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    public void addComment(String text) {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) return;
        String stamped = "[" + LocalDate.now().format(DATE_FMT) + "] " + t;
        String existing = getComments();
        if (existing == null || existing.isEmpty()) {
            setComments(stamped);
        } else {
            setComments(existing + COMMENT_DELIM + stamped);
        }
    }

    public String commentsMultiline() {
        String c = getComments();
        if (c == null || c.isEmpty()) return "";
        return Arrays.stream(c.split("\\Q" + COMMENT_DELIM + "\\E"))
                .map(String::trim).collect(Collectors.joining("\n"));
    }

    @Override
    public int compareTo(Student o) {
        String a = normalizeName(getFull_Name());
        String b = normalizeName(o.getFull_Name());
        return a.compareToIgnoreCase(b);
    }
}
