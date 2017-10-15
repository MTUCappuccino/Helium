package cappuccino.helium.network;
import com.sun.corba.se.pept.transport.Connection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
public class ServerObj {
	String name;
	Paint theme;
	String ip;
	int port;
	boolean muted = false;
	boolean allowAt = true;
	ObservableList<Message> messages = FXCollections.observableArrayList();
	Connection connection;
	
	public ServerObj(String name, Paint theme, String ip, int port) {
		this.name = name;
		this.theme = theme;
		this.ip = ip;
		this.port = port;
		connection = new Connection(ip, port);
	}
	
	public void muteServer() {
		muted = true;
	}
	public void unmuteServer() {
		muted = false;
	}
	public void muteAt() {
		allowAt = false;
	}
	public void unmuteAt() {
		allowAt = true;
	}

	public void sendMessage(Message newMessage) {
		messages.add(newMessage);
		connection.sendMessage();
	}
	public void recieveMessage(Message newMessage) {
		messages.add(newMessage);
	}
	
	public String getName() {
		return name;
	}
	public Paint getTheme() {
		return theme;
	}
	public String getIP() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	public boolean getMuted() {
		return muted;
	}
	public boolean getAt() {
		return allowAt;
	}
}
