package it.polito.mad.mad_app.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Siltes on 22/04/17.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String surname;
    public String email;
    public String username; // da vedere se utilizzarlo...
    public Map<String, Boolean> Groups = new TreeMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String name, String surname) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.Groups.put("", true);
    }


}
