package com.elan.p2pchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.elan.p2pchat.Constants.AppConstants;
import com.elan.p2pchat.Utils.AppPreferences;
import com.elan.p2pchat.ui.QRScanning;
import com.elan.p2pchat.ui.dialogs.KnowIPDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText ipet, namet,numbert;
    private TextInputLayout ipl, namel,numberl;
    private Button connect,ipBtn;
    private KnowIPDialog knowIPDialog;
    private AppPreferences appPreferences;
    private ImageView scannerView;

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

        scannerView=(ImageView) findViewById(R.id.scan_image);

        if (appPreferences.containsKey(AppConstants.NAME))
            namet.setText(appPreferences.getString(AppConstants.NAME));

        if (appPreferences.containsKey(AppConstants.PHONE_NUMBER))
            numbert.setText(appPreferences.getString(AppConstants.PHONE_NUMBER));

        connect.setOnClickListener(this);
        scannerView.setOnClickListener(this);
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
            case R.id.scan_image:
                openScanner();
                break;
        }
    }

    private void openScanner() {
        Log.d("qwerty","here11");
    Intent intent =new Intent(MainActivity.this, QRScanning.class);
    startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            String ipAddress=data.getStringExtra("ipAddress");
            ipet.setText(ipAddress);
        }
    }

    private void connectMethod() {
        String ip, name,number;
        int f = 0;
        ip = ipl.getEditText().getText().toString().trim();
        name = namel.getEditText().getText().toString().trim();
        number = numberl.getEditText().getText().toString().trim();
        ipet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_crop_free_black_24dp, 0);
        namet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        numbert.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);

        if (ip.isEmpty()) {
            ipet.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_error_black_24dp, 0,R.drawable.ic_crop_free_black_24dp, 0);
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
