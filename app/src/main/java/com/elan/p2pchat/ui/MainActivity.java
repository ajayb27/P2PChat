package com.elan.p2pchat.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.elan.p2pchat.Constants.AppConstants;
import com.elan.p2pchat.R;
import com.elan.p2pchat.Utils.AES;
import com.elan.p2pchat.Utils.AppPreferences;
import com.elan.p2pchat.Utils.Utils;
import com.elan.p2pchat.ui.dialogs.KnowIPDialog;
import com.elan.p2pchat.ui.dialogs.ProfileDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    TextInputLayout ipLayout, nameLayout, pNumberLayout;
    TextInputEditText ipEditText, nameEditText, pNumberEditText;
    Button connectBtn, ipBtn;
    ImageView scannerView;
    TextView toolbarTitleTextView1, toolbarTitleTextView2;
    Toolbar toolbar1, toolbar2;

    ConstraintLayout firstLayout, thirdLayout;
    LinearLayout conversationLayout;
    EditText messageEditText;
    ImageButton sendButton, videoBtn;
    ScrollView conversations;

    boolean editFocus;

    //Constants
    static final int MESSAGE_READ = 1;
    static final String TAG = "testWeChat";
    static final int port = 2907;
    private static final int REQUEST_CALL = 1;
    int connectionEstablished=1;


    //Objects
    AES aes;
    AppPreferences appPreferences;
    SendReceive sendReceive;
    ServerClass serverClass;
    ClientClass clientClass;

    //Dialogs
    KnowIPDialog knowIPDialog;
//    ProfileDialog profileDialog;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == MESSAGE_READ) {
                byte[] readBuff = (byte[]) msg.obj;
                String tempMsg = new String(readBuff, 0, msg.arg1);

                String actualMessage = null;
                try {
                    actualMessage = aes.decrypt(tempMsg);

                    if (actualMessage.contains("&&")) {
                        String[] details = actualMessage.split("&&", 0);

                        AppConstants.CONVERSATIONALIST_NAME = details[0];
                        AppConstants.CONVERSATIONALIST_PHONE_NUMBER = details[1];
                        setSupportActionBar(toolbar2);
                        getSupportActionBar().setDisplayShowTitleEnabled(false);

                        if (!AppConstants.CONVERSATIONALIST_NAME.isEmpty() && !AppConstants.CONVERSATIONALIST_NAME.equals("-1")) {
                            toolbarTitleTextView2.setText(AppConstants.CONVERSATIONALIST_NAME);
                        }

                    } else if (actualMessage.contains("&-&-&")) {
                        String[] details = actualMessage.split("&-&-&", 0);

                        if (details[0].equals("request")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Conversationalist wants to start a video call. \n\nAre you accepting the request ?");
                            builder.setCancelable(true);
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String message = "response&-&-&" + "no";

                                    try {
                                        String encryptedData = aes.encrypt(message);
                                        //sending the encrypted data
                                        sendReceive.write(encryptedData);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    dialogInterface.cancel();
                                }
                            });

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String message = "response&-&-&" + "yes";

                                    try {
                                        String encryptedData = aes.encrypt(message);
                                        //sending the encrypted data
                                        sendReceive.write(encryptedData);

                                        JitsiMeetConferenceOptions jitsiMeetConferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                                .setRoom(details[1])
                                                .setWelcomePageEnabled(false)
                                                .setAudioMuted(false)
                                                .setVideoMuted(false)
                                                .setAudioOnly(false)
                                                .build();
                                        JitsiMeetActivity.launch(MainActivity.this, jitsiMeetConferenceOptions);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialogInterface.cancel();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else if (details[0].equals("response")) {
                            if (details[1].equals("yes")) {
                                JitsiMeetConferenceOptions jitsiMeetConferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                        .setRoom(Utils.getIPAddress(true))
                                        .setWelcomePageEnabled(false)
                                        .setAudioMuted(false)
                                        .setVideoMuted(false)
                                        .setAudioOnly(false)
                                        .build();
                                JitsiMeetActivity.launch(MainActivity.this, jitsiMeetConferenceOptions);
                            } else
                                Toast.makeText(MainActivity.this, "Video call request denied", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        addMessage(Color.parseColor("#FFFFFF"), tempMsg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPreferences = AppPreferences.getAppPreferences(this);
        aes = AES.getInstance();
        startServer();

        iniView();

    }

//    @SuppressLint("RestrictedApi")
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
//        if (menu instanceof MenuBuilder) {
//            MenuBuilder menuBuilder = (MenuBuilder) menu;
//            menuBuilder.setOptionalIconsVisible(true);
//        }
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.profile:
//                if (profileDialog != null)
//                    profileDialog.showDialog();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

    private void iniView() {

        ipLayout = (TextInputLayout) findViewById(R.id.iptf);
        nameLayout = (TextInputLayout) findViewById(R.id.nametf);
        pNumberLayout = (TextInputLayout) findViewById(R.id.numbertf);

        ipEditText = (TextInputEditText) findViewById(R.id.ipet);
        nameEditText = (TextInputEditText) findViewById(R.id.namet);
        pNumberEditText = (TextInputEditText) findViewById(R.id.numbert);
        scannerView = (ImageView) findViewById(R.id.scan_image);
        toolbar1 = (Toolbar) findViewById(R.id.toolbar_1);
        toolbarTitleTextView1 = (TextView) findViewById(R.id.toolbar_title_1);
        videoBtn = (ImageButton) findViewById(R.id.video_btn);

        toolbar2 = (Toolbar) findViewById(R.id.toolbar_2);
        toolbarTitleTextView2 = (TextView) findViewById(R.id.toolbar_title_2);

        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (appPreferences.containsKey(AppConstants.NAME))
            nameEditText.setText(appPreferences.getString(AppConstants.NAME));

        if (appPreferences.containsKey(AppConstants.PHONE_NUMBER))
            pNumberEditText.setText(appPreferences.getString(AppConstants.PHONE_NUMBER));

        connectBtn = (Button) findViewById(R.id.connectBtn);
        ipBtn = (Button) findViewById(R.id.getIpBtn);


        knowIPDialog = new KnowIPDialog(this);
//        profileDialog = new ProfileDialog(this, connectionEstablished);

        toolbar2.setOnClickListener(this);
        connectBtn.setOnClickListener(this);
        scannerView.setOnClickListener(this);
        ipBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);

        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (ImageButton) findViewById(R.id.send_message_btn);
        conversations = findViewById(R.id.conversations);
        conversationLayout = findViewById(R.id.scroll_view_linear_layout);

        firstLayout = (ConstraintLayout) findViewById(R.id.constraint_layout1);
        thirdLayout = (ConstraintLayout) findViewById(R.id.constraint_layout2);

        sendButton.setOnClickListener(this);
//        attachmentBtn.setOnClickListener(this);

        editFocus = false;

//        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                if(!hasFocus) {
////                    v.setEnabled(true);
////                    Toast.makeText(MainActivity.this,"requesting focus",Toast.LENGTH_SHORT).show();
//                } else {
////                    Toast.makeText(MainActivity.this,"Focused",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

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
            case R.id.toolbar_2:
                openConversationistDialog();
                break;
            case R.id.video_btn:
                openVideo();
                break;
            case R.id.send_message_btn:
                messageEditText.setFocusableInTouchMode(true);
                messageEditText.setFocusable(true);
                sendMessage();
                break;

        }
    }

    private void openVideo() {
        String message = "request&-&-&" + Utils.getIPAddress(true);

        try {
            String encryptedData = aes.encrypt(message);
            //sending the encrypted data
            sendReceive.write(encryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void openConversationistDialog() {
        ProfileDialog profileDialog = new ProfileDialog(this, 3);
        profileDialog.showDialog();
    }

    private void openScanner() {
        Intent intent = new Intent(MainActivity.this, QRScanning.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {
            String ipAddress = data.getStringExtra("ipAddress");
            if (ipAddress != null) {
                ipEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ipEditText.setText(ipAddress);
            }

        }
    }

    private void connectMethod() {
        String ip, name, number;
        int f = 0;
        ip = ipLayout.getEditText().getText().toString().trim();
        name = nameLayout.getEditText().getText().toString().trim();
        number = pNumberLayout.getEditText().getText().toString().trim();

        ipEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        nameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (ip.isEmpty()) {
            ipEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_black_24dp, 0, 0, 0);
            ipLayout.setError("Please enter the target ip address");
            ipLayout.setErrorEnabled(true);
            f++;
        } else {
            ipLayout.setError(null);
            ipLayout.setErrorEnabled(false);
        }

        if (f == 0) {
            appPreferences.insertString(AppConstants.NAME, name);
            appPreferences.insertString(AppConstants.PHONE_NUMBER, number);
            //connect
            connect(ip);
        }


    }

    private void startServer() {

        try {
            serverClass = new ServerClass(port);
            serverClass.start();
            Toast.makeText(this, "Server has been started..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Server starting failure..", Toast.LENGTH_SHORT).show();
        }
    }

    private void connect(String targetIpAddress) {
        try {
            clientClass = new ClientClass(targetIpAddress, port);
            clientClass.start();
        } catch (Exception e) {
            Toast.makeText(this, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {

        String msg = messageEditText.getText().toString().trim();
        String msgTime = Utils.getTime(false);
        String msgWithTime = msg + "@%@" + msgTime;

        if (TextUtils.isEmpty(msg)) {
            messageEditText.requestFocus();
            messageEditText.setError("Enter the message");
        } else {
            try {
                String encryptedData = aes.encrypt(msgWithTime);

                //sending the encrypted data
                sendReceive.write(encryptedData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Message UI handling
    private void addMessage(int color, String message) {

        runOnUiThread(() -> {
                    TextView textView = new TextView(this);
                    TextView msgTime = new TextView(this);


                    // if it's a sender message
                    try {
                        if (color == Color.parseColor("#FCE4EC")
                                && !(aes.decrypt(message).contains("bg@%@bg"))
                                && !(aes.decrypt(message).contains("diconnect@%@d"))
                                && !(aes.decrypt(message).contains("file@%@"))
                                && !(aes.decrypt(message).contains("remove@%@"))) {
                            textView.setPadding(200, 20, 20, 20);
                            //textView.setMaxLines(5);
                            textView.setGravity(Gravity.RIGHT);
                            textView.setBackgroundResource(R.drawable.sender_messages_layout);
                            textView.setTextIsSelectable(true);

                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            // lp1.setMargins(10, 10, 10, 10);
                            // lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            lp1.leftMargin = 200;
                            //lp1.rightMargin = 50;
                            textView.setLayoutParams(lp1);

                            msgTime.setPadding(0, 0, 0, 0);

                            msgTime.setTextSize(14);
                            msgTime.setTextColor(Color.parseColor("#FCE4EC"));
                            msgTime.setTextColor(getResources().getColor(R.color.black));
                            msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            msgTime.setGravity(Gravity.LEFT);

                            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp4.leftMargin = 200;
                            msgTime.setLayoutParams(lp4);
                        }
                        // else if receiver message
                        else if (!(aes.decrypt(message).contains("bg@%@bg"))
                                && !(aes.decrypt(message).contains("diconnect@%@d"))
                                && !(aes.decrypt(message).contains("file@%@"))
                                && !(aes.decrypt(message).contains("remove@%@"))) {
                            textView.setPadding(20, 20, 200, 20);
                            //textView.setMaxLines(5);
                            textView.setGravity(Gravity.LEFT);
                            textView.setBackgroundResource(R.drawable.receiver_messages_layout);
                            textView.setTextIsSelectable(true);

                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            //lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            //lp1.leftMargin = 150;
                            lp2.rightMargin = 200;
                            textView.setLayoutParams(lp2);

                            msgTime.setTextSize(14);
                            msgTime.setTextColor(Color.parseColor("#FFFFFF"));
                            msgTime.setTextColor(getResources().getColor(R.color.black));
                            msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            //lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            //lp1.leftMargin = 150;
                            lp3.rightMargin = 200;
                            msgTime.setGravity(Gravity.RIGHT);
                            msgTime.setLayoutParams(lp3);


                        }

                        textView.setTextColor(color);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        String actualMessage = aes.decrypt(message);

                        String[] messages = actualMessage.split("@%@", 0);

                        textView.setTextSize(20);
                        textView.setText(messages[0]); // setting message on the message textview
                        String msg = "(" + Utils.getTime(false) + ")";
                        msgTime.setText(msg); // setting messing time


                        // creating divider between two messages
                        addDividerBetweenTwoMessages();

                        // adding 2 more views in linear layout every time
                        conversationLayout.addView(textView);
                        conversationLayout.addView(msgTime);
                        conversations.post(() -> conversations.fullScroll(View.FOCUS_DOWN));// for getting last message in first

                        // else it's a normal message


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

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

    public void enableComponent() {
        runOnUiThread(() -> {
            firstLayout.setVisibility(View.GONE);
            thirdLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        appPreferences.detach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appPreferences = AppPreferences.getAppPreferences(this);
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
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
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
                connectionEstablished=4;

                String name = appPreferences.getString(AppConstants.NAME);
                String phoneNumber = appPreferences.getString(AppConstants.PHONE_NUMBER);
                String message = null;

                if (name != null)
                    message = name + "&&";
                else message = "-1" + "&&";

                if (phoneNumber != null)
                    message = message + phoneNumber;
                else message = message + "-1";

                try {
                    String encryptedData = aes.encrypt(message);
                    //sending the encrypted data
                    sendReceive.write(encryptedData);

                } catch (Exception e) {
                    runOnUiThread(()->
                            Toast.makeText(MainActivity.this,"User left",Toast.LENGTH_SHORT).show()
                    );
                    e.printStackTrace();
                }


                // enabling invisible components
                enableComponent();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(()->
                        Toast.makeText(MainActivity.this,"Cannot connect at the moment",Toast.LENGTH_SHORT).show()
                );
            } catch (Exception e) {
                runOnUiThread(()->
                        Toast.makeText(MainActivity.this,"Cannot connect at the moment",Toast.LENGTH_SHORT).show()
                );
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
                    addMessage(Color.parseColor("#FCE4EC"), msg);
//                    runOnUiThread(() ->
//                            messageEditText.setText("")
//
//                    );
                    runOnUiThread(new Runnable() {
//                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void run() {
                            messageEditText.setText("");
                            editFocus = false;
//                            messageEditText.resetPivot();
//                            messageEditText.restoreDefaultFocus();
////                            messageEditText.setSelection(0);
////                            messageEditText.setCursorVisible(true);
//                            messageEditText.requestFocus();

                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(()->
                        Toast.makeText(MainActivity.this,"User left",Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    runOnUiThread(()->
                            Toast.makeText(MainActivity.this,"User left",Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ProfileDialog profileDialog = new ProfileDialog(this, 3);
                profileDialog.makePhoneCall();
            } else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to exit ?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
