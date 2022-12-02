package se.kth.databas.booksdb.model;

import java.util.ArrayList;

public class Author {
    private int authorId;
    private String firstName;
    private String lastName;
    private ArrayList<Book> books;

    //TODO: make sure that authorID is needed as a constructor (database autoincrements)
    public Author(int authorId, String firstName, String lastName) {
        this.authorId = authorId;

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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
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
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
