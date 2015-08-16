package com.almasb.fxcatalog.data;

import com.almasb.fxcatalog.dbms.DBMSKey;

public class User implements DBMSKey {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String key() {
        return username + "@" + password;
    }
}
