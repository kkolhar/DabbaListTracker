package com.kolhar.dabbatrackerv2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Dabba {
    private UUID mID;
    private String gTitle, gWinner;
    private ArrayList players;
    private Date mDate;
    private int maxScore, betamt;
    private static final String TAG = "DabbaTAG";
    private static final String JSON_ID = "id", JSON_TITLE = "title", JSON_WINNER = "winner", JSON_DATE = "date";

    public Dabba() {
        mID = UUID.randomUUID();
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getBetamt() {
        return betamt;
    }

    public void setBetamt(int betamt) {
        this.betamt = betamt;
    }

    public UUID getmID() {
        return mID;
    }

    public String getmTitle() {
        return gTitle;
    }

    public ArrayList getPlayers() {
        return players;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmID(UUID mID) {
        this.mID = mID;
    }

    public void setmTitle(String mTitle) {
        this.gTitle = mTitle;
    }

    public void setPlayers(ArrayList players) {
        this.players = players;
    }

    public void setmDate(Date date) {
        this.mDate = date;
    }

    public void setWinner(String winner) {
        Log.d(TAG, "Winner of the game is: " + gWinner);
        gWinner = winner;
    }

    public JSONObject toJSON() throws JSONException {       // Uses the methods from the JSONObject class to handle the conversion of data in Crime into something that can be written to the JSON file.
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mID.toString());
        json.put(JSON_TITLE, gTitle);
        json.put(JSON_DATE, mDate);
        json.put(JSON_WINNER, gWinner);
        return json;
    }

    public String getgWinner() {
        return gWinner;
    }

    public Dabba(JSONObject json) throws JSONException {       // New constructor to open and access the JSON file and retrieve data
        mID = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) gTitle = json.getString(JSON_TITLE);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_WINNER))
            gWinner = json.getString(JSON_WINNER);
    }
}