package com.almasb.fxcatalog;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.almasb.fxcatalog.data.Book;

import javafx.animation.TranslateTransition;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML
    private HBox bottomHBox;

    @FXML
    private VBox rightSideBar;
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
    private Button btnOK;

    @FXML
    private void onOK() {
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

        stage.setMinWidth(root.getPrefWidth());
        stage.setMinHeight(root.getPrefHeight());
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

    private void initTableView() {
        tableViewBooks.getColumns().addAll(getColumns(Book.class));
        tableViewBooks.setPrefWidth(tableViewBooks.getColumns().size() * 120);
        tableViewBooks.setEditable(true);
    }

    private <T> List<TableColumn<T, ?>> getColumns(Class<T> dataClass) {
        List<TableColumn<T, ?>> columns = Arrays.asList(dataClass.getDeclaredMethods())
            .stream()
            .filter(m -> m.isAnnotationPresent(TableColumnInfo.class))
            .map(method -> {
                TableColumnInfo columnInfo = method.getAnnotation(TableColumnInfo.class);
                TableColumn<T, String> column = new TableColumn<>();
                column.setText(columnInfo.columnName());
                column.setCellValueFactory(new PropertyValueFactory<T, String>(method.getName().replace("Property", "")));
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setPrefWidth(root.getPrefWidth() / 6);
                column.setUserData(columnInfo.columnOrder());
                column.setOnEditCommit(e -> {
                    String value = e.getNewValue().trim();
                    T rowData = e.getRowValue();
                    try {
                        StringProperty prop = (StringProperty) method.invoke(rowData);
                        if (value.isEmpty()) {
                            String old = e.getOldValue();
                            prop.set("update");
                            prop.set(old);
                        }
                        else {
                            prop.set(value);
                        }
                    }
                    catch (Exception e1) {
                        showError("Failed to edit data", e1.getMessage());
                    }
                });
                return column;
            }).collect(Collectors.toList());

        Collections.sort(columns, (c1, c2) -> (int)c1.getUserData() - (int)c2.getUserData());

        return columns;
    }

    private void initControls() {
        btnLogin.disableProperty().bind(fieldUsername.textProperty().isEmpty().or(fieldPassword.textProperty().isEmpty()));
        btnAdd.disableProperty().bind(model.loggedInProperty().not());
        btnRemove.disableProperty().bind(model.loggedInProperty().not().or(tableViewBooks.getSelectionModel().selectedItemProperty().isNull()));

        btnOK.disableProperty().bind(fieldName.textProperty().isEmpty()
                .or(fieldAuthors.textProperty().isEmpty())
                .or(fieldPublishers.textProperty().isEmpty())
                .or(fieldFormat.textProperty().isEmpty())
                .or(fieldNotes.textProperty().isEmpty())
                .or(fieldTags.textProperty().isEmpty()));

        fieldUsername.setPromptText("USERNAME");
        fieldPassword.setPromptText("PASSWORD");


        rightSideBar.setBackground(new Background(new BackgroundFill(Color.rgb(107, 173, 246), null, null)));
    }

    private void initDialogs() {
//        addDialog = new Dialog<>();
//        addDialogController = new AddBookDialogController(model);
//
//        FXMLLoader loader = new FXMLLoader();
//        loader.setController(addDialogController);
//
//        try {
//            addDialog.setDialogPane(loader.load(getClass().getResourceAsStream("/ui_dialog_add_book.fxml")));
//        }
//        catch (Exception e) {
//            showError("Failed to load dialog", e.getMessage());
//            Platform.exit();
//        }
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
                bottomHBox.getChildren().set(0, logoutPanel);
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
                bottomHBox.getChildren().set(0, loginPanel);
                tableViewBooks.setItems(FXCollections.observableArrayList());
            }
        });
    }

    //private Dialog<ButtonType> addDialog;

    @FXML
    private void onAdd() {
        if (root.getRight() == null) {
            root.setRight(rightSideBar);
            TranslateTransition tt = new TranslateTransition(
                    Duration.seconds(0.33), rightSideBar);
            tt.setToX(0);
            tt.play();
        }
        else {
            TranslateTransition tt = new TranslateTransition(
                    Duration.seconds(0.33), rightSideBar);
            tt.setToX(rightSideBar.getPrefWidth());
            tt.setOnFinished(e -> root.setRight(null));
            tt.play();
        }


        //addDialog.showAndWait();
//        NotificationPane notificationPane = new NotificationPane();
//        notificationPane.setShowFromTop(true);
//
//
//        notificationPane.getActions().addAll(new Action("Sync", ae -> {
//
//                // do sync
//                // then hide...
//
//                notificationPane.hide();
//
//        }));
//
//        Button showBtn = new Button("Show / Hide");
//
//        showBtn.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent e) {
//
//                if (notificationPane.isShowing()) {
//
//                    notificationPane.hide();
//
//                }
//                else {
//
//                    notificationPane.show();
//
//                }
//
//            }
//
//        });
//
//        notificationPane.setContent(loginPanel);
//        notificationPane.show();
//
//        loginPanel.getChildren().add(showBtn);
//        root.setLeft(notificationPane);
    }

    @FXML
    private void onRemove() {
        Book book = tableViewBooks.getSelectionModel().getSelectedItem();
        if (book != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you really want to remove [" + book.getName() + "] ?");
            alert.showAndWait().filter(type -> type == ButtonType.OK).ifPresent(t -> model.removeBook(book));
        }
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
