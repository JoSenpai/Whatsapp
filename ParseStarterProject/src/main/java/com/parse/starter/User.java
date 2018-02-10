package com.parse.starter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.parse.ParseFile;

import java.io.File;

import static android.R.attr.name;

/**
 * Created by jonathanyong on 25/12/17.
 */

public class User {

    private String username;
    private String description;
    private Bitmap profilePicture;

    public User(String username, String description, Bitmap profilePicture)
    {
        this.username = username;
        this.description = description;
        this.profilePicture = profilePicture;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }
}
