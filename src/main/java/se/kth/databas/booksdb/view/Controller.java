package se.kth.databas.booksdb.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import se.kth.databas.booksdb.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;
import static se.kth.databas.booksdb.model.BooksDbImpl.DATA;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model
    /**
     * Constructor will take in the model (BooksDbInterface) and view as parameters
    */
    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }
    /**
     * Based on which way user searches for a book it i
    */
    protected void onSearchSelected(String searchFor, SearchMode mode,boolean showResults) {
        new Thread(() -> {
            try {
                if (searchFor != null && searchFor.length() >= 1) {
                    List<Book> result = null;
                    switch (mode) {
                        case Title:
                            result = booksDb.searchBooksByTitleQuery(searchFor);
                            break;
                        case ISBN:
                            result = booksDb.searchBooksByIsbnQuery(searchFor);
                            break;
                        case Author:
                            result = booksDb.searchBookByAuthorQuery(searchFor);
                            break;
                        default:
                            result = new ArrayList<>();
                    }
                    if (result == null || result.isEmpty()) {
                        Platform.runLater(()->{
                            if (showResults) booksView.showAlertAndWait("No results found.", INFORMATION);
                        });
                    } else {
                        List<Book> finalResult = result;
                        Platform.runLater(()->{
                            if (showResults) booksView.displayBooks(finalResult);
                        });
                    }
                } else {
                    Platform.runLater(()->{
                    booksView.showAlertAndWait("Enter a search string!", WARNING);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(()->{
                    booksView.showAlertAndWait("Database error.", ERROR);
                });
            }
        }).start();
    }

    /**
     * When user exists database is disconnected and then program closes
    */
    protected void onExitSelected() throws BooksDbException {
        booksDb.disconnect();
        System.exit(0);
    }

    protected void onDisconnectSelected() throws BooksDbException {
        booksDb.disconnect();
    }
    /**
     * When user presses connect this method is called on in view class
    */
    protected void onConnectSelected() throws BooksDbException {
        booksDb.connect("LibraryDB");
    }
    /**
     * When user wants to add a book this is called
     * Creat book object, a list containing one or more authors in one string, split it then add all the authors in one go to a list
     * Add the author or authors to the database by calling model method
     * For author add the book, for book add the author
     * add the writtenBy to database to establish many-to-many between author and book
    */
    protected void onAddSelected(String isbn, String title, Date published, String authorString, int rating, Genre genre){
        new Thread(() -> {
            try {
                booksDb.onAddSelectedTransaction(isbn,title,published,authorString,rating,genre);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }).start();

    }
    /**
     * When user wants to remove a book based on isbn this is called in view class
     * Removes the book from the database by using our instance booksDb
     * Removes writtenBy connected to that book (many-to-many between author and book)
    */
    protected void onRemoveSelected(String isbn) throws SQLException {
        new Thread(() -> {
                booksDb.onRemoveSelectedTransaction(isbn);
        }).start();
    }
    /**
     * When user wants to update a book this is called in view
     * Checks if the input isn't empty, and if it isn't first remove the book and then add it again but with the new info
     *
    */
    protected void onUpdateSelected(String oldIsbn, String newIsbn, String title, Date published, String authorName,int rating,Genre genre) throws SQLException, BooksDbException {
        if (!oldIsbn.isEmpty()){
            onRemoveSelected(oldIsbn);
            Platform.runLater(()->{
                onAddSelected(newIsbn,title,published,authorName,rating,genre);
            });
        }
    }
    /**
     * Gets one book only based on isbn and shows that info to the user when user wants to update the book
    */
    public void getBookFromDatabaseByIsbnController(Controller controller,String isbn) throws SQLException {
        new Thread(() -> {

                try {
                    booksView.setSelectedBook(booksDb.getBookFromDatabaseByIsbn(isbn));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(()->{
                    try {
                        if (isbn.equals("Isbn") || isbn.equals("")){
                            booksView.showAlertAndWait("Invalid isbn", Alert.AlertType.WARNING);
                            return;
                        }
                        booksView.showUpdateDialog(controller,isbn);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (BooksDbException e) {
                        throw new RuntimeException(e);
                    }
                });

        }).start();
    }
    /**
     *Shows all books calls on model and view methods
    */
    public void showAllBooksInDb() throws SQLException {
        booksDb.addAllBooksFromTableToArray();
        booksView.displayBooks(booksDb.getArrayListOfBooks());

    }
    /**
     *inserts static books to test
    */
    public void insertAllStaticTestBooks() throws SQLException {
        for (int i = 0; i < 10; i++) {
            booksDb.insertBook(DATA[i]);
        }
    }

    public void onTestSelected(){
        booksDb.onTestSelected();
    }

}



        
