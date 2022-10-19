package com.example.fuelapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private String _id;
    private String name;
    private String email;
    private String type;

    public User(String _id) {
        this._id = _id;
    }

    public User(String name, String email, String type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }
}
