package cappuccino.helium.network;

import java.io.IOException;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class Server {

    private String name;
    private Color theme;
    private URL icon;
    private String ip;
    private int port;
    private boolean notificationsAllowed;
    private boolean mentionsAllowed;
    private String handle;
    private String password;
    ObservableList<Message> messages = FXCollections.observableArrayList();
    ObservableList<Node> messageViews = FXCollections.observableArrayList();
    Connection connection;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connection = new Connection(this);
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
}
