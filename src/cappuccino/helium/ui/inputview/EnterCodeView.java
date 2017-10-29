package cappuccino.helium.ui.inputview;

import java.io.IOException;
import java.util.function.Consumer;
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
public class EnterCodeView extends AnchorPane {

    @FXML
    private TextField textfield;
    @FXML
    private ProgressIndicator progressIndicator;

    private Consumer<String> callback;
    private Runnable notifyOnBrowseServerListing;
    private Runnable notifyOnJoinByAddress;

    public EnterCodeView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EnterCodeView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        textfield.textProperty().addListener((ov, oldValue, newValue) -> {
            textfield.setText(newValue.toUpperCase());
            if (textfield.getText().length() > 5) {
                textfield.setText(textfield.getText().substring(0, 5));
            }
        });
    }

    public void setCallbacks(Consumer<String> callback, Runnable notifyOnBrowseServerListing, Runnable notifyOnJoinByAddress) {
        this.callback = callback;
        this.notifyOnBrowseServerListing = notifyOnBrowseServerListing;
        this.notifyOnJoinByAddress = notifyOnJoinByAddress;
    }

    public void requestFocus() {
        textfield.requestFocus();
    }
    
    public void reset() {
        textfield.setText("");
    }
    
    public void hideProgressIndicator() {
        progressIndicator.setVisible(false);
    }
    
    @FXML
    private void stepAction(ActionEvent event) {
        if (callback != null) {
            callback.accept(textfield.getText());
        }
    }

    @FXML
    private void browsePublicServerList() {
        if (notifyOnBrowseServerListing != null) {
            notifyOnBrowseServerListing.run();
        }
    }

    @FXML
    private void joinServerByAddress() {
        if (notifyOnJoinByAddress != null) {
            notifyOnJoinByAddress.run();
        }
    }
}
