import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ManageUserAccountsController {

    @FXML
    private TextField tfTitle;  // This is your TextField from SceneBuilder

    @FXML
    void btnOKClicked(ActionEvent event) {
        Stage mainWindow = (Stage) tfTitle.getScene().getWindow();
        String title = tfTitle.getText();
        mainWindow.setTitle(title);  // Set the window title to the text entered in tfTitle
    }
}
