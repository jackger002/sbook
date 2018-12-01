package com.jackger.demo.sbook;

public class UserMain {

    private static String username, phone, email, address, password, linkavatar;

    public UserMain() {
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserMain.username = username;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        UserMain.phone = phone;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserMain.email = email;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        UserMain.address = address;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserMain.password = password;
    }

    public static String getLinkavatar() {
        return linkavatar;
    }

    public static void setLinkavatar(String linkavatar) {
        UserMain.linkavatar = linkavatar;
    }
}
