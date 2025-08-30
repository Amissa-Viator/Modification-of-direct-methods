package task.steady_heat_transfer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Workbook;
import task.steady_heat_transfer.excel_worker.WorkbookManager;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        WorkbookManager.createDirectoryIfNotExists();
        Workbook workbook = WorkbookManager.getWorkbook();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 330, 418);
        FormController controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.setTitle("Steady-Heat Transfer Task");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

   @Override
    public void stop() {
        WorkbookManager.saveWorkbook();
        WorkbookManager.closeWorkbook();
    }
}