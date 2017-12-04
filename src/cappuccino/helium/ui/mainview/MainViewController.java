package cappuccino.helium.ui.mainview;

import cappuccino.helium.network.Message;
import cappuccino.helium.network.Server;
import cappuccino.helium.ui.UICoordinator;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.bind.DatatypeConverter;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class MainViewController implements Initializable {

    @FXML
    private VBox servers;
    @FXML
    private VBox messages;
    @FXML
    private AnchorPane colorPane;
    @FXML
    private TextArea messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Label serverNameLabel;
    @FXML
    private Label handleLabel;
    @FXML
    private Rectangle logoRectangle;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private AnchorPane noServersView;
    @FXML
    private AnchorPane sidebar;
    @FXML
    private CheckBox allowNotifications;
    @FXML
    private CheckBox allowMentions;
    @FXML
    private Label sidebarServerName;
    @FXML
    private Label sidebarHandle;
    @FXML
    private ImageView sidebarImage;
    @FXML
    private ImageView image;

    private ContextMenu contextMenu;
    private MenuItem bookmark;
    private Message contextMenuOpenOnMessage;
    private Node contextMenuOpenOnView;
    private HashMap<Message, Node> bookmarkedViews;

    private UICoordinator coordinator;
    ObservableList<Server> serverList = FXCollections.observableArrayList();

    private Server currentServer = null;
    private Button currentButton = null;
    private Color currentColor = Color.BLACK;
    private boolean showingBookmarked;

    private StringProperty serverName = new SimpleStringProperty();
    private StringProperty handle = new SimpleStringProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serverList.addListener((ListChangeListener.Change<? extends Server> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Server s : c.getAddedSubList()) {
                        Button b = new Button(s.getNameProperty().get());
                        b.setPrefWidth(256);
                        b.setPrefHeight(27);
                        b.getStyleClass().add("sidebar-button");
                        b.setFocusTraversable(false);
                        b.setOnAction((event) -> {
                            displayServer(s);
                            switchColors(b, s.getTheme());
                        });
                        servers.getChildren().add(servers.getChildren().size() - 1, b);

                        if (currentServer == null) {
                            displayServer(s);
                            switchColors(b, s.getTheme());
                            noServersView.setVisible(false);
                        }

                        s.getMessages().addListener((ListChangeListener.Change<? extends Message> c2) -> {
                            while (c2.next()) {
                                if (c2.wasAdded()) {
                                    for (Message m : c2.getAddedSubList()) {
                                        if (m.getContentType() == Message.ContentType.TEXT) {
                                            System.out.println("GUI is handling message: " + m);
                                        } else {
                                            System.out.println("GUI is handling message: " + String.valueOf(m.getType()) + ","
                                                    + String.valueOf(m.getId()) + "," + String.valueOf(m.getContentType())
                                                    + "," + m.getSenderHandle() + ",[image data not shown]\n");
                                        }
                                        if (m.getSenderHandle().equals(s.getHandle())) {
                                            continue; // skip this message because I sent it
                                        }
                                        Platform.runLater(() -> {
                                            if (m.getContentType() == Message.ContentType.TEXT) {
                                                addMessageToScreen(m, s, messages.getChildren());
                                            } else {
                                                addImageToScreen(m, s, messages.getChildren());
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        sendButton.disableProperty().bind(Bindings.isEmpty(messageField.textProperty()));
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(true);
        sidebar.setVisible(true);
        sidebar.translateXProperty().bind(sidebar.widthProperty());

        serverNameLabel.textProperty().bind(serverName);
        handleLabel.textProperty().bind(Bindings.concat("@", handle));
        sidebarServerName.textProperty().bind(serverName);
        sidebarHandle.textProperty().bind(Bindings.concat("@", handle));
        messageField.promptTextProperty().bind(Bindings.concat("Send message as @", handle));

        messages.heightProperty().addListener((observable) -> {
            scrollpane.setVvalue(1.0);
        });

        bookmarkedViews = new HashMap<>();

        contextMenu = new ContextMenu();
        bookmark = new MenuItem("Bookmark", new ImageView(new Image(getClass().getResource("/cappuccino/helium/ui/images/bookmark_black.png").toString(), 25, 25, true, true)));
        MenuItem edit = new MenuItem("Edit Message", new ImageView(new Image(getClass().getResource("/cappuccino/helium/ui/images/edit.png").toString(), 25, 25, true, true)));
        MenuItem delete = new MenuItem("Delete Message", new ImageView(new Image(getClass().getResource("/cappuccino/helium/ui/images/delete.png").toString(), 25, 25, true, true)));
        bookmark.setOnAction((event) -> {
            if (contextMenuOpenOnMessage.isBookmarked()) {
                contextMenuOpenOnMessage.setBookmarked(false);
                currentServer.getBookmarkedMessageViews().remove(bookmarkedViews.remove(contextMenuOpenOnMessage));
                if (showingBookmarked) {
                    messages.getChildren().setAll(currentServer.getBookmarkedMessageViews());
                }
            } else {
                contextMenuOpenOnMessage.setBookmarked(true);
                if (contextMenuOpenOnMessage.getContentType() == Message.ContentType.TEXT) {
                    Node n = addMessageToScreen(contextMenuOpenOnMessage, null, currentServer.getBookmarkedMessageViews());
                    bookmarkedViews.put(contextMenuOpenOnMessage, n);
                } else {
                    Node n = addImageToScreen(contextMenuOpenOnMessage, null, currentServer.getBookmarkedMessageViews());
                    bookmarkedViews.put(contextMenuOpenOnMessage, n);
                }
            }
        });
        edit.setOnAction((event) -> {

        });
        delete.setOnAction((event) -> {

        });
        contextMenu.getItems().addAll(bookmark, edit, delete);
    }

    public void updateDisplay(Server s) {
        Platform.runLater(() -> {
            if (currentServer == s) {
                displayServer(s);
                switchColors(currentButton, s.getTheme());
            }
        });
    }

    private void displayServer(Server s) {
        serverName.set(s.getNameProperty().get());
        handle.set(s.getHandle());
        messages.getChildren().setAll(s.getMessageViews());
        if (currentServer != null) {
            allowNotifications.selectedProperty().unbindBidirectional(currentServer.getAllowNotificationsProperty());
            allowMentions.selectedProperty().unbindBidirectional(currentServer.getAllowAtMentionsProperty());
        }
        allowNotifications.selectedProperty().bindBidirectional(s.getAllowNotificationsProperty());
        allowMentions.selectedProperty().bindBidirectional(s.getAllowAtMentionsProperty());
        if (s.getIcon() != null) {
            Image image = new Image(s.getIcon().toExternalForm(), 100, 100, false, true);
            sidebarImage.setImage(image);
            this.image.setImage(image);
        }
        currentServer = s;
        showingBookmarked = false;
    }

    private void switchColors(Button b, Color c) {
        if (currentButton != null) {
            currentButton.setStyle("");
        }

        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(300));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                double inverse_blending = 1 - frac;

                double red = c.getRed() * frac + currentColor.getRed() * inverse_blending;
                double green = c.getGreen() * frac + currentColor.getGreen() * inverse_blending;
                double blue = c.getBlue() * frac + currentColor.getBlue() * inverse_blending;

                Color blended = new Color((float) red, (float) green, (float) blue, 1.0);

                b.setStyle("-fx-background-color: " + getColorCode(blended));
                logoRectangle.setFill(blended);
                colorPane.setBackground(new Background(new BackgroundFill(blended, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };
        animation.play();
        animation.setOnFinished((event) -> {
            currentColor = c;
        });
        currentButton = b;
    }

    private String getColorCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @FXML
    private void addServer(ActionEvent event) {
        coordinator.askInputViewForCode((code) -> {
            new Thread(() -> {
                try {
                    Socket socket = new Socket("141.219.201.139", 9090);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(code + "\n");
                    out.flush();
                    String response = in.readLine();
                    System.out.print("Central said: " + response);

                    if (response.equals("invalid_code")) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Invalid Code");
                            alert.setHeaderText("Invalid Code");
                            alert.setContentText("There is no server registered with that code. ");

                            alert.show();
                        });
                        coordinator.hideProgressIndicators();
                        return;
                    }

                    String[] unparsedSegments = response.split(",");
                    int length1 = Integer.parseInt(unparsedSegments[0]);
                    int length2 = Integer.parseInt(unparsedSegments[1]);
                    int length3 = Integer.parseInt(unparsedSegments[2]);
                    int lengthOfPrefix = unparsedSegments[0].length() + unparsedSegments[1].length() + unparsedSegments[2].length() + 3;

                    String name = response.substring(lengthOfPrefix, lengthOfPrefix + length1);
                    String url = response.substring(lengthOfPrefix + length1, lengthOfPrefix + length1 + length2);
                    String port = response.substring(lengthOfPrefix + length1 + length2);

                    connectToServer(url, port);
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Connection Error");
                        alert.setHeaderText("Could not connect to Helium central server");
                        alert.setContentText("Verify that you have an internet connection, and that you typed the server address and port correctly.");

                        alert.show();
                    });
                    coordinator.hideProgressIndicators();
                }
            }).start();
        }, (url, port) -> {
            // THE USER CHOSE TO ENTER A MANUAL URL AND PORT
            connectToServer(url, port);
        });
    }

    private void connectToServer(String url, String port) {
        new Thread(() -> {
            Server s = new Server(url, Integer.parseInt(port), this);
            try {
                boolean[] params = s.connect();
                if (!params[0] && !params[1]) {
                    s.authenticate();
                    coordinator.hideAddServerView();
                }
                boolean askForHandle = params[0];
                boolean askForPassword = params[1];
                getHandleAndPassword(askForHandle, askForPassword, s);
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText("Could not connect to server");
                    alert.setContentText("Verify that you have an internet connection, and that you typed the server address and port correctly.");

                    alert.show();
                });
                coordinator.hideProgressIndicators();
            }
        }).start();
    }

    private void getHandleAndPassword(boolean askForHandle, boolean askForPassword, Server s) {
        coordinator.askInputViewForInfo(askForHandle, askForPassword, (handle, password) -> {
            new Thread(() -> {
                try {
                    if (handle != null) {
                        s.setHandle(handle);
                    }
                    if (password != null) {
                        s.setPassword(password);
                    }
                    boolean success = s.authenticate();
                    if (success) {
                        // VALID PASSWORD
                        Platform.runLater(() -> {
                            serverList.add(s);
                        });
                        coordinator.hideAddServerView();
                    } else {
                        // INVALID PASSWIRD
                        coordinator.hideProgressIndicators();
                        coordinator.showBadPasswordAnimation();
                        getHandleAndPassword(false, true, s);
                    }
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Connection Error");
                        alert.setHeaderText("An error occurred communicating with the server.");
                        alert.setContentText("Please try again. If the problem persists, there could be an issue with the server. Try contacting the server maintainer.");

                        alert.show();
                    });
                    coordinator.hideAddServerView();
                }
            }).start();
        }, () -> {
            // ON CANCEL
            s.closeConnection();
        });
    }

    @FXML
    private void messageFieldKeyPress(KeyEvent event) {
        if (!event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            event.consume();
            sendMessage(null);
        } else if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            int caret = messageField.getCaretPosition();
            messageField.setText(messageField.getText().substring(0, caret) + "\n" + messageField.getText().substring(caret));
            messageField.positionCaret(caret + 1);
            event.consume();
        }
    }

    @FXML
    public void sendMessage(ActionEvent event) {
        String message = messageField.getText();
        messageField.clear();

        System.out.println("Send message: " + message);
        Message m = new Message(Message.MessageType.NEW_MESSAGE, Message.ContentType.TEXT, currentServer.getHandle(), message);
        currentServer.sendMessage(m);
        if (m.getContentType() == Message.ContentType.TEXT) {
            addMessageToScreen(m, currentServer, messages.getChildren());
        }
        if (m.getContentType() == Message.ContentType.IMAGE) {
            addImageToScreen(m, currentServer, messages.getChildren());
        }
    }

    public void closeConnections() {
        for (Server s : serverList) {
            s.closeConnection();
        }
    }

    private Node addMessageToScreen(Message m, Server s, ObservableList list) {
        MessageView view = new MessageView();
        view.setMessage(m);
        HBox line = new HBox();
        if (m.getSenderHandle().equals(currentServer.getHandle())) {
            view.setRight();
            view.setColor(currentServer.getTheme());
            line.setAlignment(Pos.CENTER_RIGHT);
        } else {
            view.setLeft();
            view.setColor(Color.valueOf("lightgray"));
            line.setAlignment(Pos.CENTER_LEFT);
        }
        view.setOnContextMenuRequested((e) -> {
            contextMenu.show(view, e.getScreenX(), e.getScreenY());
            contextMenuOpenOnMessage = m;
            contextMenuOpenOnView = line;
            if (m.isBookmarked()) {
                bookmark.setText("Unbookmark");
            } else {
                bookmark.setText("Bookmark");
            }
        });
        line.getChildren().add(view);
        if (s == currentServer || s == null) {
            list.add(line);
        }
        if (s != null) {
            s.addMessageView(line);
        }
        return line;
    }

    public void setCoordinator(UICoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @FXML
    private void openSidebar(ActionEvent event) {
        sidebar.translateXProperty().unbind();
        Timeline timeline = new Timeline();
        KeyValue kv1 = new KeyValue(sidebar.translateXProperty(), sidebar.getWidth(), Interpolator.EASE_IN);
        KeyValue kv2 = new KeyValue(sidebar.translateXProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.2), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    @FXML
    private void closeSidebar(ActionEvent event) {
        sidebar.translateXProperty().unbind();
        Timeline timeline = new Timeline();
        KeyValue kv1 = new KeyValue(sidebar.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyValue kv2 = new KeyValue(sidebar.translateXProperty(), sidebar.getWidth(), Interpolator.LINEAR);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.2), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        timeline.setOnFinished((e) -> {
            sidebar.translateXProperty().bind(sidebar.widthProperty());
        });
    }

    @FXML
    private void editHandle(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(handle.get());
        dialog.setTitle("Edit Handle");
        dialog.setHeaderText("Edit Handle");
        dialog.setContentText("New handle:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("Your name: " + result.get());
        }
    }

    @FXML
    private void leaveServer(ActionEvent event) {
        Message m = new Message(Message.MessageType.LEAVE_SERVER, Message.ContentType.TEXT, handle.get(), "");
        currentServer.sendMessage(m);
        currentServer.closeConnection();
        int index = serverList.indexOf(currentServer);
        serverList.remove(index);
        servers.getChildren().remove(index);
        if (serverList.isEmpty()) {
            noServersView.setVisible(true);
            currentServer = null;
        } else {
            int i = index - 1 < 0 ? index : index - 1;
            displayServer(serverList.get(i));
            switchColors((Button) servers.getChildren().get(i), serverList.get(i).getTheme());
        }
    }

    @FXML
    private void showBookmarked(ActionEvent event) {
        if (showingBookmarked) {
            messages.getChildren().setAll(currentServer.getMessageViews());
        } else {
            messages.getChildren().setAll(currentServer.getBookmarkedMessageViews());
        }
        showingBookmarked = !showingBookmarked;
    }

    //Send image method pulls up a file chooser and allows you to pick a jpg or png file to send.
    @FXML
    public void sendImage(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(null);
        String imageType = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println(e);
        }
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        if (!iter.hasNext()) {
            throw new RuntimeException("No image");
        }
        ImageReader reader = iter.next();
        imageType = reader.getFormatName();
        iis.close();
        String hexImage = imageToHex(image, imageType);
        Message message = new Message(Message.MessageType.NEW_MESSAGE, Message.ContentType.IMAGE, currentServer.getHandle(), hexImage);
        currentServer.sendMessage(message);
        addImageToScreen(message, currentServer, messages.getChildren());
    }

    //adds the chosen image from sendImage to the screen as well as position it based on who sent it.
    public Node addImageToScreen(Message m, Server s, ObservableList list) {
        ImageMessage view = new ImageMessage();
        view.setMessage(m);
        HBox line = new HBox();
        if (m.getSenderHandle().equals(currentServer.getHandle())) {
            view.setRight();
            view.setColor(currentServer.getTheme());
            line.setAlignment(Pos.CENTER_RIGHT);
        } else {
            view.setLeft();
            view.setColor(Color.valueOf("lightgray"));
            line.setAlignment(Pos.CENTER_LEFT);
        }
        view.setOnContextMenuRequested((e) -> {
            contextMenu.show(view, e.getScreenX(), e.getScreenY());
            contextMenuOpenOnMessage = m;
            contextMenuOpenOnView = line;
            if (m.isBookmarked()) {
                bookmark.setText("Unbookmark");
            } else {
                bookmark.setText("Bookmark");
            }
        });
        line.getChildren().add(view);
        if (s == currentServer || s == null) {
            list.add(line);
        }
        if (s != null) {
            s.addMessageView(line);
        }
        return line;

    }

    //converts the input bufferedImage to a hex string so it can be sent across the server.
    public String imageToHex(BufferedImage image, String type) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, type, baos);
            byte[] bytes = baos.toByteArray();

            return DatatypeConverter.printHexBinary(bytes);
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //converts a hex string that has been sent across the server back into a bufferedImage.
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
