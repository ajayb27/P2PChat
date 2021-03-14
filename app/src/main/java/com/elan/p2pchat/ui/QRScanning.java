package com.elan.p2pchat.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.elan.p2pchat.R;
import com.google.zxing.Result;

public class QRScanning extends AppCompatActivity {

    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;
    private TextView textView;
    private static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanning);

        codeScannerView = (CodeScannerView) findViewById(R.id.scanner_view);
        textView = (TextView) findViewById(R.id.tv1);

        codeScanner = new CodeScanner(this, codeScannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(result.getText());
                        Intent intent=new Intent();
                        intent.putExtra("ipAddress",result.getText());
                        setResult(2,intent);
                        finish();
                    }
                });
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        if (ContextCompat.checkSelfPermission(QRScanning.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRScanning.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            codeScanner.startPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                codeScanner.startPreview();
            } else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }
}
