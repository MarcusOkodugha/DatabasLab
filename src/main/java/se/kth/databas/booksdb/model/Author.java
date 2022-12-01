package se.kth.databas.booksdb.model;

import java.util.ArrayList;

public class Author {
    private int authorId;
    private String isbn;
    private String firstName;
    private String lastName;
    private ArrayList<Book> books;

    public Author(int authorId, String isbn, String firstName, String lastName) {
        this.authorId = authorId;
        this.isbn = isbn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.books =  new ArrayList<>();

    }
    public void addBook(Book book){
        if (book!=null){
            books.add(book);
        }
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", isbn='" + isbn + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
