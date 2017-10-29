package com.obscuro.obscuro;

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
        for(int i = 0; i<mine.size(); i++){
            for(int j = 0; j<theirs.size(); j++){
                Log.d("Test", "isMatch: sames.size = "+sames.size());
                if(mine.get(i).equals(theirs.get(j)) && !u.getUID().equals(ProfileActivity.currentUser.getUID())) {
                    sames.add(Boolean.TRUE);
                    any = true;
                } else{
                    sames.add(Boolean.FALSE);
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
        double maxDistance = 0.1;
        ArrayList<Match> ans = new ArrayList<Match>(0);
        User[] users = UserFB.getAllUsers();
        Match temp = null;
        for(int i = 0; i<users.length; i++){
            if((temp = isMatch(users[i])) != null){
                double myLat = ProfileActivity.getCurrentUser().getLat();
                double myLon = ProfileActivity.getCurrentUser().getLon();
                double theirLat = users[i].getLat();
                double theirLon = users[i].getLon();
                if(distFrom(theirLat,theirLon,myLat,myLon) < maxDistance){
                    ProfileActivity.currentUser.addMatch(temp);
                    ans.add(temp);
                } else{
                    //temp.sames.set(i, Boolean.FALSE);
                }
            }
        }
        return ans;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;//(or 3958.75 miles)
        double dLat = Math.toRadians(lat2-lat1);

        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;//in meters

        return dist;
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
