package com.almasb.fxcatalog;

import java.util.ResourceBundle;

import com.almasb.fxcatalog.data.BookCollection;
import com.almasb.fxcatalog.data.User;
import com.almasb.fxcatalog.dbms.OrchestrateDBMS;
import com.almasb.fxcatalog.dbms.DBMS;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXCatalogApp extends Application {

    private DBMS<User, BookCollection> buildDBMS() {
        return new OrchestrateDBMS<>(User.class, BookCollection.class, ResourceBundle.getBundle("orchestrate"));
    }

    private Model buildModel() {
        return new Model(buildDBMS());
    }

    private Controller buildController(Stage stage) {
        return new Controller(buildModel(), stage);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(t -> buildController(primaryStage));

        primaryStage.setTitle("FX Catalog");
        primaryStage.setScene(new Scene(loader.load(getClass().getResourceAsStream("/ui_main.fxml"))));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
