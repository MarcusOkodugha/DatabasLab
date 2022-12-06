package se.kth.databas.booksdb.model;

import java.util.ArrayList;

public class Author {
    private int authorId;
    private String authorName;
    private ArrayList<Book> books;

    public Author(int authorId, String authorName) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.books =  new ArrayList<>();

    }

    public Author(String authorName) {
        this.authorName = authorName;
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



    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }


    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", books=" + books +
                '}';
    }

    //todo använd date picker
}
