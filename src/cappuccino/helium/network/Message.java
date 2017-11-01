package cappuccino.helium.network;

/**
 *
 * @author Michael
 */
public class Message {
    
    public enum MessageType {
        NEW_MESSAGE(0), EDIT_MESSAGE(1), DELETE_MESSAGE(2), CLOSE_CONNECTION(3),
        LEAVE_SERVER(4);
        
        private final int value;
        private MessageType(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public enum ContentType {
        IMAGE(0), TEXT(1);
        
        private final int value;
        private ContentType(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    private MessageType type;
    private int id;
    private ContentType contentType;
    private String senderHandle;
    private long sentTime;
    private byte[] content;

    public Message(MessageType type, ContentType contentType, String senderHandle, byte[] content) {
        this(type, -1, contentType, senderHandle, System.currentTimeMillis(), content);
    }

    public Message(MessageType type, int id, ContentType contentType, String senderHandle, long sentTime, byte[] content) {
        this.type = type;
        this.id = id;
        this.contentType = contentType;
        this.senderHandle = senderHandle;
        this.sentTime = sentTime;
        this.content = content;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return String.valueOf(type.getValue()) + ";" + String.valueOf(id) + ";" + String.valueOf(contentType.getValue()) + ";" + senderHandle + ";" + content + "\n";
    }
}
