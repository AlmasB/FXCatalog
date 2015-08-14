package com.almasb.fxcatalog;

import java.util.Optional;

import com.almasb.fxcatalog.data.Book;
import com.almasb.fxcatalog.data.BookCollection;
import com.almasb.fxcatalog.data.User;
import com.almasb.fxcatalog.dbms.DBMS;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public final class Model {

    private DBMS<User, BookCollection> dbms;
    private User user;
    private Optional<BookCollection> bookCollection = Optional.empty();

    private ReadOnlyBooleanWrapper connectedDBMS = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyBooleanWrapper loggedIn = new ReadOnlyBooleanWrapper(false);

    public Model(DBMS<User, BookCollection> dbms) {
        this.dbms = dbms;
    }

    public void login(String username, String password) throws Exception {
        User user = new User(username, password);
        if (dbms.containsKey(user)) {
            this.user = user;
            bookCollection = Optional.of(dbms.get(user));
            loggedIn.set(true);
        }
        else {
            throw new IllegalArgumentException("Incorrect username/password");
        }
    }

    public void logout() {
        user = null;
        bookCollection = Optional.empty();
        loggedIn.set(false);
    }

    public void addBook(Book book) {
        bookCollection.ifPresent(col -> col.getBooks().add(book));
    }

    public void removeBook(Book book) {
        bookCollection.ifPresent(col -> col.getBooks().remove(book));
    }

    public void saveDBMS() throws Exception {
        bookCollection.ifPresent(col -> dbms.put(user, col));
    }

    public void connectDBMS() throws Exception {
        dbms.connect();
        connectedDBMS.set(true);
    }

    public void closeDBMS() throws Exception {
        dbms.close();
        loggedIn.set(false);
        connectedDBMS.set(false);
    }

    public ReadOnlyBooleanProperty connectedDBMSProperty() {
        return connectedDBMS.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty loggedInProperty() {
        return loggedIn.getReadOnlyProperty();
    }

    public Optional<BookCollection> getBookCollection() {
        return bookCollection;
    }
}
