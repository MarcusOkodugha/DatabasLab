package se.kth.databas.booksdb.view;

import se.kth.databas.booksdb.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

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
                        // ...
                        break;
                    default:
                        result = new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.", ERROR);
        }
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

    protected void onAddSelected(String isbn, String title, Date published, String authorName) throws SQLException {
        Book book =new Book(isbn,title,published);
        Author author =new Author(authorName);
        booksDb.insertBook(book);
        booksDb.insertAuthor(author);
        author.setAuthorId(booksDb.getMaxAuthorIdFromDatabase());
        author.addBook(book);
        book.addAuthor(author);

        Written written = new Written(author.getAuthorId(),book.getIsbn());
        booksDb.insertWritten(written);

        System.out.println(book);
        System.out.println(author);
        System.out.println(written);
    }

    protected void onRemoveSelected(String isbn) throws SQLException {
        booksDb.removeBookByIsbn(isbn);
        booksDb.removeWrittenByIsbn(isbn);
    }

    protected void onUpdateSelected(String oldIsbn, String newIsbn, String title, Date published, String authorName) throws SQLException {
        if (!oldIsbn.isEmpty()){//todo trow exception
            onRemoveSelected(oldIsbn);
            onAddSelected(newIsbn,title,published,authorName);
        }
    }

    //todo romve tests
    public void testShowBook() throws SQLException {
        booksDb.addAllBooksFromTableToArray();
        booksView.displayBooks(booksDb.getArrayListOfBooks());
    }
    public void onTest2Selected() throws SQLException {
        booksDb.addAllBooksFromTableToArray();
    }
    public Book getBookFromDatabaseByIsbnController(String isbn) throws SQLException {
        return booksDb.getBookFromDatabaseByIsbn(isbn);
    }

}



        
