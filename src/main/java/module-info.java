module se.kth.anderslm.booksdb {
    requires javafx.controls;
    requires javafx.base;

    opens se.kth.databas.booksdb to javafx.base;
    opens se.kth.databas.booksdb.model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports se.kth.databas.booksdb;

    requires java.sql;
    requires javafx.fxml;
}