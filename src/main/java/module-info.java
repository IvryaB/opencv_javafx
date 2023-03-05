module com.example.opencv_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.opencv_javafx to javafx.fxml;
    exports com.example.opencv_javafx;
}