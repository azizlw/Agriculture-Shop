package com.example.agroshop;

public class User {
    String name;
    String email;
    String contactno;
    String address;
    String location;
    String Authority;

 User(){}
    public User(String name, String email, String contactno, String address, String location, String authority) {
        this.name = name;
        this.email = email;
        this.contactno = contactno;
        this.address = address;
        this.location = location;
        Authority = authority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAuthority() {
        return Authority;
    }

    public void setAuthority(String authority) {
        Authority = authority;
    }
}
