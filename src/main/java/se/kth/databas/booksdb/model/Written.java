package se.kth.databas.booksdb.model;

public class Written {
    private int authorId;
    private String isbn;

    public Written(int authorId, String isbn) {
        this.authorId = authorId;
        this.isbn = isbn;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "Written{" +
                "authorId=" + authorId +
                ", authorName='" + isbn + '\'' +
                '}';
    }
}
