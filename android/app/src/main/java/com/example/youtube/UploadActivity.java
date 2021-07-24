package com.example.youtube;



//todo 사용할 변수 및 객체
/*
1.영상파일을 저장하는 변수 videoFile
2.영상의 썸네일을 저장하는 변수 t져umbnailFile
3.제목,설명,태그를 저장하는 변수 title,explain,tag
4.사용자의 id를 저장할 변수 id
5.영상가져오기 버튼 getVideoBtn
6.썸네일 지정하기 버튼 thumbnailBtn
7.제목/설명/태그 에디트뷰 객체 titleView/explainView/tagView
8.썸네일 지정 버튼 thumbnailBtn
9.썸네일을 보여주는 이미지 뷰 thumbnailView
10.요청코드 변수
11.영상과 썸네일의 uri를 저장할 변수
12.영상과 썸네일파일의 절대경로를 저장할 변수

 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.provider.MediaStore.Images.Thumbnails;

import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;
import java.util.Calendar;
import android.app.TimePickerDialog.OnTimeSetListener;
import androidx.appcompat.app.AlertDialog;

public class UploadActivity extends AppCompatActivity {

    private static String TAG = "UploadActivity";

    //다이얼로그
    ProgressDialog progressDialog;


    String id,title,explain,tag;
    EditText titleView,explainView,tagView;
    ImageView thumbnailView;

    Uri videoUri,thumbnailUri;
    String videoPath, thumbnailPath;  //이미지의 절대경로를 저장하는데 사용
    File videoFile;

    //최초공개기능에 대한 설명을 알려줄 뷰
    TextView inforView;









    //영상 요청코드 변수 선언
    private static final int pickVideoRequest = 1;

    //영상 요청코드 변수 선언
    private static final int pickImageRequest = 2;

    //썸네일 uri를 담을 파일 객체
    File thumbnailFile;

    //썸네일 추출의 경우 파일 객체
    File tempFile;

    MultipartBody.Part imagePart;

    //영상 최초공개 설정여부를 저장하는 변수
    Boolean theFirst = false;

    String startTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


//쉐어드에 저장한 회원의 아이디를 가져온다.
        final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE); //SharedPreferences를 선언
        id = Shared.getString("id", null);
        Log.i(TAG, "shared에 저장된 데이터 확인 : "+ Shared.getString("id", null));
        //임시로 사용자아이디에 변수값을 저장

        thumbnailView = findViewById(R.id.thumbnailView);
        titleView = findViewById(R.id.titleView);
        explainView = findViewById(R.id.explainView);
        tagView = findViewById(R.id.tagView);

        inforView = findViewById(R.id.inforView);

        //timePicker


        //todo 버튼이벤트들
        //영상가져오기 버튼  이벤트
        Button getVideoBtn = (Button) findViewById(R.id.getVideoBtn);
        getVideoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                    Log.i(TAG,"sdk버전 19이상으로 갤러리앱을 연다.");
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("video/*");
                    startActivityForResult(intent, pickVideoRequest);



            }
        });


        //썸네일 바꾸기 버튼  이벤트
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


        inforView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


         //다이얼로그를 통해 최초공개 기능에 대해 설명해준다.
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

                final ImageView imageView = (ImageView)dialogView.findViewById(R.id.imageView);

                Glide.with(UploadActivity.this).load(R.drawable.explanation).into(imageView);

                AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                builder.setView(dialogView);
                builder.setTitle("최초공개 기능 설명");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int pos)
                    {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();



            }
        });



        //업로드 버튼을 누르면

        //썸네일 가져오기 버튼  이벤트
        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //로딩바
                new android.os.Handler().postDelayed( new Runnable() { public void run() {  progressDialog = new ProgressDialog(UploadActivity.this); progressDialog.setIndeterminate(true); progressDialog.setMessage("잠시만 기다려 주세요"); progressDialog.show(); } }, 0);


                Log.i(TAG,"업로드 메소드 실행");
             //제목,설명,태그에 입력된 값을 변수에 저장한다.
                title = titleView.getText().toString();
                explain = explainView.getText().toString();
                tag = tagView.getText().toString();

                //영상절대경로,썸네일절대경로,제목,설명,태그,아이디 변수의 널값체크를 한다.
               // if(videoUri!=null && thumbnailUri !=null && !title.equals("")&& !explain.equals("")
                 //       && !tag.equals("")&& !id.equals("")){

                    //레트로핏 객체를 통해 서버로 전송한다.

                    Retrofit retrofit = UploadRetrofit.getRetrofitClient(UploadActivity.this);
                    RetrofitService uploadAPIs = retrofit.create(RetrofitService.class);

                    //영상파일 객체 생성
                    if(videoPath!=null) {
                        videoFile = new File(videoPath);

                        Log.i(TAG, "파일 사이즈 :" + videoFile.length());
                    }
                    //썸네일 파일 객체 생성
                    //File thumbnailFile = new File(thumbnailPath);

                    // Create a request body with file and image media type
                    RequestBody videoReqBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
                    // Create MultipartBody.Part using file request-body,file name and part name
                    MultipartBody.Part videoPart = MultipartBody.Part.createFormData("videoFile", videoFile.getName(), videoReqBody);

                    try {
                        Log.i(TAG,"영상파일 사이즈 :"+videoReqBody.contentLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "영상파일이름: "+ videoFile.getName());
                    Log.i(TAG, "영상파일타입: "+ videoReqBody.contentType());
                    Log.i(TAG, "영상파트 바디: "+ videoPart.body());


                    //Create requst body with text description and text media type
                    RequestBody videoDescription = RequestBody.create(MediaType.parse("text/plain"), "video-type");

                    //썸네일 추출일 경우
                    if(tempFile!=null){
                        RequestBody imageReqBody = RequestBody.create(MediaType.parse("image/*"), tempFile);
                        imagePart = MultipartBody.Part.createFormData("imageFile", tempFile.getName(), imageReqBody);
                        Log.i("test", "insertPromote: "+ thumbnailFile.getName());
                        Log.i("test", "insertPromote: "+ imageReqBody.contentType());
                        Log.i("test", "insertPromote: "+ imagePart.body());
                    }else {

                        RequestBody imageReqBody = RequestBody.create(MediaType.parse("image/*"), thumbnailFile);
                        imagePart = MultipartBody.Part.createFormData("imageFile", thumbnailFile.getName(), imageReqBody);

                    }


                    RequestBody imageDescription = RequestBody.create(MediaType.parse("text/plain"), "image-type");



                    //이미지를 업로드하는 레트로핏 객체를 생성
                    Call<result> call = uploadAPIs.uploadVideo(videoPart,videoDescription,imagePart,imageDescription,title,explain,tag,id,theFirst,startTime);


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
                              new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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




//            }
        });



        // 스위치 버튼입니다.
        SwitchButton switchButton = (SwitchButton) findViewById(R.id.switch1);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked){
                    init();
                    theFirst = true;
                   // timePicker.setVisibility(View.VISIBLE);

                }else{
                    theFirst = false;

                    //timePicker.setVisibility(View.INVISIBLE);

                }
            }
        });



            }


    //영상/썸네일 결과값가져오는 메소드
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //영상일경우
        if(requestCode == pickVideoRequest) {
            try {

                if (Build.VERSION.SDK_INT > 19) {
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Log.i(TAG, "sdk19이상버전으로써 영상을 가져옴");


                    videoUri = data.getData();
                    Log.i(TAG,"영상의 상대경로는 "+videoUri);

                    if(videoUri !=null) {
                        videoPath = getPath(this, videoUri); // "/mnt/sdcard/FileName.mp3"
                        Log.i(TAG, "영상의 절대 경로는 : " + videoPath);
                    }

                    //가져온 영상의 썸네일을 추출한다
                    if (videoPath != null) {
                        Log.i(TAG,"썸네일 추출");
                        //비트맵을 추출하고, 추출한 비트맵을 썸네일파일 객체 에 담는다.
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(String.valueOf(videoPath), Thumbnails.FULL_SCREEN_KIND);
                        Log.i(TAG,"썸네일 비트맵 변수:"+String.valueOf(bitmap));


                        //내부저장소 캐시 경로를 받아옵니다.
                        File storage = getCacheDir();
                        Log.i(TAG,"캐시경로:"+getCacheDir());
                        //저장할 파일 이름
                        String fileName = "temp" + ".jpg";

                        //storage 에 파일 인스턴스를 생성합니다.
                        tempFile = new File(storage, fileName);

                        try {

                            // 자동으로 빈 파일을 생성합니다.
                            tempFile.createNewFile();

                            // 파일을 쓸 수 있는 스트림을 준비합니다.
                            FileOutputStream out = new FileOutputStream(tempFile);

                            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                            // 스트림 사용후 닫아줍니다.
                            out.close();

                        } catch (FileNotFoundException e) {
                            Log.e(TAG,"FileNotFoundException : " + e.getMessage());
                        } catch (IOException e) {
                            Log.e(TAG,"IOException : " + e.getMessage());
                        }
                        Log.i(TAG,"비트맵파일:"+tempFile);


                        //캐시파일 불러오기
                        File file = new File(getCacheDir().toString());
                        File[] files = file.listFiles();

                        for(File TempFile : files) {

                            Log.i(TAG,"캐쉬파일내 파일:"+TempFile.getName());

                        }


                        //thumbnailFile = new File(String.valueOf(bitmap));
                        Log.i(TAG,"썸네일 비트맵파일화 :"+String.valueOf(thumbnailFile));
                        thumbnailView.setImageBitmap(bitmap);
                    }

                    try {
                        getApplicationContext().getContentResolver().takePersistableUriPermission(videoUri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "영상이 선택되지 않았습니다", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            //썸네일을 요청했을 경우
        } else if(requestCode == pickImageRequest){
            try {

                if (Build.VERSION.SDK_INT > 19) {
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Log.i(TAG, "sdk19이상버전으로써 사진을 가져옴");


                    thumbnailUri = data.getData();

                    thumbnailPath = getPath(this, thumbnailUri); // "/mnt/sdcard/FileName.mp3"

                    Log.i(TAG, "썸네일의 절대 경로는 : " + thumbnailPath);


                    //가져온 영상의 썸네일을 추출한다
                    if (thumbnailUri != null) {
                        thumbnailFile = new File(thumbnailPath);
                        Log.i(TAG, "썸네일이미지 파일객체화 : " + thumbnailFile);

                        thumbnailView.setImageURI(thumbnailUri);
                    }

                    try {
                        getApplicationContext().getContentResolver().takePersistableUriPermission(videoUri, takeFlags);
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


    //todo  요청


    public void flask(String censorship){

        Log.i(TAG,"flask실행");

        FlaskApiService Service = new RetrofitFlask().getRetrofit();


        //todo 검사를 요청할 영상의 mp4명릏 보낸다.
        Call<result> itemResponse = Service.request(censorship);


        itemResponse.enqueue(new Callback<result>() {
            @Override
            public void onResponse(Call<result>call, Response<result> response) {


                Log.i(TAG, "검열성공");


                //if(response.isSuccessful()){
                if (response.body().getResponse().equals("true")) {
                    new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);


                    //Toast.makeText(getApplicationContext(), id + "님 영상이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), response.body().getResponse(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"플라스크파일명:"+response.body().getResponse());
                    //영상의 선정성 판단 여부를 확인 받는다.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();




                }
                else{
                    new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);

                    Toast.makeText(getApplicationContext(), id + "님 영상이 수익제한 판정을 받으셨습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onFailure(Call<result> call, Throwable t) {


          new android.os.Handler().postDelayed( new Runnable() { @Override public void run() { progressDialog.dismiss(); } }, 0);


                Log.i(TAG, "요청 실");
                Log.d(TAG, t.toString());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }


    private void init(){

        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Toast.makeText(UploadActivity.this,
                                "설정하신 시간은 " + hourOfDay + ":" + minute+" 입니다.", Toast.LENGTH_SHORT)
                                .show();

                        startTime = hourOfDay + ":" + minute;
                    }



                };
        TimePickerDialog alert = new TimePickerDialog(this,
                mTimeSetListener, 0, 0, false);
        alert.show();


    }







}
