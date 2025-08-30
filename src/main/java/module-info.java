module task.steady_heat_transfer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    // requires java.desktop;


    opens task.steady_heat_transfer to javafx.fxml;
    exports task.steady_heat_transfer;
}