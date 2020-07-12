package com.kolhar.dabbalisttracker;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Dabba implements Serializable {
    private UUID mID;
    private String gTitle, gWinner;
    private String mDate;
    private int maxScore, betamt, roundNo;
    private static final String TAG = "DabbaTAG";
    private ArrayList<Player> players;

    public Dabba() {
        mID = UUID.randomUUID();
        mDate = DateFormat.getDateTimeInstance().format(new Date());
    }

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
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

    public void setmID(UUID mID) {
        this.mID = mID;
    }

    public void setPlayers(ArrayList players) {
        this.players = players;
    }

    public ArrayList getPlayers() {
        return players;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String date) {
        this.mDate = date;
    }

    public void setWinner(String winner) {
        //Log.d(TAG, "Winner of the game is: " + gWinner);
        gWinner = winner;
    }

    public String getgWinner() {
        return gWinner;
    }

    public String getgTitle() {
        return gTitle;
    }

    public void setgTitle(String gTitle) {
        this.gTitle = gTitle;
    }

}