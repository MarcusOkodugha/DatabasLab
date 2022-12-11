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


    public void insertBook(Book book) throws SQLException;
    public void insertAuthor(Author author) throws SQLException;
    public void insertWritten(Written written) throws SQLException;

    public void removeBookByIsbn(String isbn) throws SQLException;

    public void removeWrittenByIsbn(String isbn) throws SQLException;
    public void addAllBooksFromTableToArray() throws SQLException;
    public int getMaxAuthorIdFromDatabase() throws SQLException;
    public ArrayList getArrayListOfBooks();
    public Book getBookFromDatabaseByIsbn(String isbn) throws SQLException;

    public void onAddSelectedTransaction(String isbn, String title, Date published, String authorString, int rating, Genre genre) throws SQLException;
    public void onRemoveSelectedTransaction(String isbn) throws SQLException;
}
