package com.example.youtube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;
import android.os.Handler;


public class Main2Activity extends AppCompatActivity {


    ExoPlayerRecyclerView mRecyclerView;
    private ArrayList<MediaObject> mediaObjectList = new ArrayList<>();
    private MediaRecyclerAdapter mAdapter;
    private boolean firstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

     /*   initView();
        // Prepare demo content
        prepareVideoList();
        //set data object
        mRecyclerView.setMediaObjects(mediaObjectList);
        mAdapter = new MediaRecyclerAdapter(mediaObjectList, initGlide());
        //Set Adapter
        mRecyclerView.setAdapter(mAdapter);
        if (firstTime) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.playVideo(false);
                }
            });
            firstTime = false;
        }
    }
    private void initView() {
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView);
       *//* mRecyclerView.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());*//*

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }
    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }
    @Override
    protected void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
        super.onDestroy();
    }
    private void prepareVideoList() {
        MediaObject mediaObject = new MediaObject();
        mediaObject.setId(1);
        mediaObject.setUserHandle("@h.pandya");
        mediaObject.setTitle(
                "Do you think the concept of marriage will no longer exist in the future?");
        mediaObject.setCoverUrl(
                "http://15.164.98.15/videos/1585028201.jpg");
        mediaObject.setUrl("http://15.164.98.15/videos/1585028201.mp4");
        MediaObject mediaObject2 = new MediaObject();
        mediaObject2.setId(2);
        mediaObject2.setUserHandle("@hardik.patel");
        mediaObject2.setTitle(
                "If my future husband doesn't cook food as good as my mother should I scold him?");
        mediaObject2.setCoverUrl(
                "http://15.164.98.15/videos/1585027060.jpg");
        mediaObject2.setUrl("http://15.164.98.15/videos/1585027060.mp4");
        MediaObject mediaObject3 = new MediaObject();
        mediaObject3.setId(3);
        mediaObject3.setUserHandle("@arun.gandhi");
        mediaObject3.setTitle("Give your opinion about the Ayodhya temple controversy.");
        mediaObject3.setCoverUrl(
                "http://15.164.98.15/videos/1585027376.jpg");
        mediaObject3.setUrl("http://15.164.98.15/videos/1585027376.mp4");
        MediaObject mediaObject4 = new MediaObject();
        mediaObject4.setId(4);
        mediaObject4.setUserHandle("@sachin.patel");
        mediaObject4.setTitle("When did kama founders find sex offensive to Indian traditions");
        mediaObject4.setCoverUrl(
                "http://15.164.98.15/videos/1585027605.jpg");
        mediaObject4.setUrl("http://15.164.98.15/videos/1585027605.mp4");
        MediaObject mediaObject5 = new MediaObject();
        mediaObject5.setId(5);
        mediaObject5.setUserHandle("@monika.sharma");
        mediaObject5.setTitle("When did you last cry in front of someone?");
        mediaObject5.setCoverUrl(
                "http://15.164.98.15/videos/1585027802.jpg");
        mediaObject5.setUrl("http://15.164.98.15/videos/1585027802.mp4");
        mediaObjectList.add(mediaObject);
        mediaObjectList.add(mediaObject2);
        mediaObjectList.add(mediaObject3);
        mediaObjectList.add(mediaObject4);
        mediaObjectList.add(mediaObject5);
        mediaObjectList.add(mediaObject);
        mediaObjectList.add(mediaObject2);
        mediaObjectList.add(mediaObject3);
        mediaObjectList.add(mediaObject4);
        mediaObjectList.add(mediaObject5);

    */
    }
}
