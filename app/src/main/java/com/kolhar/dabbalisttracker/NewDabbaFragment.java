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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class is to set up the game with the max score, game bet amount and the no. of players. No. of players is restricted to 20 for now.

public class NewDabbaFragment extends Fragment {
    private EditText scoreText, betamtText;
    private Button addMeButton, addPlayersButton;
    private static final String TAG = "NewDabbaFragmentTAG";
    private TextView mDateView;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        private String tempname;

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
            tempname = mPlayers.get(position).getpName();
            //Log.i(TAG, "Deleting player: " + tempname);
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
                    if (tempname.equals("Me"))
                        addMeButton.setEnabled(false);  // In case, Me is undo-ed, disable the button
                }
            });
            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();

            adapter.notifyDataSetChanged();
        }

        // Below method to allow movement by long pressing a list item
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //Log.d(TAG, "onMove called");
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                //Log.w(TAG, "Entered if loop to swap position");
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mPlayers, i, i + 1);
                }
            } else {
                //Log.w(TAG, "Entered else loop to swap position");
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mPlayers, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    };
    private Dabba mDabba;
    private AlertDialog checkDialog;
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

    private ExtendedFloatingActionButton startGameButton, cancelNGButton;

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

    // Create the first view consisting of date, maxScore - EditText, betAmt per player - EditText and players' list
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_dabba_fragment, parent, false);

        mDateView = v.findViewById(R.id.game_dateEditText);
        mDateView.setText(mDabba.getmDate());

        scoreText = v.findViewById(R.id.max_scoreEditText);
        betamtText = v.findViewById(R.id.bet_EditText);

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
                Log.i(TAG, "RecyclerView has adapter: " + recyclerView.hasPendingAdapterUpdates());
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
                    // Set all the bet amounts to the per amount value
                    mDabba.setBetamt(Integer.parseInt(betamtText.getText().toString()));
                    for (int i = 0; i < mPlayers.size(); i++) {
                        mPlayers.get(i).setBetAmount(mDabba.getBetamt());
                    }
                    mDabba.setPlayers(mPlayers);
                    mDabba.setWinner("");
                    mDabba.setRoundNo(0);
                    mDabba.setMaxScore(Integer.parseInt(scoreText.getText().toString()));
                    // In case, all the fragments are running on the same Activity, the below code can be used
                    Intent i = new Intent(getActivity(), LiveDabbaActivity.class);
                    i.putExtra("LIVEDABBA", mDabba);
                    startActivity(i);
                }
            }
        });

        // In case, incorrect entries are entered, use the reset button and allow to re-enter the players
        cancelNGButton = (ExtendedFloatingActionButton) v.findViewById(R.id.cancelNGFABButton);
        cancelNGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FirstScreenActivity.class);
                startActivity(i);
            }
        });

        return v;
    }
}