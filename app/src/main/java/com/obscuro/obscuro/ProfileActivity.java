package com.obscuro.obscuro;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    EditText tag;
    TextView loggedInAs, logBox;//, obscures;
    Button ping, logout;
    static User currentUser;
    static String currentUID;
    static TextView welcome, obscures;
    static EditText ob1;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LocationRequest mLocationRequest;
    double lat, lon;
    private long FASTEST_INTERVAL = 1000; /* 2 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        welcome = (TextView)findViewById((R.id.loggedInAs));
        obscures = (TextView)findViewById(R.id.obscuros_textview);

        tag = (EditText)findViewById(R.id.tagOne);
        tag.setEnabled(false);
        ((Button)findViewById(R.id.edit_button)).setText("Edit");
        ob1 =tag;
        loggedInAs = (TextView)findViewById((R.id.loggedInAs));
        logBox = (TextView)findViewById((R.id.logBox));
        ping = (Button)findViewById((R.id.pingButton));
        logout = (Button)findViewById((R.id.logout));

        loggedInAs.setText("Logged in as: No oNe");
        tag.setText("");




        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

    }

    public void onSubmit(View v){
        EditText tagOne = findViewById(R.id.tagOne);
        obscures = findViewById(R.id.obscuros_textview);
        if(tagOne.isEnabled()) {//alternate between submit and edit button
            ArrayList<String> obs = new ArrayList<String>();
            obs.add(tagOne.getText().toString());
            currentUser.setObscuros(obs);
            obscures.setText("Obscuros: ");
            for(int i = 0; i< obs.size();i++){
                obscures.setText(obscures.getText()+"\n"+obs.get(i));
            }
            tagOne.setEnabled(false);
            ((Button)findViewById(R.id.edit_button)).setText("Edit");
            UserFB.updateCurrentUser();
        } else{
            tagOne.setEnabled(true);
            tagOne.setSelection(tagOne.getText().length());
            ((Button)findViewById(R.id.edit_button)).setText("Submit");


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
        Log.d("Test", "onLogout: logged out");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }

    public void onPing(View v){
        ArrayList matches = updateMatches();
        if(matches.size()>0){
            notification(matches.size(), matches);
        }
        logBox.setText("" + (lat + lon));
    }

    public ArrayList<Match> updateMatches(){
        LinearLayout layout = findViewById(R.id.layout_in_scroll);
        Button butt;
        Match temp = new Match();
        ArrayList<Match> ans =temp.findAllMatches();
        layout.removeAllViews();
        for(int i = 0; i<currentUser.getMatches().size(); i++){
            String name = currentUser.getMatches().get(i).getMatchedWith().getUsername();
            butt = new Button(this);
            TextView buttDetails = new TextView(this);
            butt.setId(i*2);
            butt.setText(name);
            butt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    viewMatch(findViewById(v.getId()));
                }
            });
            String text = "";
            ArrayList<String> ob = ans.get(i).getTags();
            for(int j = 0; j< ob.size(); j++){
                text+=""+ob.get(j)+"\n\t";
            }
            text="\t"+text+ ans.get(i).getDistance()+" Meters away";
            buttDetails.setId((i*2)+1);
            buttDetails.setText(text);
            buttDetails.setTextSize(20f);
            buttDetails.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            buttDetails.setVisibility(View.GONE);
            Log.d("TEST", "updateMatches: Added a button");
            layout.addView(butt);
            layout.addView(buttDetails);
        }
            return ans;
    }

    public void viewMatch(View v){
        v = findViewById((((int) v.getId())+1));
        if (v.getVisibility() == View.GONE)
            findViewById(v.getId()).setVisibility(View.VISIBLE);
        else
            findViewById(v.getId()).setVisibility(View.GONE);
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
            startLocationUpdates();
        }catch (SecurityException se){
            Log.d("Test", "onConnected: No Permission");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }catch(SecurityException se){
            Log.d("Security", "startLocationUpdates: " + se);
        }

    }

    @Override
    public void onLocationChanged(Location location){
        location.setAccuracy(0.1f);
        Log.d("check", "onLocationChanged: " + location.getAccuracy());
        try {
            mLastLocation = location;
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
                try {
                    currentUser.setLat(lat);
                    currentUser.setLon(lon);
                    UserFB.updateCurrentUser();
                }catch(Exception e){
                    Log.d("userLatLong", "onLocationChanged: ");
                }
                Log.d("Test", "onUpdate: " + lat + lon);
            }
            else{
                Log.d("TEST", "onLocationChanged: NULLER");
            }
        }catch (SecurityException se){
            Log.d("Security", "onLocationChanged: Updating one");
        }

        ArrayList matches = updateMatches();
        if(matches.size()>0){
            notification(matches.size(), matches);
        }
        logBox.setText("" + (lat + lon));
    }


    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    public void onProviderEnabled(String s) {

    }


    public void onProviderDisabled(String s) {

    }

    private void notification(int numberMatches, ArrayList<Match> matches){
        Log.d("test", "notification: ");
        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle("You got " + numberMatches + " hit/s with " + matches.get(0).matchedWith.getUsername() + "!")
                        .setContentText("Obscuros: " + matches.get(0).toString());
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ProfileActivity.class);
        mBuilder.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mBuilder.setLights(Color.RED, 3000, 3000);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ProfileActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(3456, mBuilder.build());
    }


    private void populateMatch(View v){

    }
}
