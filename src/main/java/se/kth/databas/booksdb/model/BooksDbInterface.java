package se.kth.databas.booksdb.model;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap in a BooksDbException and then re-throw the latter
 * exception. This way the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in BooksDbException.java.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public boolean connect(String database) throws BooksDbException;
    public void disconnect() throws BooksDbException;
    public List<Book> searchBooksByTitleQuery(String searchSting);
    public List<Book> searchBooksByIsbnQuery(String searchSting);
    public List<Book> searchBookByAuthorQuery(String searchSting);
    public void insertBook(Book book);
    public void insertAuthor(Author author);
    public void addAllBooksFromTableToArray();
    public ArrayList getArrayListOfBooks();
    public Book getBookFromDatabaseByIsbn(String isbn);
    public void onAddSelectedTransaction(String isbn, String title, Date published, String authorString, int rating, Genre genre);
    public void onRemoveSelectedTransaction(String isbn);
    public void onTestSelected();
}
