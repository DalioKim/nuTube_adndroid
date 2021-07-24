package com.example.youtube;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadLiveActivity extends AppCompatActivity {



    private static String TAG = "UploadLiveActivity";


    private static final int pickImageRequest = 2;


    //디바이스상의 썸네일 경로를 저장할 변수
    Uri thumbnailUri;
    //절대경로를 저장하는데 사용
    String thumbnailPath;


    //썸네일 uri를 담을 파일 객체
    File thumbnailFile;

    ImageView thumbnailView;
    EditText TitleEt;

    String title;


    MultipartBody.Part imagePart;

    String id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_live);


        //위젯연결
        thumbnailView = findViewById(R.id.thumbnailView);
        TitleEt = findViewById(R.id.TitleEt);

        //shared에 저장된 아이디를 들고온다.
        final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE); //SharedPreferences를 선언
        id = Shared.getString("id", null);
        Log.i(TAG, "shared에 저장된 데이터 확인 : "+ Shared.getString("id", null));


        //썸네일 지정하는 버튼 클 이벤트
        Button thumbnailBtn = (Button) findViewById(R.id.thumbnailBtn);
        thumbnailBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Log.i(TAG,"sdk버전 19이상으로 갤러리앱을 연다.");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, pickImageRequest);



            }
        });



        //라이브방송시작하기 버튼 클릭 이벤트

        Button liveStart = (Button) findViewById(R.id.liveStart);
        liveStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //레트로핏을 통해 서버에 라이브방송 정보를 업로드하고 방송하는 액티비티로 이동한다.



                title = TitleEt.getText().toString();

                //레트로핏 객체 생성
                Retrofit retrofit = UploadRetrofit.getRetrofitClient(UploadLiveActivity.this);
                RetrofitService uploadAPIs = retrofit.create(RetrofitService.class);

                //지정한 썸네일 이미지 생성
                RequestBody imageReqBody = RequestBody.create(MediaType.parse("image/*"), thumbnailFile);
                imagePart = MultipartBody.Part.createFormData("imageFile", thumbnailFile.getName(), imageReqBody);

                RequestBody imageDescription = RequestBody.create(MediaType.parse("text/plain"), "image-type");
                Log.i("test", "insertPromote: "+ thumbnailFile.getName());
                Log.i("test", "insertPromote: "+ imageReqBody.contentType());
                Log.i("test", "insertPromote: "+ imagePart.body());


                //이미지를 업로드하는 레트로핏 객체를 생성
                Call<result> call = uploadAPIs.startLive(imagePart,imageDescription,title,id);


                //Call<result> call = uploadAPIs.testVideo(videoPart,videoDescription);
                call.enqueue(new Callback<result>() {

                    @Override
                    public void onResponse(Call<result> call, Response<result> response) {

                        Log.i(TAG,"응답메소드 호출");

                        if (response.isSuccessful()) {



                            Toast.makeText(getApplicationContext(), id + "님 영상이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                            // String censorship = response.body().getResponse();
                            // Log.i(TAG,"서버 저장된 파일명:"+censorship);
                            //flask(censorship);
                            //new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);


                            //서버에 등록이 되면
                            Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                            //사용자가 설정한 방제를 실어서 해당액티비티로 보낸다.
                            intent.putExtra("title",title);
                            startActivity(intent);
                            finish();


                        }else{

                            Toast.makeText(getApplicationContext(), id + "님 영상이 업로드 실패.", Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onFailure(Call<result> call, Throwable t) {
                        Log.d("tag", t.toString());
                        Log.i(TAG,"fail");
                        Toast.makeText(getApplicationContext(), "영상업로드 제대로 되지 않았습니다.", Toast.LENGTH_SHORT).show();

                    }
                });






            }
        });







    }




    //썸네일 결과값가져오는 메소드
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == pickImageRequest){
            try {

                if (Build.VERSION.SDK_INT > 19) {
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Log.i(TAG, "sdk19이상버전으로써 사진을 가져옴");


                    thumbnailUri = data.getData();
                    Log.i(TAG, "썸네일의 상대 경로는 : " + thumbnailUri);


                    thumbnailPath = getPath(this, thumbnailUri); // "/mnt/sdcard/FileName.mp3"

                    Log.i(TAG, "썸네일의 절대 경로는 : " + thumbnailPath);


                    //가져온 영상의 썸네일을 추출한다
                    if (thumbnailUri != null) {
                        thumbnailFile = new File(thumbnailPath);
                        Log.i(TAG, "썸네일이미지 파일객체화 : " + thumbnailFile);

                        thumbnailView.setImageURI(thumbnailUri);
                    }

                    try {
                        getApplicationContext().getContentResolver().takePersistableUriPermission(thumbnailUri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "썸네일이 선택되지 않았습니다", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }



        }







    }



    //절대경로 얻는 메소드
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        Log.i(TAG,"getpath메소드 실행");
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }





}
