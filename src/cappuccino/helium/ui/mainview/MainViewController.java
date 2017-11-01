package cappuccino.helium.ui.mainview;

import cappuccino.helium.network.Message;
import cappuccino.helium.network.Server;
import cappuccino.helium.ui.UICoordinator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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
import javafx.util.Duration;
import javax.imageio.ImageIO;

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

    private UICoordinator coordinator;
    ObservableList<Server> serverList = FXCollections.observableArrayList();

    private Server currentServer = null;
    private Button currentButton = null;
    private Color currentColor = Color.BLACK;

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
                        Button b = new Button(s.getName());
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
                                        System.out.println("GUI is handling message: " + m);
                                        if (m.getSenderHandle().equals(s.getHandle())) {
                                            continue; // skip this message because I sent it
                                        }
                                        if (currentServer == s) {
                                            Platform.runLater(() -> {
                                                addMessageToScreen(m);
                                            });
                                        }
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
        messageField.promptTextProperty().bind(Bindings.concat("Send message as @", handle));
    }

    private void displayServer(Server s) {
        serverName.set(s.getName());
        handle.set(s.getHandle());
        currentServer = s;
        messages.getChildren().setAll(currentServer.getMessageViews());
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
                System.out.println(blended.toString());

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

            // connect and shit
            coordinator.askInputViewForInfo(true, true, (handle, password) -> {
                // give that crap to the server
            }, () -> {
                // on cancel
            });
        }, (url, port) -> {
            // THE USER CHOSE TO ENTER A MANUAL URL AND PORT
            new Thread(() -> {
                Server s = new Server(url, Integer.parseInt(port));
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
        });
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
        Message m = new Message(Message.MessageType.NEW_MESSAGE, Message.ContentType.TEXT, currentServer.getHandle(), message.getBytes());
        currentServer.sendMessage(m);
        if(m.getId() == 0) {
            addMessageToScreen(m);
        }
        if(m.getId() == 1) {
            addImageToScreen(m);
        }
    }

    public void closeConnections() {
        for (Server s : serverList) {
            s.closeConnection();
        }
    }

    private void addMessageToScreen(Message m) {
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
        line.getChildren().add(view);
        messages.getChildren().add(line);
        currentServer.addMessageView(line);
    }
    
    public void addImageToScreen(Message m) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(m.getContent());
            BufferedImage img = ImageIO.read(in);
            
        } catch(IOException e) {
            System.out.println(e);
        }
        
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
}
