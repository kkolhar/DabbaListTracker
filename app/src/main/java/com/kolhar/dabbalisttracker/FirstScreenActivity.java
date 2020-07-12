package com.kolhar.dabbalisttracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

public class FirstScreenActivity extends SingleFragmentActivity {
    private AlertDialog dialog;
    private static final String TAG = "FirstScreenActivityTAG";

    @Override
    protected Fragment createFragment() {
        return new FirstScreenFragment();
    }

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this).create();

        dialog.setTitle("Exit Game");
        dialog.setMessage("\n Do you want to exit the game ?");

        dialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
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
