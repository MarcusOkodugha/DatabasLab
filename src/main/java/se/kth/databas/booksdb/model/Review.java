package se.kth.databas.booksdb.model;

public class Review {
    private String isbn;
    private int grade;

    public Review(String isbn, int grade) {
        this.isbn = isbn;
        this.grade = grade;
    }



    public String getIsbn() {
        return isbn;
    }

    public int getGrade() {
        return grade;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Review{" +
                "isbn='" + isbn + '\'' +
                ", grade=" + grade +
                '}';
    }
}
