package cappuccino.helium.ui.inputview;

import java.io.IOException;
import java.util.function.BiConsumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Michael
 */
public class AddServerByAddressView extends AnchorPane {

    @FXML
    private TextField addressField;
    @FXML
    private TextField portField;
    @FXML
    private ProgressIndicator progressIndicator;

    private BiConsumer<String, String> callback;

    public AddServerByAddressView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddServerByAddressView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setCallback(BiConsumer<String, String> callback) {
        this.callback = callback;
    }

    public void requestFocus() {
        addressField.requestFocus();
    }

    public void reset() {
        addressField.setText("");
        portField.setText("");
        addressField.setStyle("");
        portField.setStyle("");
        progressIndicator.setVisible(false);
    }
    
    public void hideProgressIndicator() {
        progressIndicator.setVisible(false);
    }

    @FXML
    private void stepAction(ActionEvent event) {
        boolean errors = false;
        if (addressField.getText().equals("")) {
            addressField.setStyle("-fx-border-color: red");
            errors = true;
        } else {
            addressField.setStyle("");
        }
        try {
            int port = Integer.parseInt(portField.getText());
            if (port < 0 || port > 65535) {
                portField.setStyle("-fx-border-color: red");
                errors = true;
            } else {
                portField.setStyle("");
            }
        } catch (NumberFormatException ex) {
            portField.setStyle("-fx-border-color: red");
            errors = true;
        }
        if (errors) {
            return;
        }

        progressIndicator.setVisible(true);
        
        if (callback != null) {
            callback.accept(addressField.getText(), portField.getText());
        }
    }
}
