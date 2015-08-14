package com.almasb.fxcatalog;

import java.net.URL;
import java.util.ResourceBundle;

import com.almasb.fxcatalog.data.Book;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private TextField fieldUsername;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private TableView<Book> tableViewBooks;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnRemove;

    @FXML
    private Text loginName;

    @FXML
    private HBox loginPanel;
    @FXML
    private HBox logoutPanel;

    private Model model;
    private Stage stage;

    public Controller(Model model, Stage stage) {
        this.model = model;
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableView();
        initControls();
        initDialogs();

        stage.setOnCloseRequest(e -> {
            try {
                model.saveDBMS();
                model.closeDBMS();
            }
            catch (Exception error) {
                showError("Failed to save to DBMS", error.getMessage());
            }
        });

        runTask(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateTitle("Connecting to DBMS");
                model.connectDBMS();
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initTableView() {
        TableColumn<Book, String> nameColumn = new TableColumn<>();
        nameColumn.setText("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("name"));
        nameColumn.setPrefWidth(120);

        TableColumn<Book, String> authorsColumn = new TableColumn<>();
        authorsColumn.setText("Authors");
        authorsColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("authors"));
        authorsColumn.setPrefWidth(120);

        TableColumn<Book, String> publishersColumn = new TableColumn<>();
        publishersColumn.setText("Publishers");
        publishersColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("publishers"));
        publishersColumn.setPrefWidth(120);

        TableColumn<Book, String> formatColumn = new TableColumn<>();
        formatColumn.setText("Format");
        formatColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("format"));

        TableColumn<Book, String> notesColumn = new TableColumn<>();
        notesColumn.setText("Notes");
        notesColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("notes"));
        notesColumn.setPrefWidth(120);

        TableColumn<Book, String> tagsColumn = new TableColumn<>();
        tagsColumn.setText("Tags");
        tagsColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("tags"));
        tagsColumn.setPrefWidth(120);

        tableViewBooks.getColumns().addAll(nameColumn, authorsColumn, publishersColumn, formatColumn, notesColumn, tagsColumn);
    }

    private void initControls() {
        btnLogin.disableProperty().bind(fieldUsername.textProperty().isEmpty().or(fieldPassword.textProperty().isEmpty()));
        btnAdd.disableProperty().bind(model.loggedInProperty().not());
        btnEdit.disableProperty().bind(model.loggedInProperty().not());
        btnRemove.disableProperty().bind(model.loggedInProperty().not());

        fieldUsername.setPromptText("USERNAME");
        fieldPassword.setPromptText("PASSWORD");
    }

    private void initDialogs() {
        addDialog = new Dialog<>();
        addDialogController = new AddBookDialogController(model);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(addDialogController);

        try {
            addDialog.setDialogPane(loader.load(getClass().getResourceAsStream("/ui_dialog_add_book.fxml")));
        }
        catch (Exception e) {
            showError("Failed to load dialog", e.getMessage());
            Platform.exit();
        }
    }

    @FXML
    private void onLogin() {
        String username = fieldUsername.getText();
        String password = fieldPassword.getText();

        fieldUsername.clear();
        fieldPassword.clear();

        runTask(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateTitle("Logging In");
                model.login(username, password);
                return null;
            }

            @Override
            protected void succeeded() {
                btnLogout.setDisable(false);
                loginName.setText("Logged in as " + username);
                root.setBottom(logoutPanel);
                model.getBookCollection().ifPresent(col -> tableViewBooks.setItems(col.booksProperty()));
            }
        });
    }

    @FXML
    private void onLogout() {
        btnLogout.setDisable(true);

        runTask(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateTitle("Logging Out");
                model.saveDBMS();
                model.logout();
                return null;
            }

            @Override
            protected void succeeded() {
                root.setBottom(loginPanel);
                tableViewBooks.setItems(FXCollections.observableArrayList());
            }
        });
    }

    private Dialog<ButtonType> addDialog;
    private AddBookDialogController addDialogController;

    @FXML
    private void onAdd() {
        addDialog.showAndWait();
    }

    @FXML
    private void onEdit() {
        Book book = tableViewBooks.getSelectionModel().getSelectedItem();
        if (book != null) {
            addDialog.getDialogPane().setUserData(book);
            addDialogController.setEdit(true);
            addDialog.showAndWait();
        }
    }

    @FXML
    private void onRemove() {
        Book book = tableViewBooks.getSelectionModel().getSelectedItem();
        if (book != null)
            model.removeBook(book);
    }

    private void runTask(Task<?> task) {
        task.setOnFailed(event -> {
            Throwable e = event.getSource().getException();
            showError(event.getSource().getTitle(), e == null ? "Unknown error" : e.getMessage());
        });
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
