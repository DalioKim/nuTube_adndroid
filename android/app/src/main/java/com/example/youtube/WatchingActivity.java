package com.example.youtube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;


public class WatchingActivity extends AppCompatActivity {



    private static String TAG = "WatchingActivity";

    //채팅기능 관련 변수들
    //채팅화면 관련 변수
    ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();

    Button button, fileBtn;

    //좋아요에 올라간 댓글을 보여주는 텍스트뷰
    TextView specialChat;

    private RecyclerView recyclerView;
    private ChatDataAdapter chatDataAdapter;
    BufferedReader bufferR;
    EditText insertView;
    BufferedWriter bufferW;
    String sendMsg;
    String msg;

    //인기댓글 알려줄 음성출력
    TextToSpeech tts;

    //유저정보
    String id, thumbnail = null, content;

    //방정보
    String room;
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Handler mHandler = null;


    boolean isConnected = true;


    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching);

        //키보드 UI문제
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);



        //권한 체크

        //checkFunction();

        //음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(WatchingActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        tts.setPitch(1.5f); //1.5톤 올려서
        tts.setSpeechRate(1.0f); //1배속으로 읽기

        //메시지 입력이 종료되면 키보드 패드를 사라지게 하기 위한 객체
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //방 이름 임의로 설정
        room = "1";
        double ran = Math.random() * 100;
        int num = (int) ran;


        //로그인한 아이디 정보를 받아온다.
        final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE);
        id = Shared.getString("id", null);
        Log.i(TAG,"불러온 아이디:"+id);

        //채팅내용을 보여줄 리사이클러뷰 위젯을 연결
        recyclerView = findViewById(R.id.Rc);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        chatDataAdapter = new ChatDataAdapter(WatchingActivity.this, chatItems,id);
        recyclerView.setAdapter(chatDataAdapter);
        specialChat = findViewById(R.id.specialChat);


        //키보드와 editText 연결
        insertView = (EditText) findViewById(R.id.insertView);


        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        button = findViewById(R.id.button);

        String httpLiveUrl = "rtsp://192.168.0.103:1935/live/myStream";


        videoView.setVideoURI(Uri.parse(httpLiveUrl));

        videoView.setMediaController(new MediaController(this));

        videoView.requestFocus();

        videoView.start();


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


                                 if(sendMsg.substring(0,6).equals("like//")){
                                //if (sendMsg.equals("like//")) {


                                    Log.i("좋아요 변화", sendMsg);
                                    String size = sendMsg.substring(6, sendMsg.lastIndexOf("."));
                                    String idx = sendMsg.substring(sendMsg.lastIndexOf(".") + 1);

                                    Log.i("좋아요", "사이:" + size);
                                    Log.i("좋아요", "인덱스:" + idx);
                                    //전체 채팅내용 크기숫자를 저장한다.
                                    int i = Integer.parseInt(size) - chatItems.size();

                                    //인덱스에 전체 채팅내용크기차이만큼 더해준다.
                                    int index = Integer.parseInt(idx) + i;

                                    //해당 인덱스의 채팅을 좋아요 채팅공간에 추가해준다.
                                    //specialChat.setVsibility(View.VISIBLE);

                                    String id = chatItems.get(index).getId();
                                    String content = chatItems.get(index).getContent();

                                    specialChat.setText("Best채팅  "+id + ": " + content);
                                     specialChat.setTextColor(Color.RED);
                                    //specialChat.setTextColor(Integer.parseInt("#E91E1E"));

                                     //새 인기댓글 알림

                                    // tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);

                                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                         ttsGreater21(content);
                                     } else {
                                         ttsUnder20(content);
                                     }




                                 }else if(sendMsg.substring(0,2).equals("@@")){

                                     Log.i("전달받은 전체 채팅메시", sendMsg);
                                     String[] result = sendMsg.split("@@");
                                     Log.i("전체채팅길이:", String.valueOf(result.length));
                                     //Log.i("split 후", String.valueOf(result[0]));
                                     //Log.i("split 후", String.valueOf(result[1]));

                                  for(int i =0; i < result.length; i++){
                                      Log.i("split  후"+i, String.valueOf(result[i]));
                                      if(i!=0) {
                                          String chat = String.valueOf(result[i]);
                                          String id = chat.substring(0, chat.lastIndexOf(":"));
                                          String Msg = chat.substring(chat.lastIndexOf(":") + 7);

                                          chatItems.add(new ChatItem(id, Msg));
                                      }

                                     }
                                     chatDataAdapter.notifyDataSetChanged();
                                     recyclerView.scrollToPosition(chatDataAdapter.getItemCount()-1);

/*
                                  for (String wo : result){
                                      Log.i("split  후", wo);

                                      String ne = wo.substring(0, wo.lastIndexOf(":"));
                                      String ne2 = wo.substring(sendMsg.lastIndexOf(":") + 1);
                                      ne2 = ne2.substring(6);


                                      chatItems.add(new ChatItem(ne, ne2));

                                  }

                                     chatDataAdapter.notifyDataSetChanged();*/



                                 } else if(sendMsg.substring(0,6).equals("chat//")){

                                    //서버로부터 전달받은 메시지에서 상대방의 아이디와 상대방이 입력한 메시지를 구분한다.
                                    String sendId = sendMsg.substring(0, sendMsg.lastIndexOf(":"));
                                    sendMsg = sendMsg.substring(sendMsg.lastIndexOf(":") + 1);
                                    sendMsg = sendMsg.substring(6);
                                     Log.i("구분자 제거 후 채팅메시지", sendMsg);


                                    // 1. 위 코드를 2줄로 줄였다.
                                    /*
                                    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
                                    String time;
                                    time = sdfNow.format(new Date(System.currentTimeMillis()));
*/
                                    //chatItems.add(new ChatItem(id, sendMsg, time, 0, false));


                                    chatItems.add(new ChatItem(sendId, sendMsg));

                                /*    chatDataAdapter = new ChatDataAdapter(WatchingActivity.this, chatItems);
                                    recyclerView.setAdapter(chatDataAdapter);*/
                                    chatDataAdapter.notifyDataSetChanged();
                                     recyclerView.scrollToPosition(chatDataAdapter.getItemCount()-1);


                                }

                           /* else if(sendMsg.substring(0,6).equals("best//")) {
                                     Log.i("전달받은 best채팅", sendMsg);
                                     String bestId = sendMsg.substring(6);
                                     bestId = bestId.substring(0, sendMsg.lastIndexOf(":"));
                                     Log.i("구분자 제거 후 ID", bestId);

                                     sendMsg = sendMsg.substring(sendMsg.lastIndexOf(":") + 1);
                                     //sendMsg = sendMsg.substring(6);
                                     Log.i("구분자 제거 후 채팅메시지", sendMsg);
                                     specialChat.setText(bestId + "  " + sendMsg);
                                 }*/

                            }
                        });
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }//while
            }//run method...
        }).start();//Thread 실행..


        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {


                msg = insertView.getText().toString();

                String cls = "chat//";

                SendMessage(cls);

                  /*  bufferW.write(msg + "\n");

                    bufferW.flush();*/

                //thread.sendMsg();

//                chatItems.add(new ChatItem(id, thumbnail, msg));

                // 1. 위 코드를 2줄로 줄였다.
                SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
                String time;
                time = sdfNow.format(new Date(System.currentTimeMillis()));

                chatItems.add(new ChatItem(id, msg, time, 0, false));

            /*    chatDataAdapter = new ChatDataAdapter(WatchingActivity.this, chatItems);
                recyclerView.setAdapter(chatDataAdapter);*/
                chatDataAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatDataAdapter.getItemCount()-1);


                Log.i("ClientThread", "서버로 보냄.");
                insertView.setText("");
                imm.hideSoftInputFromWindow(insertView.getWindowToken(), 0);


            /*    chatDataAdapter.setOnItemClickListener(new ChatDataAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos) {
                        Log.i("아이템 클릭이벤", "아이템 클릭 이벤트");

                        //likeMessage();


                      position = pos;
                        String cls = "like//";

                        SendMessage(cls);*//*


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


                });*/


            }
        });


    }


    public void SendMessage(String cls) {

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


                    if (cls.equals("chat//")) {
                        Log.i("채팅", "채팅메소드 ");
                        Log.i("채팅", "채팅전송내용 "+msg);

                        bufferW.write("chat//" + msg + "\n");
                        bufferW.flush();
                    } else {

                        Log.i("채팅", "like메소드 ");

                        int size = chatItems.size();

                        //bufferW.write("like//"+size+"."+position);
                        bufferW.write("like//" + size + "." + position);


                        mHandler = new Handler(Looper.getMainLooper());

                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // UI 작업 수행 X
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // UI 작업 수행 O

                                        try {
                                            bufferW.flush();
                                            Log.i("채팅", "라이크 전송완료");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        });
                        t.start();


                    }


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }//run method..

        }).start(); //Thread 실행..
    }


    public void likeMessage() {

        if (bufferW == null) return;   //서버와 연결되어 있지 않다면 전송불가..

        //네트워크 작업이므로 Thread 생성
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //서버로 보낼 메세지 EditText로 부터 얻어오기
                String msg = "like";
                Log.i("좋아요 전송", "좋아요 전송");

                try {


                    // int size = chatItems.size();

                    //bufferW.write("like//"+size+"."+pos);
                    bufferW.write("like");

                    bufferW.flush();


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }//run method..

        }).start(); //Thread 실행..
    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        Log.i("음성출력","음성출력");
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public void checkFunction() {
        int permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissioninfo == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SDCard 쓰기 권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "권한 설명", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }
}
