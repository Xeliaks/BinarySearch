module com.example.project5binarysearch {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project5binarysearch to javafx.fxml;
    exports com.example.project5binarysearch;
}