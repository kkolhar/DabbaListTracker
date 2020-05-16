package com.kolhar.dabbatrackerv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

// This class is to set up the game with the max score, game bet amount and the no. of players. No. of players is restricted to 20 for now.

public class LiveDabbaFragment extends Fragment {
    private TextView date_TextView, score_TextView, betamt_TextView, roundno_TextView, liveNameView, liveScoreView;
    private ExtendedFloatingActionButton nextRound, exitGame;
    private TableLayout playersLiveView;
    private static final String TAG = "LiveDabbaTAG";
    public static final String LDF_LIVE_SCORE = "live_score", LDF_PLAYERS_LIST = "players", LDF_BET_AMT = "bet_amts", LDF_GAMEMAXSCORE = "max_score", LDF_GAMEDATE = "date", LDF_LIVECOUNT = "PLAYERS_COUNT", LDF_RE_ENTRY = "re-entries", LDF_ROUNDNO = "roundno";
    private Bundle fromNDFBundle;
    private AlertDialog restartDialog, changeScoreDialog;
    private String[] LDFPlayerNames;
    private static int gamebet, maxscore, LDFLiveCount, gLosers, highscore, i;
    private static String date;
    private String winner, losers = "";
    private ImageView LDFkavtiView;
    private int[] LDFLiveScores, LDFBetAmounts;
    private TableRow.LayoutParams namecellParams, middlecellParams, liveScorecellParams;
    private int[] LDFReentries;
    private int roundcount, dealerno;
    private static Animation shakeAnimation;
    private ImageButton changeMaxScoreButton;
    private Button[] LDFreentryButtons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //Log.e(TAG, "**************************************************this.getArguments(): " + this.getArguments());

        // get all the variables from LDF as a Bundle. This bundle continues to exist during the entire activity
        fromNDFBundle = this.getArguments();

        // Get the count of players
        LDFLiveCount = fromNDFBundle.getInt("PLAYERS_COUNT");
        Log.d(TAG, "globalLiveCount: " + LDFLiveCount);

        // Get all the live scores from the bundle which keeps updating from UpdateScoreFragment
        LDFLiveScores = new int[LDFLiveCount];
        LDFLiveScores = fromNDFBundle.getIntArray("live_score");

        // Get all the players names from the bundle which remains in the bundle - unchanged
        LDFPlayerNames = new String[LDFLiveCount];
        LDFPlayerNames = fromNDFBundle.getStringArray("players");

        // Get all the players re-entries scenario
        LDFReentries = new int[LDFLiveCount];
        LDFReentries = fromNDFBundle.getIntArray("re-entry");

        // Get all the players bet amounts. At the beginning, each bet amount is same. Bet amount is doubled on re-entry
        LDFBetAmounts = new int[LDFLiveCount];
        LDFBetAmounts = fromNDFBundle.getIntArray("bet_amts");

        // Get the date and time of the game
        date = fromNDFBundle.getString("date");

        // Get the max score for the game, this remains constant throughout the game
        maxscore = fromNDFBundle.getInt("max_score");

        // Get the total bet amount for the game. This is the summation of individual bet amounts for each player
        gamebet = 0;
        for (i = 0; i < LDFLiveCount; i++) gamebet = gamebet + LDFBetAmounts[i];
        Log.w(TAG, "gamebet Amount is: " + gamebet);

        // Initialize Re-entry buttons for each player
        LDFreentryButtons = new Button[LDFLiveCount];

        // Initialize the losers count
        gLosers = 0;

        // Initialize the highscore variable. This is used to find the highscore for re-entry from existing players
        highscore = 0;

        // Initialize the round count
        roundcount = fromNDFBundle.getInt("roundno");

        // Initialize the dealer no.
        // to be completed
        dealerno = fromNDFBundle.getInt("dealerno");

        // Setting each cell's parameters used ahead in the ScoreTable
        namecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
        middlecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f);
        liveScorecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.15f);

        // initiate the animation for the kavti view
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.live_dabba_fragment, parent, false);
        Log.e(TAG, "from NDF or USF Bundle: " + fromNDFBundle);

        // Initializing all the views for a Live game
        date_TextView = v.findViewById(R.id.game_dateText);
        score_TextView = v.findViewById(R.id.max_scoreText);     // Shows the max score set for the game
        betamt_TextView = v.findViewById(R.id.bet_Text);        // Shows the bet amount for the game
        changeMaxScoreButton = v.findViewById(R.id.changeMaxScoreButton);
        roundno_TextView = v.findViewById(R.id.roundno_Text);     // Shows the round no. for each round
        exitGame = (ExtendedFloatingActionButton) v.findViewById(R.id.exit_FABButton);
        playersLiveView = (TableLayout) v.findViewById(R.id.liveScoreTables);
        nextRound = (ExtendedFloatingActionButton) v.findViewById(R.id.next_RoundFABButton);

        // Set the date, maxScore, total bet_Amount and round_count for the game. Only the total bet_Amount will vary in case of re-Entry
        date_TextView.setText(date);
        score_TextView.setText(String.valueOf(maxscore));
        betamt_TextView.setText(String.valueOf(gamebet));
        roundno_TextView.setText(String.valueOf(roundcount));

        // Set up the ScoreTable using the various parameters from the fromNDFBundle
        tempPlayersLayout();

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
                fromNDFBundle.putInt(LDF_GAMEMAXSCORE, maxscore);
                fromNDFBundle.putInt(LDF_LIVECOUNT, LDFLiveCount);
                fromNDFBundle.putInt(LDF_ROUNDNO, roundcount);
                fromNDFBundle.putIntArray(LDF_BET_AMT, LDFBetAmounts);
                fromNDFBundle.putIntArray(LDF_LIVE_SCORE, LDFLiveScores);
                fromNDFBundle.putIntArray(LDF_RE_ENTRY, LDFReentries);
                fromNDFBundle.putString(LDF_GAMEDATE, date);
                fromNDFBundle.putStringArray(LDF_PLAYERS_LIST, LDFPlayerNames);

                // Create the new fragment and pass the bundle using .setArguments()
                Fragment updateScoreFragment = new UpdateScoreFragment();
                updateScoreFragment.setArguments(fromNDFBundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, updateScoreFragment)
                        .commit();
            }
        });

        // Change the max score in the middle of the game
        changeMaxScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       // Dialog box to start a new game or close the app
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
                changeScoreDialog = changeMaxScoreMethod(changeScoreDialog, input);
                changeScoreDialog.show();
            }
        });

        return v;
    }

    // Set up the ScoreTable view
    private void tempPlayersLayout() {
        for (i = 0; i <= LDFLiveCount; i++) {
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

            // Log.d(TAG, "No. of players received is: " + globalPlayerNames.length);
            // The first row will contain only the headers: Player & Live Scores
            if (i == 0) {
                //Log.w(TAG, "Entered if statement to create first row");
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
                //Log.w(TAG, "Entered else statement to create further rows");
                liveNameView.setText(LDFPlayerNames[i - 1]);
                liveScoreView.setText(String.valueOf(LDFLiveScores[i - 1]));
                if (LDFLiveScores[i - 1] < maxscore && LDFLiveScores[i - 1] >= (maxscore - 20)) {
                    liveScoreView.setTextColor(Color.BLUE); // In case the player is compulsory, his score is shown in blue with a chime
                    MediaPlayer mp1 = MediaPlayer.create(getActivity(), R.raw.storedoorchime);
                    mp1.start();
                    Toast.makeText(getActivity(), "Players highlighted in blue are compulsory", Toast.LENGTH_SHORT).show();
                }
                if (LDFLiveScores[i - 1] >= maxscore) {  // In case the player has already crossed the maxScore, his score is shown in red
                    liveScoreView.setTextColor(Color.RED);
                    gLosers++;                              // Losers count is increased
                    Log.e(TAG, "gLosers value is: " + gLosers);

                    // globalReentries have 4 values: 0 for a fresh player, 1 if the player has lost once, 2 if the player has lost once and taken re-entry, 3 for final loser without playing sound and 4 is for final loser with sound once played
                    if (LDFReentries[i - 1] == 2)        // if the player had taken re-entry, his globalReentries would be 2. He has entered this loop, meaning he has lost again, so make his the final loser by making his globalReentries = 3
                        LDFReentries[i - 1] = 3;
                }

                if (checkWinner()) {            // Keep checking if all have lost and we have a final winner
                    row.addView(liveNameView);

                    // enter if loop in case the player has lost
                    if (LDFLiveScores[i - 1] >= maxscore) {
                        // in case the player has lost and this was his first loss, enter the below if loop
                        if (LDFReentries[i - 1] == 0) {
                            //Log.d(TAG, "Added the Re-Entry button");
                            LDFReentries[i - 1] = 1;
                            LDFreentryButtons[i - 1] = new Button(getActivity());
                            LDFreentryButtons[i - 1].setText("Re-Entry");
                            LDFreentryButtons[i - 1].setId(i - 1);         // Setting the reEntryButton ID equal to the player no.
                            LDFreentryButtons[i - 1].setOnClickListener(reentryButtonListener);    // Setting the listener for the corresponding button

                            row.addView(LDFreentryButtons[i - 1]);
                        }
                        // in case the player has lost and this was his second loss, enter the below if loop
                        else if (LDFReentries[i - 1] == 1) {
                            Log.d(TAG, "One re-entry for this player is done. No more re-entries: " + LDFPlayerNames[i - 1]);
                            LDFReentries[i - 1] = 3;
                        }

                        // initiate the kavti view
                        LDFkavtiView = new ImageView(getContext());
                        LDFkavtiView.setImageResource(R.drawable.kavti);
                        LDFkavtiView.setLayoutParams(liveScorecellParams);
                        row.addView(LDFkavtiView);
                        LDFkavtiView.startAnimation(shakeAnimation);
                        if (LDFReentries[i - 1] != 4) {       // if the player has lost in the previous round, don't play the sound again
                            MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.wickedwitchlaugh);
                            mp.start();
                            LDFReentries[i - 1] = 4;
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
                        if (LDFLiveScores[winc] < maxscore) {
                            winner = LDFPlayerNames[winc];
                        } else {
                            losersList(LDFPlayerNames[winc], LDFLiveScores[winc]);
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

    // Check if we have a final Winner
    private boolean checkWinner() {
        if (gLosers >= (LDFLiveCount - 1))
            return false;

        return true;
    }

    // DialogBox to either quit or start a new game
    private AlertDialog exitAlertMethod(AlertDialog dialog) {
        dialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment restartGame = new NewDabbaFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, restartGame)
                        .commit();
                //dialog.dismiss();
            }
        });
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fromNDFBundle.clear();
                System.exit(0);
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

    // Re-Entry function to check for re-entry validation
    private void onReEntryFunction(int ientry) {
        Log.i(TAG, "pressed Button ID is: " + ientry);
        Log.d(TAG, "Changing entries for player: " + LDFPlayerNames[ientry]);
        Log.d(TAG, "Current Entry: Player[" + (ientry) + "]: " +
                "score: " + LDFLiveScores[ientry]
                + ", name: " + LDFPlayerNames[ientry] +
                ", bet amount: " + LDFBetAmounts[ientry]);

        // Get the highScore amongst the live players
        for (int h = 0; h < LDFLiveCount; h++) {
            if ((highscore < LDFLiveScores[h]) && LDFLiveScores[h] < maxscore)
                highscore = LDFLiveScores[h];
            Log.d(TAG, "Highest score found is: " + highscore);
        }
        LDFLiveScores[ientry] = highscore;       // assign the highScore to the player attempting re-entry
        LDFPlayerNames[ientry] = LDFPlayerNames[ientry] + "->RE";     // tag the player with re-entry
        LDFBetAmounts[ientry] = 2 * LDFBetAmounts[ientry];            // increase the player's bet amount by 2x
        Log.d(TAG, "Updated for player[" + (ientry) + "]: " +
                "score: " + LDFLiveScores[ientry]
                + ", name: " + LDFPlayerNames[ientry] +
                ", bet amount: " + LDFBetAmounts[ientry]);
        LDFreentryButtons[ientry].setEnabled(false);  // Disable the re-entry button after clicking once
        LDFReentries[ientry] = 2;                     // This set the globalReentries for that player = 2 i.e. lost once and re-entered
        Toast.makeText(getActivity(), "Re-entry given to " + LDFPlayerNames[ientry], Toast.LENGTH_SHORT).show();
    }

    // Below is a common listener for all the re-entry buttons for individual players
    View.OnClickListener reentryButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Log.d(TAG, "Attempting to change entries for player: " + globalPlayerNames[(i - 1)]);
            Log.d(TAG, "getting ID of player to be changed: " + v.getId());
            onReEntryFunction(v.getId());       // Get the player's ID and pass it to the onReEntryFunction to activate re-entry option
        }
    };

    private void losersList(String loser, int loserScore) {
        losers = losers + " " + loser + ": " + loserScore + "\n";
    }

    // DialogBox to either quit or start a new game
    private AlertDialog changeMaxScoreMethod(AlertDialog dialog, final EditText input) {
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
                    fromNDFBundle.putInt(LDF_GAMEMAXSCORE, maxscore);
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

}
