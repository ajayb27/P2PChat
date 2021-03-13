package com.elan.p2pchat.ui.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.elan.p2pchat.Constants.AppConstants;
import com.elan.p2pchat.R;
import com.elan.p2pchat.Utils.AppPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileDialog implements View.OnClickListener {

    private Activity activity;
    private AlertDialog alertDialog;
    private Button btn;
    private int type;
    private TextView title;
    private TextInputLayout nameLayout, pNumberLayout;
    private TextInputEditText nameEditText, pNumberEditText;
    private AppPreferences appPreferences;
    private static final int REQUEST_CALL = 1;


    //type=1 view
    //type=2 edit
    //type=3 sender

    public ProfileDialog(Activity activity, int type) {
        this.activity = activity;
        this.type = type;
        initView();
    }

    private void initView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.profile_dialog, null);
        builder.setView(null);
        builder.setView(layout);
        builder.setCancelable(true);
        alertDialog = builder.create();

        title = (TextView) layout.findViewById(R.id.details_title);
        nameLayout = (TextInputLayout) layout.findViewById(R.id.nametf);
        pNumberLayout = (TextInputLayout) layout.findViewById(R.id.numbertf);
        nameEditText = (TextInputEditText) layout.findViewById(R.id.namet);
        pNumberEditText = (TextInputEditText) layout.findViewById(R.id.numbert);
        btn = (Button) layout.findViewById(R.id.profile_btn);
        btn.setOnClickListener(this);
    }

    private void closeDialog() {
        alertDialog.dismiss();
    }

    public void showDialog() {
        if (type == 1 || type == 2) {

            appPreferences = AppPreferences.getAppPreferences(activity);
            if (appPreferences.containsKey(AppConstants.NAME))
                nameEditText.setText(appPreferences.getString(AppConstants.NAME));

            if (appPreferences.containsKey(AppConstants.PHONE_NUMBER))
                pNumberEditText.setText(appPreferences.getString(AppConstants.PHONE_NUMBER));
        } else {
            title.setText("YOUR CONVERSATIONALIST DETAILS");
            btn.setText("CALL");
            if (!AppConstants.CONVERSATIONALIST_NAME.equals("-1"))
                nameEditText.setText(AppConstants.CONVERSATIONALIST_NAME);
            if (!AppConstants.CONVERSATIONALIST_PHONE_NUMBER.equals("-1"))
                pNumberEditText.setText(AppConstants.CONVERSATIONALIST_PHONE_NUMBER);
        }


        nameEditText.setFocusableInTouchMode(false);
        nameEditText.setFocusable(false);
        nameEditText.setClickable(false);
        pNumberEditText.setFocusableInTouchMode(false);
        pNumberEditText.setFocusable(false);
        pNumberEditText.setClickable(false);


        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile_btn) {
            if (type == 1) {
                nameEditText.setFocusable(true);
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.setClickable(true);
                pNumberEditText.setFocusable(true);
                pNumberEditText.setFocusableInTouchMode(true);
                pNumberEditText.setClickable(true);
                btn.setText("SAVE DETAILS");
                type = 2;
            } else if (type == 2) {
                String name, number;
                name = nameLayout.getEditText().getText().toString().trim();
                number = pNumberLayout.getEditText().getText().toString().trim();

                appPreferences.insertString(AppConstants.NAME, name);
                appPreferences.insertString(AppConstants.PHONE_NUMBER, number);

                Toast.makeText(activity, "Details saved", Toast.LENGTH_SHORT).show();
            } else {
                if (!AppConstants.CONVERSATIONALIST_PHONE_NUMBER.isEmpty() && !AppConstants.CONVERSATIONALIST_PHONE_NUMBER.equals("-1")) {
                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(activity);
                    dialog.setMessage("Are you sure you want to make this call ???");
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            makePhoneCall();
                        }
                    });

                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }

                    });

                    androidx.appcompat.app.AlertDialog alertDialog1 = dialog.create();
                    alertDialog1.show();

                } else {
                    Toast.makeText(activity, "Cannot make this phone call", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + AppConstants.CONVERSATIONALIST_PHONE_NUMBER;
            activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CALL) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                makePhoneCall();
//            } else
//                Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show();
//        }
//    }

}
