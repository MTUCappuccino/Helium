package cappuccino.helium.network;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Server {

    private String name;
    private Paint theme;
    private String ip;
    private int port;
    private boolean notificationsAllowed;
    private boolean mentionsAllowed;
    private String handle;
    private String password;
    ObservableList<Message> messages = FXCollections.observableArrayList();
    Connection connection;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connection = new Connection(this);
        connection.connect();
    }

    public void sendMessage(Message newMessage) {
        messages.add(newMessage);
        connection.sendMessage(newMessage);
    }

    public void recieveMessage(Message newMessage) {
        messages.add(newMessage);
        if(notificationsAllowed == true) {
        	try{
        		AudioInputStream audioInputStream =
        			AudioSystem.getAudioInputStream(
        				this.getClass().getResource("src\mp_grail.wav"));
        		Clip clip = AudioSystem.getClip();
        		clip.open(audioInputStream);
        		clip.start();
        	}
        	catch(Exception e) { }
        }
    }
    
    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Paint getTheme() {
        return theme;
    }

    public void setTheme(Paint theme) {
        this.theme = theme;
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
        notificationsAllowed = false;
    }
    
    public void unmuteServer() {
        notificationsAllowed = true;
    }
    
    public void muteMentions() {
        mentionsAllowed = false;
    }
    
    public void unmuteMentions() {
        mentionsAllowed = true;
    }
    
    public ObservableList<Message> getMessages() {
        return messages;
    }
    
    public void closeConnection() {
        connection.disconnect();
    }
}
