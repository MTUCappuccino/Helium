package cappuccino.helium.network;

/**
 *
 * @author Michael
 */
public class Message {
    
    public enum MessageType {
        NEW_MESSAGE, EDIT_MESSAGE, DELETE_MESSAGE, CLOSE_CONNECTION,
        LEAVE_SERVER
    }
    
    public enum ContentType {
        IMAGE, TEXT
    }
    
    private MessageType type;
    private int id;
    private ContentType contentType;
    private String senderHandle;
    private long sentTime;
    private byte[] data;

    public Message(MessageType type, ContentType contentType, String senderHandle, byte[] data) {
        this(type, -1, contentType, senderHandle, System.currentTimeMillis(), data);
    }

    public Message(MessageType type, int id, ContentType contentType, String senderHandle, long sentTime, byte[] data) {
        this.type = type;
        this.id = id;
        this.contentType = contentType;
        this.senderHandle = senderHandle;
        this.sentTime = sentTime;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getSenderHandle() {
        return senderHandle;
    }

    public void setSenderHandle(String senderHandle) {
        this.senderHandle = senderHandle;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] toNetworkPacketBytes() {
        // TODO
        return null;
    }
}
