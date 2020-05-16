package com.kolhar.dabbatrackerv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class NewDabbaActivity extends SingleFragmentActivity {
    private AlertDialog dialog;

    private static final String TAG = "ActivityDabbaTAG";

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
        dialog = new AlertDialog.Builder(this).create();

        dialog.setTitle("Exit Game");
        dialog.setMessage("\n Do you want to exit the game ?");

        dialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
