package cs151.application;

import javafx.beans.property.*;

public class Student implements Comparable<Student> {
    private final StringProperty full_Name = new SimpleStringProperty("");
    private final StringProperty academic_Status = new SimpleStringProperty("");
    private final StringProperty current_Job_Status = new SimpleStringProperty("");
    private final StringProperty job_Details = new SimpleStringProperty("");

    public Student() {}

    public Student(String fullName, String academic, String currentJob, String jobDetails) {
        setFull_Name(fullName);
        setAcademic_Status(academic);
        setCurrent_Job_Status(currentJob);
        setJob_Details(jobDetails);
    }

    public static String normalizeName(String name) { return name == null ? "" : name.trim(); }

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

    @Override
    public int compareTo(Student o) {
        String a = normalizeName(getFull_Name());
        String b = normalizeName(o.getFull_Name());
        return a.compareToIgnoreCase(b);
    }
}
