package com.almasb.fxcatalog.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Book {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty authors = new SimpleStringProperty();
    private StringProperty publishers = new SimpleStringProperty();
    private StringProperty format = new SimpleStringProperty();
    private StringProperty notes = new SimpleStringProperty();
    private StringProperty tags = new SimpleStringProperty();

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setAuthors(String authors) {
        this.authors.set(authors);
    }

    public String getAuthors() {
        return authors.get();
    }

    public StringProperty authorsProperty() {
        return authors;
    }

    public void setPublishers(String publishers) {
        this.publishers.set(publishers);
    }

    public String getPublishers() {
        return publishers.get();
    }

    public StringProperty publishersProperty() {
        return publishers;
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    public String getFormat() {
        return format.get();
    }

    public StringProperty formatProperty() {
        return format;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public void setTags(String tags) {
        this.tags.set(tags);
    }

    public String getTags() {
        return tags.get();
    }

    public StringProperty tagsProperty() {
        return tags;
    }
}
