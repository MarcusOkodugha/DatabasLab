package se.kth.databas.booksdb.model;

import java.util.ArrayList;
//todo ta bort klass
public class Genre {
    private int genreId;
    private String genreName;
    private ArrayList<Book> books;
    public void setGenreId(int genreId) {
        this.genreId = genreId;
        this.books = new ArrayList<>();
    }
    public void addBook(Book book){
        if (book!=null){
            books.add(book);
        }
    }

    public ArrayList<Book> getBooks() {
        return (ArrayList<Book>) books.clone();
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public Genre(int genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + genreId +
                ", genreName='" + genreName + '\'' +
                '}';
    }
}
