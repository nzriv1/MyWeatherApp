package com.nzriv.myweatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.error_title)
        .setMessage(R.string.error_message)
//                null OnClickListener will close the dialog. nothing will happen when OK button is tapped.
        .setPositiveButton(R.string.error_button_ok_text, null);

//        creates builder (dialog box) and returns it.
        return builder.create();
    }
}
