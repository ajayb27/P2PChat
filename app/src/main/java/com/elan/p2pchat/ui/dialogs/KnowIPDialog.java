package com.elan.p2pchat.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.elan.p2pchat.R;
import com.elan.p2pchat.Utils.Utils;
import com.elan.p2pchat.ui.QRGenerator;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class KnowIPDialog implements View.OnClickListener {

    private Activity activity;
    private AlertDialog alertDialog;
    private Button ok;
    private TextView textViewIP;
    private String ipAddress;
    private ImageView imageView;
    private final static String NXTLINE = "\n\nOR\nSCAN THE CODE";

    public KnowIPDialog(Activity activity) {
        this.activity = activity;
        intiView();
    }

    public void showDialog() {
        ipAddress = Utils.getIPAddress(true);
        String txt = ipAddress+NXTLINE;
        textViewIP.setText(txt);
        generateQR();
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
        imageView = (ImageView) layout.findViewById(R.id.imgIP);
        ok.setOnClickListener(this);
    }

    private void generateQR() {
        QRGEncoder qrgEncoder = new QRGEncoder(ipAddress, null, QRGContents.Type.TEXT, 500);
        Bitmap bitmap = qrgEncoder.getBitmap();
        imageView.setImageBitmap(bitmap);
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
