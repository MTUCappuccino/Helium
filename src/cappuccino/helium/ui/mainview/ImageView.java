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
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import sun.misc.BASE64Decoder;

/**
 *
 * @author silvr
 */
public class ImageView extends AnchorPane {
    
    @FXML
    private Image image;
    @FXML
    private Text handle;
    @FXML
    private AnchorPane bodyPane;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Polygon mainShape;
    
    public ImageView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public void setImage(Message m) {
        handle.setText(m.getSenderHandle());
        image = SwingFXUtils.toFXImage(hexToImage(m.getContent()), null);
    }
    
    public void setColor(Color color) {
        handle.setFill(getTextColor(color));
        mainShape.setFill(color);
        bodyPane.requestLayout();
    }
    
    private String getColorCode(Color color) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
    
    private Color getTextColor(Color color) {
        double luminance = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
        return luminance > 0.179 ? Color.BLACK : Color.WHITE;
    }
    
    public BufferedImage hexToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageBytes;
        
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageBytes = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(bis);
            bis.close();
        } catch (IOException e) {
        }
        return image;
    }
}
