package com.onetoonechat;

public class OneMessage {
    String message;
    String id;
    long timestamp;
    String timeMessage;

    public OneMessage() {
    }

    public OneMessage(String message, String id, long timestamp, String timeMessage) {
        this.message = message;
        this.id = id;
        this.timestamp = timestamp;
        this.timeMessage = timeMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }
}
