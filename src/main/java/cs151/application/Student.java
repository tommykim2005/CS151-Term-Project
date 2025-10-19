package cs151.application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

public class Student {
    @FXML
    private final String full_Name;

    @FXML
    private final String academic_Status;

    @FXML
    private final String current_Job_Status;

    @FXML
    private final String job_Details;

    @FXML
    private final String programming_Languages;

    @FXML
    private final String databases;

    @FXML
    private final String preferred_Role;

    @FXML
    private final String comments;

    @FXML
    private final BooleanProperty whitelist;

    @FXML
    private final BooleanProperty blacklist;

    public BooleanProperty whitelistProperty() {
        return this.whitelist;
    }

    public Boolean isWhitelist() {
        return this.whitelist.get();
    }

    public BooleanProperty blacklistProperty() {
        return this.blacklist;
    }

    public Boolean isBlacklist() {
        return this.blacklist.get();
    }

    public String getComments() {
        return this.comments;
    }

    public String getPreferred_Role() {
        return this.preferred_Role;
    }

    public String getDatabases() {
        return this.databases;
    }

    public String getProgramming_Languages() {
        return this.programming_Languages;
    }

    public String getJob_Details() {
        return this.job_Details;
    }

    public String getCurrent_Job_Status() {
        return this.current_Job_Status;
    }

    public String getAcademic_Status() {
        return this.academic_Status;
    }

    public String getFull_Name() {
        return this.full_Name;
    }

    public Student(String full_Name, String academic_Status, String current_Job_Status, String job_Details, String programming_Languages, String databases, String preferred_Role, String comments, Boolean whitelist, Boolean blacklist) {
        this.full_Name = full_Name;
        this.academic_Status = academic_Status;
        this.current_Job_Status = current_Job_Status;
        this.job_Details = job_Details;
        this.programming_Languages = programming_Languages;
        this.databases = databases;
        this.preferred_Role = preferred_Role;
        this.comments = comments;
        this.whitelist = new SimpleBooleanProperty(whitelist);
        this.blacklist = new SimpleBooleanProperty(blacklist);
    }
}
