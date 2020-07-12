package com.kolhar.dabbalisttracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class NewDabbaActivity extends SingleFragmentActivity {
    private static final String TAG = "NewDabbaActivityTAG";

    @Override
    protected Fragment createFragment() {
        return new NewDabbaFragment();
    }

    // Below onRequestPermissionsResult required for the app to seek runtime permissions to access Contacts from the user
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Contacts Permission is granted");
                } else {
                    Toast.makeText(getApplicationContext(), "No permission to read contacts!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    // Change the Back press action to include a pop-up to confirm if you want to exit the game
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, FirstScreenActivity.class);
        startActivity(i);
        finish();
    }
}