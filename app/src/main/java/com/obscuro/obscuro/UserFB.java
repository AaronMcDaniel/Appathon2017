package com.obscuro.obscuro;

/**
 * Created by aaron on 10/28/2017.
 */
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
        Log.d("TEST", "Called constructor for RatFB");
        fbDB = FirebaseDatabase.getInstance();
        dbRef = fbDB.getReference();
        masterMap = new HashMap<String, Object>();
        allUsers = new User[0];
        firstCall = true;
        Log.d("TEST","initialized all variables");
        //UserFB.addListener();
    }
}
