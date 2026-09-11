package com.example.demo.dto;

public class ActivityDTO {

    private Integer id;
    private String text;
    private String time;
    private String type;

    public ActivityDTO() {
    }

    public ActivityDTO(Integer id, String text, String time, String type) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
