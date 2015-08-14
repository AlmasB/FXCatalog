package com.almasb.fxcatalog;

import java.net.URL;
import java.util.ResourceBundle;

import com.almasb.fxcatalog.data.Book;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddBookDialogController implements Initializable {

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

    @FXML
    private void onAdd() {
        Book book = new Book();
        book.setName(fieldName.getText());
        book.setAuthors(fieldAuthors.getText());
        book.setPublishers(fieldPublishers.getText());
        book.setFormat(fieldFormat.getText());
        book.setNotes(fieldNotes.getText());
        book.setTags(fieldTags.getText());

        model.addBook(book);

        fieldName.clear();
        fieldAuthors.clear();
        fieldPublishers.clear();
        fieldFormat.clear();
        fieldNotes.clear();
        fieldTags.clear();
    }
}
