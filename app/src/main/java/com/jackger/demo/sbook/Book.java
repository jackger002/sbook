package com.jackger.demo.sbook;

import java.io.Serializable;

public class Book implements Serializable {

    private String title, author, description, linkimage, emailuser;
    private int price;
    private boolean state;

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkimage() {
        return linkimage;
    }

    public void setLinkimage(String linkimage) {
        this.linkimage = linkimage;
    }

    public String getEmailuser() {
        return emailuser;
    }

    public void setEmailuser(String emailuser) {
        this.emailuser = emailuser;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Book(String title, String author, String description, String linkimage, String emailuser, int price, boolean state) {

        this.title = title;
        this.author = author;
        this.description = description;
        this.linkimage = linkimage;
        this.emailuser = emailuser;
        this.price = price;
        this.state = state;
    }
}
