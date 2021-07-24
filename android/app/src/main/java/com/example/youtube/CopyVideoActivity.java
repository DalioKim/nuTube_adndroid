package com.example.youtube;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CopyVideoActivity extends AppCompatActivity {

    private ArrayList<VideoItem> data;
    private ArrayList<CommentItem> commentItems = new ArrayList<CommentItem>();


    ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();

    VideoView videoView;
    TextView videoInfo;
    EditText input;
    TextView textView;
    String videoIdx;
    Uri video;
    Uri uri;

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


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {


            initializePlayer();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {


            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {


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

        if(theFirst.equals("false")) {

            player.prepare(mediaSource, false, false);
        }else{


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

   /* private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "youtube");
        DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
        return mediaSourceFactory.createMediaSource(uri);


    }*/

    private MediaSource buildMediaSource(Uri uri) { DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua"); return new ExtractorMediaSource.Factory(manifestDataSourceFactory).createMediaSource(uri); }

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

        //uri = Uri.parse(ex);
        //위젯연


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
        adapter = new CommentDataAdapter(CopyVideoActivity.this, commentItems);
        recyclerView.setAdapter(adapter);


        //최초공개시 뷰
        waitingView = findViewById(R.id.waitingView);
        timerView = findViewById(R.id.timerView);


        textView = findViewById(R.id.textView);
        data = getIntent().getParcelableArrayListExtra("data");

        videoIdx = data.get(0).getIdx();
        Log.i(TAG,"비디오 idx "+videoIdx);
        /*String ex = "![CDATA["+data.get(0).getVideoName()+"]]";
        uri  = Uri.parse(ex);*/
        video = Uri.parse(data.get(0).getVideoName());
        theFirst = data.get(0).getTheFirst();
        startTime = data.get(0).getStartTime();

        if(theFirst.equals("true")){

            recyclerView.setVisibility(View.INVISIBLE);
            waitingView.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),  "최초공개 영상시간 "+startTime, Toast.LENGTH_SHORT).show();




            SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
            String nowtime = sdfNow.format(new Date(System.currentTimeMillis()));
            Log.i(TAG,"현재시간: "+nowtime);

            startTime = startTime+":00";
            Log.i(TAG,"예약시간: "+startTime);


            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdfNow.parse(nowtime);
                d2 = sdfNow.parse(startTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            sec = d2.getTime() - d1.getTime();
            Log.i(TAG,"방송까지 남은밀리미초: "+sec);





            isRun = true;

            Thread t = new Thread(new Runnable(){



                @Override public void run() {
                    // UI 작업 수행 X

                    while ((isRun)) {

                        //시간을 잰다.
                        try {
                            Thread.sleep(1000);
                            sec -= 1000;
                            Log.i(TAG,"핸들러/방송까지 남은 밀리미초: "+sec);
                            long diffSeconds = sec / 1000 % 60;
                            second = String.valueOf(diffSeconds);
                            if(diffSeconds <10){
                                second = "0"+second;
                            }
                            long diffMinutes = sec / (60 * 1000) % 60;
                            minute = String.valueOf(diffMinutes);
                            if(diffMinutes <10){
                                minute = "0"+minute;
                            }
                            long diffHours = sec / (60 * 60 * 1000) % 24;
                            hour = String.valueOf(diffHours);
                            if(diffHours <10){
                                hour = "0"+hour;
                            }
                            Log.i(TAG,"핸들러/방송까지 남은시간: "+hour+":"+minute+":"+second);
                            //int hour = (int) ((sec * 60 * 60 ) / (60 * 60));

                            // Log.i(TAG, "핸들러 카운트: ");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // UI 작업 수행 O

                                    timerView.setText("남은시간: "+hour+":"+minute+":"+second);
                                    //Log.i(TAG,"남으시간 변환 값/:"+waitTime);
                                    //시작시간에 도달하면 방송을 켜준다.
                                    if (sec == 0 || sec<0) {
                                        isRun = false;

                                        recyclerView.setVisibility(View.VISIBLE);
                                        waitingView.setVisibility(View.INVISIBLE);
                                        player.prepare(mediaSource, false, false);

                                    }
                                }
                            });



                        } catch (Exception e) {
                        }





                    }

                }
            }); t.start();

        }else{



        }
        //엑소플레이어뷰




    /*    if(data.get(0).getCensorship().equals("false")){
            textView.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.VISIBLE);

            String url = data.get(0).censorPhoto;
            String image1 = data.get(0).censorPhoto.substring(0,url.lastIndexOf(","));
            String image2 = data.get(0).censorPhoto.substring(url.lastIndexOf(",")+1);
            Log.i(TAG,"이미지1주소:"+image1);
            Log.i(TAG,"이미지2주소:"+image2);


            Glide.with(CopyVideoActivity.this).load(Uri.parse(image1)).into(imageView1);
            Glide.with(CopyVideoActivity.this).load(Uri.parse(image2)).into(imageView2);

        }*/
        /*//adsLoader = new ImaAdsLoader(this,video);

        //엑소플레이어 초기화
        if (player == null) {

            player = ExoPlayerFactory.newSimpleInstance(this.getApplicationContext());

            //플레이어 연결
            exoPlayerView.setPlayer(player);
            //adsLoader.setPlayer(player);

            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM); // or RESIZE_MODE_FILL






        }*/

        //엑소플레이어 영상uri 주소 할당 및 영상재생

        //MediaSource mediaSource = buildMediaSource(video);
        // Create a data source factory.
        /*DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "youtube"));
        // Create a DASH media source pointing to a DASH manifest uri.
        MediaSource mediaSource = new DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(video);*/

        /*DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"));

        DashMediaSource dashMediaSource = new DashMediaSource(video, dataSourceFactory,
                new DefaultDashChunkSource.Factory(dataSourceFactory), null, null);*/



      /*  DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, getString(R.string.app_name)));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(video);
        MediaSource adsMediaSource =
                new AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, exoPlayerView);*/

       /* player.prepare(mediaSource);

        player.setPlayWhenReady(true);
*/

        //videoView.setVideoURI(video);


        //videoView.setMediaController(mediaController);
        //mediaController.setAnchorView(videoView);

      /*videoView.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                Log.i("비디오","비디오뷰 준비");
                videoView.start();
            }
        });*/
        String tag = data.get(0).getTag();
        /*
        if(!tag.equals("")) {
            SpannableString text = new SpannableString(tag);
            text.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")), 0, tag.length(), Spanned.SPAN_INTERMEDIATE);
            videoInfo.setText(text+"\n"+data.get(0).getTitle()+"\n"+data.get(0).getSubtitle());
        }else{
            videoInfo.setText(tag+"\n"+data.get(0).getTitle()+"\n"+data.get(0).getSubtitle());

        }*/
        /*SpannableStringBuilder ssb = new SpannableStringBuilder(tag);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),0,tag.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
*/



//todo 레트로핏으로 댓글 데이터 요청
        RetrofitService itemService = new RequestHelper().getRetrofit();

        //영상의 idx를 보낸다.
        Call<JsonResponse> itemResponse = itemService.requestComment(data.get(0).getIdx());


        itemResponse.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse>call, Response<JsonResponse> response) {

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
                    if(commentItems.size() >0) {
                        Log.i(TAG,"아이템 조건문 통과");
                        adapter = new CommentDataAdapter(CopyVideoActivity.this, commentItems);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        adapter.setOnItemClickListener(new CommentDataAdapter.OnCommentItemClickListener() {
                            @Override
                            public void onItemClick(View v, int pos,String span) {

                                Log.i(TAG,"클릭한 아이템 span값:"+span);

                                String target = ":";
                                int target_num = span.indexOf(target);

                                int min = Integer.parseInt(span.substring(0,target_num));

                                int sec = Integer.parseInt(span.substring(target_num+1,5));

                                int total = min*60 + sec;
                                long totalPosition=total*1000;

                                Log.i(TAG,"클릭한 아이템 분:"+min+"초"+sec);
                                Log.i(TAG,"클릭한 아이템 총 시간:"+total);
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
        Button comment_btn = (Button) findViewById(R.id.comment_btn);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                mInputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);



                String id = "tester";
                String content = input.getText().toString();
                input.setText("");

                RetrofitService itemService = new RequestHelper().getRetrofit();


                Call<JsonResponse> itemResponse = itemService.uploadComment(id,content,videoIdx);


                itemResponse.enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse>call, Response<JsonResponse> response) {

                        JsonResponse jsonResponse = response.body();

                        Log.i(TAG, "댓글입");


                        if (response.isSuccessful()) {

                            Log.i(TAG, "댓글로드 성공");

                            //영상정보를 요청 후 받아오면 리스트에 담고
                            commentItems = new ArrayList<>(Arrays.asList(jsonResponse.getCommentItem()));
                            adapter = new CommentDataAdapter(CopyVideoActivity.this, commentItems);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            adapter.setOnItemClickListener(new CommentDataAdapter.OnCommentItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position, String span) {
                                    Log.i(TAG,"클릭한 아이템 span 값:"+span);

                                    String target = ":";
                                    int target_num = span.indexOf(target);

                                    int min = Integer.parseInt(span.substring(0,target_num));

                                    int sec = Integer.parseInt(span.substring(target_num+1,5));

                                    int total = min*60 + sec;
                                    long totalPosition=total*1000;

                                    Log.i(TAG,"클릭한 아이템 분:"+min+"초"+sec);
                                    Log.i(TAG,"클릭한 아이템 총 시간:"+total);

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



        //선택한 언어의 댓글만 보게하는 기능

        //버튼을 클릭하면 다이얼로그가 뜨고 다이얼로그의 언어 중 하나를 골라 해당언어의 댓글만 보이게 한다.
        Button languageBtn = (Button) findViewById(R.id.languageBtn);
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(CopyVideoActivity.this);
                final ArrayList<String> selectedItems = new ArrayList<String>();
                final String[] items = getResources().getStringArray(R.array.LAN);

                builder.setTitle("리스트 추가 예제");

                builder.setMultiChoiceItems(R.array.LAN, null, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos, boolean isChecked)
                    {
                        if(isChecked == true) // Checked 상태일 때 추가
                        {
                            selectedItems.add(items[pos]);
                        }
                        else				  // Check 해제 되었을 때 제거
                        {
                            selectedItems.remove(pos);
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {

                        for(int i =0; i<selectedItems.size();i++)
                        {
                            //SeletedLanguage =  SeletedLanguage + "," + selectedItems.get(i);
                            SeletedLanguage =  selectedItems.get(i);

                        }

                        Toast toast = Toast.makeText(getApplicationContext(), "선택 된 항목은 :" + SeletedLanguage,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();


                        //todo 레트로핏으로 특정언어의 댓글 데이터 요청
                        RetrofitService languageService = new RequestHelper().getRetrofit();

                        //영상의 idx를 보낸다.
                        Call<JsonResponse> languageResponse = languageService.languageComment(data.get(0).getIdx(),SeletedLanguage);


                        languageResponse.enqueue(new Callback<JsonResponse>() {
                            @Override
                            public void onResponse(Call<JsonResponse>call, Response<JsonResponse> response) {

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
                                    Log.i(TAG,"아이템 조건문 통과");
                                    adapter = new CommentDataAdapter(CopyVideoActivity.this, selectedItems);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    adapter.setOnItemClickListener(new CommentDataAdapter.OnCommentItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int pos,String span) {

                                            Log.i(TAG,"클릭한 아이템 span값:"+span);

                                            String target = ":";
                                            int target_num = span.indexOf(target);

                                            int min = Integer.parseInt(span.substring(0,target_num));

                                            int sec = Integer.parseInt(span.substring(target_num+1,5));

                                            int total = min*60 + sec;
                                            long totalPosition=total*1000;

                                            Log.i(TAG,"클릭한 아이템 분:"+min+"초"+sec);
                                            Log.i(TAG,"클릭한 아이템 총 시간:"+total);
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

    //미디어 소스 초기화
   /* private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(this, "blackJin");

        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);
    }*/


    /*//엑소플레이어 해제
    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();

            exoPlayerView.setPlayer(null);
            player.release();
            player = null;

        }
    }*/


    /*private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory("ua",BANDWIDTH_METER );
        DashChunkSource.Factory dashChunkSourceFactory =
                new DefaultDashChunkSource.Factory(dataSourceFactory);
        return new DashMediaSource(uri, dataSourceFactory,
                dashChunkSourceFactory, null, null);
    }*/



    //뒤로가기로 나가서 다른 영상을 볼경우에 exoplayer와 시간스레드를 정지시키기 위해 뒤로가기 버튼 인식후 엑소플레이어 종료와 시간스레드 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();


        isRun = false;
        releasePlayer();

    }



}

