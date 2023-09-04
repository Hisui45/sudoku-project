module com.example.sudokuproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;

    opens com.example.sudokuproject to javafx.fxml;
    exports com.example.sudokuproject;
}