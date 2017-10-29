package com.obscuro.obscuro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    EditText tag;
    TextView loggedInAs, logBox;
    Button ping, logout;
    static User currentUser;
    static String currentUID;
    static TextView forUserFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        forUserFB = (TextView)findViewById((R.id.loggedInAs));
        tag = (EditText)findViewById(R.id.tagOne);
        loggedInAs = (TextView)findViewById((R.id.loggedInAs));
        logBox = (TextView)findViewById((R.id.logBox));
        ping = (Button)findViewById((R.id.pingButton));
        logout = (Button)findViewById((R.id.logout));

        loggedInAs.setText("Logged in as: ...");

    }

    public void onLogout(View v){
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }

    public void onPing(View v){

    }

    public static void setCurrentUser(User u){
        currentUser = u;
    }

    public static User getCurrentUser(){
        return currentUser;
    }

    public static void setCurrentUID(String uid){
        currentUID = uid;
    }
    public static String getCurrentUID(){
        return currentUID;
    }
}
