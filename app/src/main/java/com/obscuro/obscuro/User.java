package com.obscuro.obscuro;;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A user is anyone who wants to use the system to view data, or enter new sightings.
 */
public class User {
    private String email;
    private String username;
    private String password;
    private ArrayList<String> obscuros;
    private boolean isAdmin;
    private String uid;
    private double lat, lon;

    public User(){}//defualt constructor for FB stufffffff

    public User(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isAdmin = false;
        this.uid = "NO ID";
        lat = 0;
        lon = 0;
        obscuros = new ArrayList<String>();
    }

    /**
     * Getter for user name
     *
     * @return username the login name of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for user name
     *
     * @param username the login name of the user, which is the user's email
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for email
     *
     * @return username the login name of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email
     *
     * @param email the login name of the user, which is the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Getter for password
     *
     * @return password the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for password
     *
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for isAdmin
     *
     * @return isAdmin whether or not user is an admin
     */
    public boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * Setter for isAdmin
     *
     * @param isAdmin whether or not user is an admin
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUID(){
        return uid;
    }

    public void setLat(double la){this.lat=la;}
    public double getLat(){return lat;}
    public void setLon(double lo){this.lon=lo;}
    public double getLon(){return lon;}
    public void setObscuros(ArrayList<String> ob){obscuros = ob;}
    public ArrayList<String> getObscuros(){return obscuros;}

}
