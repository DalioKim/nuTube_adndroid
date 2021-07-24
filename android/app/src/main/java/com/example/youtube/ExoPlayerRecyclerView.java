package com.example.youtube;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import android.util.Log;
import com.example.youtube.MediaRecyclerAdapter.PlayerViewHolder;

import java.util.ArrayList;
import java.util.Objects;

import com.example.youtube.VideoDataAdapter.ItemViewHolder;

//사용자가 해당 비디오에 포커스가 맞춰질때마다, 영상목록의 비디오뷰를 엑소플레이어로 바꿔주어 실행해주는 리사이클러뷰 클래스
//비디오의 각 아이템의 뷰에 프레임레이아웃을 선언하고, 프레임레이아웃을 컨트롤

public class ExoPlayerRecyclerView extends RecyclerView {
    private static final String TAG = "ExoPlayerRecyclerView";
    private static final String AppName = "Android ExoPlayer";
    /**
     * PlayerViewHolder UI component
     * Watch PlayerViewHolder class
     */
    private ImageView mediaCoverImage, volumeControl;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private FrameLayout mediaContainer;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;
    /**
     * variable declaration
     */
    // Media List
    //private ArrayList<MediaObject> mediaObjects = new ArrayList<>();

    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    //private ArrayList<VideoItem> videoItems;

    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;
    // controlling volume state
    private VolumeState volumeState;
  /* private OnClickListener videoViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //toggleVolume();




        }
    };*/
    public ExoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }
    public ExoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    //리사이클러뷰 객체개 생선되면 초기화해주는 메소드
    //context와 point 객체를 이용하여, 엑소플레이어로 바꿔줄 뷰의 좌표값을 얻는다.
    //비디오surface는 프레임레이아웃의 종횡비를 컨트롤 할 수 있다.

    //리사이클러뷰 상속 메소드인 스크롤리스너를 통해, 사용자가 디바이스를 통해 보고 있는 영상목록의 인덱스를 얻어낸다.


    private void init(Context context) {
        Log.i(TAG,"초기화");

        this.context = context.getApplicationContext();

                //디바이스의 실제화면크기를 구하는 부분
        Display display = ((WindowManager) Objects.requireNonNull(
                getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);


        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;
        videoSurfaceView = new PlayerView(this.context);

        //로세로화면 크기조정
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        //Create the player using ExoPlayerFactory
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // Disable Player Control
        videoSurfaceView.setUseController(false);
        // Bind the player to the view.
        videoSurfaceView.setPlayer(videoPlayer);
        // Turn on Volume
        //볼륨을 꺼놓는다.
        setVolumeControl(VolumeState.OFF);


        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);



                //스크롤상태가 변화하면 2초후에 영상목록의 영상들을 재생하거나 다시 썸네일로 바꿔준다.





                //기존의 스크롤할때마다 호출되는 것과 달리, 스크롤하지 않는 상태 일경우 호출된다.
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                   /* //현재시각을 저장하고 2초후의 값과 비교하기 위해 변수를 선언한다.
                    long before = System.currentTimeMillis();
                    long after = before+2000;

                    try {
                        Thread.sleep(1000) ;
                        before +=1000;
                    } catch (Exception e) {
                        e.printStackTrace() ;
                    }*/

                    Log.i(TAG,"스크롤변화 감지");
                    if (mediaCoverImage != null) {
                        // show the old thumbnail
                        mediaCoverImage.setVisibility(VISIBLE);
                    }


                    //2초동안 스크롤감지가 되지 않으면 화면을 틀어준다.
                    //if(before == after) {

                        // There's a special case when the end of the list has been reached.
                        // Need to handle that with this bit of logic
                        //리사이클러뷰상에서 최하단일경우

                        if (!recyclerView.canScrollVertically(1)) {
                            playVideo(true);

                            //최하단이 아닌경우
                        } else {
                            playVideo(false);
                        }

                    }

             //   }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
            }
            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                //보여지는 뷰와 지정되었던 뷰가 일치되지 않으면 영상재생을 중지하고, 썸네일 화면으로 돌린다.
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });



        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups,
                                        TrackSelectionArray trackSelections) {
            }
            @Override
            public void onLoadingChanged(boolean isLoading) {
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (progressBar != null) {
                            progressBar.setVisibility(VISIBLE);
                        }
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
                        break;
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (progressBar != null) {
                            progressBar.setVisibility(GONE);
                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }
            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }
            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }
            @Override
            public void onPositionDiscontinuity(int reason) {
            }
            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }
            @Override
            public void onSeekProcessed() {
            }
        });


    }

    // TODO: 2020-04-03 영상목록에서 포커스 받는 영상을 재생시켜주는 부분
    //2가지 경우의 수
    public void playVideo(boolean isEndOfList) {
        int targetPosition;



        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(
                    getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }
            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }
            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
                targetPosition =
                        startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }


        } else {
            targetPosition = videoItems.size() - 1;
        }
        Log.d(TAG, "playVideo: target position: " + targetPosition);
        // video is already playing so return
        if (targetPosition == playPosition) {
            return;
        }
        // set the position of the list-item that is to be played
        playPosition = targetPosition;
        if (videoSurfaceView == null) {
            return;
        }
        // remove any old surface views from previously playing videos
        videoSurfaceView.setVisibility(INVISIBLE);
        removeVideoView(videoSurfaceView);


        // TODO: 2020-04-03 포커스받고 있는 아이템의 포지션값을 받아오고 영상재생을 시작하는 부분
        //현재 리사이클러뷰상에서 포커스 받고있는 아이템의 포지션값을 받는다.
        //포지션값을 통해서 해당 포지션의 holder객체를 생성한다.
        //holder객체의 뷰와 해당 포지션의 아이템에 저장된 데이터를 이용해서 영상을 재생한다.

       //현재 포지션을 얻어온다.
        int currentPosition =
                targetPosition - ((LinearLayoutManager) Objects.requireNonNull(
                        getLayoutManager())).findFirstVisibleItemPosition();



        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }



        PlayerViewHolder holder = (PlayerViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }


        mediaCoverImage = holder.mediaCoverImage;
        progressBar = holder.progressBar;
        viewHolderParent = holder.itemView;
        mediaContainer = holder.mediaContainer;
        videoSurfaceView.setPlayer(videoPlayer);
        //viewHolderParent.setOnClickListener(videoViewClickListener);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, AppName));
        //String mediaUrl = mediaObjects.get(targetPosition).getUrl();

        //영상아이템의 영상url 주소를 얻어 미디어url변수에 저장한다.
        String mediaUrl = videoItems.get(targetPosition).getVideoName();



        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
        }




    }
    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     */



    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(
                getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);
        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }
        int[] location = new int[2];
        child.getLocationInWindow(location);
        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }
    // Remove the old player
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }
    private void addVideoView() {
        mediaContainer.addView(videoSurfaceView);
        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
        mediaCoverImage.setVisibility(GONE);
    }

    //
    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            videoSurfaceView.setVisibility(INVISIBLE);
            mediaCoverImage.setVisibility(VISIBLE);
        }
    }
    public void releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        viewHolderParent = null;
    }
    public void onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer.stop(true);
        }
    }

    /*private void toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
            }
        }
    }
    */
    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
            videoPlayer.setVolume(1f);
            animateVolumeControl();
        }
    }
    private void animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl.bringToFront();
            if (volumeState == VolumeState.OFF) {
             /*   requestManager.load(R.drawable.ic_volume_off)
                        .into(volumeControl);*/
            } else if (volumeState == VolumeState.ON) {
               /* requestManager.load(R.drawable.ic_volume_on)
                        .into(volumeControl);*/
            }
            volumeControl.animate().cancel();
            volumeControl.setAlpha(1f);
            volumeControl.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
        }
    }
    public void setMediaObjects(ArrayList<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }
    /**
     * Volume ENUM
     */
    private enum VolumeState {
        ON, OFF
    }
}
