package com.example.youtube;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoItem implements Parcelable {

    String videoName,videoThumbnail,date,hit,title,id,userThumbnail,start,idx,subtitle,tag;
    //영상검열결과
    String censorship;
    //문제되는 부분 사진
    String censorPhoto;

    //영상 최초공개여부와 최초공개 시간을 저장할 변수
    String theFirst, startTime;


    protected VideoItem(Parcel in) {
        videoName = in.readString();
        videoThumbnail = in.readString();
        date = in.readString();
        hit = in.readString();
        title = in.readString();
        id = in.readString();
        userThumbnail = in.readString();
        start = in.readString();
        idx = in.readString();
        subtitle = in.readString();
        tag = in.readString();
        theFirst = in.readString();
        startTime  = in.readString();
        //censorship = in.readString();
        //censorPhoto = in.readString();
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCensorship() {
        return censorship;
    }

    public void setCensorship(String censorship) {
        this.censorship = censorship;
    }

    public static Creator<VideoItem> getCREATOR() {
        return CREATOR;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserThumbnail() {
        return userThumbnail;
    }

    public void setUserThumbnail(String userThumbnail) {
        this.userThumbnail = userThumbnail;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCensorPhoto() {
        return censorPhoto;
    }

    public void setCensorPhoto(String censorPhoto) {
        this.censorPhoto = censorPhoto;
    }

    public String getTheFirst() {
        return theFirst;
    }

    public void setTheFirst(String theFirst) {
        this.theFirst = theFirst;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /*  public VideoItem(String videoName, String videoThumbnail, String date, String hit, String title, String id, String start, String idx, String subtitle, String tag) {
        this.videoName = videoName;
        this.videoThumbnail = videoThumbnail;
        this.date = date;
        this.hit = hit;
        this.title = title;
        this.id = id;
        this.start = start;
        this.idx = idx;
        this.subtitle = subtitle;
        this.tag = tag;

    }*/

   /* public VideoItem(String videoName, String hit, String title, String id, String idx, String subtitle, String tag) {
        this.videoName = videoName;
        this.hit = hit;
        this.title = title;
        this.id = id;
        this.idx = idx;
        this.subtitle = subtitle;
        this.tag = tag;
    }

    public VideoItem(String videoName, String hit, String title, String id, String idx, String subtitle, String tag) {
        this.videoName = videoName;
        this.hit = hit;
        this.title = title;
        this.id = id;
        this.idx = idx;
        this.subtitle = subtitle;
        this.tag = tag;
        //this.censorship = censorship;
        //this.censorPhoto = censorPhoto;
    }
*/

    public VideoItem(String videoName, String hit, String title, String id, String idx, String subtitle, String tag, String theFirst, String startTime) {
        this.videoName = videoName;
        this.hit = hit;
        this.title = title;
        this.id = id;
        this.idx = idx;
        this.subtitle = subtitle;
        this.tag = tag;
        this.theFirst = theFirst;
        this.startTime = startTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoName);
        dest.writeString(videoThumbnail);
        dest.writeString(date);
        dest.writeString(hit);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeString(userThumbnail);
        dest.writeString(start);
        dest.writeString(idx);
        dest.writeString(subtitle);
        dest.writeString(tag);
        dest.writeString(theFirst);
        dest.writeString(startTime);
        //dest.writeString(censorship);
        //dest.writeString(censorPhoto);

    }
}
