package com.kolhar.dabbalisttracker;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable {
    private UUID pID;
    private String pName;
    private int liveScore;

    public Player() {
        // Generate unique identifier
        pID = UUID.randomUUID();
        liveScore = 0;
        reEntry = 0;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    private int betAmount;

    public int getReEntry() {
        return reEntry;
    }

    public void setReEntry(int reEntry) {
        this.reEntry = reEntry;
    }

    private int reEntry;

    public int getLiveScore() {
        return liveScore;
    }

    public void setLiveScore(int liveScore) {
        this.liveScore = liveScore;
    }

    public UUID getID() {
        return pID;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String title) {
        pName = title;
    }

}
