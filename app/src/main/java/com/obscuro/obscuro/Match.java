package com.obscuro.obscuro;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jacob on 10/29/2017.
 */

public class Match {
    User matchedWith;
    ArrayList<String> tags;
    ArrayList<Boolean> sames;
    boolean isCurrent;
    double distance;

    public Match(){}

    public Match(User u, ArrayList<String> tags, ArrayList<Boolean> sames, boolean isCurrent){
        this.matchedWith = u;
        this.sames = sames;
        this.tags = tags;
        this.isCurrent = isCurrent;
    }

    public Match isMatch(User u){//returns Match and is null if no match
        Match ans = null;
        ArrayList<String> theirs = u.getObscuros();
        ArrayList<String> mine = ProfileActivity.currentUser.getObscuros();
        ArrayList<Boolean> sames = new ArrayList<>();
        boolean any = false;
        boolean previousMatch = false;
        for(int k = 0; ProfileActivity.currentUser.getMatches() != null && k < ProfileActivity.currentUser.getMatches().size(); k++){
            if(ProfileActivity.currentUser.getMatches().get(k).matchedWith.getUID().equals(u.getUID())){
                previousMatch = true;
            }
        }
        if(!previousMatch) {
            for (int i = 0; i < mine.size(); i++) {
                for (int j = 0; j < theirs.size(); j++) {
                    Log.d("Test", "isMatch: sames.size = " + sames.size());

                    if (mine.get(i).equals(theirs.get(j)) && !u.getUID().equals(ProfileActivity.currentUser.getUID())) {
                        sames.add(Boolean.TRUE);
                        any = true;
                    } else {
                        sames.add(Boolean.FALSE);
                    }
                }
            }
        }
        this.sames =sames;
        Log.d("TEST", "isMatch: sames.length: "+sames.size());
        if(any)
            //Log.d(TEST, "isMatch: Mine-> "mine.get(0));
            ans = new Match(u,theirs,sames,true);
        return ans;
    }

    public User getMatchedWith(){
        return matchedWith;
    }

    public void setMatchedWith(User user){
        matchedWith = user;
    }

    public ArrayList<String> getTags(){
        return tags;
    }

    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }

    public ArrayList<Boolean> getSames(){
        return sames;
    }

    public void setSames(ArrayList<Boolean> sames){
        this.sames = sames;
    }

    public Boolean getIsCurrent(){
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent){
        this.isCurrent = isCurrent;
    }


    public ArrayList<Match> findAllMatches(){//finds all matches. Gets from updated users.
        double maxDistance = 100.0;
        ArrayList<Match> ans = new ArrayList<Match>(0);
        User[] users = UserFB.getAllUsers();
        Match temp = null;
        for(int i = 0; i<users.length; i++){
            if((temp = isMatch(users[i])) != null ){
                double myLat = ProfileActivity.getCurrentUser().getLat();
                double myLon = ProfileActivity.getCurrentUser().getLon();
                double theirLat = users[i].getLat();
                double theirLon = users[i].getLon();
                Location here = new Location("");
                here.setLatitude(myLat);
                here.setLongitude(myLon);
                Location there = new Location("");
                there.setLatitude(theirLat);
                there.setLongitude(theirLon);
                Log.d("position", "findAllMatches: " + myLat + " " + myLon + " " + theirLat + " " + theirLon);
                distance = distFrom(here, there);
                Log.d("position", "findAllMatches: " + distance);
                if(distance < maxDistance){
                    ProfileActivity.currentUser.addMatch(temp);
                    ans.add(temp);

                } else{
                    //temp.sames.set(i, Boolean.FALSE);
                }
            }
        }
        return ans;
    }

    public double getDistance(){
        return distance;
    }

    public static double distFrom(Location here, Location there) {
        return here.distanceTo(there);
    }
    public void setCurrent(boolean b){
        isCurrent = b;
    }
    public void sendNotification(){

    }

    public String toString(){
        String sum = "";
        for(int i = 0; i < sames.size(); i++){
            if(sames.get(i)){
                sum += tags.get(i);
                if(i < sames.size() - 1){
                    sum += " ";
                }
            }

        }
        return sum;
    }

}
