package com.obscuro.obscuro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    EditText tag;
    TextView loggedInAs, logBox;
    Button ping, logout;
    static User currentUser;
    static String currentUID;
    static TextView forUserFB;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    double lat, lon;

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

        loggedInAs.setText("Logged in as: No oNe");




        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onLogout(View v){
        //end location tracking
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }

    public void onPing(View v){
        logBox.setText("" + (lat + lon));
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

    protected void onStart() {
        Log.d("Test", "onStart: HELLO");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.d("CF", "onConnectionFailed: CONNECTION FAILED");
        // ...
    }


    public void onConnected(Bundle connectionHint) {
        Log.d("Test", "onConnected: Success");
        try {
            //mLastLocation = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {

                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
                Log.d("Test", "onConnected: " + lat + lon);

            }
        }catch (SecurityException se){
            Log.d("Test", "onConnected: No Permission");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
