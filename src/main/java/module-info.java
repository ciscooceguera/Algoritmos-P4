module com.example.practica4algoritmos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.practica4algoritmos to javafx.fxml;
    exports com.example.practica4algoritmos;
}