package com.kolhar.dabbatrackerv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// This class is used to accept scores for the new round, if the player is still in the game

public class UpdateScoreFragment extends Fragment {
    public static final String TAG = "UpdateScoreDabbaTAG";
    private String[] globalplNames;
    private int[] globalplScores, individualRoundScores;
    private Bundle fromLDFBundle, toLDFBundle;
    private TableLayout tempTable;
    private ExtendedFloatingActionButton doneButton, cancelButton;
    private int globalCount, counter, USFMaxScore;
    private TextView tempNameView, tempScoreView, firstUpdateView;
    private ImageView USFkavtiView;
    private EditText tempNewScoreEdit;
    private static Animation shakeAnimation;
    List<EditText> allEds = new ArrayList<EditText>();
    private int[] USFReentries;
    private int roundcount;
    private int dealerno;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        fromLDFBundle = this.getArguments();
        //Log.e(TAG, "------------------------------onCreate called, fromLDFBundle is: " + fromLDFBundle);
        toLDFBundle = fromLDFBundle;        // get the fromLDFBundle to a local bundle: toLDFBundle
        globalCount = fromLDFBundle.getInt("PLAYERS_COUNT");
        Log.d(TAG, "GlobalCount in USF is: " + globalCount);

        globalplNames = new String[globalCount];
        globalplNames = fromLDFBundle.getStringArray("players");
        //   Log.d(TAG, "globalplNames array created of length: " + globalplNames.length);

        globalplScores = new int[globalCount];
        globalplScores = fromLDFBundle.getIntArray("live_score");
        //   Log.d(TAG, "globalplScores array created of length: " + globalplScores.length);

        USFMaxScore = fromLDFBundle.getInt("max_score");

        individualRoundScores = new int[globalCount];
        //  Log.d(TAG, "individualRoundScores array created of length: " + individualRoundScores.length);

        // Get all the players re-entries scenario
        USFReentries = new int[globalCount];
        USFReentries = fromLDFBundle.getIntArray("re-entry");

        // get the round_count from NDF
        roundcount = fromLDFBundle.getInt("roundno");

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.update_score_fragment, parent, false);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams namecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f);
        TableRow.LayoutParams liveScorecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f);
        TableRow.LayoutParams newScorecellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f);

        // Below table shows 3 columns: playerNames, ExistingScores and ThisRound's Scores to be updated via EditText
        tempTable = (TableLayout) v.findViewById(R.id.scoreTables);

        // Create the tempTable in the below for loop
        for (counter = 0; counter <= globalCount; counter++) {
            //Log.d(TAG, "Value of counter is: " + counter);

            TableRow row = new TableRow(getContext());

            row.setLayoutParams(rowParams);
            row.setWeightSum(1f);
            // Log.d(TAG, "counter Value is: " + counter);

            tempNameView = new TextView(getContext());
            tempNameView.setTextSize(14);
            tempNameView.setLayoutParams(namecellParams);

            tempScoreView = new TextView(getContext());
            tempScoreView.setTextSize(14);
            tempScoreView.setLayoutParams(liveScorecellParams);
            tempScoreView.setGravity(android.view.Gravity.CENTER);

            tempNewScoreEdit = new EditText(getContext());
            allEds.add(tempNewScoreEdit);
            tempNewScoreEdit.setTextSize(14);
            tempNewScoreEdit.setLayoutParams(newScorecellParams);
            tempNewScoreEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            tempNewScoreEdit.setHint("+/-");
            tempNewScoreEdit.setFocusableInTouchMode(true);         // Used for touch options for various EditText's
            tempNewScoreEdit.setGravity(android.view.Gravity.CENTER);

            firstUpdateView = new TextView(getContext());           // This is used only for the first row, third column named 'This Round'
            firstUpdateView.setLayoutParams(newScorecellParams);

            USFkavtiView = new ImageView(getContext());
            USFkavtiView.setImageResource(R.drawable.kavti);
            USFkavtiView.setLayoutParams(newScorecellParams);
            // Below if loop to create the first row of headers: 'Player', 'Existing' and 'This Round'
            if (counter == 0) {
                //Log.w(TAG, "Entered if statement to create first row");
                tempNameView.setText("Player");
                tempScoreView.setText("Existing");
                firstUpdateView.setText("This Round");

                tempNameView.setTypeface(null, Typeface.BOLD);
                tempScoreView.setTypeface(null, Typeface.BOLD);
                firstUpdateView.setTypeface(null, Typeface.BOLD);

                row.addView(tempNameView);
                row.addView(tempScoreView);
                row.addView(firstUpdateView);
                tempTable.addView(row, counter);
            }
            // Continue looping through the various bundle arrays and show them in the table
            else {
                // Log.w(TAG, "Entered else statement to create further rows");
                tempNameView.setText(globalplNames[counter - 1]);
                tempScoreView.setText(String.valueOf(globalplScores[counter - 1]));

                // In case the player has already lost, show kavti
                if (globalplScores[counter - 1] >= USFMaxScore) {
                    tempScoreView.setTextColor(Color.RED);
                    //tempNewScoreEdit.setEnabled(false);       -- this code is replaced by the kavtiView below
                    //tempNewScoreEdit.setHint("Lost");

                    row.addView(tempNameView);
                    row.addView(tempScoreView);
                    row.addView(USFkavtiView);
                    USFkavtiView.startAnimation(shakeAnimation);
                }
                // If the player is still in the game, enter his row to take in new scores
                else {
                    row.addView(tempNameView);
                    row.addView(tempScoreView);
                    row.addView(tempNewScoreEdit);
                }
                row.setGravity(android.view.Gravity.CENTER);
                tempTable.addView(row, counter);
            }
        }

        // Done button is to revert to the live ScoreTable view
        doneButton = (ExtendedFloatingActionButton) v.findViewById(R.id.doneFABButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate the no. of entries before goinog to LDF screen
                if (updateScores())     // Before going to the main ScoreTable, update the scores for each player by adding this round's scores
                {
                    toLDFBundle.putIntArray("live_score", globalplScores);   // put the updates scores in the same common bundle
                    toLDFBundle.putInt("roundno", roundcount);
                    startLDFFragment();
                }
            }
        });

        cancelButton = (ExtendedFloatingActionButton) v.findViewById(R.id.cancelFABButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLDFFragment();
            }
        });
        return v;
    }

    // Update the scores by adding this round's scores to the existing scores
    public Boolean updateScores() {
        // Check if all the entries are done. If yes, add the scores to the existing scores, else show AlertDialog
        if (validateEntries()) {
            for (int j = 0; j < globalCount; j++) {
                //Log.e(TAG, "allEds.get(" + (j) + "): " + allEds.get(j).getText().toString());
                if (allEds.get(j + 1).getText().toString().equals("")) {
                    globalplScores[j] = globalplScores[j] + 0;
                } else {
                    individualRoundScores[j] = Integer.parseInt(allEds.get(j + 1).getText().toString());
                    globalplScores[j] = globalplScores[j] + individualRoundScores[j];
                    //Log.i(TAG, "globalplScores[" + j + "]: " + globalplScores[j]);
                }
            }
            roundcount++;
            return true;
        } else {
            AlertDialog entriesDialog = new AlertDialog.Builder(getActivity()).create();
            entriesDialog.setTitle("Incorrect No. of Entries");
            entriesDialog.setMessage("Come on! There can be only one winner for each round. " +
                    "You have entered too many or too few scores for this round");
            entriesDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            entriesDialog.show();
            return false;
        }
    }

    // to choose the next dealer
    public void chooseDealer() {
        // set the dealer no. as the next player
        dealerno = (roundcount + 1) % globalCount;

        // in case the next player is already out, then move to the next player
        while (globalplScores[dealerno] > USFMaxScore) {
            dealerno = (dealerno + 1) % globalCount;
        }

        Log.d(TAG, "Next game's dealer is:" + globalplNames[dealerno]);
    }

    // to validate if all entries are done
    private Boolean validateEntries() {
        // Initiate the no. of live players
        int emptyScores = 0, livePlayers = 0;

        for (int j = 0; j < globalCount; j++) {
            if (globalplScores[j] < USFMaxScore)
                livePlayers++;
        }

        Log.d(TAG, "Value of livePlayers is: " + livePlayers);

        for (int j = 0; j < globalCount; j++) {
            if (allEds.get(j + 1).getText().toString().equals("") || Integer.parseInt(allEds.get(j + 1).getText().toString()) == 0)
                emptyScores++;
        }

        if (emptyScores == ((globalCount - livePlayers) + 1))
            return true;
        else return false;
    }

    private void startLDFFragment(){
        Fragment ldFragment = new LiveDabbaFragment();
        ldFragment.setArguments(toLDFBundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, ldFragment)
                .commit();
    }
}

