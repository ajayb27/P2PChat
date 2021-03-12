package com.elan.p2pchat.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.elan.p2pchat.R;

public class KnowIPDialog implements View.OnClickListener {

    private Activity activity;
    private AlertDialog alertDialog;
    private Button ok;

    public KnowIPDialog(Activity activity) {
        this.activity = activity;
        intiView();
    }

    public void showDialog() {
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

        ok = (Button) layout.findViewById(R.id.okBtn);
        ok.setOnClickListener(this);
    }


    public void closeDialog() {
        alertDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okBtn:
                closeDialog();
                break;
        }
    }

}
