package com.praveen.learningapp.registerandlogin;


public class UserProfile {

    private String username;
    private String password;
    private boolean regComplete;

    public UserProfile() {
        //firebase constructor
    }


    public UserProfile(String username, String password, Boolean regComplete) {

        this.username = username;
        this.password = password;
        this.regComplete = regComplete;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRegComplete() {
        return regComplete;
    }

    public void setRegComplete(Boolean regComplete) {
        this.regComplete = regComplete;
    }
}
