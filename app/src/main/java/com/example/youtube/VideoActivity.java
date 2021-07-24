package com.example.youtube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;
import android.media.MediaPlayer.OnPreparedListener;

import com.bumptech.glide.Glide;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AlertDialog;

import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.PowerManager;


public class VideoActivity extends AppCompatActivity {

    private ArrayList<VideoItem> data;
    private ArrayList<CommentItem> commentItems = new ArrayList<CommentItem>();

    VideoItem videoItem;

    ImageView notifyBtn;
    VideoView videoView;
    TextView videoInfo, alarmView;
    EditText input;
    TextView textView;
    String videoIdx;
    Uri video;
    Uri uri;
    Button languageBtn, comment_btn;

    MediaSource mediaSource;

    //영상의 최초공개여부를 확인하고, 영상의 최초공개 시간을 저장하는 변수들
    String theFirst, startTime;
    Handler mHandler = null;
    //대기시간을 알려주고, 시간에 도달할떄까지 while문을 돌릴 변수
    boolean isRun = false;

    TextView timerView;
    ConstraintLayout waitingView;
    int value = 0;

    //방송예정시간과 현재시간의 차이를 저장할 변수
    long sec;
    //대기시간으로 변환한 데이터를 저장할 변수
    String second, minute, hour;


    private static String TAG = "VideoActivity";

    private RecyclerView recyclerView;
    private CommentDataAdapter adapter;


    //엑소플레이어 관련 변수
    private PlayerView exoPlayerView;
    private SimpleExoPlayer player;
    private ImaAdsLoader adsLoader;
    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();


    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;


    //댓글언어선택한 내용을 담을 변수
    String SeletedLanguage = "";

    //채팅관련 변수
    ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
    private ChatDataAdapter chatDataAdapter;
    BufferedReader bufferR;
    EditText chatInput;
    BufferedWriter bufferW;
    String sendMsg;
    String msg;
    Button chat_btn;
    private RecyclerView chatRc;
    //유저정보
    String id, thumbnail = null, content;
    //방정보
    String room;
    Socket socket;
    Handler chatHandler = null;
    boolean isConnected = false;
    TextView chatTx;

    //영상알람설정시 FCM 서버에 보낼 기기의 토큰데이터를 저장할 변수
    String token;
    String apiKey = "AAAASS7TEQY:APA91bH-qwaudcEVcER7331nRkTb4RzAnhswyEB2D1xuFK5oz4kuu519_JuXH7oQL9REK_4pxqNKAATaj9bdWSPO5mNhI71LsAYLZh0doLKx7-tLfnEwclcLJLzEvEUpr1d853DIaOFc";
    String title;


    //백그라운드로 미디어 재상하기 위해 서비스로 보내는 인텐트 변수
    private Intent serviceIntent;


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            Log.i(TAG,"onStart");


            initializePlayer();
        }
    }


    //메인화면 혹은 검은화면에서 돓아오면 onResume을 거친다.
    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {

            Log.i(TAG,"onResume");


            //앱으로 다시 복귀하면 서비스를 종료시킨다.

            /*if (serviceIntent!=null) {
                Log.i(TAG,"백그라운드 서비스종료");

                stopService(serviceIntent);
                serviceIntent = null;
            }*/
            initializePlayer();
            if (sec == 0 || sec < 0) {
                isRun = false;

                recyclerView.setVisibility(View.VISIBLE);
                waitingView.setVisibility(View.INVISIBLE);

                if(player!=null)
                    player.prepare(mediaSource, false, false);

            }



        }
    }


    //핸드폰 메인화면으로 나가거나, 핸드폰화면이 검해지면 onPause 상태에 진입하고
    //이떄 백그라운드로 재생을 시킨다.
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            Log.i(TAG,"onPause");
            releasePlayer();

            //핸드폰에서 배터리 절약으로 서비스를 종료시키는 것을 방지하기 위한 인텐트
           /* Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);*/
/*
            if (BackgroundService.serviceIntent==null) {

                //백그라운드 서비스를 실행시킨다.
                Log.i(TAG,"백그라운드 서비스실행");

                serviceIntent = new Intent(this, BackgroundService.class);
                startService(serviceIntent);

            }*/
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            Log.i(TAG,"onStop");
            releasePlayer();
        }
    }

    private void initializePlayer() {

        Log.i(TAG,"initializePlayer");

        if (player == null) {

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd());
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        }

        exoPlayerView.setPlayer(player);
        mediaSource = buildMediaSource(video);

        exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        //최초공개 여부에 따라 달라진다.

        if (theFirst.equals("false")) {

            player.prepare(mediaSource, false, false);
        } else {


            //Toast.makeText(getApplicationContext(),  "최초공개까지 "+startTime, Toast.LENGTH_SHORT).show();

            //최초영상 공개 일시 댓글창이 아닌 채팅창을 키며,
            //대기시간까지 스레드를 통해서 남은시간을 알려주고 시간에 도달하면, 영상을 공개한다.


        }

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;

        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua");
        return new ExtractorMediaSource.Factory(manifestDataSourceFactory).createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        exoPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mHandler = new Handler();


        //바로 영상보여줄 시 뷰
        //videoView = findViewById(R.id.videoView);
        exoPlayerView = findViewById(R.id.videoView);
        //videoInfo = findViewById(R.id.videoInfo);
        input = findViewById(R.id.input);
        recyclerView = findViewById(R.id.rcView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentDataAdapter(VideoActivity.this, commentItems);
        recyclerView.setAdapter(adapter);
        languageBtn = (Button) findViewById(R.id.languageBtn);
        comment_btn = (Button) findViewById(R.id.comment_btn);


        //todo 최초공개시 뷰
        waitingView = findViewById(R.id.waitingView);
        timerView = findViewById(R.id.timerView);
        notifyBtn = findViewById(R.id.notifyBtn);
        alarmView = findViewById(R.id.alarmView);
        chatTx = findViewById(R.id.chatTx);


        //인텐트를 통해서 메인에서 클릭해서 온건지, 알람설정을 통해 들어온건지 구분해준다.

        String route = getIntent().getStringExtra("route");


        //알람설정으로 켜진 경우 shared에 비디오아이템에 대한 정보가 있기때문에 체크하고 intent로 데이터를 받아오지않는다.
        final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = Shared.edit();
        String share = Shared.getString("videoItem", null);

        //알람설정으로 액티비티에 들어온 경우
        //if(share !=null){
        if(route.equals("push")){
            Log.i(TAG,"알람설정으로 진입");
            Gson gson = new Gson();
            String json = Shared.getString("videoItem", null);

            videoItem  = gson.fromJson(json, VideoItem.class);

            videoIdx = videoItem.getIdx();
            Log.i(TAG, "비디오 idx " + videoIdx);
            video = Uri.parse(videoItem.getVideoName());
            theFirst = videoItem.getTheFirst();
            startTime = videoItem.getStartTime();

            room = videoItem.getTitle();

            //editor.remove("videoItem"); // will delete key key_name4

// Save the changes in SharedPreferences
            //editor.commit(); // commit changes

        }else {
            Log.i(TAG,"메인에서 진입");

            videoItem = (VideoItem) getIntent().getParcelableExtra("data");
            //인텐트로 영상에 대한 정보 받아오기
            videoIdx = videoItem.getIdx();
            Log.i(TAG, "비디오 idx " + videoIdx);
            video = Uri.parse(videoItem.getVideoName());
            theFirst = videoItem.getTheFirst();
            startTime = videoItem.getStartTime();

            room = videoItem.getTitle();


        }
        /*
        data = getIntent().getParcelableArrayListExtra("data");


        //인텐트로 영상에 대한 정보 받아오기
        videoIdx = data.get(0).getIdx();
        Log.i(TAG, "비디오 idx " + videoIdx);
        *//*String ex = "![CDATA["+data.get(0).getVideoName()+"]]";
        uri  = Uri.parse(ex);*//*
        video = Uri.parse(data.get(0).getVideoName());
        theFirst = data.get(0).getTheFirst();
        startTime = data.get(0).getStartTime();

        room = data.get(0).getTitle();*/





        //채팅내용을 보여줄 리사이클러뷰 위젯을 연결
        chatRc = findViewById(R.id.chatRc);
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());

        chatRc.setLayoutManager(cLayoutManager);
        chatRc.setItemAnimator(new DefaultItemAnimator());

        //로그인한 아이디 정보를 받아온다.
        final SharedPreferences mShared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE);
        id = mShared.getString("id", null);
        Log.i(TAG, "불러온 아이디:" + id);


        chatDataAdapter = new ChatDataAdapter(VideoActivity.this, chatItems, id);
        chatRc.setAdapter(chatDataAdapter);

        //키보드와 editText 연결
        chatInput = (EditText) findViewById(R.id.chatInput);

        chat_btn = findViewById(R.id.chat_btn);


        if (theFirst.equals("true")) {

            isConnected = true;

            //일반 VOd의 뷰들을 INVISble 처리해준다.
            recyclerView.setVisibility(View.INVISIBLE);
            languageBtn.setVisibility(View.INVISIBLE);
            comment_btn.setVisibility(View.INVISIBLE);
            input.setVisibility(View.INVISIBLE);
            chatRc.setVisibility(View.VISIBLE);
            chatInput.setVisibility(View.VISIBLE);
            chat_btn.setVisibility(View.VISIBLE);
            chatTx.setVisibility(View.VISIBLE);
            alarmView.setVisibility(View.VISIBLE);
            notifyBtn.setVisibility(View.VISIBLE);

           InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            mInputMethodManager.hideSoftInputFromWindow(chatInput.getWindowToken(), 0);
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);



            //최초공개 영상에 관한 정보(FCM서버에 등록할 토큰 값, 영상 정보를 레트로핏으로 서버에 보낸다.
            //토큰 얻고 저장
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token

                            token = task.getResult().getToken();

                            // Log and toast
                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.d(TAG, msg);
                            //Toast.makeText(VideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });


            // TODO: 2020-03-27 예약시간 보여주
            waitingView.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "최초공개 영상시간 " + startTime, Toast.LENGTH_SHORT).show();


            SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
            String nowtime = sdfNow.format(new Date(System.currentTimeMillis()));
            Log.i(TAG, "현재시간: " + nowtime);

            startTime = startTime + ":00";
            Log.i(TAG, "예약시간: " + startTime);


            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdfNow.parse(nowtime);
                d2 = sdfNow.parse(startTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            sec = d2.getTime() - d1.getTime();
            Log.i(TAG, "방송까지 남은밀리미초: " + sec);


            isRun = true;

            Thread t = new Thread(new Runnable() {


                @Override
                public void run() {
                    // UI 작업 수행 X

                    while ((isRun)) {

                        //시간을 잰다.
                        try {
                            Thread.sleep(1000);
                            sec -= 1000;
                            Log.i(TAG, "핸들러/방송까지 남은 밀리미초: " + sec);
                            long diffSeconds = sec / 1000 % 60;
                            second = String.valueOf(diffSeconds);
                            if (diffSeconds < 10) {
                                second = "0" + second;
                            }
                            long diffMinutes = sec / (60 * 1000) % 60;
                            minute = String.valueOf(diffMinutes);
                            if (diffMinutes < 10) {
                                minute = "0" + minute;
                            }
                            long diffHours = sec / (60 * 60 * 1000) % 24;
                            hour = String.valueOf(diffHours);
                            if (diffHours < 10) {
                                hour = "0" + hour;
                            }
                            Log.i(TAG, "핸들러/방송까지 남은시간: " + hour + ":" + minute + ":" + second);
                            //int hour = (int) ((sec * 60 * 60 ) / (60 * 60));

                            // Log.i(TAG, "핸들러 카운트: ");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // UI 작업 수행 O

                                    timerView.setText("남은시간: " + hour + ":" + minute + ":" + second);
                                    //Log.i(TAG,"남으시간 변환 값/:"+waitTime);
                                    //시작시간에 도달하면 방송을 켜준다.
                                    if (sec == 0 || sec < 0) {
                                        isRun = false;

                                        recyclerView.setVisibility(View.VISIBLE);
                                        waitingView.setVisibility(View.INVISIBLE);

                                        if(player!=null)
                                            player.prepare(mediaSource, false, false);

                                    }
                                }
                            });


                        } catch (Exception e) {
                        }


                    }

                }
            });
            t.start();
            


            // TODO: 2020-03-27 알람설정 클릭이벤트
            notifyBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {

               /*     //최초공개 영상에 관한 정보(FCM서버에 등록할 토큰 값, 영상 정보를 레트로핏으로 서버에 보낸다.
                    //토큰 얻고 저장
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    // Get new Instance ID token

                                    token = task.getResult().getToken();

                                    // Log and toast
                                    String msg = getString(R.string.msg_token_fmt, token);
                                    Log.d(TAG, msg);
                                    //Toast.makeText(VideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });*/

                    //shared를 선언하고
                    final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE); //SharedPreferences를 선언
                    SharedPreferences.Editor editor = Shared.edit();


                    //gson으로 영상아이템을 저장한다.
                    Gson gson = new Gson();
                    String json = gson.toJson(videoItem);

                    editor.putString("videoItem", json);
                    editor.commit();
                    Log.i(TAG,"저장된 비디오아이템:"+Shared.getString("videoItem", null));


                    //레트로핏을 통해서 FCM서버로 보낸다.
                    //서버 apikey,디바이스 토큰,비디오타이틀,비디오 예약시간을 보낸다.


                    RetrofitService notifyService = new RequestHelper().getRetrofit();

                    Call<result> notifyResponse = notifyService.requestPush(apiKey, token, room, startTime);


                    notifyResponse.enqueue(new Callback<result>() {
                        @Override
                        public void onResponse(Call<result> call, Response<result>response) {



                            if (response.isSuccessful()) {


                                //Toast.makeText(getApplicationContext(), "최초공개 영상 알람등록이 되었습니다.", Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(), response.body().getResponse(), Toast.LENGTH_LONG).show();


                            }


                        }

                        @Override
                        public void onFailure(Call<result> call, Throwable t) {
                            Log.i(TAG, "푸쉬 실패");
                            Log.d(TAG, t.toString());
                            //Toast.makeText(getApplicationContext(), "최초공개 영상 알람등록이 되지않았습니다.", Toast.LENGTH_LONG).show();

                        }
                    });


                    Toast.makeText(getApplicationContext(), "최초공개 영상 알람등록이 되었습니다.", Toast.LENGTH_LONG).show();
                    notifyBtn.setImageResource(R.drawable.ic_notifications_active_black_24dp);
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

// TODO: 2020-03-28 채팅입력 이벤
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


            //최초공개가 아닐시 댓글을 불러오고 댓글과 관련된 설정을 해준다.
        } else {

//todo 레트로핏으로 댓글 데이터 요청
            RetrofitService itemService = new RequestHelper().getRetrofit();

            //영상의 idx를 보낸다.
            //Call<JsonResponse> itemResponse = itemService.requestComment(data.get(0).getIdx());
            Call<JsonResponse> itemResponse = itemService.requestComment(videoItem.getIdx());


            itemResponse.enqueue(new Callback<JsonResponse>() {
                @Override
                public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                    JsonResponse jsonResponse = response.body();

                    Log.i(TAG, "댓 요청메소드");


                    if (response.isSuccessful()) {

                        Log.i(TAG, "응답성공");

                        //영상정보를 요청 후 받아오면 리스트에 담고
                        commentItems = new ArrayList<>(Arrays.asList(jsonResponse.getCommentItem()));
                        //Log.i(TAG, "댓글 내용"+commentItems.get(0).getContent());

                        //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ videoItems.size());

                        //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ jsonResponse.getVideoItem());

                        //레트로핏으로 영상정보 리스트가 만들어지면
                        //영상정보리스트를 이용하여 어댑터를 생성
                        if (commentItems.size() > 0) {
                            Log.i(TAG, "아이템 조건문 통과");
                            adapter = new CommentDataAdapter(VideoActivity.this, commentItems);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            adapter.setOnItemClickListener(new CommentDataAdapter.OnCommentItemClickListener() {
                                @Override
                                public void onItemClick(View v, int pos, String span) {

                                    Log.i(TAG, "클릭한 아이템 span값:" + span);

                                    String target = ":";
                                    int target_num = span.indexOf(target);

                                    int min = Integer.parseInt(span.substring(0, target_num));

                                    int sec = Integer.parseInt(span.substring(target_num + 1, 5));

                                    int total = min * 60 + sec;
                                    long totalPosition = total * 1000;

                                    Log.i(TAG, "클릭한 아이템 분:" + min + "초" + sec);
                                    Log.i(TAG, "클릭한 아이템 총 시간:" + total);
                                    Long playbackPosition = 5000L;
                                    player.seekTo(currentWindow, totalPosition);
                                    //videoView.seekTo(total*1000);
                                    //videoView.start();


                                    //Toast.makeText(getApplicationContext(), adapter.getItem(pos).getId(), Toast.LENGTH_LONG).show();


                                }
                            });


                        }


                    }


                }

                @Override
                public void onFailure(Call<JsonResponse> call, Throwable t) {
                    Log.i(TAG, "데이터요청 실");
                    Log.d(TAG, t.toString());
                }
            });


            //todo 댓글입력 이벤트
            comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String content = input.getText().toString();

                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    mInputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);


                    String id = "tester";
                    input.setText("");

                    RetrofitService itemService = new RequestHelper().getRetrofit();


                    Call<JsonResponse> itemResponse = itemService.uploadComment(id, content, videoIdx);


                    itemResponse.enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                            JsonResponse jsonResponse = response.body();

                            Log.i(TAG, "댓글입");


                            if (response.isSuccessful()) {

                                Log.i(TAG, "댓글로드 성공");

                                //영상정보를 요청 후 받아오면 리스트에 담고
                                commentItems = new ArrayList<>(Arrays.asList(jsonResponse.getCommentItem()));
                                adapter = new CommentDataAdapter(VideoActivity.this, commentItems);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                adapter.setOnItemClickListener(new CommentDataAdapter.OnCommentItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position, String span) {
                                        Log.i(TAG, "클릭한 아이템 span 값:" + span);

                                        String target = ":";
                                        int target_num = span.indexOf(target);

                                        int min = Integer.parseInt(span.substring(0, target_num));

                                        int sec = Integer.parseInt(span.substring(target_num + 1, 5));

                                        int total = min * 60 + sec;
                                        long totalPosition = total * 1000;

                                        Log.i(TAG, "클릭한 아이템 분:" + min + "초" + sec);
                                        Log.i(TAG, "클릭한 아이템 총 시간:" + total);

                                        player.seekTo(currentWindow, totalPosition);

                                        // videoView.seekTo(total*1000);
                                    }


                                });


                            }


                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            Log.i(TAG, "데이터요청 실");
                            Log.d(TAG, t.toString());
                        }
                    });



                }
            });

            //타임라인(시간링크) 클릭이벤트
// 메모 클릭 이벤트(수정)


            //todo 댓글언어설정
            //선택한 언어의 댓글만 보게하는 기능

            //버튼을 클릭하면 다이얼로그가 뜨고 다이얼로그의 언어 중 하나를 골라 해당언어의 댓글만 보이게 한다.
            languageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
                    final ArrayList<String> selectedItems = new ArrayList<String>();
                    final String[] items = getResources().getStringArray(R.array.LAN);

                    builder.setTitle("리스트 추가 예제");

                    builder.setMultiChoiceItems(R.array.LAN, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos, boolean isChecked) {
                            if (isChecked == true) // Checked 상태일 때 추가
                            {
                                selectedItems.add(items[pos]);
                            } else                  // Check 해제 되었을 때 제거
                            {
                                selectedItems.remove(pos);
                            }
                        }
                    });

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {

                            for (int i = 0; i < selectedItems.size(); i++) {
                                //SeletedLanguage =  SeletedLanguage + "," + selectedItems.get(i);
                                SeletedLanguage = selectedItems.get(i);

                            }

                            Toast toast = Toast.makeText(getApplicationContext(), "선택 된 항목은 :" + SeletedLanguage, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();


                            //todo 레트로핏으로 특정언어의 댓글 데이터 요청
                            RetrofitService languageService = new RequestHelper().getRetrofit();

                            //영상의 idx를 보낸다.
                           // Call<JsonResponse> languageResponse = languageService.languageComment(data.get(0).getIdx(), SeletedLanguage);
                            Call<JsonResponse> languageResponse = languageService.languageComment(videoItem.getIdx(), SeletedLanguage);


                            languageResponse.enqueue(new Callback<JsonResponse>() {
                                @Override
                                public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                                    JsonResponse jsonResponse = response.body();

                                    Log.i(TAG, "특정언어 댓글만");


                                    if (response.isSuccessful()) {

                                        Log.i(TAG, "응답성공");

                                        ArrayList<CommentItem> selectedItems = new ArrayList<CommentItem>();


                                        //영상정보를 요청 후 받아오면 리스트에 담고
                                        selectedItems = new ArrayList<>(Arrays.asList(jsonResponse.getCommentItem()));
                                        //Log.i(TAG, "댓글 내용"+commentItems.get(0).getContent());

                                        //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ videoItems.size());

                                        //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ jsonResponse.getVideoItem());

                                        //레트로핏으로 영상정보 리스트가 만들어지면
                                        //영상정보리스트를 이용하여 어댑터를 생성
                                        Log.i(TAG, "아이템 조건문 통과");
                                        adapter = new CommentDataAdapter(VideoActivity.this, selectedItems);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        adapter.setOnItemClickListener(new CommentDataAdapter.OnCommentItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int pos, String span) {

                                                Log.i(TAG, "클릭한 아이템 span값:" + span);

                                                String target = ":";
                                                int target_num = span.indexOf(target);

                                                int min = Integer.parseInt(span.substring(0, target_num));

                                                int sec = Integer.parseInt(span.substring(target_num + 1, 5));

                                                int total = min * 60 + sec;
                                                long totalPosition = total * 1000;

                                                Log.i(TAG, "클릭한 아이템 분:" + min + "초" + sec);
                                                Log.i(TAG, "클릭한 아이템 총 시간:" + total);
                                                Long playbackPosition = 5000L;
                                                player.seekTo(currentWindow, totalPosition);
                                                //videoView.seekTo(total*1000);
                                                //videoView.start();


                                                //Toast.makeText(getApplicationContext(), adapter.getItem(pos).getId(), Toast.LENGTH_LONG).show();


                                            }
                                        });


                                    }


                                }

                                @Override
                                public void onFailure(Call<JsonResponse> call, Throwable t) {
                                    Log.i(TAG, "데이터요청 실");
                                    Log.d(TAG, t.toString());
                                }
                            });


                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    //선택된 언어를 레트로핏을 통해 서버로 전송한다


                    //SeletedLanguage


                }
            });


        }
        //엑소플레이어뷰


    }


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


    //뒤로가기로 나가서 다른 영상을 볼경우에 exoplayer와 시간스레드를 정지시키기 위해 뒤로가기 버튼 인식후 엑소플레이어 종료와 시간스레드 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();



//채팅과 비디오를 모두 종료시킨다.
        isRun = false;
        releasePlayer();

        if (socket != null) {
            isConnected = false;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //액티비티를 종료시킨다.
        finish();



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket != null) {

            isConnected = false;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

           /* if (serviceIntent!=null) {
                stopService(serviceIntent);
                serviceIntent = null;
            }*/

        }
    }


}
