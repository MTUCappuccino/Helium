package cappuccino.helium.ui.mainview;

import cappuccino.helium.network.Message;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

/**
 *
 * @author Michael
 */
public class MessageView extends AnchorPane {
   
    @FXML
    private Polygon leftShape;
    @FXML
    private Polygon rightShape;
    @FXML
    private AnchorPane bodyPane;
    @FXML
    private Text text;
    @FXML
    private Text handle;
    @FXML
    private AnchorPane rootPane;
    
    public MessageView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MessageView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public void setMessage(Message m) {
        this.text.setText(m.getContent());
        this.handle.setText(m.getSenderHandle());
    }
    
    public void setLeft() {
        rightShape.setVisible(false);
    }
    
    public void setRight() {
        leftShape.setVisible(false);
    }
    
    public void setColor(Color color) {
        leftShape.setFill(color);
        rightShape.setFill(color);
        text.setFill(getTextColor(color));
        handle.setFill(getTextColor(color));
        bodyPane.setStyle("-fx-background-radius: 10px; -fx-background-color: " + getColorCode(color) + ";");
        bodyPane.requestLayout();
    }
    
    private String getColorCode(Color color) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
    
    /**
     * This method adheres to the W3C recommendations for calculating relative
     * luminance of a color and choosing the correct color for maximum contrast.
     * More info: https://www.w3.org/TR/WCAG20/#relativeluminancedef
     
     * @param color input color (accent)
     * @return Contrasting text color
     */
    private Color getTextColor(Color color) {
        double luminance = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
        return luminance > 0.179 ? Color.BLACK : Color.WHITE;
    }
}
