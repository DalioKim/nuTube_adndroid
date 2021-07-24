package com.example.youtube;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.app.Notification;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import android.os.Handler;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    //서버로부터 전달받은 푸쉬내용을 반영할 변수
    String title;
    String body;
    Context context = this;


    //커스텀 푸쉬
    public RemoteViews contentView;
    Notification notification;

    Uri video;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "data: " +         remoteMessage.getData()
        );
        Log.d(TAG, "body: " +         remoteMessage.getData().get("body")
        );


        title =  remoteMessage.getData().get("title");
        body =  remoteMessage.getData().get("body");


        sendNotification(title,body);
        // Check if message contains a data payload.
       // if (remoteMessage.getData().size() > 0) {
        if(remoteMessage.getNotification() !=null){
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //sendNotification(remoteMessage.getNotification().getBody());


           /* if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.



    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {

        Log.d(TAG, "푸쉬메소드 진입");


      /*  try {
            Thread.sleep(5000); // 1초간 Thread를 잠재운다
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.d(TAG, "sleep 종료");


        Intent intent = new Intent(this, VideoActivity.class);
        //알람설정으로 들어가는 경우
        intent.putExtra("route", "push");


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        /*contentView = new RemoteViews(getPackageName(), R.layout.notify);


        NotificationTarget notificationTarget = new NotificationTarget(
                        context,
                R.id.thumbnailView,
                contentView,
                        notification,
                        0);*/

        //shared에 저장된 비디오아이템의 썸네일주소를 가져온다.
        final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = Shared.edit();
        String share = Shared.getString("videoItem", null);

        //알람설정으로 액티비티에 들어온 경우
        if(share !=null){
            Log.i(TAG,"알람설정으로 진입");
            Gson gson = new Gson();
            String json = Shared.getString("videoItem", null);

            VideoItem videoItem  = gson.fromJson(json, VideoItem.class);


            video = Uri.parse(videoItem.getVideoName());



        }

        /*
        Glide.with(getApplicationContext())
                .load()
                .asBitmap()
                .into( notificationTarget );*/


/*
        contentView.setTextViewText(R.id.titleView, title);
        contentView.setTextViewText(R.id.bodyView, body);
        contentView.setOnClickPendingIntent(R.layout.notify, pendingIntent);
*/




        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_live_tv_black_24dp)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());



/*
        //NotificationManagerを取得
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//Intentを生成
        Intent intent2 = new Intent(this, VideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent2,
                PendingIntent.FLAG_ONE_SHOT);

//カスタムレイアウトを使う場合は、RemoteViewsを使用する
//今回は、TextView ×2 と ImageViewのレイアウト

        contentView = new RemoteViews(getPackageName(), R.layout.notify);

        contentView.setTextViewText(R.id.titleView, title);
        contentView.setTextViewText(R.id.bodyView, body);

//Notification インスタンスを生成
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Ticker")
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContent(contentView)
                .setContentIntent(pendingIntent2)
                .setAutoCancel(true)
                .build();

//idはなんでも良い
        manager.notify(1, notification);

//☆ここからがGlideの処理 =========================================

//NotificationTarget インスタンスを生成する
        NotificationTarget notificationTarget = new NotificationTarget(
                context,
                R.id.thumbnailView,
                contentView,
                notification,
                1);

//StringのURLをUriクラスにパースする

//Gride 4.1.1




        new Handler(Looper.getMainLooper()).post(new Runnable(){
            public void run(){
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(video)
                        .into( notificationTarget );
            }
        });*/

    }
}
