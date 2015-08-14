package com.almasb.fxcatalog;

import java.net.URL;
import java.util.ResourceBundle;

import com.almasb.fxcatalog.data.Book;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

public class AddBookDialogController implements Initializable {

    @FXML
    private DialogPane root;

    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldAuthors;
    @FXML
    private TextField fieldPublishers;
    @FXML
    private TextField fieldFormat;
    @FXML
    private TextField fieldNotes;
    @FXML
    private TextField fieldTags;

    @FXML
    private Button btnAdd;

    private Model model;

    public AddBookDialogController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAdd.disableProperty().bind(fieldName.textProperty().isEmpty()
                .or(fieldAuthors.textProperty().isEmpty())
                .or(fieldPublishers.textProperty().isEmpty())
                .or(fieldFormat.textProperty().isEmpty())
                .or(fieldNotes.textProperty().isEmpty())
                .or(fieldTags.textProperty().isEmpty()));
    }

    private boolean edit = false;

    public void setEdit(boolean b) {
        edit = b;
        Book book = (Book) root.getUserData();
        fieldName.setText(book.getName());
        fieldAuthors.setText(book.getAuthors());
        fieldPublishers.setText(book.getPublishers());
        fieldFormat.setText(book.getFormat());
        fieldNotes.setText(book.getNotes());
        fieldTags.setText(book.getTags());
    }

    @FXML
    private void onAdd() {
        Book book = new Book();
        book.setName(fieldName.getText());
        book.setAuthors(fieldAuthors.getText());
        book.setPublishers(fieldPublishers.getText());
        book.setFormat(fieldFormat.getText());
        book.setNotes(fieldNotes.getText());
        book.setTags(fieldTags.getText());

        if (!edit) {
            model.addBook(book);
        }
        else {
            model.removeBook((Book) root.getUserData());
            model.addBook(book);
            edit = false;
        }

        fieldName.clear();
        fieldAuthors.clear();
        fieldPublishers.clear();
        fieldFormat.clear();
        fieldNotes.clear();
        fieldTags.clear();
    }
}
