package se.kth.databas.booksdb.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Representation of a book.
 * Class has regular setters and getters for all the attributes (except authors and genresList)
 */
public class Book {
    
    private int bookId;
    private String isbn; // should check format
    private String title;
    private Date published;
    private String storyLine = "";
    private ArrayList<Author> authors;
    private int rating;
    private Genre genre;
    /**
     * constructor taking in our attributes
     * @param bookId
     * @param isbn
     * @param title
     * @param published
     */
    public Book(int bookId, String isbn, String title, Date published, int rating, Genre genre) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        setValidISBN(isbn);
        authors = new ArrayList<>();
        this.rating = rating;
        this.genre = genre;
    }
     /**
      * constructor called when the bookId isn't given, set to -1 by default
      * @param isbn
      * @param title
      * @param published
      * @pa
      */
    public Book(String isbn, String title, Date published, int rating, Genre genre) {
        this(-1, isbn, title, published,rating, genre);
    }

    /**
     * before setting the isbn makes sure it is long enough
     * @param isbn
     */
    private void setValidISBN(String isbn){
        if(isbn.length() == 9){
            this.isbn = isbn;
        }else {
            throw new IllegalArgumentException("ISBN not 9 numbers");
        }
    }

    public int getRating() {
        return rating;
    }

    /**
     *
     * @return returns the id of the book, an integer
     */
    public int getBookId() {
        return bookId;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getTitle() {
        return title;
    }
    public Date getPublished() {
        return published;
    }
    public String getStoryLine() {
        return storyLine;
    }

    /**
      *
      * @return returns the genre, an enum
      */
    public Genre getGenre() {
        return genre;
    }
    /**
     * sets the attribute storyLine
     * @param storyLine
     */
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }
    /**
     *
     * @return returns a clone of the author list
     */
    public ArrayList<Author> getAuthors() {
        return (ArrayList<Author>) authors.clone();
    }
     /**
      * Before adding the author to the list check if it's not null
      * @param author
      */
    public void addAuthor(Author author){
        if (author!= null){
            authors.add(author);
        }
    }
    /**
     *
     * @return returns a string in the format seen below, shows the values for the attributes
     */
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", storyLine='" + storyLine + '\'' +
                ", authors=" + authors +
                ", rating=" + rating +
                ", genre=" + genre +
                '}';
    }
}
