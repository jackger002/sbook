package com.jackger.demo.sbook;

import java.io.Serializable;

public class Post implements Serializable {

    private Book book;
    private User user;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post(Book book, User user) {

        this.book = book;
        this.user = user;
    }

    public Post() {

    }
}
