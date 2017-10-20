package cappuccino.helium.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Paint;

/**
 *
 * @author Michael
 */
public class Connection implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Server server;
    private boolean handleRequired = false;
    private boolean passwordRequired = false;
    private boolean initialized = false;

    private boolean running = true;

    public Connection(Server server) {
        this.server = server;
    }

    private void start() {
        Thread t = new Thread(this);
        t.start();
    }

    public boolean isHandleRequired() {
        return handleRequired;
    }

    public boolean isPasswordRequired() {
        return passwordRequired;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void sendMessage(Message message) {
        try {
            out.write(message.toString());
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        start();
    }
    
    private void reconnect() {
        boolean success = false;
        while (!success) {
            try {
                socket = new Socket(server.getIP(), server.getPort());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void disconnect() {
        try {
//            sendMessage(new Message(Message.MessageType.CLOSE_CONNECTION, null, null, null));
            out.flush();
            out.close();
            in.close();
            socket.close();
            running = false;
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initialConnection() throws IOException {
        String requiredElements = in.readLine();
        System.out.println("Server says: " + requiredElements);
        String response = "";
        if (requiredElements.substring(0, 1).equals("1")) { // handle is required
            handleRequired = true;
            response += server.getHandle() + ";";
        } else {
            handleRequired = false;
            response += "NULL";
        }
        if (requiredElements.substring(1, 2).equals("1")) { // password is required
            passwordRequired = true;
            response += server.getPassword();
        } else {
            passwordRequired = false;
            response += "NULL";
        }
        
        while(server.getHandle() == null);
        //This is temporary:
        response = String.valueOf(server.getHandle().length()) + "," + 
                String.valueOf(server.getPassword().length()) + "," + 
                server.getHandle() + server.getPassword() + "\n";
        
        out.write(response);
        out.flush();
        String server_response = in.readLine();
        System.out.println("Server says: " + server_response);
        if (server_response.equals("invalid_password")) {
            initialConnection();
        }
        String[] items = server_response.split(";");
        server.setName(items[0]);
        server.setTheme(Paint.valueOf(items[1]));
        // TODO set background image to items[2]
        initialized = true;
    }

    @Override
    public void run() {
        reconnect();
        try {
            initialConnection();
            while (running) {
                String message = in.readLine();

                System.out.println("Received message from server: " + message);
                
                String[] unparsedSegments = message.split(";");

                Message.MessageType type = Message.MessageType.values()[Integer.parseInt(unparsedSegments[0])];
                int id = Integer.parseInt(unparsedSegments[1]);
                Message.ContentType contentType = Message.ContentType.values()[Integer.parseInt(unparsedSegments[2])];
                String sender = unparsedSegments[3];
                String userMessage = unparsedSegments[4];

                Message m = new Message(type, id, contentType, sender, System.currentTimeMillis(), userMessage);
                server.recieveMessage(m);
            }
        } catch (IOException ex) {
            reconnect();
        }
    }
}
