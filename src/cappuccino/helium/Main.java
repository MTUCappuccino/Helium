package cappuccino.helium;

import cappuccino.helium.ui.UICoordinator;
import cappuccino.helium.ui.inputview.InputViewController;
import cappuccino.helium.ui.mainview.MainViewController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Michael
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            FXMLLoader mainViewLayoutLoader = new FXMLLoader();
            mainViewLayoutLoader.setLocation(getClass().getResource("ui/mainview/MainView.fxml"));
            Parent mainViewLayoutRoot = (Parent) mainViewLayoutLoader.load();
            MainViewController mainViewController = (MainViewController) mainViewLayoutLoader.getController();

            FXMLLoader inputViewLayoutLoader = new FXMLLoader();
            inputViewLayoutLoader.setLocation(getClass().getResource("ui/inputview/InputView.fxml"));
            Parent inputViewLayoutRoot = (Parent) inputViewLayoutLoader.load();
            InputViewController inputViewController = (InputViewController) inputViewLayoutLoader.getController();

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setStyle("-fx-background-color: #000");
            anchorPane.getChildren().add(mainViewLayoutRoot);
            anchorPane.getChildren().add(inputViewLayoutRoot);
            AnchorPane.setTopAnchor(mainViewLayoutRoot, 0.0);
            AnchorPane.setRightAnchor(mainViewLayoutRoot, 0.0);
            AnchorPane.setBottomAnchor(mainViewLayoutRoot, 0.0);
            AnchorPane.setLeftAnchor(mainViewLayoutRoot, 0.0);
            AnchorPane.setTopAnchor(inputViewLayoutRoot, 0.0);
            AnchorPane.setRightAnchor(inputViewLayoutRoot, 0.0);
            AnchorPane.setBottomAnchor(inputViewLayoutRoot, 0.0);
            AnchorPane.setLeftAnchor(inputViewLayoutRoot, 0.0);

            inputViewLayoutRoot.setVisible(false);
            inputViewLayoutRoot.setOpacity(0.0);
            
            UICoordinator coordinator = new UICoordinator(inputViewLayoutRoot, inputViewController);
            mainViewController.setCoordinator(coordinator);
            inputViewController.setCoordinator(coordinator);
            
            Scene controllerScene = new Scene(anchorPane);
            primaryStage.setScene(controllerScene);

            primaryStage.setTitle("Helium");

            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                System.out.println("Custom Close Event");
                mainViewController.closeConnections();
                System.exit(0);
            });

            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
