package com.kolhar.dabbalisttracker;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Dabba implements Parcelable {
    private UUID mID;
    private String gWinner;
    private ArrayList players;
    private Date mDate;
    private int maxScore, betamt, roundNo;
    private static final String TAG = "DabbaTAG";
    private static final String JSON_ID = "id", JSON_TITLE = "title", JSON_WINNER = "winner", JSON_DATE = "date";

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }

    public Dabba() {
        mID = UUID.randomUUID();
        roundNo = 0;
        mDate = new Date();
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

    public ArrayList getPlayers() {
        return players;
    }

    public Date getmDate() {
        return mDate;
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
        //json.put(JSON_TITLE, gTitle);
        json.put(JSON_DATE, mDate);
        json.put(JSON_WINNER, gWinner);
        return json;
    }

    public String getgWinner() {
        return gWinner;
    }

    public Dabba(JSONObject json) throws JSONException {       // New constructor to open and access the JSON file and retrieve data
        mID = UUID.fromString(json.getString(JSON_ID));
        // if (json.has(JSON_TITLE)) gTitle = json.getString(JSON_TITLE);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_WINNER))
            gWinner = json.getString(JSON_WINNER);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}