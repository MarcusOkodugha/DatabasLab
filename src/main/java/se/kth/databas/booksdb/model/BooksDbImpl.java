/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.databas.booksdb.model;

import java.sql.*;
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
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
        // mock implementation

        String[] args = new String[2];
        args[0]="root";
        args[1]="root";


        if (args.length != 2) {
            System.out.println("Usage: java JDBCTest <user> <password>");
            System.exit(0);
        }

        String user = args[0]; // user name
        String pwd = args[1]; // password
        System.out.println("user "+user);
        System.out.println("pwd "+pwd);
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
    @Override
    public List<Book> searchBookByAuthorQuery(String searchSting){
        String query="SELECT * FROM T_Author WHERE authorName LIKE"+"\'"+searchSting+"%\'";

        List<Author> authorResults =  new ArrayList<>();
        addAuthorResults(query, authorResults);

        List<Written> writtenResults = new ArrayList<>();
        addWrittenResults(authorResults, writtenResults);

        List<Book> bookResults= new ArrayList<>();
        addBookResults(writtenResults,bookResults);

        return bookResults;
    }
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

    @Override
    public void sqlInjection(String sql) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//    @Override
//    public void insertBook(Book book) throws SQLException {//Takes a book and inserts to the database
//        String bookIsbn= "\'"+book.getIsbn()+"\'";
//        String bookTitle= "\'"+book.getTitle()+"\'";
//        String bookDate= "\'"+book.getPublished().toString()+"\'";
//        String bookStoryLine= "\"test Story Line\"";
//        String rating= "\'"+book.getRating()+"\'";
//          String sql= "INSERT into T_Book(isbn,title,published,storyLine,rating)VALUES ("+bookIsbn+","+bookTitle+","+bookDate+","+bookStoryLine+","+rating+");";
//
//        try (Statement stmt = con.createStatement()) {
//            stmt.executeUpdate(sql);
//        }
//    }
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

        System.out.println("book insert result "+n);

//        String bookIsbn= "\'"+book.getIsbn()+"\'";
//        String bookTitle= "\'"+book.getTitle()+"\'";
//        String bookDate= "\'"+book.getPublished().toString()+"\'";
//        String bookStoryLine= "\"test Story Line\"";
//        String rating= "\'"+book.getRating()+"\'";
//        String s= "INSERT into T_Book(isbn,title,published,storyLine,rating)VALUES ("+bookIsbn+","+bookTitle+","+bookDate+","+bookStoryLine+","+rating+");";

//        try (Statement stmt = con.createStatement()) {
//            stmt.executeUpdate(sql);
//        }
    }
    @Override
    public void insertAuthor(Author author) {
        String authorId= "\'"+author.getAuthorId()+"\'";
        String authorName= "\'"+author.getAuthorName()+"\'";
        String sql= "INSERT into T_Author(authorId,authorName)VALUES ("+authorId+","+authorName+");";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertWritten(Written written) {
        String authorId= "\'"+written.getAuthorId()+"\'";
        String bookIsbn= "\'"+written.getIsbn()+"\'";
        String sql= "INSERT into T_Written(authorId,isbn)VALUES ("+authorId+","+bookIsbn+");";
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void removeBookByIsbn(String isbn) throws SQLException {
        String query="DELETE FROM T_Book WHERE isbn="+isbn;
        executeUpdate(query);
    }
    public void removeWrittenByIsbn(String isbn) throws SQLException {
        String query="DELETE FROM T_Written WHERE isbn="+isbn;
        executeUpdate(query);
    }
    private void executeUpdate(String query) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        }
    }
//    private ResultSet executeAndGetQueryResults(String query) throws SQLException {
//        ResultSet rs;
//        try (Statement stmt = con.createStatement()) {
//            rs = stmt.executeQuery(query);
//        }
//        return rs;
//    }

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

    @Override
    public void executeQuery(String query) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int ccount = metaData.getColumnCount();
            for (int c = 1; c <= ccount; c++) {
                System.out.print(metaData.getColumnName(c) + "\t");
            }
            while (rs.next()) {
                for (int c = 1; c <= ccount; c++) {
                    System.out.print(rs.getObject(c) + "\t");
                }

            }
        }
    }

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
    };

    public ArrayList getArrayListOfBooks() {
        return arrayListOfBooks;
    }

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
    public Book getBookFromDatabaseByIsbn(String isbn) throws SQLException {
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
}
