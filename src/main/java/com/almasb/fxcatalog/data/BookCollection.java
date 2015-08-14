package com.almasb.fxcatalog.data;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookCollection {
    private ObservableList<Book> books = FXCollections.observableArrayList();

    public void setBooks(List<Book> books) {
        this.books.addAll(books);
    }

    public List<Book> getBooks() {
        return books;
    }

    public ObservableList<Book> booksProperty() {
        return books;
    }
}
