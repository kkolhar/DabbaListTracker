package com.kolhar.dabbalisttracker;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class LiveDabbaActivity extends SingleFragmentActivity {
    private static final String TAG = "LiveDabbaActivityTAG";

    @Override
    protected Fragment createFragment() {
        Log.i(TAG, "Creating the LiveDabbaFragment");
        return new LiveDabbaFragment();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, FirstScreenActivity.class);
        startActivity(i);
        finish();
    }
}
