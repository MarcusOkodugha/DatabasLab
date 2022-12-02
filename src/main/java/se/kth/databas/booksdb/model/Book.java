package se.kth.databas.booksdb.model;

import java.sql.Array;
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
    // TODO: 
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    private ArrayList<Author> authors;
    public Book(int bookId, String isbn, String title, Date published) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        setValidISBN(isbn);
        authors = new ArrayList<>();
    }

    private void setValidISBN(String isbn){
        if(isbn.length() == 13){
            this.isbn = isbn;
        }else {
            throw new IllegalArgumentException("ISBN not 13 numbers");
        }
    }

    
    public Book(String isbn, String title, Date published) {
        this(-1, isbn, title, published); 
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
                '}';
    }
}
