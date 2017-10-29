package com.obscuro.obscuro;

/**
 * Created by aaron on 10/28/2017.
 */
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.obscuro.obscuro.ProfileActivity.currentUID;
import static com.obscuro.obscuro.ProfileActivity.currentUser;

public class UserFB {
    private static FirebaseDatabase fbDB;
    private static DatabaseReference dbRef;
    private static  Map<String, Object> masterMap;
    private static User[] allUsers;
    private static Boolean firstCall;

    public static void init(){
        Log.d("TEST", "Called constructor for UserFB");
        fbDB = FirebaseDatabase.getInstance();
        dbRef = fbDB.getReference().child("users");
        masterMap = new HashMap<String, Object>();
        allUsers = new User[0];
        firstCall = true;
        UserFB.addListener();
    }

    public static void addListener(){
        DatabaseReference dbTemp = FirebaseDatabase.getInstance().getReference().child("users");
        //only way i could find to get data from fireBase
        dbTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserFB.makeList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void makeList(DataSnapshot data){
        Log.d("TEST", "MakeList called");
        Map<String, Object> m = new HashMap<String, Object>();
        User[] users = new User[(int) data.getChildrenCount()];
        int i = 0;
        for (DataSnapshot snap : data.getChildren()) {
            User value = (User)snap.getValue(User.class);
            String key = value.getUID();
            if (key == null) {
                Log.d("TEST", "Found a pesky rat. ID: " + ((User) value).getUID() + "\nnumber: " + i);
            }else {
                m.put(key, value);
                //Log.d("TEST", "successful put #"+i);
                users[i] = (User) value;
                i++;
            }
        }
        UserFB.setAllUsers(users);
        UserFB.setMasterMap(m);
        Log.d("TEST", "Total Users: "+users.length);

        if (firstCall) {//sets current user when first makes connection
            UserFB.setUp();
            Log.d("TEST", "got that info BOIIIII");
            firstCall = false;
        }
        Log.d("TEST", "madeList of size: "+allUsers.length);

    }

    public static User findUserByID(String uid){
        String target = ProfileActivity.getCurrentUID();
        Log.d("TEST","target UID: "+target);
        for(int i = 0; i<allUsers.length; i++){
            if(allUsers[i].getUID().equals(target)){
                Log.d("TEST", "Found User: "+allUsers[i].getUID());
                return allUsers[i];
            }
        }
        Log.d("TEST", "USER NOT FOUND");
        return null;
    }

    public static void updateCurrentUser(){
        Map<String, Object> current = new HashMap<String, Object>();
        current.put(ProfileActivity.currentUID, currentUser);
        dbRef.updateChildren(current);
    }

    public static void setUp(){//things to do on the first connection to firebase
        Log.d("TEST","CALLED SETUP");
        try {
            currentUser = UserFB.findUserByID(currentUID);
            Log.d("TEST", "CURRENT USER USERNAME: " + currentUser.getObscuros()[0]);
            ProfileActivity.obscures.setText("Obscuros: ");
        } catch(Exception e){
                ProfileActivity.welcome.setText("NO USER FOUND :(");
            }
        try{
            for(int j = 0; j< currentUser.getObscuros().length; j++){
                ProfileActivity.obscures.setText(ProfileActivity.obscures.getText()+"\n"+currentUser.getObscuros()[j]);
            }
            ProfileActivity.welcome.setText("Logged In As: " + currentUser.getUsername());
        }  catch(Exception e){
            e.printStackTrace();
            ProfileActivity.obscures.setText(" Error :(");
        }
    }

    public static void setAllUsers(User[] users){
        allUsers = users;
    }
    public static User[] getAllUsers(){
        return allUsers;
    }

    public static void setMasterMap(Map<String, Object> map){
        masterMap = map;
    }
    public Map<String, Object> getMasterMap(){
        return masterMap;
    }
}
