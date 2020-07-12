package com.kolhar.dabbalisttracker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class DabbaJSONSerializer {
    private Context mContext;
    private Dabba oldDabba;
    private String JSONFILENAME = "dabba.json";
    private static final String TAG = "DabbaJSONTAG";
    private static final String JSON_ID = "id", JSON_WINNER = "winner", JSON_ROUNDNO = "roundno", JSON_DATE = "date", JSON_MAXSCORE = "maxscore", JSON_BETAMT = "betamt", JSON_PLAYERS = "players";
    private Gson gson;

    public DabbaJSONSerializer(Context c) {
        mContext = c;
    }

    public void saveDabba(Dabba dabba) {
        JSONObject jsondabba = new JSONObject();
        gson = new Gson();
        String playerlist = gson.toJson(dabba.getPlayers());
        try {
            jsondabba.put(JSON_ID, dabba.getmID());
            jsondabba.put(JSON_DATE, dabba.getmDate());
            jsondabba.put(JSON_MAXSCORE, dabba.getMaxScore());
            jsondabba.put(JSON_ROUNDNO, dabba.getRoundNo());
            jsondabba.put(JSON_BETAMT, dabba.getBetamt());
            jsondabba.put(JSON_WINNER, dabba.getgWinner());
            jsondabba.put(JSON_PLAYERS, playerlist);
            //Log.i(TAG, "JSON Object created: " + jsondabba);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException Error: " + e);
        }

        File opfile = new File(mContext.getFilesDir(), JSONFILENAME);

        if (opfile.exists()) {
            Log.i(TAG, "Deleting the existing file at: " + opfile);
            opfile.delete();
        }

        try {
            String userString = jsondabba.toString();
            FileWriter fileWriter = new FileWriter(opfile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
            Log.i(TAG, "JSON Object written to the file: " + opfile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "IOException Error: " + e);
        }
    }

    public Dabba loadDabba() {
        oldDabba = new Dabba();
        gson = new Gson();

        try {
            // Open & read the file into a StringBuilder
            File infile = new File(mContext.getFilesDir(), JSONFILENAME);
            FileReader fileReader = new FileReader(infile);
            BufferedReader reader = new BufferedReader(fileReader);
            StringBuilder jsonBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                // Line breaks are omitted and irrelevant
                jsonBuilder.append(line);
                line = reader.readLine();
            }
            Log.i(TAG, "Read from the file: " + infile.getAbsolutePath());

            if (jsonBuilder.equals("")) {
                Log.i(TAG, "No previous Dabba available to load:" + jsonBuilder + "**");
                Toast.makeText(mContext, "No previous Dabba available to load!", Toast.LENGTH_LONG);
            } else {
                //Log.i(TAG, "Read the following string from the file: " + jsonBuilder);

                // Convert the StringBuilder to String and further to JSON Object
                String jsonString = jsonBuilder.toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                // Pick individual strings from the JsonObject and assign them to the various variables of the Dabba
                oldDabba.setmID(UUID.fromString(jsonObject.get(JSON_ID).toString()));
                oldDabba.setmDate(jsonObject.get(JSON_DATE).toString());
                oldDabba.setWinner(jsonObject.get(JSON_WINNER).toString());
                oldDabba.setBetamt(Integer.parseInt(jsonObject.get(JSON_BETAMT).toString()));
                oldDabba.setMaxScore(Integer.parseInt(jsonObject.get(JSON_MAXSCORE).toString()));
                oldDabba.setRoundNo(Integer.parseInt(jsonObject.get(JSON_ROUNDNO).toString()));
                // Pick the Players arraylist using gson and set the Players arraylist to oldDabba
                ArrayList<Player> plList = gson.fromJson(jsonObject.get(JSON_PLAYERS).toString(), new TypeToken<ArrayList<Player>>() {
                }.getType());
                oldDabba.setPlayers(plList);
            }

            if (reader != null)
                reader.close();
        } catch (FileNotFoundException e) {
            oldDabba = null;
            Log.e(TAG, "FileNotFoundException Error: " + e);
        } catch (IOException e) {
            // Ignore this one, it happens when starting fresh
            oldDabba = null;
            Log.e(TAG, "IOException Error: " + e);
        } catch (JSONException e) {
            oldDabba = null;
            Log.e(TAG, "JSONException Error: " + e);
        }

        return oldDabba;
    }
}
