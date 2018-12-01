package com.jackger.demo.sbook;

public class Address {

    private int id;
    private String addressName;

    public Address() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Address(int id, String addressName) {

        this.id = id;
        this.addressName = addressName;
    }
}
