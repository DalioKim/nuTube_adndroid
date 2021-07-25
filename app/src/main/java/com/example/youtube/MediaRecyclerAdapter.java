package com.example.youtube;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MediaObject> mediaObjects;
    private RequestManager requestManager;
    private ArrayList<VideoItem> videoItems;
    Context context;
    private VideoDataAdapter.OnItemClickListener mListener = null;




    //생성자
    public MediaRecyclerAdapter(ArrayList<VideoItem> videoItems,
                                RequestManager requestManager, Context context) {
        this.videoItems = videoItems;
        this.requestManager = requestManager;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_video, viewGroup, false));
     //                   .inflate(R.layout.layout_media_list_item, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((PlayerViewHolder) viewHolder).onBind(videoItems.get(i), requestManager);
    }
    @Override
    public int getItemCount() {
        return videoItems.size();
    }



    public void setOnItemClickListener(VideoDataAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }


    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        /**
         * below view have public modifier because
         * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
         */
        public FrameLayout mediaContainer;
        public ImageView mediaCoverImage, volumeControl;
        public ProgressBar progressBar;
        public RequestManager requestManager;
        private TextView titleView, explainView, firstAlertView;
        private View parent;



        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            mediaContainer = itemView.findViewById(R.id.mediaContainer);
            mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
            titleView = itemView.findViewById(R.id.titleView);
            explainView = itemView.findViewById(R.id.explainView);
            progressBar = itemView.findViewById(R.id.progressBar);
            firstAlertView = itemView.findViewById(R.id.firstAlertView);


        }

        // void onBind(MediaObject mediaObject, RequestManager requestManager) {
        void onBind(VideoItem videoObject, RequestManager requestManager) {

            this.requestManager = requestManager;
            parent.setTag(this);
            titleView.setText(videoObject.getTitle());
            explainView.setText("유튜버:" + videoObject.getId() + "            조회수:" + videoObject.getHit() + "회           게시시간:" + videoObject.getDate());
            this.requestManager
                    .load(videoObject.getVideoThumbnail())
                    .into(mediaCoverImage);

            if (videoObject.getTheFirst().equals("true")) {

                firstAlertView.setVisibility(View.VISIBLE);

            } else {
                firstAlertView.setVisibility(View.INVISIBLE);

            }


            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();


                    VideoItem videoItem = new VideoItem(videoObject.getVideoName(),
                            videoObject.getHit(), videoObject.getTitle(), videoObject.getId(),
                            videoObject.getIdx(), videoObject.getSubtitle(), videoObject.getTag(),
                            videoObject.getTheFirst(), videoObject.getStartTime());


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


}
