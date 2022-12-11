package se.kth.databas.booksdb.view;

import javafx.application.Platform;
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
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        new Thread(() -> {
            try {
                if (searchFor != null && searchFor.length() >= 1) {//todo vi ändra till minst en
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
                        booksView.showAlertAndWait("No results found.", INFORMATION);
                    } else {
//                        booksView.displayBooks(result);
                        List<Book> finalResult = result;
                        Platform.runLater(()->{
                            booksView.displayBooks(finalResult);
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

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).

    protected void onExitSelected() throws BooksDbException {
        booksDb.disconnect();
        System.exit(0);
    }
    protected void onDisconnectSelected() throws BooksDbException {
        booksDb.disconnect();
    }
    protected void onConnectSelected() throws BooksDbException {
        booksDb.connect("LibraryDB");
    }

    protected void onAddSelected(String isbn, String title, Date published, String authorName, int rating, Genre genre) throws SQLException, BooksDbException {
        new Thread(() -> {
            if (!isbn.matches("[0-9]+")) try {
                throw new BooksDbException("Isbn is not numbers");
            } catch (BooksDbException e) {
                throw new RuntimeException(e);
            }
            Book book = new Book(isbn, title, published,rating, genre);
            Author author = new Author(authorName);
            try {
                booksDb.insertBook(book);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            booksDb.insertAuthor(author);
            try {
                author.setAuthorId(booksDb.getMaxAuthorIdFromDatabase());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            author.addBook(book);
            book.addAuthor(author);
            Written written = new Written(author.getAuthorId(), book.getIsbn());
            booksDb.insertWritten(written);
            System.out.println(book);
            System.out.println(author);
            System.out.println(written);
        }).start();

    }

    protected void onRemoveSelected(String isbn) throws SQLException {
        new Thread(() -> {
            try {
                booksDb.removeBookByIsbn(isbn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                booksDb.removeWrittenByIsbn(isbn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("book removed!");

        }).start();
    }

    protected void onUpdateSelected(String oldIsbn, String newIsbn, String title, Date published, String authorName,int rating,Genre genre) throws SQLException, BooksDbException {
        if (!oldIsbn.isEmpty()){//todo trow exception
            onRemoveSelected(oldIsbn);
            onAddSelected(newIsbn,title,published,authorName,rating,genre);
        }
    }

    public void getBookFromDatabaseByIsbnController(Controller controller,String isbn) throws SQLException {
        new Thread(() -> {

                try {
                    booksView.setSelectedBook(booksDb.getBookFromDatabaseByIsbn(isbn));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(()->{
                    try {
                        booksView.showUpdateDialog(controller,isbn);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (BooksDbException e) {
                        throw new RuntimeException(e);
                    }
                });

        }).start();
    }














    //todo romve tests
    public void testShowBook() throws SQLException {
        booksDb.addAllBooksFromTableToArray();
        booksView.displayBooks(booksDb.getArrayListOfBooks());
    }
    public void onTest2Selected() throws SQLException {
        for (int i = 0; i < 10; i++) {
            booksDb.insertBook(DATA[i]);
        }
    }

}



        
