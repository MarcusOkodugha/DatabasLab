/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.databas.booksdb.model;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    MongoClient client = null;
    MongoDatabase db = null;

    public BooksDbImpl() {
        books = Arrays.asList(DATA);
    }    /**
     * Representation of an author
     * has the attributes authorId, authorName and a list of books written by the author
     */
    @Override
    public boolean connect(String database) throws BooksDbException {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        try {
            this.client = MongoClients.create("mongodb://localhost:27017");//auto connect on
            this.db = client.getDatabase(database);
            System.out.println("connected to "+database+" database");
            return true;
        } catch (MongoException e) {
            throw new BooksDbException("Error connecting to the database: " + e.getMessage(), e);
        }
    }
    /**
     *When clicking on disconnect button this method will run
     *Disconnects from database
    */
    @Override
    public void disconnect() throws BooksDbException {
        try {
            client.close();
        } catch (MongoException e) {
            throw new BooksDbException("Error disconnecting from the database: " + e.getMessage(), e);
        }
    }
    /**
     * Returns a list of the books that were brought from the database
     * Creates a new book object for every book found in the database, adds it to the arraylist
     * The query is done by selecting all matching titles from our database table T_Book
    */

    @Override
    public List<Book> searchBooksByTitleQuery(String searchString) {
        MongoCollection col = db.getCollection("Book");
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put("title", new BasicDBObject("$regex", searchString+ ".*").append("$options", "i"));
         ArrayList result = new ArrayList<Book>();
        FindIterable<Document> books =col.find(regexQuery);
        for (Document doc:books) {
            Book book = new Book(
                    doc.getString("isbn"),
                    doc.getString("title"),
                    doc.getDate("published"),
                    doc.getInteger("rating"),
                    Genre.valueOf(doc.getString("genre")));
            result.add(book);
        }
            return result;
    }
    /**
     * Returns a list of all the brought books from the database
     * The query selects all the matching books, matched after our search string which is the isbn
    */
    @Override
    public List<Book> searchBooksByIsbnQuery(String searchSting){
        MongoCollection col = db.getCollection("Book");
         FindIterable foundBooks  = col.find(Filters.eq("isbn",searchSting));
         ArrayList result = new ArrayList<Book>();

         for (MongoCursor<Document> cursor = foundBooks.iterator(); cursor.hasNext();) {
             Document doc = cursor.next();
             Book book = new Book(
                     doc.getString("isbn"),
                     doc.getString("title"),
                     doc.getDate("published"),
                     doc.getInteger("rating"),
                     Genre.valueOf(doc.getString("genre")));
             result.add(book);
           }

        return result;
    }
    /**
     * Returns a list of all the brought books from the database
     * The query selects all the matching books, matched after our search string which is the author
     * Since there is a many-to-many relationship between author and book it is shown by also using a Written list
     * Adds all author, book and writtenBy tuples to seperate lists
    */

    @Override
    public List<Book> searchBookByAuthorQuery(String searchString){
        MongoCollection col = db.getCollection("Author");
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put("authorName", new BasicDBObject("$regex", searchString+ ".*").append("$options", "i"));
        FindIterable<Document> books =col.find(regexQuery);
        ArrayList result = new ArrayList<Book>();
        for (Document doc:books) {
            for (Book b:convertDocListToBookList((doc.getList("books",Document.class)))) {
                result.add(b);
            }
        }
        return result;
    }
    private List<Book> convertDocListToBookList(List<Document> documentList){
        List<Book> books= new ArrayList<>();
        for (Document doc:documentList) {
            books.add(new Book(
                    doc.getString("isbn"),
                    doc.getString("title"),
                    doc.getDate("published"),
                    doc.getInteger("rating"),
                    Genre.valueOf(doc.getString("genre"))));
        }
        return books;
    }

    /**
     * Insert query to insert a new book into the T_Book table with all its attributes
     * Insert a book object from java, use the Book class getter methods to insert its values
     * PreparedStatement is used to prevent SQl-injections
    */
    @Override
    public void insertBook(Book book) {
        MongoCollection col = db.getCollection("Book");
        Document doc = new Document()
                .append("isbn", book.getIsbn())
                .append("title", book.getTitle())
                .append("published", book.getPublished())
                .append("storyLine", "test story line")
                .append("genre", book.getGenre().toString())
                .append("rating", book.getRating());
        col.insertOne(doc);
    }
    /**
     * SQL query to add an author row to our T_Author table
     * Author is represented as a class in java, use its getter methods to add the proper values to that specific object to the table
    */
    @Override
    public void insertAuthor(Author author){
        MongoCollection col = db.getCollection("Author");
        Document doc = new Document()
                .append("authorName", author.getAuthorName())
                .append("dob",author.getDob())
                .append("books",convertBookListToDoc(author.getBooks()));
        col.insertOne(doc);
    }

    private List<Document> convertBookListToDoc(ArrayList<Book> books){
        List<Document> documentList= new ArrayList<>();
        for (Book book:books) {
        Document doc = new Document()
                .append("isbn", book.getIsbn())
                .append("title", book.getTitle())
                .append("published", book.getPublished())
                .append("storyLine", "test story line")
                .append("genre", book.getGenre().toString())
                .append("rating", book.getRating());
            documentList.add(doc);
        }
        return documentList;
    }

    /**
     * Adds every single book from the table T_Book into an array using the query seen down below
     * Creates book objects based on retrieved data and adds them to the list
    */
    @Override
    public void addAllBooksFromTableToArray() {
        MongoCollection col = db.getCollection("Book");
        FindIterable<Document> books =col.find();
        arrayListOfBooks.clear();
        for (Document doc : books) {
            Book book = new Book(
                    doc.getString("isbn"),
                    doc.getString("title"),
                    doc.getDate("published"),
                    doc.getInteger("rating"),
                    Genre.valueOf(doc.getString("genre"))
            );
            arrayListOfBooks.add(book);
        }
    }

    /**
     * Static data, several books with all their attributes
    */
    public static final Book[] DATA = {
            new Book( "123456789", "Databases Illuminated", new Date(2018, 1, 1),3,Genre.ACADEMIC),
            new Book( "234567891", "Dark Databases", new Date(1990, 1, 1),4,Genre.ACADEMIC),
            new Book( "456789012", "The buried giant", new Date(2000, 1, 1),2,Genre.FICTION),
            new Book( "567890123", "Never let me go", new Date(2000, 1, 1),4,Genre.FANTASY),
            new Book( "678901234", "The remains of the day", new Date(2000, 1, 1),2,Genre.HISTORY),
            new Book( "234567890", "Alias Grace", new Date(2000, 1, 1),1,Genre.SCI_FI),
            new Book( "345678911", "The handmaids tale", new Date(2010, 1, 1),3,Genre.FICTION),
            new Book( "345678901", "Shuggie Bain", new Date(2020, 1, 1),2,Genre.DRAMA),
            new Book( "345678912", "Microserfs", new Date(2000, 1, 1),5,Genre.SCI_FI),
            new Book( "111111111", "Lord of the rings", Date.valueOf(LocalDate.now()),5,Genre.FANTASY),
    };
    /**
     * Returns a clone of the list containing all books from T_Book table
    */
    public ArrayList getArrayListOfBooks() {
        return (ArrayList) arrayListOfBooks.clone();
    }

    /**
     * Runs a query on T_Book table to retrieve a book from database based on ISBN
     * Returns the book object once it has all the corresponding attributes retrieved from the table
    */
    public Book getBookFromDatabaseByIsbn(String isbn){
        Book nextBook = searchBooksByIsbnQuery(isbn).get(0);
        return nextBook;
    }

    public void onAddSelectedTransaction(String isbn, String title, Date published, String authorString, int rating, Genre genre) {
            Book book = new Book(isbn, title, published,rating, genre);
            List<String> list = new ArrayList<String>(Arrays.asList(authorString.split(",")));
            ArrayList<Author> authorArrayList =new ArrayList<>();
            for (String s:list) {
                authorArrayList.add(new Author(s));
            }
            insertBook(book);
            for (Author author:authorArrayList) {//todo om det fins tid gör så dob väljs med en datepickker
                author.setDob(Date.valueOf(LocalDate.now()));
                author.addBook(book);
                insertAuthor(author);
                book.addAuthor(author);
           }
    }

    public void onRemoveSelectedTransaction(String isbn){
       MongoCollection col= db.getCollection("Book");
        col.deleteOne(Filters.eq("isbn", isbn));
    }

    public void printAllBooksInDb() {
        MongoCollection col = db.getCollection("Book");
        FindIterable<Document> books =col.find();
        for (Document bok:books) {
            System.out.println(bok);
        }
    }

    public void onTestSelected(){

    }

}
