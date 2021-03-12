package com.elan.p2pchat.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elan.p2pchat.R;
import com.elan.p2pchat.Utils.AES;
import com.elan.p2pchat.dialogs.KnowIPDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    TextInputLayout ipLayout, portLayout;
    TextInputEditText ipEditText, portEditText;
    Button connectBtn,ipBtn;

    LinearLayout conversationLayout;
    EditText messageEditText;


    //Constants
    static final int MESSAGE_READ=1;
    static final String TAG = "testWeChat";
    static  final int port = 2907;


    //Objects
    AES aes;
    SendReceive sendReceive;
    ServerClass serverClass;
    ClientClass clientClass;

    //Dialogs
    KnowIPDialog knowIPDialog;



    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == MESSAGE_READ) {
                byte[] readBuff = (byte[]) msg.obj;
                String tempMsg = new String(readBuff, 0, msg.arg1);
//                addMessage(Color.parseColor("#FFFFFF"), tempMsg);
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aes = AES.getInstance();
        startServer();

        iniView();

    }

    private void iniView() {

        ipLayout = (TextInputLayout) findViewById(R.id.iptf);
        portLayout = (TextInputLayout) findViewById(R.id.porttf);

        ipEditText = (TextInputEditText) findViewById(R.id.ipet);
        portEditText = (TextInputEditText) findViewById(R.id.portet);

        connectBtn = (Button) findViewById(R.id.connectBtn);
        ipBtn = (Button) findViewById(R.id.getIpBtn);

        knowIPDialog=new KnowIPDialog(this);

        connectBtn.setOnClickListener(this);
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
        ip = ipLayout.getEditText().getText().toString().trim();
        port = portLayout.getEditText().getText().toString().trim();
        ipEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        portEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (ip.isEmpty()) {
            ipEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
            ipLayout.setError("Field cannot be empty");
            ipLayout.setErrorEnabled(true);
            f++;
        } else {
            ipLayout.setError(null);
            ipLayout.setErrorEnabled(false);
        }

        if (port.isEmpty()) {
            portEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
            portLayout.setError("Field cannot be empty");
            portLayout.setErrorEnabled(true);
            f++;
        } else {
            portLayout.setError(null);
            portLayout.setErrorEnabled(false);
        }

        if(f==0)
        {
            //connect
        }


    }

    private void startServer() {

        try {
            serverClass = new ServerClass(port);
            serverClass.start();
            Toast.makeText(this, "Server has been started..", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            Toast.makeText(this, "Server starting failure..", Toast.LENGTH_SHORT).show();
        }
    }

    private void connect() {
        String targetIpAddress="";

        try {
            clientClass = new ClientClass(targetIpAddress, port);
            clientClass.start();
            Toast.makeText(this,"Your sending port and listening port have been set successfully",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this,"ERROR : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    //Message UI handling
//    private void addMessage(int color, String message) {
//
//        runOnUiThread(() -> {
//                    TextView textView = new TextView(this);
//                    TextView msgTime = new TextView(this);
//
//                    // if it's a sender message
//                    if (color == Color.parseColor("#FCE4EC")
//                            && !(aes.decrypt(message).contains("bg@%@bg"))
//                            && !(aes.decrypt(message).contains("diconnect@%@d"))
//                            && !(aes.decrypt(message).contains("file@%@"))
//                            && !(aes.decrypt(message).contains("remove@%@"))) {
//                        textView.setPadding(200, 20, 10, 10);
//                        //textView.setMaxLines(5);
//                        textView.setGravity(Gravity.RIGHT);
//                        textView.setBackgroundResource(R.drawable.sender_messages_layout);
//                        textView.setTextIsSelectable(true);
//
//                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        // lp1.setMargins(10, 10, 10, 10);
//                        // lp1.setMargins(10, 10, 10, 10);
//                        //lp1.width = 400;
//                        lp1.leftMargin = 200;
//                        //lp1.rightMargin = 50;
//                        textView.setLayoutParams(lp1);
//
//                        msgTime.setPadding(0,0,0,0);
//
//                        msgTime.setTextSize(14);
//                        msgTime.setTextColor(Color.parseColor("#FCE4EC"));
//                        msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC );
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        msgTime.setGravity(Gravity.LEFT);
//
//                        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        // lp1.setMargins(10, 10, 10, 10);
//                        // lp1.setMargins(10, 10, 10, 10);
//                        //lp1.width = 400;
//                        lp4.leftMargin = 200;
//                        msgTime.setLayoutParams(lp4);
//
//                        //textView.setBackgroundResource(R.drawable.sender_messages_layout);
//                    }
//                    // else if receiver message
//                    else if(!(aes.decrypt(message).contains("bg@%@bg"))
//                            && !(aes.decrypt(message).contains("diconnect@%@d"))
//                            && !(aes.decrypt(message).contains("file@%@"))
//                            && !(aes.decrypt(message).contains("remove@%@"))) {
//                        textView.setPadding(10, 20, 200, 10);
//                        //textView.setMaxLines(5);
//                        textView.setGravity(Gravity.LEFT);
//                        textView.setBackgroundResource(R.drawable.receiver_messages_layout);
//                        textView.setTextIsSelectable(true);
//
//                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        //lp1.setMargins(10, 10, 10, 10);
//                        //lp1.width = 400;
//                        //lp1.leftMargin = 150;
//                        lp2.rightMargin = 200;
//                        textView.setLayoutParams(lp2);
//
//                        msgTime.setTextSize(14);
//                        msgTime.setTextColor(Color.parseColor("#FFFFFF"));
//                        msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        //lp1.setMargins(10, 10, 10, 10);
//                        //lp1.width = 400;
//                        //lp1.leftMargin = 150;
//                        lp3.rightMargin = 200;
//                        msgTime.setGravity(Gravity.RIGHT);
//                        msgTime.setLayoutParams(lp3);
//
//
//                    }
//                    textView.setTextColor(color);
//                    Log.d(TAG, "encrypted msg: " + message);
//                    String actualMessage = aes.decrypt(message);
//                    Log.d(TAG, "decrypted msg: " + actualMessage);
//
//
//                    String[]  messages = actualMessage.split("@%@", 0);
//
//                    // if its a file
//                    if(messages[0].equals("file")){
//                        textView.setPadding(0,0,0,0);
//
//                        textView.setTextSize(15);
//                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//
//
//                        Log.d(TAG, "File Name: "+messages[1]);
//                        Log.d(TAG, "File Contains:\n "+messages[2]);
//                        if(color == Color.parseColor("#FCE4EC"))
//                            textView.setText(messages[1]+" has been sent");
//                        else{
//                            textView.setText(messages[1]+" has been received and downloaded on android/data/com.example.p2p/");
//                            writeToFile(messages[2], false, messages[1]);
//                        }
//
//                    }
//                    // if its a remove message
//                    else if(messages[0].equals("remove")){
//                        textView.setPadding(0,0,0,0);
//
//                        textView.setTextSize(15);
//                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        removeAllChatForHim();
//
//                        if(color == Color.parseColor("#FCE4EC"))
//                            textView.setText("You have removed all the previous message");
//                        else{
//                            textView.setText("Your pair has removed all the previous message");
//                        }
//
//                    }
//                    // if its a bg change message
//                    else if(actualMessage.contains("bg@%@bg")){
//                        changeBGforHim(actualMessage);
//                        textView.setPadding(0,0,0,0);
//
//                        textView.setTextSize(13);
//                        textView.setTextSize(15);
//                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        if(actualMessage.equals("bg@%@bg0")){
//                            textView.setText("Background reset to default");
//                        }
//                        else
//                            textView.setText("Background has been changed");
//
//                    }
//                    // if its a disconnect message
//                    else if(actualMessage.contains("diconnect@%@d")){
//                        textView.setPadding(0,0,0,0);
//
//                        textView.setTextSize(13);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        textView.setText("Your Pair has been disconnected.");
//
//                    }
//                    // else it's a normal message
//                    else{
//                        Log.d(TAG, "messages[0]: " +messages[0]);
//                        Log.d(TAG, "messages[0]: " +messages[1]);
//
//                        textView.setTextSize(20);
//                        textView.setText(messages[0]); // setting message on the message textview
//
//                        msgTime.setText("(" + getTime(false) + ")"); // setting messing time
//                    }
//
//
//                    // creating divider between two messages
//                    addDividerBetweenTwoMessages();
//
//                    // adding 2 more views in linear layout every time
//                    conversationLayout.addView(textView);
//                    conversationLayout.addView(msgTime);
//                    conversations.post(() -> conversations.fullScroll(View.FOCUS_DOWN)); // for getting last message in first
//                }
//        );
//    }

    private void addDividerBetweenTwoMessages() {
        ImageView divider = new ImageView(this);
        conversationLayout.addView(divider);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4, 4, 4, 4);
        divider.setLayoutParams(lp);
        divider.setBackgroundColor(Color.TRANSPARENT);
    }

    // custom show toast function
    public void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }



    /*
        Inner Classes
        ..
        ..
        ServerClass
        ClientClass
        SendReceiveClass

     */

    // server class for listening
    public class ServerClass extends Thread {

        Socket socket;
        ServerSocket serverSocket;
        int port;

        ServerClass(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {

                serverSocket = new ServerSocket(port);
                Looper.prepare();

                showToast("Server Started. Waiting for client...");
                Log.d(TAG, "Waiting for client...");
                socket = serverSocket.accept();
                Log.d(TAG, "Connection established from server");
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "ERROR: " + e);
            } catch (Exception e) {
                Log.d(TAG, "ERROR: " + e);
            }
        }
    }


    // client class for sending
    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;
        int port;

        ClientClass(String hostAddress, int port) {
            this.port = port;
            this.hostAdd = hostAddress;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(hostAdd, port);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
                showToast("Connected to other device. You can now exchange messages.");

                Log.d(TAG, "Client is connected to server");

                // enabling invisible components
//                enableComponent();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Can't connect to server. Check the IP address and Port number and try again: " + e);
            } catch (Exception e) {
                Log.d(TAG, "ERROR: " + e);
            }
        }
    }


    //SendReceive object for sending and reading message
    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        SendReceive(Socket skt) {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // writing
        public void write(String msg) {
            new Thread(() -> {
                try {
                    outputStream.write(msg.getBytes());
//                    addMessage(Color.parseColor("#FCE4EC"), msg);
                    runOnUiThread(() ->
                            messageEditText.setText("")
                    );
                } catch (IOException e) {
                    Log.d(TAG, "Can't send message: " + e);
                } catch (Exception e) {
                    Log.d(TAG, "Error: " + e);
                }
            }).start();

        }
    }
}
