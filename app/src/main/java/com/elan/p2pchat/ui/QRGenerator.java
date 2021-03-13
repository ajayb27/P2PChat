package com.elan.p2pchat.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elan.p2pchat.R;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);

        ipAddress=getIntent().getExtras().getString("ipAddress");

        imageView = (ImageView) findViewById(R.id.img);
        textView = (TextView) findViewById(R.id.tv);

        if(!ipAddress.isEmpty())
        {
            textView.setText("YOUR IP ADDRESS IS \n"+ipAddress);
            QRGEncoder qrgEncoder = new QRGEncoder(ipAddress, null, QRGContents.Type.TEXT, 500);
            Bitmap bitmap = qrgEncoder.getBitmap();
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            Toast.makeText(QRGenerator.this,"Error ",Toast.LENGTH_SHORT).show();
        }
    }
}
