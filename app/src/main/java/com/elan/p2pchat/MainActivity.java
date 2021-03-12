package com.elan.p2pchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elan.p2pchat.Constants.AppConstants;
import com.elan.p2pchat.Utils.AppPreferences;
import com.elan.p2pchat.ui.dialogs.KnowIPDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText ipet, namet,numbert;
    private TextInputLayout ipl, namel,numberl;
    private Button connect,ipBtn;
    private KnowIPDialog knowIPDialog;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        appPreferences = AppPreferences.getAppPreferences(this);
        ipet = (TextInputEditText) findViewById(R.id.ipet);
        namet = (TextInputEditText) findViewById(R.id.namet);
        numbert = (TextInputEditText) findViewById(R.id.numbert);
        ipl = (TextInputLayout) findViewById(R.id.iptf);
        namel = (TextInputLayout) findViewById(R.id.nametf);
        numberl = (TextInputLayout) findViewById(R.id.numbertf);
        connect = (Button) findViewById(R.id.connectBtn);
        ipBtn = (Button) findViewById(R.id.getIpBtn);
        knowIPDialog=new KnowIPDialog(this);

        if (appPreferences.containsKey(AppConstants.NAME))
            namet.setText(appPreferences.getString(AppConstants.NAME));

        if (appPreferences.containsKey(AppConstants.PHONE_NUMBER))
            numbert.setText(appPreferences.getString(AppConstants.PHONE_NUMBER));

        connect.setOnClickListener(this);
        ipBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connectBtn:
                connectMethod();
                break;
            case R.id.getIpBtn:
                knowIPDialog.showDialog();
                break;
        }
    }

    private void connectMethod() {
        String ip, name,number;
        int f = 0;
        ip = ipl.getEditText().getText().toString().trim();
        name = namel.getEditText().getText().toString().trim();
        number = numberl.getEditText().getText().toString().trim();
        ipet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        namet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        numbert.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);

        if (ip.isEmpty()) {
            ipet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
            ipl.setError("Field cannot be empty");
            ipl.setErrorEnabled(true);
            f++;
        } else {
            ipl.setError(null);
            ipl.setErrorEnabled(false);
        }

        if(f==0)
        {
            appPreferences.insertString(AppConstants.NAME, name);
            appPreferences.insertString(AppConstants.PHONE_NUMBER, number);
            //connect
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        appPreferences.detach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appPreferences=AppPreferences.getAppPreferences(this);
    }
}
