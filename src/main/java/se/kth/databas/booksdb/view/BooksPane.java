package se.kth.databas.booksdb.view;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import se.kth.databas.booksdb.model.*;


/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;
    private Button testButton;
    private Button testButton2;
    private Book selectedBook;

    private MenuBar menuBar;

    public BooksPane(BooksDbImpl booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }

    /**
     * Notify user on input error or exceptions.
     * 
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {
        booksInTable = FXCollections.observableArrayList();
        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus(controller);
        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton,testButton,testButton2);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, Date> ratingCol = new TableColumn<>("Rating");
        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol,ratingCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");
        testButton = new Button("Show All Books");
        testButton2 = new Button("Dont press!!");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
        testButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    controller.testShowBook();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        testButton2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    controller.onTest2Selected();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void initMenus(Controller controller) {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        //event handeler vi controller
        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Exit");
                try {
                    controller.onExitSelected();
                } catch (BooksDbException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        connectItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    controller.onConnectSelected();
                } catch (BooksDbException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        disconnectItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    controller.onDisconnectSelected();
                } catch (BooksDbException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem);
        //event handeler
        titleItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSearchDialogBox(controller,"Title",SearchMode.Title);
            }
        });
        isbnItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSearchDialogBox(controller,"Isbn",SearchMode.ISBN);
            }
        });
        authorItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSearchDialogBox(controller,"Author",SearchMode.Author);
            }
        });
        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    showAddItemDialog(controller);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (BooksDbException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    showRemoveDialog(controller);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        updateItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    showIsbnDialog(controller);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (BooksDbException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }
    public void showSearchDialogBox(Controller controller,String sType,SearchMode searchMode){
        TextInputDialog textInputDialog = new TextInputDialog(sType);
        textInputDialog.setHeaderText("Search for "+sType);
        textInputDialog.showAndWait();
        controller.onSearchSelected(textInputDialog.getEditor().getText(),searchMode);
    }

    public void showAddItemDialog(Controller controller) throws SQLException, BooksDbException {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Add Book");
        dialog.setHeaderText("Add Book");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleTextField = new TextField("Title");
        TextField isbnTextField = new TextField("Isbn");
        TextField authorNameTextField = new TextField("Author name");
        DatePicker datePicker = new DatePicker(LocalDate.now());

        ObservableList<Integer> ratingOptions = FXCollections.observableArrayList();
        ratingOptions.addAll(1,2,3,4,5);
        ComboBox<Integer> ratingComboBox = new ComboBox<>(ratingOptions);
        ratingComboBox.getSelectionModel().selectFirst();
        ObservableList<Genres> genreOptions = FXCollections.observableArrayList();
        genreOptions.addAll(Arrays.asList(Genres.values()));
        ComboBox<Genres> genreComboBox = new ComboBox<>(genreOptions);
        genreComboBox.getSelectionModel().selectFirst();

        GridPane gridPane = initGridPane(titleTextField, isbnTextField, authorNameTextField, datePicker,genreComboBox,ratingComboBox);

        VBox vBox =new VBox(8,gridPane);
        vBox.setPadding(new Insets(20,20,20,20));
        dialogPane.setContent(vBox);
        dialog.showAndWait();

        System.out.println("combo box value "+ratingComboBox.getValue());//todo remove print
        System.out.println("rating box value "+genreComboBox.getValue());
        Date date = Date.valueOf(datePicker.getEditor().getText());
        controller.onAddSelected(isbnTextField.getText(),titleTextField.getText(),date,authorNameTextField.getText(),ratingComboBox.getValue());
    }
    public void showRemoveDialog(Controller controller) throws SQLException {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Remove Book");
        dialog.setHeaderText("Remove book");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField isbnTextField = new TextField("Isbn");
        dialogPane.setContent(new VBox(8,isbnTextField));
        dialog.showAndWait();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.onSearchSelected(isbnTextField.getText(),SearchMode.ISBN);
            if (!booksInTable.isEmpty()){
                controller.onRemoveSelected(isbnTextField.getText());
            }
        }
    }
    public void showIsbnDialog(Controller controller) throws SQLException, BooksDbException {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Update Book");
        dialog.setHeaderText("Enter isbn of the book you want to update");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField isbnTextField = new TextField("Isbn");
        dialogPane.setContent(new VBox(8,isbnTextField));
//        dialog.showAndWait();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.onSearchSelected(isbnTextField.getText(),SearchMode.ISBN);
            if (!booksInTable.isEmpty()){

                controller.getBookFromDatabaseByIsbnController(controller,isbnTextField.getText());

            }
        }
    }
        public void showUpdateDialog(Controller controller,String oldIsbn) throws SQLException, BooksDbException {//todo
            Dialog dialog = new Dialog<>();
            dialog.setTitle("Update book");
            dialog.setHeaderText("Update book");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            ObservableList<Integer> ratingOptions = FXCollections.observableArrayList();
            ratingOptions.addAll(1,2,3,4,5);
            ComboBox<Integer> ratingComboBox = new ComboBox<>(ratingOptions);
            ratingComboBox.getSelectionModel().selectFirst();

            ObservableList<Genres> genreOptions = FXCollections.observableArrayList();
            genreOptions.addAll(Arrays.asList(Genres.values()));
            ComboBox<Genres> genreComboBox = new ComboBox<>(genreOptions);
            genreComboBox.getSelectionModel().selectFirst();

            TextField titleTextField = new TextField(selectedBook.getTitle());
            TextField isbnTextField = new TextField(oldIsbn);
            TextField authorNameTextField = new TextField("Author name");
            DatePicker datePicker = new DatePicker(LocalDate.now());

            GridPane gridPane = initGridPane(titleTextField, isbnTextField, authorNameTextField, datePicker,genreComboBox,ratingComboBox);



            VBox vBox =new VBox(8,gridPane);
            vBox.setPadding(new Insets(20,20,20,20));

            dialogPane.setContent(vBox);
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                    Date date = Date.valueOf(datePicker.getEditor().getText());
                    controller.onUpdateSelected(oldIsbn,isbnTextField.getText(),titleTextField.getText(),date,authorNameTextField.getText(),ratingComboBox.getValue());
                }
    }

    private GridPane initGridPane( TextField titleTextField, TextField isbnTextField, TextField authorNameTextField, DatePicker datePicker,ComboBox<Genres> genreComboBox,ComboBox<Integer> ratingComboBox) {
        Label titleLabel = new Label("Title");
        Label isbnLabel = new Label("Isbn");
        Label authorLabel = new Label("Author");
        Label dateLabel = new Label("Date");
        Label ratingLabel = new Label("Rating");
        Label genreLabel = new Label("Genre");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(titleLabel,0,0);
        gridPane.add(titleTextField,1,0);
        gridPane.add(isbnLabel,0,1);
        gridPane.add(isbnTextField,1,1);
        gridPane.add(authorLabel,0,2);
        gridPane.add(authorNameTextField,1,2);
        gridPane.add(dateLabel,0,3);
        gridPane.add(datePicker,1,3);

        gridPane.add(genreLabel,0,4);
        gridPane.add(genreComboBox,1,4);

        gridPane.add(ratingLabel,0,5);
        gridPane.add(ratingComboBox,1,5);

        return gridPane;
    }


}
