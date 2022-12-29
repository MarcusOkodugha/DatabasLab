package se.kth.databas.booksdb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import se.kth.databas.booksdb.model.BooksDbException;
import se.kth.databas.booksdb.model.BooksDbImpl;
import se.kth.databas.booksdb.view.BooksPane;

import java.sql.*;



/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class OldMain extends Application {

    @Override
    public void start(Stage primaryStage) throws BooksDbException, SQLException {
        BooksDbImpl booksDb = new BooksDbImpl(); // model

        booksDb.connect("LibraryDB");//auto connect on

        BooksPane root = new BooksPane(booksDb);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Books Database Client");
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception e) {}
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
