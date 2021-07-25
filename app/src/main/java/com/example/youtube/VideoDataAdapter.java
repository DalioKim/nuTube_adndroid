package com.example.youtube;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;
import android.media.MediaPlayer.OnPreparedListener;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import android.os.Handler;


import java.util.ArrayList;
import java.util.List;

public class VideoDataAdapter extends RecyclerView.Adapter<VideoDataAdapter.ItemViewHolder> {

    private ArrayList<VideoItem> videoItems;
    private OnItemClickListener mListener = null;
    boolean check;

    Context context;   //콘텍스트 선언


    //테마데이터 어댑터 생성자
    public VideoDataAdapter(Context context, ArrayList<VideoItem> videoItems) {

        this.context = context;
        this.videoItems = videoItems;

    }

    //
    @Override
    public VideoDataAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

        VideoDataAdapter.ItemViewHolder viewHolder = new VideoDataAdapter.ItemViewHolder(view); // 뷰객체를 파라미터로 받아 뷰 홀더객체를 생성

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoDataAdapter.ItemViewHolder holder, int position) {


        Glide.with(context).load(Uri.parse(videoItems.get(position).getVideoThumbnail())).into(holder.mediaCoverImage);
        holder.titleView.setText(videoItems.get(position).getTitle());
       // holder.explainView.setText("유튜버:" + videoItems.get(position).getId() + "            조회수:" + videoItems.get(position).getHit() + "회           게시시간:" + videoItems.get(position).getDate());


  /*      if (videoItems.get(position).getTheFirst().equals("true")) {

            holder.firstAlertView.setVisibility(View.VISIBLE);

        } else {
            holder.firstAlertView.setVisibility(View.INVISIBLE);

        }*/


    }



    @Override
    public int getItemCount() {//어댑터에서 관리하는 리스트에 저장된 아이템의 갯수를 확인 = 리스트의 크기를 확인
        return videoItems.size();
    }


    public void addItem(VideoItem item) {
        videoItems.add(item);
    }

    public void setItems(ArrayList<VideoItem> Item) {
        this.videoItems = Item;
    }

    public VideoItem getItem(int position) {
        return videoItems.get(position);
    }

    public void setItem(int position, VideoItem item) {
        videoItems.set(position, item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public SimpleExoPlayer player(Context context) {
        Log.i("어댑터", "플레이어 객체생성");
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd());

        return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }


    //테마 아이템뷰 정의 부분
    public class ItemViewHolder extends RecyclerView.ViewHolder {


        //사용자로부터 포커스를 받고 있는 영상아이템의 비디오(엑소)플레이어와 썸네일을 바꿔줄 프레임레이아웃 위젯객체
        public FrameLayout mediaContainer;
        public ProgressBar progressBar;
        public ImageView mediaCoverImage;
        private View parent;
        private TextView titleView, explainView, censorshipView, firstAlertView;


        public ItemViewHolder(final View itemView) {
            super(itemView);

            parent = itemView;
            mediaContainer = itemView.findViewById(R.id.mediaContainer);
            mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
            //explainView = itemView.findViewById(R.id.explainView);
            titleView = itemView.findViewById(R.id.titleView);
            //firstAlertView = itemView.findViewById(R.id.firstAlertView);
            progressBar = itemView.findViewById(R.id.progressBar);


            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.i("어댑터","아이템클릭인식");

                    VideoItem videoItem = new VideoItem(videoItems.get(pos).getVideoName(),
                            videoItems.get(pos).getHit(), videoItems.get(pos).getTitle(), videoItems.get(pos).getId(),
                            videoItems.get(pos).getIdx(), videoItems.get(pos).getSubtitle(), videoItems.get(pos).getTag(),
                            videoItems.get(pos).getTheFirst(), videoItems.get(pos).getStartTime());


                    Intent intent = new Intent(context, VideoActivity.class); //수정클래스로 목적지를 정해주고
                    intent.putExtra("data", videoItem);
                    //어느 경로에서 비디오액티비티로 이동했는지 알려준다.
                    intent.putExtra("route", "main");


                    context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


                }


            });


        }
    }

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }


    //비디오 재생 및 정지 메소드


}
