package com.elan.p2pchat.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elan.p2pchat.R;
import com.elan.p2pchat.Utils.Utils;

public class KnowIPDialog implements View.OnClickListener {

    private Activity activity;
    private AlertDialog alertDialog;
    private Button ok;
    private TextView textViewIP;

    public KnowIPDialog(Activity activity) {
        this.activity = activity;
        intiView();
    }

    public void showDialog() {
        String ipAddress = Utils.getIPAddress(true);
        textViewIP.setText(ipAddress);
        alertDialog.show();
    }

    private void intiView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.ip_dialog, null);
        builder.setView(null);
        builder.setView(layout);
        builder.setCancelable(true);
        alertDialog = builder.create();

        textViewIP = (TextView) layout.findViewById(R.id.ip_tv);
        ok = (Button) layout.findViewById(R.id.okBtn);
        ok.setOnClickListener(this);
    }


    private void closeDialog() {
        alertDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.okBtn) {
            closeDialog();
        }
    }

}
