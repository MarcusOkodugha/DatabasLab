package se.kth.databas.booksdb.model;

import java.sql.Date;
import java.util.ArrayList;


 /**
 * Representation of an author
 * has the attributes authorId, authorName and a list of books written by the author
 */
public class Author {
    private String authorName;
    private ArrayList<Book> books;
    private Date dob;

     public ArrayList<Book> getBooks() {
         return books;
     }

     public Date getDob() {
         return dob;
     }

     public void setDob(Date dob) {
         this.dob = dob;
     }

     /**
         * constructor takes two parameters
         * @param authorId
         * @param authorName
         */
    public Author(int authorId, String authorName) {
        this.authorName = authorName;
        this.books =  new ArrayList<>();

    }
        /**
         * a constructor taking in only the authorName
         * @param authorName
         */
    public Author(String authorName) {
        this.authorName = authorName;
        this.books =  new ArrayList<>();
    }
    /**
     * checks if book is null or not before adding book object to the list
     * @param book
     */
    public void addBook(Book book){
        if (book!=null){
            books.add(book);
        }
    }
     /**
      *
      * @return returns the authorId,an integer
      */



        /**
     * setter for authorName
     * @param authorName
         */


 /**
         * setter for authorId
      */    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    /**
     * getter for authorName
      * @return returns the authorName, a string
     */
    public String getAuthorName() {
        return authorName;
    }
    /**
     *
     * @return returns a string formatted in the way seen below
     */
     @Override
     public String toString() {
         return "Author{" +
                 "authorName='" + authorName + '\'' +
                 ", dob=" + dob +
                 '}';
     }
 }
