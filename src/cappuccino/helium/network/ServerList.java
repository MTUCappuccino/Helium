package cappuccino.helium.network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
public class ServerList {
	ObservableList<Server> servers = FXCollections.observableArrayList();
	
	public void addServer(String ip, int port) {
		servers.add(new Server(ip, port));
	}
}
