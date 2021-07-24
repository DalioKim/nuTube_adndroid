package com.example.youtube;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {


    //part는 filed와 달리 데이터를 직렬화하여 전송한다.
    @Multipart
    @POST("video.php")
    //Call<result> uploadImage(@Part MultipartBody.Part file, @Part("file") RequestBody requestBody);
    Call<result> uploadVideo(@Part MultipartBody.Part videoFile, @Part("videoFile") RequestBody videoReqBody,
                             @Part MultipartBody.Part imageFile, @Part("imageFile") RequestBody imageReqBody,
                             @Part("title")  String title, @Part("explain")  String explian, @Part("tag")  String tag,@Part("id")  String id
                              ,@Part("theFirst")  Boolean theFirst, @Part("startTime")  String startTime  );


    //part는 filed와 달리 데이터를 직렬화하여 전송한다.
    @Multipart
    @POST("video.php")
    //Call<result> uploadImage(@Part MultipartBody.Part file, @Part("file") RequestBody requestBody);
    Call<result> testVideo(@Part MultipartBody.Part videoFile, @Part("videoFile") RequestBody videoReqBody);




    //메인화면 영상목록  요청 메소드
    @FormUrlEncoded
    @POST("main_video.php")
    Call<JsonResponse> requestMain(@Field("request") String request);


    //댓글 요청
    @FormUrlEncoded
    @POST("comment.php")
    Call<JsonResponse> requestComment(@Field("idx") String idx);

    //선택한 특정 언어의 댓글 요청
    @FormUrlEncoded
    @POST("languageComment.php")
    Call<JsonResponse> languageComment(@Field("idx") String idx,@Field("language") String language);

    //댓글등
    @FormUrlEncoded
    @POST("upload_comment.php")
    Call<JsonResponse> uploadComment(@Field("id") String id,@Field("content") String content,@Field("videoIdx") String videoIdx);

    //최초공개영상 알람푸쉬등록
    @FormUrlEncoded
    @POST("push_notification.php")
    Call<result> requestPush(@Field("apiKey") String apiKey,@Field("token") String token,@Field("title") String title,@Field("startTime") String startTime);




    //라이브영상을 시작과 관련된 정보를 올리는 레트로핏 메소드

    @Multipart
    @POST("startLive.php")
        //Call<result> uploadImage(@Part MultipartBody.Part file, @Part("file") RequestBody requestBody);
    Call<result> startLive(@Part MultipartBody.Part imageFile, @Part("imageFile") RequestBody imageReqBody,
                             @Part("title")  String title, @Part("id")  String id);

}
