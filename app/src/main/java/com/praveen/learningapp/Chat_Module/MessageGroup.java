package com.praveen.learningapp.Chat_Module;

public class MessageGroup {

    private String date,message,name, time;
    private boolean isCurrentUser;

    public MessageGroup(){

    }

    public MessageGroup(String date, String message, String name, String time) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.name = name;
        this.isCurrentUser = false;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
