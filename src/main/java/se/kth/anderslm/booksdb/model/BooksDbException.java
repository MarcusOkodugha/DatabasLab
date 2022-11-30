package se.kth.anderslm.booksdb.model;

/**
 * Representing exceptions in the BooksDb model.
 * When a call to the underlying JDBC driver generates a SQLException or
 * a MongoDbException (depends on which type of DBMS we are connected to), wrap that exception in
 * a new BooksDbException, adding the former exception as "cause". This way, the controller and view
 * do not need to know what specific types of exceptions are thrown form the underlying implementation.
 * Example, code in the _implementation_ of som method in BooksDbInterface:
 * try {
 *     ...
 * }
 * catch(SQLException/MongoDbException e) { // depends on specific implementation
 *     // wrap in BooksDbException and throw again
 *     throw new BooksDbException(e.getMessage(), e);
 * }
 */
public class BooksDbException extends Exception {

    public BooksDbException(String msg, Exception cause) {
        super(msg, cause);
    }

    public BooksDbException(String msg) {
        super(msg);
    }

    public BooksDbException() {
        super();
    }
}
