package com.example.a3rdhand.FCM;

public class NotificationSender {
    Data data;
    String to;

    public NotificationSender() {}

    public NotificationSender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
