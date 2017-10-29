package cappuccino.helium.ui;

import cappuccino.helium.ui.inputview.InputViewController;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class UICoordinator {

    private Parent inputView;
    private InputViewController inputViewController;

    public UICoordinator(Parent inputView, InputViewController inputViewController) {
        this.inputView = inputView;
        this.inputViewController = inputViewController;
    }

    public void askInputViewForCode(Consumer<String> callback, BiConsumer<String, String> manualCallback) {
        inputView.setOpacity(0.0);
        inputView.setVisible(true);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(inputView.opacityProperty(), 0.0)),
                new KeyFrame(Duration.seconds(.25), new KeyValue(inputView.opacityProperty(), 1.0)));

        timeline.play();

        inputViewController.askForServerCode(callback, manualCallback, () -> {
            hideAddServerView();
        });

    }

    public void askInputViewForInfo(boolean askForHandle, boolean askForPassword, BiConsumer<String, String> callback, Runnable cancelCallback) {
        Platform.runLater(() -> {
            inputViewController.askForInformation(askForHandle, askForPassword, callback, () -> {
                hideAddServerView();
                cancelCallback.run();
            });
        });
    }

    public void hideAddServerView() {
        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(inputView.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(.25), new KeyValue(inputView.opacityProperty(), 0.0)));
        t.play();
        t.setOnFinished((event) -> {
            inputView.setVisible(false);
        });
    }

    public void hideProgressIndicators() {
        inputViewController.hideProgressIndicators();
    }

    public void showBadPasswordAnimation() {
        inputViewController.showBadPasswordAnimation();
    }
}
