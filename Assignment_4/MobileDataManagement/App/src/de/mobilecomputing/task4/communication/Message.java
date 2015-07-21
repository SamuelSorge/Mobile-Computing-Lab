package de.mobilecomputing.task4.communication;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -5690417953798429447L;

    private long time;
    private String sender;
    private String text;
    private Map<String, Long> history;

    private Message() {
        this.time = System.currentTimeMillis();
    }

    public Message(String sender, String text) {
        this();

        this.sender = sender;
        this.text = text;
        this.history = null;
    }

    public Message(String sender, Map<String, Long> history, String text) {
        this();

        this.sender = sender;
        this.text = text;
        this.history = history;
    }

    public long getTime() {
        return time;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Map<String, Long> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "Message{" +
                "time=" + time +
                ", sender=" + sender +
                ", text='" + text + '\'' +
                ", history=" + history +
                '}';
    }
}
