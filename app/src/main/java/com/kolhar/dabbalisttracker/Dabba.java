package com.kolhar.dabbalisttracker;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Dabba implements Parcelable {
    private UUID mID;
    private static final String JSON_ID = "id", JSON_TITLE = "title", JSON_WINNER = "winner", JSON_DATE = "date", JSON_SCORE = "score", JSON_AMT = "amount", JSON_ROUNDNO = "roundno", JSON_PLAYERS = "players", JSONPLAYERNO = "playerno", JSONSCORENO = "scoreno";
    private String gTitle, gWinner;
    private Date mDate;
    private int maxScore, betamt, roundNo;
    private static final String TAG = "DabbaTAG";
    private ArrayList<Player> players;

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

    /*public JSONObject toJSON() throws JSONException {       // Uses the methods from the JSONObject class to handle the conversion of data in Crime into something that can be written to the JSON file.
        Log.d(TAG, "Entered Dabba : toJSON() method");
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mID.toString());
        json.put(JSON_DATE, mDate);
        json.put(JSON_AMT, betamt);
        json.put(JSON_ROUNDNO, roundNo);
        json.put(JSON_SCORE, maxScore);

        // For the players array, create an inner json array
        JSONObject jsonplayers = new JSONObject();
        for (int i = 0; i < players.size(); i++) {
            jsonplayers.put(JSONPLAYERNO, players.get(i).getpName());
            jsonplayers.put(JSONSCORENO, players.get(i).getLiveScore());
        }
        json.put(JSON_PLAYERS, players);

        // In case the winner is already decided
        if (gWinner != null)
            json.put(JSON_WINNER, gWinner);

        Log.d(TAG, "Exiting Dabba : toJSON() method");
        return json;
    }*/

    public String getgWinner() {
        return gWinner;
    }

    /*public Dabba(JSONObject json) throws JSONException {
        mID = UUID.fromString(json.getString(JSON_ID));
        maxScore = json.getInt(JSON_SCORE);
        betamt = json.getInt(JSON_AMT);
        roundNo = json.getInt(JSON_ROUNDNO);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_WINNER))
            gWinner = json.getString(JSON_WINNER);

        JSONArray jsonplayers = json.getJSONArray(JSON_PLAYERS);
        for (int i = 0; i < jsonplayers.length(); i++) {
            Player p = new Player();
            JSONObject tempname = jsonplayers.getJSONObject(i);
            p.setpName(tempname.getString(JSONPLAYERNO));
            p.setLiveScore(tempname.getInt(JSONSCORENO));
            players.add(p);
        }
    }*/

    public String getgTitle() {
        return gTitle;
    }

    public void setgTitle(String gTitle) {
        this.gTitle = gTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}