package com.example.youtube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    //메인메뉴에서 사용할 탭바 객체 선언
    Toolbar topBar;
    Toolbar bottomBar;

    ProgressDialog progressDialog;


    private static String TAG = "MainActivity";



    //영상아이템 목록을 보여줄 리사이클러뷰
    //private RecyclerView recyclerView;

    RecyclerView.LayoutManager mLayoutManager;

    //영상아이템 데이터를 관리할 어댑터
    //private VideoDataAdapter adapter;

    //홀더 객체
    //VideoDataAdapter.ItemViewHolder holder;
    //adapter.test(holder,postion);

    //스크롤인식에 사용할 포지션
    int position;


    //엑소플레이를 활용할 리사이클러뷰 객체
    ExoPlayerRecyclerView mRecyclerView;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();;
    private MediaRecyclerAdapter mAdapter;
    private boolean firstTime = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomBar = (Toolbar) findViewById(R.id.bottomBar);
        setSupportActionBar(bottomBar);
        //getSupportActionBar().setIcon(R.drawable.menu_logo); // 타이틀 대신 로고를 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 이름 안보이게*/


        //firebase 메인에서 과다한 동작으로 인해 일단 주석처리

       /* FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.i(TAG, "getInstanceId 성공"+token);

                        // Log and toast
                        *//*String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*//*
                    }
                });*/

//리사이클러뷰 관련 설정



        initView();



//todo 레트로핏으로 서버에 영상정보 요청
        RetrofitService itemService = new RequestHelper().getRetrofit();

        String rq ="asd";
        Call<JsonResponse> itemResponse = itemService.requestMain(rq);


        itemResponse.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse>call, Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                Log.i(TAG, "재차 요청메소드");


                if (response.isSuccessful()) {

                    Log.i(TAG, "응답성공");

                    //영상정보를 요청 후 받아오면 리스트에 담고
                    videoItems = new ArrayList<>(Arrays.asList(jsonResponse.getVideoItem()));
                   // temporalList = new ArrayList<>(Arrays.asList(jsonResponse.getVideoItem()));
                    Log.i(TAG, "아이 수 : "+ videoItems.size());

                    //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ jsonResponse.getVideoItem());

                    //레트로핏으로 영상정보 리스트가 만들어지면
                    //영상정보리스트를 이용하여 어댑터를 생성

                    /*mAdapter = new VideoDataAdapter(MainActivity.this, videoItems);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
*/
//set data object
                    mRecyclerView.setMediaObjects(videoItems);
                    mAdapter = new MediaRecyclerAdapter(videoItems, initGlide(),MainActivity.this);

                    //Set Adapter

                    mRecyclerView.setAdapter(mAdapter);

                    //메인화면에 처음 들어오면 영상목록상에서 가장 첫번째의 화면읠 재생시킨다. - 주석처리
             /*
                    if (firstTime) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {


                                mRecyclerView.playVideo(false);
                            }
                        });
                        firstTime = false;
                    }*/



                }


            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.i(TAG, "데이터요청 실");
                Log.d(TAG, t.toString());
            }
        });



        //아이템을 받아온 후





    }


    // TODO: 2020-04-02 exoplayer 리사이클러뷰 설정

    private void initView() {
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }












    // TODO: 2020-04-02 툴바 메뉴 설정

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        //MenuInflater topInflater = getMenuInflater();
        MenuInflater bottomInflater = getMenuInflater();


        bottomInflater.inflate(R.menu.bottombar, menu);
        //topInflater.inflate(R.menu.topbar, menu);


        return true;

    }


    @Override
    protected void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
        super.onDestroy();
    }


    // 툴바에 삽입된 메뉴에 대해서 이벤트 처리

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


                //일반영상업로드 액티비티류 이동
            case R.id.upload:

                //Toast.makeText(getApplicationContext(), "검색 버튼이 클릭됨", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);

                return true;


             //Live방송시작하는 액티비티로 이동
            case R.id.LiveBroadcast:

                //Toast.makeText(getApplicationContext(), "검색 버튼이 클릭됨", Toast.LENGTH_LONG).show();

                Intent broadcastIntent = new Intent(getApplicationContext(), UploadLiveActivity.class);
                startActivity(broadcastIntent);

                return true;

            //Live방송시청 하는 액티비티로 이동
            case R.id.WatchingLive:

                //Toast.makeText(getApplicationContext(), "검색 버튼이 클릭됨", Toast.LENGTH_LONG).show();

                Intent watchIntent = new Intent(getApplicationContext(), UploadLiveActivity.class);
                startActivity(watchIntent);

                return true;



            default:

                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();

                return super.onOptionsItemSelected(item);

        }

    }




}

