package com.jackger.demo.sbook;

import java.io.Serializable;

public class User implements Serializable {

    private String username, phone, email, address, password, linkavatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLinkavatar() {
        return linkavatar;
    }

    public void setLinkavatar(String linkavatar) {
        this.linkavatar = linkavatar;
    }

    public User(String username, String phone, String email, String address, String password, String linkavatar) {

        this.username = username;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.password = password;
        this.linkavatar = linkavatar;
    }

    public User() {

    }
}
