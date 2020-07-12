package com.kolhar.dabbalisttracker;

// Adapter to connect the Player objects to the RecyclerView. This class is used only for NDF fragment

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.UUID;

public class NDFAdapter extends RecyclerView.Adapter<NDFAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Player> mPlayers;
    private final static String TAG = "NDFAdapterTAG";

    public NDFAdapter(Context context, ArrayList<Player> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        mPlayers = list;
    }

    public ArrayList<Player> getmPlayers() {
        return mPlayers;
    }

    // Mandatory class to be overridden in RecyclerView.Adapter
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ndf_playerslist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    // Mandatory class to be overridden in RecyclerView.Adapter
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Log.d(TAG, "Position is: " + list.get[position]);
        holder.playerName.setText(mPlayers.get(position).getpName());
    }

    @Override
    public int getItemCount() {
        //return <size of your string array list>;
        return mPlayers.size();
    }

    // Class to connect each view with its corresponding type of object. This class forms a single ViewHolder for the list of views in the RecyclerView
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;

        public MyViewHolder(View itemView) {
            super(itemView);
            playerName = (TextView) itemView.findViewById(R.id.playerAListTextView);
        }
    }

    // Individual crimes are not used after the Crimes Array is introduced above
    public Player getPlayer(UUID id) {
        for (Player p : mPlayers) {
            if (p.getID().equals(id)) return p;
        }
        return null;
    }

    public void addPlayer(Player p) {
        Log.i(TAG, "Adding player " + p + " to the mPlayers arrayList");
        mPlayers.add(p);
    }

    public void deletePlayer(Player p) {
        mPlayers.remove(p);
    }
}
