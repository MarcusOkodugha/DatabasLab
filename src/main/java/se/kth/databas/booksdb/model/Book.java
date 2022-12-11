package se.kth.databas.booksdb.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
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
    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
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
    public Book(String isbn, String title, Date published, int rating, Genre genre) {
        this(-1, isbn, title, published,rating, genre);
    }


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
    public Genre getGenre() {
        return genre;
    }

    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }

    public ArrayList<Author> getAuthors() {
        return (ArrayList<Author>) authors.clone();
    }
    public void addAuthor(Author author){
        if (author!= null){
            authors.add(author);
        }
    }

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
