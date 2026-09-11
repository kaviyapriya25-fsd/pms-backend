package com.example.demo.dto;

public class NotificationDTO {

    private int id;
    private String title;
    private String message;
    private String type;
    private String time;
    private boolean isRead;

    public NotificationDTO() {
    }

    public NotificationDTO(int id, String title, String message, String type, String time, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.time = time;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
