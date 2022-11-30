module se.kth.anderslm.booksdb {
    requires javafx.controls;
    requires javafx.base;

    opens se.kth.anderslm.booksdb to javafx.base;
    opens se.kth.anderslm.booksdb.model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports se.kth.anderslm.booksdb;

    requires java.sql;
    requires javafx.fxml;
}