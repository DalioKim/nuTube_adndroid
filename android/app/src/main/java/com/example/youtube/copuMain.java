package com.example.youtube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class copuMain extends AppCompatActivity {

    //메인메뉴에서 사용할 탭바 객체 선언
    Toolbar topBar;
    Toolbar bottomBar;

    ProgressDialog progressDialog;


    private static String TAG = "MainActivity";

    //리사이클러뷰 관련
    //영상아이템의 데이터를 담을 리스트
    private ArrayList<VideoItem> videoItems;

    private SimpleExoPlayer player;

    //영상아이템 목록을 보여줄 리사이클러뷰
    private RecyclerView recyclerView;

    RecyclerView.LayoutManager mLayoutManager;

    //영상아이템 데이터를 관리할 어댑터
    private VideoDataAdapter adapter;

    //홀더 객체
    //VideoDataAdapter.ItemViewHolder holder;
    //adapter.test(holder,postion);

    //스크롤인식에 사용할 포지션
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomBar = (Toolbar) findViewById(R.id.bottomBar);
        setSupportActionBar(bottomBar);
        //getSupportActionBar().setIcon(R.drawable.menu_logo); // 타이틀 대신 로고를 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 이름 안보이게*/


        //firebase
        FirebaseInstanceId.getInstance().getInstanceId()
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
                        /*String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(copuMain.this, msg, Toast.LENGTH_SHORT).show();*/
                    }
                });



//리사이클러뷰 관련 설정
        recyclerView = findViewById(R.id.rcView);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //todo 리사이클러뷰 스크롤감지
        //로직 스크롤이 감지된다, 2초후에 이전 포지션의 비디오뷰를 꺼준다.
        //새로운 포지션의 아이템을 받아 비디오뷰를 켜준다
        //코드흐름: 스크롤 메소드 호출 -> 2초 슬립 -> 이전에 비디오뷰가 켜져있는 아이템의 포지션번호가 있으면
        //조건문으로 검사해주고, 현재 포지션과 비교해서 같을 경우, 이전 포지션의 비디오뷰를 꺼준다 -> 새로운 포지션
        //아이템의 비디오뷰를 켜준다. 어댑터로 알려준다.

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
                    Log.i(TAG, "Top of list");

                    //로딩바
                    new android.os.Handler().postDelayed( new Runnable() { public void run() {  progressDialog = new ProgressDialog(copuMain.this); progressDialog.setIndeterminate(true); progressDialog.setMessage("영상목록 업데이트"); progressDialog.show(); } }, 0);


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
                                new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);

                                //영상정보를 요청 후 받아오면 리스트에 담고
                                videoItems = new ArrayList<>(Arrays.asList(jsonResponse.getVideoItem()));
                                // temporalList = new ArrayList<>(Arrays.asList(jsonResponse.getVideoItem()));
                                //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ videoItems.size());

                                //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ jsonResponse.getVideoItem());

                                //레트로핏으로 영상정보 리스트가 만들어지면
                                //영상정보리스트를 이용하여 어댑터를 생성

                                adapter = new VideoDataAdapter(copuMain.this, videoItems);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();





                            }


                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            Log.i(TAG, "데이터요청 실");
                            Log.d(TAG, t.toString());
                            new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);

                        }
                    });



                } else if (!recyclerView.canScrollVertically(1)) {
                    Log.i(TAG, "End of list");
                } else {
                    Log.i(TAG, "idle");
                }
            }
        });


     /*   recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){



            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                // 2초간 멈추게 하고싶다면
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {


                        recyclerView.post(new Runnable() {
                            public void run() {

                                videoItems.clear();
                                videoItems.addAll(temporalList);

                                Integer check = new Integer(position);

                                if(check!=null) {
                                    adapter.getItem(position).setStart("false");
                                    //adapter.notifyDataSetChanged();
                                }

                                //position = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                                position = ((LinearLayoutManager) mLayoutManager).findFirstCompletelyVisibleItemPosition();

                                adapter.getItem(position).setStart("true");
                                // There is no need to use notifyDataSetChanged()
                                // videoItems.clear(); // adapter에 연결한 list의 내용을 모두 지웠다가
                                // videoItems.addAll(temporalList);
                                adapter.notifyDataSetChanged();                    }
                        });


                    }
                }, 5000);  // 2000은 2초를 의미합니다.



            }
        });*/
       /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                //adapter.getItem(position).setStart("true");

                    videoItems.clear(); // adapter에 연결한 list의 내용을 모두 지웠다가
                    videoItems.addAll(temporalList);
                recyclerView.post(new Runnable() {
                    public void run() {
                        // There is no need to use notifyDataSetChanged()
                       // videoItems.clear(); // adapter에 연결한 list의 내용을 모두 지웠다가
                       // videoItems.addAll(temporalList);
                        adapter.notifyDataSetChanged();                    }
                });

                Integer check = new Integer(position);

                if(check!=null) {
                    adapter.getItem(position).setStart("false");
                    //adapter.notifyDataSetChanged();
                }

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG,"핸들러");
                        position = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                        adapter.getItem(position).setStart("true");
                        adapter.notifyDataSetChanged();
                        }
                }, 3000);
           Log.i(TAG,"스크롤 감지");
                int postion = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                Log.i(TAG,"보여지는 포지션"+postion);
                //adapter.test(holder,postion);

                //adapter.getItem().
                //recyclerView.findViewHolderForAdapterPosition(postion).itemView.` `
                //View v= viewHolder.itemView;
                //viewHolder.
                //int position = (int) recyclerView.getTag();
                //new VideoDataAdapter.ItemViewHolder holder = holder.getAdapterPosition();
                //holder.videoView.start();
                //VideoView v= (VideoView) adapter.videoView.getTag(postion);
                //adapter.videoView
                //Log.i(TAG,"홀더위치값 : "+positon);
                //recyclerView.findViewHolderForAdapterPosition(postion)
                //adapter.getItem(postion).setHit("1");
                //adapter.notifyDataSetChanged();


            }



                                           @Override
                                             public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                 Log.i(TAG,"스크롤 감지");
                                                 int postion = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                                                 Log.i(TAG,"보여지는 포지션"+postion);
                                             }


        });*/

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
                    //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ videoItems.size());

                    //Log.i(TAG, "추가로 받아온 주식종목 수 : "+ jsonResponse.getVideoItem());

                    //레트로핏으로 영상정보 리스트가 만들어지면
                    //영상정보리스트를 이용하여 어댑터를 생성

                    adapter = new VideoDataAdapter(copuMain.this, videoItems);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();





                }


            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.i(TAG, "데이터요청 실");
                Log.d(TAG, t.toString());
            }
        });






    }



    // 하단바바에 bottombar.xml을 집어넣는다

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        //MenuInflater topInflater = getMenuInflater();
        MenuInflater bottomInflater = getMenuInflater();


        bottomInflater.inflate(R.menu.bottombar, menu);
        //topInflater.inflate(R.menu.topbar, menu);


        return true;

    }




    // 툴바에 삽입된 메뉴에 대해서 이벤트 처리

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.upload:

                //Toast.makeText(getApplicationContext(), "검색 버튼이 클릭됨", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);

                return true;







            default:

                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();

                return super.onOptionsItemSelected(item);

        }

    }




}
