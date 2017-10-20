package cappuccino.helium;

import cappuccino.helium.ui.mainview.MainViewController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            FXMLLoader controllerLayout = new FXMLLoader();
            controllerLayout.setLocation(getClass().getResource("ui/mainview/MainView.fxml"));
            Parent controllerLayoutRoot = (Parent) controllerLayout.load();
            MainViewController controller = (MainViewController) controllerLayout.getController();
            Scene controllerScene = new Scene(controllerLayoutRoot);
            primaryStage.setScene(controllerScene);

            primaryStage.setTitle("Helium");

            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                System.out.println("Custom Close Event");
                controller.closeConnections();
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
