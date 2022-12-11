package se.kth.databas.booksdb.model;

import java.sql.Date;
import java.time.LocalDate;

public class testingClasses {
    public static void main(String[] args) {
        System.out.println("Hello,World");

        Book book1 = new Book(1, "0295673429234", "Lord of the rings", Date.valueOf(LocalDate.now()),5);

        Author author1 = new Author(1,"jamal okodugha");
        book1.addAuthor(new Author(2,"Giga Chad"));

        book1.addAuthor(author1);
        System.out.println(book1);
        System.out.println(author1);
    }
}
