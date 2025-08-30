package task.steady_heat_transfer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.steady_heat_transfer.algorithm.Data;
import static task.steady_heat_transfer.LoadCalculation.startMethod;

public class FormController {
    @FXML
    private TextField alphaField;

    @FXML
    private TextField epsilonField;

    @FXML
    private Label formNameLb;

    @FXML
    private TextField maxNField;

    @FXML
    private TextField minNField;

    @FXML
    private Button sendBtn;

    private Stage stage;

    @FXML
    void handleGetData(ActionEvent event) {
        String eps = epsilonField.getText();
        String gradientStep = alphaField.getText();
        String nMaxValue = maxNField.getText();
        String startNValue = minNField.getText();
        double epsilon, alpha;
        int nMax, nMin;

        if (gradientStep.isEmpty() || eps.isEmpty() || nMaxValue.isEmpty() || startNValue.isEmpty()) {
            showAlert("Some of the text fields are empty");
            return;
        } else {
            try {
                epsilon = Double.parseDouble(eps);
                if (epsilon < 0) {
                    showAlert("You can't put negative value to indicate epsilon.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for epsilon");
                return;
            }

            try {
                alpha = Double.parseDouble(gradientStep);
                if (alpha < 0) {
                    showAlert("You can't put negative value to indicate gradient step (alpha)");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for alpha");
                return;
            }

            try {
                nMax = Integer.parseInt(nMaxValue);
                if (nMax <= 0) {
                    showAlert("You can't put negative or zero value to indicate max N");
                    return;
                }
                if(nMax%2 != 0) {
                    showAlert("Max N value must be even number");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for max N");
                return;
            }

            try {
                nMin = Integer.parseInt(startNValue);
                if (nMin <= 0) {
                    showAlert("You can't put negative or zero value to indicate minimum N");
                    return;
                }
                if(nMin%2 != 0) {
                    showAlert("Minimum N value must be even number");
                    return;
                }
                if(nMin >= nMax) {
                    showAlert("Minimum N value must be less than max values");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error: invalid number format for minimum N");
                return;
            }
        }

        Data data = new Data(epsilon, nMax, alpha, nMin);
        startMethod(data);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setContentText(message);
        alert.setHeaderText("Error alert");
        alert.showAndWait();
    }
}
