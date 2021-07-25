package com.example.youtube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;


import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus;
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatusCallback;
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus.BroadcastState;
import com.wowza.gocoder.sdk.support.status.WOWZStatus;
import com.wowza.gocoder.sdk.support.status.WOWZStatusCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;


public class TestActivity extends AppCompatActivity implements WOWZBroadcastStatusCallback, WOWZStatusCallback {


    private static String TAG = "TestActivity";


    //채팅기능에 사용되는 변수 정의
    //채팅화면 관련 변수
    ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();

    private RecyclerView recyclerView;
    private ChatDataAdapter chatDataAdapter;

    //인기댓글 알려줄 음성출력
    TextToSpeech tts;


    String sendMsg;
    String msg;

    //유저정보
    String id, thumbnail = null, content;

    //방정보
    String room;


    String nickName;
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    BufferedReader bufferR;
    EditText insertView;
    BufferedWriter bufferW;

    TextView specialChat;

    EditText chatInput;

    Button chat_btn;


    boolean isConnected = true;

    Handler mHandler = null;


    // The top-level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK camera view
    private WOWZCameraView goCoderCameraView;

    // The GoCoder SDK audio device
    private WOWZAudioDevice goCoderAudioDevice;

    // The GoCoder SDK broadcaster
    private WOWZBroadcast goCoderBroadcaster;

    // The broadcast configuration settings
    private WOWZBroadcastConfig goCoderBroadcastConfig;

    // Properties needed for Android 6+ permissions handling
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    private boolean mPermissionsGranted = true;
    private String[] mRequiredPermissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mHandler = new Handler();


        //title에 저장된 값을  전달받아 채팅방과 방송에 사용한다.

        Intent intent = getIntent(); /*데이터 수신*/

        room = intent.getExtras().getString("title");



        //저장돤 ID를 들고오낟.
        final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE); //SharedPreferences를 선언
        id = Shared.getString("id", null);
        Log.i(TAG, "shared에 저장된 데이터 확인 : "+ Shared.getString("id", null));

        //키보드와 editText 연결
        chatInput = (EditText) findViewById(R.id.chatInput);

        chat_btn = findViewById(R.id.chat_btn);


//음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(TestActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });


    //권한 체크
//        checkFunction();


// Initialize the GoCoder SDK
        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-A147-010C-F2CB-2A0E-EA7C");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WOWZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(this,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Associate the WOWZCameraView defined in the U/I layout with the corresponding class member
        goCoderCameraView = (WOWZCameraView) findViewById(R.id.camera_preview);

        // Create an audio device instance for capturing and broadcasting audio
        goCoderAudioDevice = new WOWZAudioDevice();

        // Create a broadcaster instance
        goCoderBroadcaster = new WOWZBroadcast();

        // Create a configuration instance for the broadcaster
        goCoderBroadcastConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1920x1080);
        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Streaming Cloud live stream
        goCoderBroadcastConfig.setHostAddress("192.168.0.12");

        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("live");
        //goCoderBroadcastConfig.setStreamName("myStream");
        goCoderBroadcastConfig.setStreamName(room);

        // Designate the camera preview as the video broadcaster
        goCoderBroadcastConfig.setVideoBroadcaster(goCoderCameraView);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);

        // Associate the onClick() method as the callback for the broadcast button's click event
        /*Button broadcastButton = (Button) findViewById(R.id.broadcast_button);
        broadcastButton.setOnClickListener(this);*/

        if (!mPermissionsGranted) return;

        // Ensure the minimum set of configuration settings have been specified necessary to
        // initiate a broadcast streaming session
        WOWZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (goCoderBroadcaster.getStatus().isBroadcasting()) {
            // Stop the broadcast that is currently broadcasting
            goCoderBroadcaster.endBroadcast(TestActivity.this);
        } else {
            // Start streaming
            goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
        }



        //채팅내용을 보여줄 리사이클러뷰 위젯을 연결
        recyclerView = findViewById(R.id.Rc);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        specialChat = findViewById(R.id.specialChat);

        chatDataAdapter = new ChatDataAdapter(TestActivity.this, chatItems,id);
        recyclerView.setAdapter(chatDataAdapter);

        chatDataAdapter.id = id;


        chatDataAdapter.setOnItemClickListener(new ChatDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Log.i("아이템 클릭이벤", "아이템 클릭 이벤트");



                Thread s = new Thread(new Runnable(){ @Override public void run() {


                    try {

                        //Log.i("사이즈확인", String.valueOf(chatItems.size()));
                        //Log.i("인덱확인", String.valueOf(pos));

                        bufferW.write("like//" + chatItems.size()+"." +pos +"\n");
                        bufferW.flush();



                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } }); s.start();




            }


        });


        //채팅설정
        //todo 채팅소켓
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {

                    String host = "15.164.98.15";
                    int port = 5000;
                    //서버와 연결하는 소켓 생성..
                    socket = new Socket(host, port);
                    bufferR = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    bufferW = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

//id와 방번호를 보낸다.

                    bufferW.write(room + "," + id + "\n");
                    // bufferW.write(id + "\n");
                    bufferW.flush();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //서버와 접속이 끊길 때까지 무한반복하면서 서버의 메세지 수신
                while (isConnected) {
                    try {

                        sendMsg = bufferR.readLine();

                        //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드이다
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub


                                if (sendMsg.substring(0, 6).equals("total/")) {
                                    sendMsg = sendMsg.substring(6);
                                    Log.i(TAG,"전달받은 전체 채팅메시지:"+sendMsg);
                                    String[] result = sendMsg.split("-");
                                    Log.i(TAG,"전체채팅길이:"+String.valueOf(result.length));
                                    //Log.i("split 후", String.valueOf(result[0]));
                                    //Log.i("split 후", String.valueOf(result[1]));

                                    for (int i = 0; i < result.length; i++) {
                                        Log.i(TAG,"split  후"+String.valueOf(result[i]));
                                        //if (i != 0) {
                                        String chat = String.valueOf(result[i]);
                                        Log.i(i+"번", chat);
                                        //chat = chat.substring(1);
                                        //Log.i("잘라낸 "+i+"번", chat);

                                        String id = chat.substring(0,chat.lastIndexOf(":"));
                                        int length = id.length();
                                        Log.i("아이디",id);
                                        String Msg = chat.substring(length+1);

                                        chatItems.add(new ChatItem(id, Msg));
                                        // }

                                    }
                                    chatDataAdapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(chatDataAdapter.getItemCount() - 1);


                                } else if (sendMsg.substring(0, 6).equals("chat//")) {

                                    //서버로부터 전달받은 메시지에서 상대방의 아이디와 상대방이 입력한 메시지를 구분한다.
                                    String sendId = sendMsg.substring(6, sendMsg.lastIndexOf(":"));

                                    //자신이 보낸 메시지가 아닐떄만 반영한다.
                                    if(!sendId.equals("id")) {

                                        Log.i("남이 보낸 메시지", sendMsg);


                                        sendMsg = sendMsg.substring(sendMsg.lastIndexOf(":") + 1);
                                        //sendMsg = sendMsg.substring(6);
                                        Log.i("구분자 제거 후 채팅메시지", sendMsg);


                                        chatItems.add(new ChatItem(sendId, sendMsg));


                                        chatDataAdapter.notifyDataSetChanged();
                                        recyclerView.scrollToPosition(chatDataAdapter.getItemCount() - 1);
                                    }


                                }


                            }
                        });
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }//while
            }//run method...
        }).start();//Thread 실행..




        // TODO: 2020-03-28 채팅입력 이벤트
        chat_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {


                msg = chatInput.getText().toString();

                if(!msg.equals("")) {
                    SendMessage();
                }


                chatItems.add(new ChatItem(id, msg));


                chatDataAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatDataAdapter.getItemCount() - 1);


                Log.i("ClientThread", "서버로 보냄.");
                chatInput.setText("");

                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                mInputMethodManager.hideSoftInputFromWindow(chatInput.getWindowToken(), 0);


            }
        });


    }


    //
    // Called when an activity is brought to the foreground
    //
    @Override
    protected void onResume() {
        super.onResume();

        // If running on Android 6 (Marshmallow) or above, check to see if the necessary permissions
        // have been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsGranted = hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted)
                ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        } else
            mPermissionsGranted = true;

        // Start the camera preview display
        if (mPermissionsGranted && goCoderCameraView != null) {
            if (goCoderCameraView.isPreviewPaused())
                goCoderCameraView.onResume();
            else
                goCoderCameraView.startPreview();
        }

    }

    //
    // Callback invoked in response to a call to ActivityCompat.requestPermissions() to interpret
    // the results of the permissions request
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // Check the result of each permission granted
                for(int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
    }

    //
    // Utility method to check the status of a permissions request for an array of permission identifiers
    //
    private static boolean hasPermissions(Context context, String[] permissions) {
        for(String permission : permissions)
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    //
    // The callback invoked when the broadcast button is tapped
    //
    /*@Override
    public void onClick(View view) {
        // return if the user hasn't granted the app the necessary permissions
        if (!mPermissionsGranted) return;

        // Ensure the minimum set of configuration settings have been specified necessary to
        // initiate a broadcast streaming session
        WOWZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (goCoderBroadcaster.getStatus().isBroadcasting()) {
            // Stop the broadcast that is currently broadcasting
            goCoderBroadcaster.endBroadcast(TestActivity.this);
        } else {
            // Start streaming
            goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
        }
    }*/

    //
    // The callback invoked upon changes to the state of the steaming broadcast
    //
    @Override
    public void onWZStatus(final WOWZBroadcastStatus goCoderStatus) {
        // A successful status transition has been reported by the GoCoder SDK
     /*   final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (goCoderStatus.getState()) {

            case BroadcastState.READY:
                statusMessage.append("Ready to begin broadcasting");
                break;

            case BroadcastState.BROADCASTING:
                statusMessage.append("Broadcast is active");
                break;

            case BroadcastState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });*/
    }

    @Override
    public void onWZError(final WOWZBroadcastStatus wowzBroadcastStatus) {

        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestActivity.this,
                        "Streaming error: " + wowzBroadcastStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    //
    // The callback invoked when an error occurs during a broadcast
    //
   /* @Override
    public void onWOWZError(final WOWZBroadcastStatus goCoderStatus) {

    }*/

    //
    // Enable Android's immersive, sticky full-screen mode
    //
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWZStatus(WOWZStatus wowzStatus) {

    }

    @Override
    public void onWZError(WOWZStatus wowzStatus) {

    }


    //채팅관련 메소드 선언

    //채팅보내기 메소드
    public void SendMessage() {

        if (bufferW == null) return;   //서버와 연결되어 있지 않다면 전송불가..

        //네트워크 작업이므로 Thread 생성
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //서버로 보낼 메세지 EditText로 부터 얻어오기
                // String msg = insertView.getText().toString();
                try {
                   /* runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg= insertView.getText().toString();
                            // TODO Auto-generated method stub
                            text_msg.setText("[SEND]" +msg);
                        }
                    });*/


                    Log.i("채팅", "채팅메소드 ");
                    Log.i("채팅", "채팅전송내용 " + msg);

                    bufferW.write("chat//" + msg + "\n");
                    bufferW.flush();



                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }//run method..

        }).start(); //Thread 실행..
    }


    public void checkFunction(){
        int permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"SDCard 쓰기 권한 있음",Toast.LENGTH_SHORT).show();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "권한 설명",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
        }
    }

    //뒤로가기 버튼 시 소켓연결 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        isConnected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        isConnected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}