package com.elan.p2pchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elan.p2pchat.dialogs.KnowIPDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText ipet, portet;
    private TextInputLayout ipl, portl;
    private Button connect,ipBtn;
    private TextView knowIPTv;
    private KnowIPDialog knowIPDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ipet = (TextInputEditText) findViewById(R.id.ipet);
        portet = (TextInputEditText) findViewById(R.id.portet);
        ipl = (TextInputLayout) findViewById(R.id.iptf);
        portl = (TextInputLayout) findViewById(R.id.porttf);
        connect = (Button) findViewById(R.id.connectBtn);
        ipBtn = (Button) findViewById(R.id.getIpBtn);
        knowIPDialog=new KnowIPDialog(this);

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
        String ip, port;
        int f = 0;
        ip = ipl.getEditText().getText().toString().trim();
        port = portl.getEditText().getText().toString().trim();
        ipet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        portet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (ip.isEmpty()) {
            ipet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
            ipl.setError("Field cannot be empty");
            ipl.setErrorEnabled(true);
            f++;
        } else {
            ipl.setError(null);
            ipl.setErrorEnabled(false);
        }

        if (port.isEmpty()) {
            portet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
            portl.setError("Field cannot be empty");
            portl.setErrorEnabled(true);
            f++;
        } else {
            portl.setError(null);
            portl.setErrorEnabled(false);
        }

        if(f==0)
        {
            //connect
        }


    }

}
