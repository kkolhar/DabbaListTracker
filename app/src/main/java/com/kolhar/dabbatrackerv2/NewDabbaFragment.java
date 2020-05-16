package com.kolhar.dabbatrackerv2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

// This class is to set up the game with the max score, game bet amount and the no. of players. No. of players is restricted to 20 for now.

// Next update to include the players' list as ArrayList in the Dabba object. This object should be passed along to LDF and USF and its elements should be updated in LDF and USF via code

public class NewDabbaFragment extends Fragment {
    private EditText scoreText, betamtText;
    private Button addMeButton, addPlayersButton;
    private ExtendedFloatingActionButton startGameButton, resetPlayersButton;
    private LinearLayout playersView;
    private TextView mDateView;
    private static final String TAG = "NewDabbaTAG";
    public static final String NDF_GAME_DATE = "date", NDF_MAX_SCORE = "max_score", NDF_BET_AMT = "bet_amts", NDF_LIVE_SCORE = "live_score", NDF_REENTRY = "re-entry";
    public static final String NDF_PLAYERS_LIST = "players", NDF_COUNT = "PLAYERS_COUNT", NDF_ROUND = "roundno", NDF_DEALERNO = "dealerno";
    private Dabba mDabba;
    private AlertDialog checkDialog;
    public static int count;
    private TextView plname_Temp;
    private String player_temp, date;
    private String[] playerNames;
    private int[] playerScores, betAmounts;
    private int[] reentry;
    private int roundno = 0, dealerno = 1;

    @Override
    public void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setRetainInstance(true);
        mDabba = new Dabba();       // creating a Dabba object to save certain parameters
        mDabba.setmDate(new Date());
        count = 0;
        Log.d(TAG, "onCreate() of NDF called, connected to Activity: " + getActivity());
        playerNames = new String[20];
        checkDialog = new AlertDialog.Builder(getActivity()).create();
        checkDialog.setTitle("Hygiene Checks");
    }

    // Create the first view consisting of date, maxScore - EditText, betAmt per player - EditText and players' list
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_dabba_fragment, parent, false);

        mDateView = v.findViewById(R.id.game_dateEditText);
        date = DateFormat.getDateTimeInstance().format(mDabba.getmDate());
        mDateView.setText(date);

        scoreText = v.findViewById(R.id.max_scoreEditText);
        scoreText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDabba.setMaxScore(Integer.parseInt(s.toString()));     // add the maxScore element to the mDabba object
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        betamtText = v.findViewById(R.id.bet_EditText);
        betamtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDabba.setBetamt(Integer.parseInt(s.toString()));       // add each player's betAmount to the mDabba object
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        playersView = v.findViewById(R.id.players_List);

        // addMeButton is included as players are being added from the contact list. The current phone may not have details about the owner's name stored in the contactlist
        addMeButton = (Button) v.findViewById(R.id.add_meButton);
        addMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plname_Temp = new TextView(getContext());
                player_temp = "Me";
                plname_Temp.setText(player_temp);
                plname_Temp.setPadding(8, 4, 2, 4);
                //Log.e(TAG, "Adding Me to the Players List");
                playersView.addView(plname_Temp);
                playerNames[count] = player_temp;
                count++;
                // Log.d(TAG, "Player count is: " + count);
                addMeButton.setEnabled(false);
                //addMeButton.setBackgroundColor(Color.parseColor("#E0F7FA"));
            }
        });

        // Choose new players from the phone's CONTACTS list
        addPlayersButton = (Button) v.findViewById(R.id.choose_playersButton);
        addPlayersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Checking if reading contacts is permitted
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Contacts Permission is not granted. Requesting user for the Contacts permission...");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }
                // if permissions are already present
                else {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    //  Log.e(TAG, "Checking for Package Manager for Contacts App to choose a Player");
                    PackageManager pm = getActivity().getPackageManager();
                    List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                    // Log.w(TAG, "activities.size(): " + activities.size());

                    boolean isIntentSafe = activities.size() > 0;
                    //Log.w(TAG, "isIntentSafe: " + isIntentSafe);

                    if (isIntentSafe) startActivityForResult(i, 1);
                    else Log.e(TAG, "No app to handle this request");
                }
            }
        });

        // below Button validates the entries, creates the game and passes all the attributes as a bundle to the LiveDabbaFragment
        startGameButton = (ExtendedFloatingActionButton) v.findViewById(R.id.create_gameFABButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            public void onClick(View v) {
                if (checkValidation() == 1) {               // check if max score is entered for the game
                    checkDialog.setMessage("Enter Max score for the Game");
                    checkDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", checkButtonListener);
                    checkDialog.show();
                } else if (checkValidation() == 2) {        // check if bet amounts per player is entered for the game
                    checkDialog.setMessage("Enter bet amounts per player");
                    checkDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", checkButtonListener);
                    checkDialog.show();
                } else if (checkValidation() == 3) {        // check if there are atleast 2 players for the game
                    checkDialog.setMessage("Enter at least 2 players");
                    checkDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", checkButtonListener);
                    checkDialog.show();
                } else {
                    //Log.d(TAG, "Creating a new game...");
                    // Log.d(TAG, "NDF Current activity is: " + getActivity());
                    //Log.d(TAG, "List of players being transferred: " + playerNames);
                    playerScores = new int[count];
                    reentry = new int[count];
                    betAmounts = new int[count];
                    // Initialize a fresh array of playerScores, reEntry data and betAmounts. Only betAmounts are set as per the betamtText
                    for (int i = 0; i < count; i++) {
                        playerScores[i] = 0;
                        reentry[i] = 0;
                        betAmounts[i] = Integer.parseInt(betamtText.getText().toString());
                    }


                    // Create the bundle which will be circulated in the game. Add the score, playersCount, eachplayerBetAmount, 0-playerScores array, 0-reEntry array, game date and the Names array
                    Bundle toLiveDabbabundle = new Bundle();
                    toLiveDabbabundle.putInt(NDF_MAX_SCORE, Integer.parseInt(scoreText.getText().toString()));
                    toLiveDabbabundle.putInt(NDF_COUNT, count);
                    toLiveDabbabundle.putInt(NDF_ROUND, roundno);
                    toLiveDabbabundle.putInt(NDF_DEALERNO, dealerno);
                    toLiveDabbabundle.putIntArray(NDF_BET_AMT, betAmounts);
                    toLiveDabbabundle.putIntArray(NDF_LIVE_SCORE, playerScores);
                    toLiveDabbabundle.putIntArray(NDF_REENTRY, reentry);
                    toLiveDabbabundle.putString(NDF_GAME_DATE, date);
                    toLiveDabbabundle.putStringArray(NDF_PLAYERS_LIST, playerNames);

                    Fragment liveDabbaFragment = new LiveDabbaFragment();
                    liveDabbaFragment.setArguments(toLiveDabbabundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, liveDabbaFragment)
                            .commit();
                }
            }
        });

        // In case, incorrect entries are entered, use the reset button and allow to re-enter the players
        resetPlayersButton = (ExtendedFloatingActionButton) v.findViewById(R.id.reset_playersFABButton);
        resetPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playersView.removeAllViewsInLayout();
                addMeButton.setEnabled(true);
                count = 0;
            }
        });

        return v;
    }

    // Validate if maxScore, betAmt per player and no. of players > 1
    private int checkValidation() {
        //Log.d(TAG, "Checking validation....");
        if (scoreText.getText().toString().isEmpty()) {
            return 1;
        }
        if (betamtText.getText().toString().isEmpty()) {
            return 2;
        }
        if (count == 0 || count == 1) {
            return 3;
        }
        return 0;
    }

    // Below onActivityResult is to open up the ContactsContract from the phone Contacts list and add them in the players' list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == 1) {        // Build the result for the suspect contact being selected
            Uri contactUri = data.getData();        // This URI will point to a single contact that the user picked
            // Specify which fields you want your query to return values for
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            // Perform your query - the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);     // Create a query asking for all the diplay names of the contacts in the returned data and get a Cursor object to work with.

            // Double-check that you actually got results
            if (c.getCount() == 0) {
                c.close();
                return;
            }
            //Log.e(TAG, "Chosen no. of contacts: " + c.getCount());
            // Pull out the first column of the first row of data - that is the player's name
            c.moveToFirst();        // Since the Cursor object only contains one item, move to the first item and get it as a String
            String player_temp = c.getString(0);
            //Log.e(TAG, "Received temp player: " + player_temp);

            // Add a new TextView to the LinearView with the name of the player for the game
            plname_Temp = new TextView(getContext());
            plname_Temp.setText(player_temp);
            //Log.e(TAG, "Adding player to the LinearView");
            plname_Temp.setPadding(8, 4, 2, 4);
            playersView.addView(plname_Temp);
            playerNames[count] = player_temp;
            count++;
            //Log.w(TAG, "Player count is: " + count);

            c.close();
        }
    }

    // Common dialogInterface to restart a NewDabbaFragment in case of insufficient inputs
    DialogInterface.OnClickListener checkButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            new NewDabbaFragment();
        }
    };

}
