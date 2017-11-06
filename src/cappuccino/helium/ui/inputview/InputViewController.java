package cappuccino.helium.ui.inputview;

import cappuccino.helium.ui.UICoordinator;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class InputViewController implements Initializable {

    @FXML
    private StepView step1;
    @FXML
    private StepView step2;
    @FXML
    private EnterCodeView enterCode;
    @FXML
    private AddServerByAddressView addByAddress;
    @FXML
    private Label label;

    private boolean askForHandle;
    private boolean askForPassword;
    private BiConsumer<String, String> callback;
    private Runnable cancelCallback;

    private String handle;
    private UICoordinator coordinator;

    private AnchorPane currentView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        step1.setText("Choose a handle");
        step2.setText("Enter password");
        step2.setButtonText("Finish");
        step2.hideInput();
        currentView = enterCode;
    }

    public void reset() {
        enterCode.reset();
        addByAddress.reset();
        step1.reset();
        step2.reset();
    }

    public void askForServerCode(Consumer<String> callback, BiConsumer<String, String> manualCallback, Runnable cancelCallback) {
        this.cancelCallback = cancelCallback;
        reset();

        enterCode.translateXProperty().unbind();
        enterCode.translateXProperty().set(0);
        addByAddress.translateXProperty().bind(addByAddress.widthProperty());
        step1.translateXProperty().bind(step1.widthProperty());
        step2.translateXProperty().bind(step2.widthProperty());
        currentView = enterCode;

        enterCode.setCallbacks(callback, () -> {
            // TODO: notifyOnBrowseServerListing
        }, () -> {
            transition(enterCode, addByAddress);
            addByAddress.setCallback(manualCallback);
        });
    }

    public void askForInformation(boolean askForHandle, boolean askForPassword, BiConsumer<String, String> callback, Runnable cancelCallback) {
        this.askForHandle = askForHandle;
        this.askForPassword = askForPassword;
        this.callback = callback;
        this.cancelCallback = cancelCallback;

        AnchorPane secondView = step1;

        step1.setCallback((t) -> {
            transition(step1, step2);
        });

        if (!askForHandle) {
            step1.setVisible(false);
            secondView = step2;
            step2.setCallback((t) -> {
                callback.accept(null, t);
            });
        } else if (!askForPassword) {
            step2.setVisible(false);
            step1.setButtonText("Finish");
            step1.setCallback((t) -> {
                callback.accept(t, null);
            });
        } else {
            step2.translateXProperty().bind(step2.widthProperty());
            step1.setCallback((t) -> {
                handle = t;
                transition(step1, step2);
            });
            step2.setCallback((t) -> {
                callback.accept(handle, t);
            });
        }
        if (currentView != secondView) {
            transition(currentView, secondView);
        }
    }
    
    public void showBadPasswordAnimation() {
        step2.showBadPasswordAnimation();
    }

    private void transition(AnchorPane from, AnchorPane to) {
        to.translateXProperty().unbind();
        from.translateXProperty().unbind();

        Timeline timeline = new Timeline();
        to.translateXProperty().set(to.getWidth());
        KeyValue kv1 = new KeyValue(to.translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kv2 = new KeyValue(from.translateXProperty(), -from.widthProperty().get(), Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.3), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(t -> {
            to.requestFocus();
            to.translateXProperty().set(0);
            from.translateXProperty().bind(from.widthProperty());
        });
        timeline.play();

        currentView = to;
    }

    @FXML
    private void cancel(ActionEvent action) {
        cancelCallback.run();
    }

    public void setCoordinator(UICoordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void hideProgressIndicators() {
        step1.hideProgressIndicator();
        step2.hideProgressIndicator();
        enterCode.hideProgressIndicator();
        addByAddress.hideProgressIndicator();
    }
}
