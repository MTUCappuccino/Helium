/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cappuccino.helium.ui.mainview;

import cappuccino.helium.network.Message;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;

/**
 *
 * @author Kyle Alberth
 */
//class creates the box that will show the sent image in the format of our app.
public class ImageMessage extends AnchorPane {
    
      @FXML
    private Polygon leftShape;
    @FXML
    private Polygon rightShape;
    @FXML
    private AnchorPane bodyPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Text handle;
    @FXML
    private AnchorPane rootPane;
    
    public ImageMessage() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageMessage.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public void setMessage(Message m) {
        this.handle.setText(m.getSenderHandle() + " - " + new SimpleDateFormat("hh:mm aa").format(new Date(m.getSentTime())));
        imageView.setImage(SwingFXUtils.toFXImage(hexToImage(m.getContent()), null));
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
            
    //converts a hex string back into the bufferedImage it came from.
    public BufferedImage hexToImage(String imageString) {
        int len = imageString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(imageString.charAt(i), 16) << 4)
                    + Character.digit(imageString.charAt(i + 1), 16));
        }
        InputStream in = new ByteArrayInputStream(data);
        BufferedImage bImageFromConvert = null;
        try {
            bImageFromConvert = ImageIO.read(in);
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bImageFromConvert;
    }
}

