package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
