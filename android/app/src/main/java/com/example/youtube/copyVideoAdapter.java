package com.example.youtube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class copyVideoAdapter {


/*
    public class VideoDataAdapter extends RecyclerView.Adapter<com.example.youtube.VideoDataAdapter.ItemViewHolder> {

        private ArrayList<VideoItem> videoItems;
        private com.example.youtube.VideoDataAdapter.OnItemClickListener mListener = null;
        boolean check;
        //private VideoDataAdapter.ItemViewHolder holder;

        Context context;   //콘텍스트 선언
        int position;
        String path;
        final SimpleExoPlayer player;

    VideoView videoView;
    ImageView videoThumbnail;
    TextView titleView, explainView;

        //엑소플레이어 설정관련 변수들

        //private PlayerView exoPlayerView;




        private Boolean playWhenReady = true;
        private int currentWindow = 0;
        private Long playbackPosition = 0L;



        //테마데이터 어댑터 생성자
        public VideoDataAdapter(Context context, ArrayList<VideoItem> videoItems) {

            this.context = context;
            this.videoItems = videoItems;
            player = player(context);

        }

        //
        @Override
        public com.example.youtube.VideoDataAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

            com.example.youtube.VideoDataAdapter.ItemViewHolder viewHolder = new com.example.youtube.VideoDataAdapter.ItemViewHolder(view); // 뷰객체를 파라미터로 받아 뷰 홀더객체를 생성

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final com.example.youtube.VideoDataAdapter.ItemViewHolder holder, int position) {

            //holder.videoView.setVideoURI(Uri.parse(videoItems.get(position).getVideoThumbnail()));
            //Glide.with(context).load(Uri.parse(videoItems.get(position).getVideoThumbnail())).into(holder.videoView);
            //holder.videoView.setBackgroundDrawable(new BitmapDrawable(videoItems.get(position).getVideoThumbnail()));
            Glide.with(context).load(Uri.parse(videoItems.get(position).getVideoThumbnail())).into(holder.videoThumbnail);
            holder.titleView.setText(videoItems.get(position).getTitle());
            holder.explainView.setText("유튜버:" + videoItems.get(position).getId() + "            조회수:" + videoItems.get(position).getHit() + "회           게시시간:" + videoItems.get(position).getDate());
            //path = videoItems.get(position).getVideoName();
            Uri video = Uri.parse(videoItems.get(position).getVideoName());
            holder.videoView.setVideoURI(video);

            if(videoItems.get(position).getTheFirst().equals("true")){

                holder.firstAlertView.setVisibility(View.VISIBLE);

            }else{
                holder.firstAlertView.setVisibility(View.INVISIBLE);

            }


            //노딱판별
            // String censorship = videoItems.get(position).getCensorship();


        if(censorship.equals("false")){
            holder.censorshipView.setVisibility(View.VISIBLE);
        }

        if (videoItems.get(position).getStart().equals("true")) {
            Log.i("비디오", "비디오뷰 시작");
            Log.i("비디오", "url" + videoItems.get(position).getVideoName());

    holder.videoThumbnail.setVisibility(View.INVISIBLE);
    holder.videoView.setVisibility(View.VISIBLE);


        Log.i("비디오", "player 생성완");
      holder.videoView.setPlayer(player);
        //DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua");
        //mediaSource = new ExtractorMediaSource.Factory(manifestDataSourceFactory).createMediaSource(video);
        MediaSource mediaSource = buildMediaSource(video);


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Code for the UiThread
                    player.setPlayWhenReady(playWhenReady);
                    player.seekTo(currentWindow, playbackPosition);
                    player.prepare(mediaSource, false, false);
                }
            });


            // UI 작업 수행 O






            holder.videoThumbnail.setVisibility(View.INVISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.start();*//*



            MediaController mediacontroller = new MediaController(context);
            mediacontroller.setAnchorView(holder.videoView);
            // Get the URL from String videoUrl            Uri video = Uri.parse(videoUrl);
           // holder.videoView.setMediaController(mediacontroller);

            // holder.videoView.setVisibility(View.VISIBLE);
            //holder.videoView.setVisibility(View.VISIBLE);

            holder.videoView.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    Log.i("비디오", "비디오뷰 준비");
                    mp.setVolume(0, 0);
                    holder.videoView.start();
                    holder.videoThumbnail.setVisibility(View.INVISIBLE);

                    //check =true;
                }
            });

            if(check) {
                Log.i("비디오","비디오뷰 준비완료");
                holder.videoThumbnail.setVisibility(View.INVISIBLE);
                holder.videoView.setVisibility(View.VISIBLE);
                holder.videoView.start();
            }
      }else{
            videoThumbnail.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
            videoView.stopPlayback();


        }
        else {
            //check = false;
            holder.videoThumbnail.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.INVISIBLE);
            holder.videoView.stopPlayback();

            //사용자의 포커스에서 벗어나면 영상을 중지한다.

            if (player != null) {
              playbackPosition = player.getCurrentPosition();
                currentWindow = player.getCurrentWindowIndex();
                playWhenReady = player.getPlayWhenReady();
                player.release();
                //player = null;
            }
        }
            //홀더위치값
            //this.position = position;
            //videoView.
            //videoView.setTag(position);


        }

        //리사이클러뷰에서 아이템의 위치를 파악하고 보여지는 뷰에 영상을 재생하는 메소드

      public void test(VideoDataAdapter.ItemViewHolder holder, int position){
        holder.videoThumbnail.setVisibility(View.INVISIBLE);
        holder.videoView.setVisibility(View.VISIBLE);
        //videoView.setVideoPath(path);
        //videoView.setVideoURI(Uri.parse(path));
        //Log.i("포커스이벤트", "영상경로: " + path);
        //String Url = "http://52.79.240.52/videos/1577768578.mp4";
        //String Url = "http://52.79.240.52/videos/Wolf.mp4";
        videoItems.get(position);

        Uri video = Uri.parse(videoItems.get(position).getVideoName());
        holder.videoView.setVideoURI(video);
        Log.i("클릭","영상경로 : "+ videoItems.get(position).getVideoName());
        //videoView.requestFocus();

        holder.videoView.start();
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

        public void setOnItemClickListener(com.example.youtube.VideoDataAdapter.OnItemClickListener listener)
        {
            this.mListener = listener;
        }

        public SimpleExoPlayer player(Context context) {
            Log.i("어댑터","플레이어 객체생성");
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd());

            return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        }



    //비디오 시작
    public void setVideo(VideoDataAdapter.ItemViewHolder holder){
    this.holder = holder;
    Log.i("비디오","메소드");
        //holder.videoThumbnail.setVisibility(View.INVISIBLE);
        holder.videoView.setVisibility(View.VISIBLE);
        Uri video = Uri.parse(videoItems.get(position).getVideoName());
        holder.videoView.setVideoURI(video);
        holder.videoView.start();
    }

        //테마 아이템뷰 정의 부분
        public class ItemViewHolder extends RecyclerView.ViewHolder {

            private VideoView videoView;
            //private PlayerView videoView;
            private ImageView videoThumbnail;
            private TextView titleView, explainView, censorshipView, firstAlertView;


            public ItemViewHolder(final View itemView) {
                super(itemView);



                videoView = itemView.findViewById(R.id.videoView);
                explainView = itemView.findViewById(R.id.explainView);
                titleView = itemView.findViewById(R.id.titleView);
                videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
                censorshipView = itemView.findViewById(R.id.censorshipView);
                firstAlertView = itemView.findViewById(R.id.firstAlertView);


                //videoThumbnail =  itemView.findViewById(R.id.videoThumbnail);



            //아이템에 포커스가 맞춰졌을때 썸네일에서 영상재생으로 바뀐다.
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    //포커스가 맞춰진경
                    if(hasFocus) {
                        Log.i("포커스이벤트", "포커스이벤트 시작");
                        videoThumbnail.setVisibility(View.INVISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoPath(path);
                        //videoView.setVideoURI(Uri.parse(path));
                        Log.i("포커스이벤트", "영상경로: " + path);
                        String Url = "http://52.79.240.52/videos/1577768578.mp4";
                        //String Url = "http://52.79.240.52/videos/Wolf.mp4";
                        videoItems.get(position);

                        Uri video = Uri.parse(videoItems.get(position).getVideoName());
                        videoView.setVideoURI(video);

                        //videoView.requestFocus();

                        videoView.start();


                //포커스가 나간경
                    }else{
                        Log.i("포커스","포커스 아웃");
                       videoView.stopPlayback();
                        videoThumbnail.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.INVISIBLE);


                    }

                }
            });



                // 아이템 클릭 이벤트 처리.
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();


                    ArrayList<VideoItem> data = new ArrayList<>();

                    //String videoName, String hit, String title, String id, String idx, String subtitle, String tag
                    data.add(0, new VideoItem(videoItems.get(pos).getVideoName(),
                            videoItems.get(pos).getHit(), videoItems.get(pos).getTitle(), videoItems.get(pos).getId(),
                            videoItems.get(pos).getIdx(), videoItems.get(pos).getSubtitle(), videoItems.get(pos).getTag(),videoItems.get(pos).getCensorship(),videoItems.get(pos).getCensorPhoto()));*//*
                    data.add(0, new VideoItem(videoItems.get(pos).getVideoName(),
                            videoItems.get(pos).getHit(), videoItems.get(pos).getTitle(), videoItems.get(pos).getId(),
                            videoItems.get(pos).getIdx(), videoItems.get(pos).getSubtitle(), videoItems.get(pos).getTag(),
                            videoItems.get(pos).getTheFirst(),videoItems.get(pos).getStartTime()));
                                                intent.putParcelableArrayListExtra("data", data);


                        VideoItem videoItem = new VideoItem(videoItems.get(pos).getVideoName(),
                                videoItems.get(pos).getHit(), videoItems.get(pos).getTitle(), videoItems.get(pos).getId(),
                                videoItems.get(pos).getIdx(), videoItems.get(pos).getSubtitle(), videoItems.get(pos).getTag(),
                                videoItems.get(pos).getTheFirst(),videoItems.get(pos).getStartTime());


                        Intent intent = new Intent(context, VideoActivity.class); //수정클래스로 목적지를 정해주고
                        intent.putExtra("data", videoItem);
                        //어느 경로에서 비디오액티비티로 이동했는지 알려준다.
                        intent.putExtra("route", "main");



                        context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


//                    Toast.makeText(context,pos,Toast.LENGTH_LONG).show();



                    }


                });





            }
        }

        // 커스텀 리스너 인터페이스
        public interface OnItemClickListener
        {
            void onItemClick(View v, int pos);
        }


        //비디오 재생 및 정지 메소드




        //비디오 재생에 사용할 미디어 만드는 부분
        private MediaSource buildMediaSource(Uri uri) { DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua"); return new ExtractorMediaSource.Factory(manifestDataSourceFactory).createMediaSource(uri); }
        // @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        exoPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //미디어 소스 초기화
    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(context, "blackJin");

        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);
    }





    }

    */

}
