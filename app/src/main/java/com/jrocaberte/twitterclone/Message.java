package com.jrocaberte.twitterclone;

public class Message {

    public String sender_name;
    public String message;
    public String send_time;

    public Message() {

    }

    public Message(String sender_name, String message, String send_time) {
        this.sender_name = sender_name;
        this.message = message;
        this.send_time = send_time;
    }

    public void setSenderName(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSendTime(String send_time) {
        this.send_time = send_time;
    }

    public String getSenderName() {
        return sender_name;
    }

    public String getMessage() {
        return message;
    }

    public String getSendTime() {
        return send_time;
    }

}
