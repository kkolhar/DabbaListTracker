package com.kolhar.dabbalisttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// This class is to set up the ScoreBoard view

public class LiveDabbaFragment extends Fragment {
    private TextView date_TextView, score_TextView, betamt_TextView, roundno_TextView, liveNameView, liveScoreView;
    private ExtendedFloatingActionButton nextRound, exitGame;
    private TableLayout playersLiveView;
    private AlertDialog restartDialog, changeScoreDialog;
    private static int singlebetamount, gamebet, maxscore, LDFLiveCount, gLosers, highscore;
    private static final String TAG = "LiveDabbaFragmentTAG";
    private String winner, losers = "";
    private ImageView LDFkavtiView;
    private TableRow.LayoutParams namecellParams, middlecellParams, liveScorecellParams;
    private int roundcount, dealerno;
    private static Animation shakeAnimation;
    private Button[] LDFreentryButtons;
    private Dabba LDFDabba;
    private ArrayList<Player> ldf_ALplayers;
    private Toolbar toolbar;
    // Below is a common listener for all the re-entry buttons for individual players - Updated
    View.OnClickListener reentryButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Log.d(TAG, "getting ID of player to be changed: " + v.getId());
            onReEntryFunction(v.getId());       // Get the player's ID and pass it to the onReEntryFunction to activate re-entry option
        }
    };
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.app_name);
        // Log.e(TAG, "**************************************************LDF Bundle Arguments: " + getActivity().getIntent().getSerializableExtra("LIVEDABBA"));
        LDFDabba = (Dabba) getActivity().getIntent().getSerializableExtra("LIVEDABBA");

        ldf_ALplayers = LDFDabba.getPlayers();
        // Get the count of players which will be used throughout this class
        LDFLiveCount = ldf_ALplayers.size();
        // Log.d(TAG, "globalLiveCount: " + LDFLiveCount);

        // Get the date and time of the game
        date = LDFDabba.getmDate();

        // Get the max score for the game, this remains constant throughout the game
        maxscore = LDFDabba.getMaxScore();

        // Get the total bet amount for the game. This is the summation of individual bet amounts for each player
        gamebet = 0;
        for (int i = 0; i < LDFLiveCount; i++)
            gamebet = gamebet + ldf_ALplayers.get(i).getBetAmount();
        // Log.w(TAG, "gamebet Amount is: " + gamebet);

        // Initialize the round count
        roundcount = LDFDabba.getRoundNo();
        //Log.w(TAG, "Round No.: " + LDFDabba.getRoundNo());

        // Initialize Re-entry buttons for each player
        LDFreentryButtons = new Button[LDFLiveCount];

        // Initialize the losers count
        gLosers = 0;

        // Initialize the highscore variable. This is used to find the highscore for re-entry from existing players
        highscore = 0;

        // TODO: Work on choosing the next dealer
        // dealerno = fromNDFBundle.getInt("dealerno");

        // Setting each cell's parameters used ahead in the ScoreTable
        namecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
        middlecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f);
        liveScorecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.15f);

        // initiate the animation for the kavti view
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    }

    // Inflating the Options menu in the toolbar and setting up a listener on the various options
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        toolbar.inflateMenu(R.menu.dabba_menulist);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.changeMaxScoreOption:
                                changeScoreDialog = new AlertDialog.Builder(getActivity()).create();
                                changeScoreDialog.setTitle("Change Max Score");
                                changeScoreDialog.setMessage("Enter the new Max. Score for the Game: \n");
                                final EditText input = new EditText(getActivity());
                                input.setTextSize(18);
                                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                input.setLayoutParams(lp);
                                input.setHint("Enter the new Max score here");
                                changeScoreDialog.setView(input);
                                changeScoreDialog = changeMaxScore(changeScoreDialog, input);
                                changeScoreDialog.show();
                                return true;
                            case R.id.addMorePlayersOption:
                                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                //  Log.e(TAG, "Checking for Package Manager for Contacts App to choose a Player");
                                PackageManager pm = getActivity().getPackageManager();
                                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                                // Log.w(TAG, "activities.size(): " + activities.size());

                                boolean isIntentSafe = activities.size() > 0;
                                //Log.w(TAG, "isIntentSafe: " + isIntentSafe);
// to implement reset of tableview when a new player is added
                                if (isIntentSafe) {
                                    startActivityForResult(i, 1);
                                } else Log.e(TAG, "No app to handle this request");
                                return true;
                            default:                // call the superclass implementation if the menu item ID is not in our implementation
                                return false;
                        }
                    }
                }
        );
    }

    // Set up the ScoreTable view - Updated
    private void tempPlayersLayout() {
        for (int i = 0; i <= LDFLiveCount; i++) {
            //Log.d(TAG, "Value of i is: " + i);
            TableRow row = new TableRow(getContext());          // Create a new row for each line item
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);                            // Set the row parameters based on the above LayoutParams
            row.setGravity(android.view.Gravity.CENTER);
            row.setPadding(2, 52, 2, 0);

            // Set the attributes for 2 columns, Names(liveNameView) and Scores(liveScoreView)
            liveNameView = new TextView(getContext());
            liveNameView.setTextSize(14);
            liveNameView.setLayoutParams(namecellParams);

            liveScoreView = new TextView(getContext());
            liveScoreView.setTextSize(14);
            liveScoreView.setLayoutParams(liveScorecellParams);
            liveScoreView.setGravity(android.view.Gravity.CENTER);

            // The first row will contain only the headers: Player & Live Scores
            if (i == 0) {
                liveNameView.setText("Player");
                liveScoreView.setText("Live Scores");
                liveNameView.setTypeface(null, Typeface.BOLD);
                liveNameView.setTypeface(null, Typeface.BOLD);

                row.addView(liveNameView);
                row.addView(liveScoreView);
                playersLiveView.addView(row, i);
            }
            // 2nd and further rows are created dynamically, based on the no. of players (i-1 is used to refer 1st element in corresponding array in the bundle. Each element in the array is referenced by its name and i-1 as its position)
            else {
                int score = ldf_ALplayers.get((i - 1)).getLiveScore();
                int reentry = ldf_ALplayers.get((i - 1)).getReEntry();

                liveNameView.setText(ldf_ALplayers.get(i - 1).getpName());
                liveScoreView.setText(String.valueOf(score));
                if (score < maxscore && score >= (maxscore - 20)) {
                    liveScoreView.setTextColor(Color.BLUE); // In case the player is compulsory, his score is shown in blue with a chime
                    MediaPlayer mp1 = MediaPlayer.create(getActivity(), R.raw.storedoorchime);
                    mp1.start();
                    Toast.makeText(getActivity(), "Players highlighted in blue are compulsory", Toast.LENGTH_SHORT).show();
                }
                if (score >= maxscore) {  // In case the player has already crossed the maxScore, his score is shown in red
                    liveScoreView.setTextColor(Color.RED);
                    gLosers++;                              // Losers count is increased
                    Log.e(TAG, "gLosers value is: " + gLosers);

                    // Reentry have 4 values: 0 for a fresh player, 1 if the player has lost once, 2 if the player has lost once and taken re-entry, 3 for final loser without playing sound and 4 is for final loser with sound once played
                    if (reentry == 2)        // if the player had taken re-entry, his Reentry would be 2. He has entered this loop, meaning he has lost again, so make his the final loser by making his Reentry = 3
                        ldf_ALplayers.get(i - 1).setReEntry(3);
                }

                if (checkWinner()) {            // Keep checking if all have lost and we have a final winner
                    row.addView(liveNameView);

                    // enter if loop in case the player has lost
                    if (score >= maxscore) {
                        // in case the player has lost and this was his first loss, enter the below if loop
                        if (reentry == 0) {
                            //Log.d(TAG, "Added the Re-Entry button");
                            ldf_ALplayers.get(i - 1).setReEntry(1);
                            LDFreentryButtons[i - 1] = new Button(getActivity());
                            LDFreentryButtons[i - 1].setText("Re-Entry");
                            LDFreentryButtons[i - 1].setId(i - 1);         // Setting the reEntryButton ID equal to the player no.
                            LDFreentryButtons[i - 1].setOnClickListener(reentryButtonListener);    // Setting the listener for the corresponding button

                            row.addView(LDFreentryButtons[i - 1]);
                        }
                        // in case the player has lost and this was his second loss, enter the below if loop
                        else if (reentry == 1) {
                            Log.d(TAG, "One re-entry for this player is done: " + ldf_ALplayers.get(i - 1).getpName());
                            ldf_ALplayers.get(i - 1).setReEntry(3);
                        }

                        // initiate the kavti view
                        LDFkavtiView = new ImageView(getContext());
                        LDFkavtiView.setImageResource(R.drawable.kavti);
                        LDFkavtiView.setLayoutParams(liveScorecellParams);
                        row.addView(LDFkavtiView);
                        LDFkavtiView.startAnimation(shakeAnimation);
                        if (reentry != 4) {       // if the player has lost in the previous round, don't play the sound again
                            MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.wickedwitchlaugh);
                            mp.start();
                            ldf_ALplayers.get(i - 1).setReEntry(4);
                        }
                    }

                    // in case he has not lost yet, just display his score
                    else {
                        row.addView(liveScoreView);
                    }
                    playersLiveView.addView(row, i);        // add the row to the table
                }
                // If all the players have lost and we have a final winner, find the winner in the for loop and declare him
                else {
                    for (int winc = 0; winc < LDFLiveCount; winc++) {
                        if (ldf_ALplayers.get(winc).getLiveScore() < maxscore) {
                            winner = ldf_ALplayers.get(winc).getpName();
                        } else {
                            losersList(ldf_ALplayers.get(winc).getpName(), ldf_ALplayers.get(winc).getLiveScore());
                        }
                    }

                    //  Play the sound for one last time
                    MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.wickedwitchlaugh);
                    mp.start();

                    restartDialog = new AlertDialog.Builder(getActivity()).create();
                    restartDialog.setTitle("Game Over!");
                    restartDialog.setMessage("Winner: " + winner + "!!!\n\n" + "Losers: \n" + losers +
                            "\n Do you want to restart a new game?");
                    restartDialog = exitAlertMethod(restartDialog);
                    restartDialog.show();
                }
            }
        }
    }

    // Check if we have a final Winner - Updated
    private boolean checkWinner() {
        if (gLosers >= (LDFLiveCount - 1))
            return false;
        // return true in case winner is found
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.live_dabba_fragment, parent, false);
        //Log.e(TAG, "LDF Dabba Bundle: " + LDFDabba);

        // toolBar is a simpler way to create an ActionBar in androidx. Earlier, there was ActionBar which was painful
        toolbar = (Toolbar) v.findViewById(R.id.ldftoolbar);

        // Initializing all the views for a Live game
        date_TextView = v.findViewById(R.id.game_dateText);
        score_TextView = v.findViewById(R.id.max_scoreText);     // Shows the max score set for the game
        betamt_TextView = v.findViewById(R.id.bet_Text);        // Shows the bet amount for the game
        //changeMaxScoreButton = v.findViewById(R.id.changeMaxScoreButton);
        roundno_TextView = v.findViewById(R.id.roundno_Text);     // Shows the round no. for each round
        playersLiveView = (TableLayout) v.findViewById(R.id.liveScoreTables);
        exitGame = (ExtendedFloatingActionButton) v.findViewById(R.id.exit_FABButton);
        nextRound = (ExtendedFloatingActionButton) v.findViewById(R.id.next_RoundFABButton);

        // Set the date, maxScore, total bet_Amount and round_count for the game. Only the total bet_Amount will vary in case of re-Entry
        date_TextView.setText(date);
        score_TextView.setText(String.valueOf(maxscore));
        betamt_TextView.setText(String.valueOf(gamebet));
        roundno_TextView.setText(String.valueOf(roundcount));

        // In case, the game has to be exited in between
        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       // Dialog box to start a new game or close the app
                restartDialog = new AlertDialog.Builder(getActivity()).create();
                restartDialog.setTitle("Exit Game");
                restartDialog.setMessage("Do you want to restart a new game?");
                restartDialog = exitAlertMethod(restartDialog);  // DialogBox button options and actions are set in a separate class
                restartDialog.show();
            }
        });

        // Button to update the scores for each round. All the variables are put in the fromNDFBundle and passed to UpdateScoreFragment. Only some of them are used/updated in the USFragment
        nextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the new fragment and pass the bundle using .setArguments()
                Fragment updateScoreFragment = new UpdateScoreFragment(LDFDabba);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, updateScoreFragment)
                        .commit();
            }
        });

        // Set up the ScoreTable using the various parameters from the fromNDFBundle
        tempPlayersLayout();

        return v;
    }

    // Code to be added to create a list of games screen at launch
    // DialogBox to either quit or start a new game - Updated
    private AlertDialog exitAlertMethod(AlertDialog dialog) {
        dialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment restartGame = new NewDabbaFragment();
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, restartGame)
                        .commit();
            }
        });
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ldf_ALplayers.removeAll(ldf_ALplayers);     // Need to check
                getActivity().finish();
            }
        });
        dialog.setButton(Dialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    // Re-Entry function to check for re-entry validation - Updated
    private void onReEntryFunction(int ientry) {
        String name = ldf_ALplayers.get(ientry).getpName();
        int betamnt = ldf_ALplayers.get(ientry).getBetAmount();
        int rescore = findHighScore();
        //Log.i(TAG, "pressed Button ID is: " + ientry);
        //Log.d(TAG, "Changing entries for player: " + name);
        Log.d(TAG, "Current Entry: Player[" + ientry + "]: " +
                "score: " + ldf_ALplayers.get(ientry).getLiveScore()
                + ", name: " + name +
                ", bet amount: " + betamnt);

        // Re-Entry logic: Bet Amount is doubled, High score is the highest live score available, player Name is attached RE
        // Find the highScore amongst the live players

        name = name + " -> RE";              // tag the player with re-entry
        betamnt = 2 * betamnt;            // increase the player's bet amount by 2x

        Log.d(TAG, "Updated for player[" + ientry + "]: " +
                "score: " + rescore
                + ", name: " + name +
                ", bet amount: " + betamnt);
        LDFreentryButtons[ientry].setEnabled(false);  // Disable the re-entry button after clicking once

        // Changing the entries in the ArrayList to be carried further
        ldf_ALplayers.get(ientry).setpName(name);
        ldf_ALplayers.get(ientry).setBetAmount(betamnt);
        ldf_ALplayers.get(ientry).setLiveScore(rescore); // assign the highScore to the player attempting re-entry
        ldf_ALplayers.get(ientry).setReEntry(2);           // This set the globalReentries for that player = 2 i.e. lost once and re-entered

        Toast.makeText(getActivity(), "Re-entry given to " + name + ", bet amount: " + betamnt, Toast.LENGTH_LONG).show();
    }

    private void losersList(String loser, int loserScore) {
        losers = losers + " " + loser + ": " + loserScore + "\n";
    }

    // DialogBox to either quit or start a new game - Updated
    private AlertDialog changeMaxScore(AlertDialog dialog, final EditText input) {
        dialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check if the max score has a new value or not
                if (input.getText().toString() == "" || Integer.parseInt(input.getText().toString()) == 0) {
                    Toast.makeText(getActivity(), "Enter a valid score Number", Toast.LENGTH_SHORT).show();
                }
                // if it has a new value, change the max score to this value
                else {
                    maxscore = Integer.parseInt(input.getText().toString());
                    score_TextView.setText(String.valueOf(maxscore));
                    LDFDabba.setMaxScore(maxscore);
                    Log.w(TAG, "New Max Score: " + LDFDabba.getMaxScore());
                    Toast.makeText(getActivity(), "Changes applicable from next round", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.setButton(Dialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

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
            // Pull out the first column of the first row of data - that is the player's name
            c.moveToFirst();        // Since the Cursor object only contains one item, move to the first item and get it as a String
            String player_temp = c.getString(0);
            c.close();

            // Find the highScore to be assigned to the newly entered player
            int score = findHighScore();

            // Setting the single bet amount, in case a player wants to enter in the middle of a live game
            for (int i = 0; i < LDFLiveCount; i++) {
                if (singlebetamount < ldf_ALplayers.get(i).getBetAmount())
                    singlebetamount = ldf_ALplayers.get(i).getBetAmount();
            }

            // Add the player to the ArrayList of players
            Player player = new Player();
            player.setpName(player_temp);
            player.setLiveScore(score);
            player.setBetAmount(singlebetamount);
            ldf_ALplayers.add(player);

            //Log.w(TAG, "Added a new player: " + player.getpName() + ", and score: " + score);
            Toast.makeText(getActivity(), "New Player added: " + player_temp + ", \n Score: " + score + ", Bet Amt.: " + singlebetamount, Toast.LENGTH_LONG).show();
        }
    }

    private int findHighScore() {
        for (int i = 0; i < LDFLiveCount; i++) {
            if ((highscore < ldf_ALplayers.get(i).getLiveScore()) && (ldf_ALplayers.get(i).getLiveScore() < maxscore))
                highscore = ldf_ALplayers.get(i).getLiveScore();
            Log.i(TAG, "Highest score found is: " + highscore);
        }
        return highscore;
    }
}