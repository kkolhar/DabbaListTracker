package com.kolhar.dabbalisttracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class FirstScreenFragment extends Fragment {
    private Button ngButton, prevButton;
    private Dabba prevDabba;
    private DabbaJSONSerializer djSerializer;
    private static final String TAG = "FirstScreenDabbaTAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djSerializer = new DabbaJSONSerializer(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.firstscreen, parent, false);

        ngButton = v.findViewById(R.id.newGameButton);
        ngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new Dabba Activity
                Intent i = new Intent(getActivity(), NewDabbaActivity.class);
                startActivity(i);
            }
        });

        prevButton = v.findViewById(R.id.previousGameButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the previous Dabba from the json file and reload it in LiveDabbaActivity
                prevDabba = djSerializer.loadDabba();
                if (prevDabba == null) {
                    Log.i(TAG, "No previous Dabba available to load!");
                    Toast.makeText(getActivity(), "No previous Dabba available to load!", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getActivity(), LiveDabbaActivity.class);
                    i.putExtra("LIVEDABBA", prevDabba);
                    startActivity(i);
                }
            }
        });
        return v;
    }
}
