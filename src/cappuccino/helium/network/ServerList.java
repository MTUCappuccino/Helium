package cappuccino.helium.network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
public class ServerList {
	ObservableList<ServerObj> servers = FXCollections.observableArrayList();
	
	public void addServer(String name, Paint theme, String ip, int port) {
		servers.add(new ServerObj(name, theme, ip, port));
	}
}
