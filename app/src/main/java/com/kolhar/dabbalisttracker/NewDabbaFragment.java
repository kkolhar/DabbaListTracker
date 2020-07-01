package com.kolhar.dabbalisttracker;

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
import android.graphics.Color;
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
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class is to set up the game with the max score, game bet amount and the no. of players. No. of players is restricted to 20 for now.

public class NewDabbaFragment extends Fragment {
    private EditText scoreText, betamtText;
    private Button addMeButton, addPlayersButton;
    private ExtendedFloatingActionButton startGameButton, resetPlayersButton;
    private TextView mDateView;
    private static final String TAG = "NewDabbaTAG";
    private Dabba mDabba;
    private AlertDialog checkDialog;
    private String date;
    private ArrayList<Player> mPlayers;
    private NDFAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setRetainInstance(true);
        mDabba = new Dabba();       // creating a Dabba object to save certain parameters

        //Log.d(TAG, "onCreate() of NDF called, connected to Activity: " + getActivity());
        checkDialog = new AlertDialog.Builder(getActivity()).create();
        checkDialog.setTitle("Hygiene Checks");

        // Creating the ArrayList of players to be included in the list
        mPlayers = new ArrayList<Player>();
        adapter = new NDFAdapter(getActivity(), mPlayers);
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

        // Set up the recyclerView, mandatorily need the LayoutManager and adapter for the view, set the decoration as required
        recyclerView = v.findViewById(R.id.playersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        // ItemTouchHelper is required to do any actions. In this case, we are doing swipe and hold-&-press reordering in the view itself
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // addMeButton is included as players are being added from the contact list. The current phone may not have details about the owner's name stored in the contactlist
        addMeButton = (Button) v.findViewById(R.id.add_meButton);
        addMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player player = new Player();
                player.setpName("Me");
                mPlayers.add(player);
                adapter.notifyDataSetChanged();
                Log.w(TAG, "RecyclerView has adapter: " + recyclerView.hasPendingAdapterUpdates());
                Log.e(TAG, "Adapter updated with: " + player.getpName());

                // Log.d(TAG, "Player count is: " + count);
                addMeButton.setEnabled(false);
            }
        });

        // Choose new players from the phone's CONTACTS list
        addPlayersButton = (Button) v.findViewById(R.id.choose_playersButton);
        addPlayersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Check if reading contacts is permitted
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
                    //Log.d(TAG, "List of players being transferred: " + playerNames);

                    // Set all the bet amounts to the per amount value
                    for (int i = 0; i < mPlayers.size(); i++) {
                        mPlayers.get(i).setBetAmount(mDabba.getBetamt());
                    }
                    mDabba.setPlayers(mPlayers);
                    // mDabba.setBetamt(Integer.parseInt(betamtText.toString()));
                    mDabba.setWinner("");
                    // mDabba.setMaxScore(Integer.parseInt(scoreText.toString()));
                    // Create the bundle which will be circulated in the game. Add the score, playersCount, eachplayerBetAmount, 0-playerScores array, 0-reEntry array, game date and the Names array
                    Bundle toLiveDabbabundle = new Bundle();
                    toLiveDabbabundle.putParcelable("GAME_DABBA", mDabba);
                    // In case, all the fragments are running on the same Activity, the below code can be used
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
                mPlayers.clear();
                adapter.notifyDataSetChanged();
                addMeButton.setEnabled(true);
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
        if (mPlayers.size() == 0 || mPlayers.size() == 1) {
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
            // Pull out the first column of the first row of data - that is the player's name
            c.moveToFirst();        // Since the Cursor object only contains one item, move to the first item and get it as a String
            String player_temp = c.getString(0);
            //Log.e(TAG, "Received temp player: " + player_temp);

            //Log.w(TAG, "Player count is: " + count);
            Player player = new Player();
            player.setpName(player_temp);
            mPlayers.add(player);
            adapter.notifyDataSetChanged();
            Log.e(TAG, "Adapter updated with: " + player.getpName());
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

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        //override getMovementFlags() to specify which directions of drags and swipes are supported
        // Below 3 methods needed to enable swipe movement and Sort (up & down) movement
        @Override
        public int getMovementFlags(RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        // Implementations should return true from isLongPressDragEnabled() in order to support starting drag events from a long press on a RecyclerView item.
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        // Below function to define what is to be done, Undo option available
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Log.d(TAG, "onSwiped called");
            // Get ready to swipe
            int position = viewHolder.getAdapterPosition();
            final Player removedPlayer = mPlayers.get(position);       // Temporarily store the name in case it needs to be revived
            String tempname = mPlayers.get(position).getpName();
            Log.i(TAG, "Deleting player: " + tempname);
            // Remove the item
            mPlayers.remove(position);

            if (tempname.equals("Me"))
                addMeButton.setEnabled(true);    // In case, Me is deleted, the Me button is enabled
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(viewHolder.itemView, removedPlayer.getpName() + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mPlayers.add(removedPlayer);
                    adapter.notifyDataSetChanged();
                }
            });
            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();

            adapter.notifyDataSetChanged();
        }

        // Below method to allow movement by long pressing a list item
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.d(TAG, "onMove called");
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                Log.w(TAG, "Entered if loop to swap position");
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mPlayers, i, i + 1);
                }
            } else {
                Log.w(TAG, "Entered else loop to swap position");
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mPlayers, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    };
}