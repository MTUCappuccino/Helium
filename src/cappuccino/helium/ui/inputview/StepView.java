package cappuccino.helium.ui.inputview;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class StepView extends AnchorPane {

    @FXML
    private Label label;
    @FXML
    private TextField textfield;
    @FXML
    private PasswordField passwordfield;
    @FXML
    private Button button;
    @FXML
    private ProgressIndicator progressIndicator;

    private boolean passwordMode = false;
    private Consumer<String> callback;

    public StepView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StepView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void hideInput() {
        passwordfield.setVisible(true);
        passwordMode = true;
        textfield.setVisible(false);
    }

    public void setCallback(Consumer<String> callback) {
        this.callback = callback;
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public void reset() {
        textfield.setText("");
        passwordfield.setText("");
        progressIndicator.setVisible(false);
    }

    public void requestFocus() {
        if (passwordMode) {
            passwordfield.requestFocus();
        } else {
            textfield.requestFocus();
        }
    }

    @FXML
    public void stepAction(ActionEvent event) {
        if (passwordMode) {
            progressIndicator.setVisible(true);
        }
        if (callback != null) {
            String text = passwordMode ? passwordfield.getText() : textfield.getText();
            callback.accept(text);
        }
    }

    public void hideProgressIndicator() {
        progressIndicator.setVisible(false);
    }

    public void showBadPasswordAnimation() {
        passwordfield.setStyle("-fx-border-color: red");
        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(passwordfield.translateXProperty(), 0.0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(passwordfield.translateXProperty(), -20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(3.0), new KeyValue(passwordfield.translateXProperty(), 20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(4.5), new KeyValue(passwordfield.translateXProperty(), -20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(6.0), new KeyValue(passwordfield.translateXProperty(), 20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(7.5), new KeyValue(passwordfield.translateXProperty(), -20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(8.5), new KeyValue(passwordfield.translateXProperty(), 20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(10.0), new KeyValue(passwordfield.translateXProperty(), -20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(11.5), new KeyValue(passwordfield.translateXProperty(), 20.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(12.5), new KeyValue(passwordfield.translateXProperty(), 0.0, Interpolator.EASE_OUT)));
        t.setRate(25.0);
        t.play();
    }
}
