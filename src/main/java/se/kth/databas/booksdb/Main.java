package se.kth.databas.booksdb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import se.kth.databas.booksdb.model.Book;
import se.kth.databas.booksdb.model.BooksDbException;
import se.kth.databas.booksdb.model.BooksDbImpl;
import se.kth.databas.booksdb.view.BooksPane;

import java.sql.*;

import static se.kth.databas.booksdb.model.BooksDbImpl.DATA;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws BooksDbException, SQLException {
        BooksDbImpl booksDb = new BooksDbImpl(); // model
        // Don't forget to connect to the db, somewhere...
        booksDb.connect("LibraryDB");//todo trun of auto connect
//        testingMethod(booksDb);
//        insertAllBooksFromDATA(booksDb);

        BooksPane root = new BooksPane(booksDb);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception e) {}
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void testingMethod(BooksDbImpl booksDb) throws SQLException {
//        System.out.println(booksDb.getBookFromDatabaseByIsbn("123456789"));
        //        System.out.println(booksDb.getMaxAuthorIdFromDatabase());
//        booksDb.insertBook(DATA[1]);
////        System.out.println("inside T_Author Table");
////        booksDb.executeQuery("SELECT * FROM T_Author");
//        System.out.println("inside T_Book Table");
//        booksDb.executeQuery("SELECT * FROM T_Book");
    }

    public void insertAllBooksFromDATA(BooksDbImpl booksDb) throws SQLException {

    }

    public static void main(String[] args) {
        launch(args);
    }

}
