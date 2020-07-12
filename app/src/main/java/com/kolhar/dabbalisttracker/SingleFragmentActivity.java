package com.kolhar.dabbalisttracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // Log.d(SFActivity_TAG, "Entering Single Fragment Activity");
        FragmentManager fm = getSupportFragmentManager();  // when an activity is destroyed, its FragmentManager saves out its list of fragments. When the activity is recreated, the new FragmentManager retrieves the list and recreates the listed fragments to make everything as it was before.
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);    // the UI fragment is identified by the resource ID of its container view
        if (fragment == null) {     // when an activity is destroyed, the FM saves out its list of fragments and retrieves it when the activity is recreated. Hence, check if this fragment is null
            fragment = createFragment();
            fm.beginTransaction()   //This method creates and returns an instance of FragmentTransaction
                    .add(R.id.fragmentContainer, fragment).commit();    // Creates a new fragment transaction, include one add operation in it, and then commit it
        }
    }
}