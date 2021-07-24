package com.example.youtube;

//댓글의 아이템에는 사용자의 아이디와 내용을 저장하고, 좋아요와 유튜버가 선정한 채팅에 대한 정보를 관리한다.
//좋아요를 구분하기 위해서는 채팅입장한 시간마다 채팅내용의 인덱스가 다르기때문에 내용과 시간으로 관리한한다.
//시간은 타임스탬프로 찍고, String형태로 저장하고 다시 찾아 비교한다.

public class ChatItem {

    String id,content,timeStamp;
    int like;
    Boolean select;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }


    public ChatItem(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public ChatItem(String id, String content, String timeStamp, int like, Boolean select) {
        this.id = id;
        this.content = content;
        this.timeStamp = timeStamp;
        this.like = like;
        this.select = select;
    }
}
