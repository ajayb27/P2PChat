package com.elan.p2pchat.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.elan.p2pchat.Constants.AppConstants;
import com.elan.p2pchat.R;
import com.elan.p2pchat.Utils.AppPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileDialog implements View.OnClickListener{

    private Activity activity;
    private AlertDialog alertDialog;
    private Button btn;
    private int type;
    private TextInputLayout  nameLayout, pNumberLayout;
    private TextInputEditText  nameEditText, pNumberEditText;
    private AppPreferences appPreferences;

    public ProfileDialog(Activity activity)
    {
        this.activity=activity;
        appPreferences=AppPreferences.getAppPreferences(activity);
        type=1;
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
        type=1;
        if (appPreferences.containsKey(AppConstants.NAME))
            nameEditText.setText(appPreferences.getString(AppConstants.NAME));

        if (appPreferences.containsKey(AppConstants.PHONE_NUMBER))
            pNumberEditText.setText(appPreferences.getString(AppConstants.PHONE_NUMBER));

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
           if(type==1)
           {
               nameEditText.setFocusable(true);
               nameEditText.setFocusableInTouchMode(true);
               nameEditText.setClickable(true);
               pNumberEditText.setFocusable(true);
               pNumberEditText.setFocusableInTouchMode(true);
               pNumberEditText.setClickable(true);
               btn.setText("SAVE DETAILS");
               type=2;
           }
           else
           {
               String name, number;
               name = nameLayout.getEditText().getText().toString().trim();
               number = pNumberLayout.getEditText().getText().toString().trim();

               appPreferences.insertString(AppConstants.NAME, name);
               appPreferences.insertString(AppConstants.PHONE_NUMBER, number);

               Toast.makeText(activity,"Details saved",Toast.LENGTH_SHORT).show();
           }
        }
    }
}
