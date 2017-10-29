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

public class UserFB {
    private static FirebaseDatabase fbDB;
    private static DatabaseReference dbRef;
    private static  Map<String, Object> masterMap;
    private static User[] allUsers;
    private static Boolean firstCall;

    public static void init(){
        Log.d("TEST", "Called constructor for UserFB");
        fbDB = FirebaseDatabase.getInstance();
        dbRef = fbDB.getReference();
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
        data= data.child("users");//sets data to only have users
        Log.d("TEST", "MakeList called");
        Map<String, Object> m = new HashMap<String, Object>();
        User[] users = new User[(int) data.getChildrenCount()];
        int i = 0;
        for (DataSnapshot snap : data.getChildren()) {
            Object value = snap.getValue(User.class);
            String key = (String) ((User) value).getUID();
            if (key == null) {
                key = "invalidInput";
                Log.d("TEST", "Found a pesky rat. ID: " + ((User) value).getUID() + "\nnumber: " + i);
            }
            m.put(key, value);
            //Log.d("TEST", "successful put #"+i);
            users[i] = (User) value;
            i++;
        }
        UserFB.setAllUsers(users);
        UserFB.setMasterMap(m);
        Log.d("TEST", "Total Users: "+users.length);

        if (firstCall) {//sets current user when first makes connection
            ProfileActivity.setCurrentUser(UserFB.findUserByID(ProfileActivity.currentUID));
            Log.d("TEST", "got that info BOIIIII");
            firstCall = false;
            ProfileActivity.forUserFB.setText("Logged In As: "+ProfileActivity.currentUser.getUsername());
        }
        Log.d("TEST", "madeList of size: "+allUsers.length);

    }

    public static User findUserByID(String uid){
        String target = ProfileActivity.getCurrentUID();
        Log.d("TEST","target UID: "+target);
        for(int i = 0; i<allUsers.length; i++){
            if(allUsers[i].getUID().equals(target)){
                return allUsers[i];
            }
        }
        Log.d("TEST", "USER NOT FOUND");
        return null;
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
