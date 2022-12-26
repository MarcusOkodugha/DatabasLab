/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.databas.booksdb.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {

    private final List<Book> books;
    private ArrayList arrayListOfBooks = new ArrayList<Book>();
    Connection con = null;

    public BooksDbImpl() {
        books = Arrays.asList(DATA);
    }    /**
     * Representation of an author
     * has the attributes authorId, authorName and a list of books written by the author
     */
    @Override
    public boolean connect(String database) throws BooksDbException {
        // mock implementation

        String[] args = new String[2];
        args[0]="root";
        args[1]="root";

        String superUser = "super@localhost";
        String passWd = "Secure1Pass";

        if (args.length != 2) {
            System.out.println("Usage: java JDBCTest <user> <password>");
            System.exit(0);
        }

        String user = args[0]; // user name
        String pwd = args[1]; // password

        System.out.println(user + ", *********");
//        String database = "Company"; // the name of the specific database
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    /**
     *When clicking on disconnect button this method will run
     *Disconnects from database
    */
    @Override
    public void disconnect() throws BooksDbException {
        // mock implementation
        try {
            if (con != null) {
                con.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
        }

    }
    /**
     * Returns a list of the books that were brought from the database
     * Creates a new book object for every book found in the database, adds it to the arraylist
     * The query is done by selecting all matching titles from our database table T_Book
    */
    @Override
    public List<Book> searchBooksByTitleQuery(String searchSting){
        String query="SELECT * FROM T_Book WHERE title LIKE"+"\'"+searchSting+"%\'";
        List<Book> result = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);
            // Get the attribute values
            while (rs.next()) {
                Book nextBook = new Book(rs.getInt("bookId"),rs.getString("isbn"),rs.getString("title"),rs.getDate("published"),rs.getInt("rating"),Genre.valueOf(rs.getString("genre")));
                 result.add(nextBook);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    /**
     * Returns a list of all the brought books from the database
     * The query selects all the matching books, matched after our search string which is the isbn
    */
    @Override
    public List<Book> searchBooksByIsbnQuery(String searchSting){
        String query="SELECT * FROM T_Book WHERE isbn LIKE"+"\'"+searchSting+"%\'";
        List<Book> result = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);
            // Get the attribute values
            while (rs.next()) {
                Book nextBook = new Book(rs.getInt("bookId"),rs.getString("isbn"),rs.getString("title"),rs.getDate("published"),rs.getInt("rating"),Genre.valueOf(rs.getString("genre")));
                result.add(nextBook);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (result.isEmpty()) return null;

        return result;
    }
    /**
     * Returns a list of all the brought books from the database
     * The query selects all the matching books, matched after our search string which is the author
     * Since there is a many-to-many relationship between author and book it is shown by also using a Written list
     * Adds all author, book and writtenBy tuples to seperate lists
    */

    @Override
    public List<Book> searchBookByAuthorQuery(String searchSting) throws SQLException {
        String query ="SELECT T_Book.title,T_Book.isbn,T_Book.published,T_Book.genre, T_Book.rating FROM T_Author INNER JOIN T_Written ON  T_Author.authorId=T_Written.authorId INNER JOIN T_Book ON T_Written.isbn = T_Book.isbn WHERE T_Author.authorName LIKE ?";

        List<Book> bookResults= new ArrayList<>();
        addJoinResults(query,bookResults,searchSting);

        return bookResults;
    }

    private void addJoinResults(String query, List<Book> bookResults,String searchSting) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1,searchSting+"%");
        ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Book nextBook = new Book(rs.getString("isbn"), rs.getString("title"), rs.getDate("published"), rs.getInt("rating"), Genre.valueOf(rs.getString("genre")));
                System.out.println(nextBook);
                bookResults.add(nextBook);

            }
    }

    /**
     * adds the next author to the list, gets the daata from the table T_Author
    */
    private void addAuthorResults(String query, List<Author> authorResults) {
        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);
            // Get the attribute values
            while (rs.next()) {
                Author nextAuthor = new Author(rs.getInt("authorId"),rs.getString("authorName"));
                authorResults.add(nextAuthor);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Retrieves all the data from the written_by table and adds it to the list writtenResults
     * Loops through all the authors, gets each authors id and uses it for the query to retrieve data
    */
    private void addWrittenResults(List<Author> authorResults, List<Written> writtenResults) {
        String query;
        for (Author a: authorResults) {
            int authorId =a.getAuthorId();
            query="SELECT * FROM T_Written WHERE authorId LIKE"+"\'"+authorId +"%\'";
            try (Statement stmt = con.createStatement()) {
                // Execute the SQL statement
                ResultSet rs = stmt.executeQuery(query);
                // Get the attribute values
                while (rs.next()) {
                    Written nextWritten = new Written(rs.getInt("authorId"),rs.getString("isbn"));
                    writtenResults.add(nextWritten);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * goes through all the tuples in writtenby table and runs a query after the isbn each tuple has
     * Retrieves all the books that match, adds them to the arrayList bookResults
    */
    private void addBookResults(List<Written> writtenResults, List<Book> bookResults){
        for (Written w:writtenResults) {
            String query="SELECT * FROM T_Book WHERE isbn LIKE"+"\'"+w.getIsbn()+"%\'";
            try (Statement stmt = con.createStatement()) {
                // Execute the SQL statement
                ResultSet rs = stmt.executeQuery(query);
                // Get the attribute values
                while (rs.next()) {
                    Book nextBook = new Book(rs.getInt("bookId"),rs.getString("isbn"),rs.getString("title"),rs.getDate("published"),rs.getInt("rating"),Genre.valueOf(rs.getString("genre")));
                    bookResults.add(nextBook);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Insert query to insert a new book into the T_Book table with all its attributes
     * Insert a book object from java, use the Book class getter methods to insert its values
     * PreparedStatement is used to prevent SQl-injections
    */
    @Override
    public void insertBook(Book book) throws SQLException {//Takes a book and inserts to the database
        String sql = "INSERT into T_Book(isbn,title,published,storyLine,genre,rating)VALUES (?,?,?,?,?,?)";
        PreparedStatement insertBook=con.prepareStatement(sql);

        insertBook.setString(1, book.getIsbn());
        insertBook.setString(2, book.getTitle());
        insertBook.setString(3, book.getPublished().toString());
        insertBook.setString(4, "test story line");
        insertBook.setString(5, book.getGenre().toString());
        insertBook.setInt(6,book.getRating());

        int n = insertBook.executeUpdate();


    }
    /**
     * SQL query to add an author row to our T_Author table
     * Author is represented as a class in java, use its getter methods to add the proper values to that specific object to the table
    */
    @Override
    public void insertAuthor(Author author) throws SQLException {

        String sql= "INSERT into T_Author(authorId,authorName,dob)VALUES (?,?,?);";
        PreparedStatement insertAuthor=con.prepareStatement(sql);
        insertAuthor.setString(1, String.valueOf(author.getAuthorId()));
        insertAuthor.setString(2, author.getAuthorName());
        insertAuthor.setString(3, author.getDob().toString());

        int n = insertAuthor.executeUpdate();


    }
    /**
     * SQL query to add who a book is written by to the T_Written table
     * Written class getters used to get the proper values
     * This method is needed in the controller class when adding a book for the many-to-many relationship between author and book
    */
    public void insertWritten(Written written) throws SQLException {
        String sql= "INSERT into T_Written(authorId,isbn)VALUES (?,?);";
        PreparedStatement insertWritten=con.prepareStatement(sql);
        insertWritten.setString(1, String.valueOf(written.getAuthorId()));
        insertWritten.setString(2, written.getIsbn());
        int n = insertWritten.executeUpdate();
        insertWritten.close();

    }
    /**
     * SQL delete query to remove a book based on its isbn
    */
    public void removeBookByIsbn(String isbn) throws SQLException {
        String query="DELETE FROM T_Book WHERE isbn="+isbn;
        executeUpdate(query);
    }
    /**
     * SQL delete query to remove writtenBy based on book isbn
    */
    public void removeWrittenByIsbn(String isbn) throws SQLException {
        String query="DELETE FROM T_Written WHERE isbn="+isbn;
        executeUpdate(query);
    }
    /**
     * Made this into a method for future reuse, used to execute an update to the database
    */
    private void executeUpdate(String query) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    /**
     * Adds every single book from the table T_Book into an array using the query seen down below
     * Creates book objects based on retrieved data and adds them to the list
    */
    @Override
    public void addAllBooksFromTableToArray() throws SQLException {
        arrayListOfBooks.clear();
        String query="SELECT * FROM T_Book";
        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);
            // Get the attribute values
            while (rs.next()) {
                Book nextBook = new Book(rs.getInt("bookId"),rs.getString("isbn"),rs.getString("title"),rs.getDate("published"),rs.getInt("rating"),Genre.valueOf(rs.getString("genre")));
                arrayListOfBooks.add(nextBook);
            }
        }
    }


    /**
     * Static data, several books with all their attributes
    */
    public static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1),3,Genre.ACADEMIC),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1),4,Genre.ACADEMIC),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1),2,Genre.FICTION),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1),4,Genre.FANTASY),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1),2,Genre.HISTORY),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1),1,Genre.SCI_FI),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1),3,Genre.FICTION),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1),2,Genre.DRAMA),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1),5,Genre.SCI_FI),
            new Book(1, "111111111", "Lord of the rings", Date.valueOf(LocalDate.now()),5,Genre.FANTASY),
    };
    /**
     * Returns a clone of the list containing all books from T_Book table
    */
    public ArrayList getArrayListOfBooks() {
        return (ArrayList) arrayListOfBooks.clone();
    }
    /**
     * Query run to get the highest author id in database
    */
    public int getMaxAuthorIdFromDatabase() throws SQLException {
        int result = 0;
        String query = "SELECT MAX(authorId) FROM T_Author";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result = rs.getInt(1);
            }
        }
        return result;
    }
    /**
     * Runs a query on T_Book table to retrieve a book from database based on ISBN
     * Returns the book object once it has all the corresponding attributes retrieved from the table
    */
    public Book getBookFromDatabaseByIsbn(String isbn) throws SQLException   {
        String query="SELECT * FROM T_Book WHERE isbn="+isbn;
        Book nextBook = null;
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                nextBook = new Book(rs.getInt("bookId"),rs.getString("isbn"),rs.getString("title"),rs.getDate("published"),rs.getInt("rating"),Genre.valueOf(rs.getString("genre")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return nextBook;
    }

    public void onAddSelectedTransaction(String isbn, String title, Date published, String authorString, int rating, Genre genre) throws SQLException {

        try {
            con.setAutoCommit(false);
            if (!isbn.matches("[0-9]+")) try {
                throw new BooksDbException("Isbn is not numbers");
            } catch (BooksDbException e) {
                throw new RuntimeException(e);
            }
            Book book = new Book(isbn, title, published,rating, genre);

            List<String> list = new ArrayList<String>(Arrays.asList(authorString.split(",")));
            ArrayList<Author> authorArrayList =new ArrayList<>();
            for (String s:list) {
                authorArrayList.add(new Author(s));
            }
            try {
                insertBook(book);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for (Author author:authorArrayList) {
                author.setDob(Date.valueOf(LocalDate.now()));
                try {
                    insertAuthor(author);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    author.setAuthorId(getMaxAuthorIdFromDatabase());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                author.addBook(book);
                book.addAuthor(author);
                Written written = new Written(author.getAuthorId(), book.getIsbn());
                try {
                    insertWritten(written);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
//            System.out.println(book);
            con.commit();
        }
        catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        }
        finally {
            con.setAutoCommit(true);
        }

    }

    public void onRemoveSelectedTransaction(String isbn) throws SQLException {
        try {
            con.setAutoCommit(false);
            try {
                removeBookByIsbn(isbn);
            } catch (SQLException e) {
                throw new RuntimeException("book not removed correctly");
            }
            try {
                removeWrittenByIsbn(isbn);
            } catch (SQLException e) {
                throw new RuntimeException("written by not remove");
            }
            con.commit();
        }
        catch (Exception e) {
            if (con != null) con.rollback();
            throw e;
        }
        finally {
            con.setAutoCommit(true);
        }



    }

}
