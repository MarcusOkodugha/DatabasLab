package se.kth.databas.booksdb;

import com.mongodb.client.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.Document;
import se.kth.databas.booksdb.model.Book;
import se.kth.databas.booksdb.model.BooksDbException;
import se.kth.databas.booksdb.model.BooksDbImpl;
import se.kth.databas.booksdb.model.Genre;
import se.kth.databas.booksdb.view.BooksPane;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBMain extends Application {

    @Override
    public void start(Stage primaryStage) throws BooksDbException, SQLException {
        BooksDbImpl booksDb = new BooksDbImpl(); // model
        booksDb.connect("lab2");//auto connect on
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
