module com.example.databaslab1 {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.databaslab1 to javafx.fxml;
    exports com.example.databaslab1;
}