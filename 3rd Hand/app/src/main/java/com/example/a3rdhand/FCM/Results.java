package com.example.a3rdhand.FCM;

public class Results {
    public String message_id;

    public Results() {
    }

    public Results(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
