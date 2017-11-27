package cappuccino.helium.network;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Server {

    private StringProperty name;
    private Color theme;
    private URL icon;
    private String ip;
    private int port;
    private BooleanProperty notificationsAllowed;
    private BooleanProperty mentionsAllowed;
    private String handle;
    private String password;
    ObservableList<Message> messages = FXCollections.observableArrayList();
    ObservableList<Node> messageViews = FXCollections.observableArrayList();
    ObservableList<Node> bookmarkedMessageViews = FXCollections.observableArrayList();
    Connection connection;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connection = new Connection(this);
        name = new SimpleStringProperty("");
        notificationsAllowed = new SimpleBooleanProperty(true);
        mentionsAllowed = new SimpleBooleanProperty(true);
    }

    public boolean[] connect() throws IOException {
        return connection.connect();
    }
    
    public boolean authenticate() throws IOException {
        return connection.sendAuthentication();
    }
    
    public void sendMessage(Message newMessage) {
        messages.add(newMessage);
        connection.sendMessage(newMessage);
    }

    public void recieveMessage(Message newMessage) {
        messages.add(newMessage);
        if(notificationsAllowed.get()) {
            try{
                AudioInputStream audioInputStream;
                audioInputStream = AudioSystem.getAudioInputStream(
                        this.getClass().getResource("src\\message2.wav"));
        		Clip clip = AudioSystem.getClip();
        		clip.open(audioInputStream);
        		clip.start();
        	}
        	catch(Exception e) { }
        }
        if(notificationsAllowed.get() || (newMessage.getContent().contains("@" + handle + " ") && mentionsAllowed.get()) && (newMessage.getContentType() != Message.ContentType.IMAGE)) {
            try {
                displayTray(newMessage);
            } catch (AWTException | MalformedURLException e){
                System.out.println(e);
            }
        }
    }
    
    private void displayTray(Message message) throws AWTException, java.net.MalformedURLException {
        SystemTray tray = SystemTray.getSystemTray();

        URL url = System.class.getResource("src/cappuccino.helium.ui.images/logo.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Helium");
        tray.add(trayIcon);
        trayIcon.displayMessage(message.getSenderHandle(), new String(message.getContent()), MessageType.INFO);
    }
        
    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Color getTheme() {
        return theme;
    }

    public void setTheme(Color theme) {
        this.theme = theme;
    }

    public URL getIcon() {
        return icon;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void muteServer() {
        notificationsAllowed.set(false);
    }
    
    public void unmuteServer() {
        notificationsAllowed.set(true);
    }
    
    public void muteMentions() {
        mentionsAllowed.set(false);
    }
    
    public void unmuteMentions() {
        mentionsAllowed.set(true);
    }
    
    public BooleanProperty getAllowNotificationsProperty() {
        return notificationsAllowed;
    }
    
    public BooleanProperty getAllowAtMentionsProperty() {
        return mentionsAllowed;
    }
    
    public ObservableList<Message> getMessages() {
        return messages;
    }
    
    public void closeConnection() {
        connection.disconnect();
    }
    
    public boolean isHandleRequired() {
        return connection.isHandleRequired();
    }
    
    public boolean isPasswordRequired() {
        return connection.isHandleRequired();
    }
    
    public void addMessageView(Node n) {
        messageViews.add(n);
    }
    
    public ObservableList<Node> getMessageViews() {
        return messageViews;
    }
    
    public ObservableList<Node> getBookmarkedMessageViews() {
        return bookmarkedMessageViews;
    }
}
