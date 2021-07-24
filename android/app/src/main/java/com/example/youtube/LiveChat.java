package com.example.youtube;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

//1대 1채팅의 서버
public class LiveChat {


    //채팅방 채팅자 명단
    static HashMap<String, ArrayList<ServerChatter>> hash;

    //채팅방 채팅 내용
    static HashMap<String, ArrayList<ChatItem>> hashChat;

    static HashMap<String, String> hashfavorite;



    public static void main(String[] args) {


        // 채팅방별로 관리하기
        hash = new HashMap<String, ArrayList<ServerChatter>>();
        hashChat = new HashMap<String, ArrayList<ChatItem>>();
        //각 방 별 인기채팅을 저장할 해쉬
        hashfavorite = new HashMap<String, String>();


        //새방이 생길때마다 채팅방명단 채팅방의 채팅을 저장할 리스틀 생성
        //ArrayList<ServerChatter> chatters = new ArrayList<ServerChatter>();
        ArrayList<ServerChatter> chatters = null;
        ArrayList<ChatItem> chatItems = null;



        // 서버소켓 객체 선언
        ServerSocket serverSocket = null;
        Socket socket = null;

        // 접속된 순서 번호
        String roomNum;
        ServerChatter chatter = null;
        try{
            // 서버소켓 생성
            serverSocket = new ServerSocket(5000);
            while(true){
                System.out.println("***********클라이언트 접속 대기중*************");
                socket = serverSocket.accept();

                // 채팅 객체 생성
                chatter = new ServerChatter(socket,hash,hashChat,hashfavorite);
                //chatter.login();  // 대화명 입력 처리
                roomNum = chatter.login();
                System.out.println("방번호 : "+roomNum);

                //방이 존재하는지 여부를 hash에서 확인한다.
                //이미 방이 존재하면
                if(hash.containsKey(roomNum)){
                    //해당 이름의 방의 소켓리스트에 소켓을 추가한다.
                    synchronized (hash) {

                        //  chatters = //new ArrayList<>(Arrays.asList(new ServerChatter (hash.get(roomNum))));
                        //  new ArrayList<>(Arrays.asList(new ServerChatter(hash.get(roomNum))));


                        //chatters = new ArrayList<>(hashmap.values(hash.get(roomNum)));
                        //chatters = ArrayList<ServerChatter> hash.get(msg.split("/")[0].replace("a", "p"));


                        //ArrayList<ServerChatter> chatters = new ArrayList<ServerChatter>(hash.get(roomNum));
                        //  chatters =  <ServerChatter>Arrays.asList(hash.get(roomNum));
                        //  chatters.add(chatter);

                        //채팅자 명단에 새로 들어온 채팅자를 추가하는 작업
                        chatters = hash.get(roomNum);


                        System.out.println("추가 전 채팅 참여자 수"+chatters.size());
                        chatters.add(chatter);
                        chatter.no = chatters.size()-1;
                        System.out.println("추가 후 채팅 참여자 수"+chatters.size());
                        chatters.get(chatter.no).start();

                        //방의 채팅내용이 있으면 보내준다,
                        chatItems = hashChat.get(roomNum);
                        if(chatItems.size() > 0){
                            System.out.println("채팅내용 존재"+chatters.size());

                            //반복문으로 string에 저장
                            //String totalchat ="";
                            StringBuilder totalchat = new StringBuilder();

                            for(int i = 0; i<chatItems.size(); i++){

                                totalchat.append("@@"+chatItems.get(i).getId()+":"+chatItems.get(i).getContent());


                            }
                            String singleString = totalchat.toString();

                            //다 합친내용을 보낸다.
                            chatter.sendTotal(singleString);
                            System.out.println("전체채팅내용 보내기:"+totalchat);


                        }

                        //베스트 채팅을 받아온다.
                        String bestMsg = hashfavorite.get(roomNum);
                        System.out.println("베스트 채팅:"+bestMsg);

                        if(!bestMsg.equals("")){

                            chatter.sendBest(bestMsg);
                        }

                    }



                }else{

                    System.out.println("새 채팅방 시작 ");

                    //새로운 방의 채팅참여자를 관리할 리스트를 생성하고 추가한다.
                    chatters = new ArrayList<ServerChatter>();
                    chatItems = new ArrayList<ChatItem>();
                    hashfavorite = new HashMap<String, String>();

                    //새로운 방의 채팅내용을 관리할 리스트를 생성하고 추가한다.
                    //채팅아이템

                    // 채팅 객체를 ArrayList에 저장한다.
                    chatters.add(chatter);

/*
      if(chatters.size()<2){
      chatters.add(chatter);
      chatter.no = chatters.size()-1;
}*/


                    synchronized (hash) {

                        //해당 방에 채팅명단과 채팅내용을 저장할 리스트를 추가
                        hash.put(roomNum, chatters);
                        hashChat.put(roomNum, chatItems);
                        //Best 채팅의 기본값을 ""값으로 설정해준다.
                        hashfavorite.put(roomNum,"");

                        chatters.get(chatter.no).start();


                    }


                }




            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                serverSocket.close();
            }catch(IOException e){
            }
        }
    }
}

// 소켓을 이용하여 클라이언트 1개와 직접 연결되어 있고
// ArrayList<> 인 chatters에 소속되어있는 또다른 소켓과 데이타를 주고받는 쓰래드 클래스
class ServerChatter extends Thread{
    // 클라이언트와 직접 연결되어 있는 소켓

    Socket socket;
    BufferedReader br; // 소켓으로부터의 최종 입력 스트림
    PrintWriter pw;  // 소켓으로부터의 최종 출력 스트림

    // 현재 서버에 접속된 전체 클라이언트 정보가 저장되어 있다.
    // 이들중 1개의 클라이언트와 채팅을 한다(1대1채팅)
    ArrayList<ServerChatter> chatters;
    HashMap<String, ArrayList<ServerChatter>> hash;
    HashMap<String, ArrayList<ChatItem>> hashChat;
    ArrayList<ChatItem> chatItems;
    HashMap<String, String> hashfavorite;


    // 접속된 순번 --> 현재 1대 1 채팅 대상을 구하기 위한 자신의 접속 순번
    String id; // 아이디(별칭)--> 대화메세지에 보여질 id(대화명) ==> 로그인처리에 의해 구함
    String roomNum;
    int no;
    Boolean partner = false;
    ServerChatter pair;
    int pairNo;

    //베스트 채팅저
    String Bc;

    //채팅 인덱스를 저장할 변수
    int index;

    //생성자
    public ServerChatter(Socket socket,HashMap<String, ArrayList<ServerChatter>> hash, HashMap<String, ArrayList<ChatItem>> hashChat,HashMap<String, String> hashfavorite ){

        this.socket = socket;
        this.hash = hash;
        this.hashChat = hashChat;
        this.hashfavorite = hashfavorite;


        //this.chatters = chatters;
        System.out.println("서버채팅 스레드 생성자 생성 ");

        // 소켓으로부터 최종 입출력 스트림 얻기
        try{
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    // 대화명을 입력받는 처리 --> 확장되어지면 데이타베이스에 id/pass를 검색하여
    //         로그인 기능으로 확장할 수 있다.
    public String login(){
        try{
            System.out.println("로그인 메소");

            //소켓으로부터 방번호와 아이디를 전달받고, 구분자(,)를 통해 구분해 저장한다.
            id = br.readLine();
            roomNum = id.substring(0, id.lastIndexOf(","));
            id = id.substring(id.lastIndexOf(",") + 1);
            //roomNum = "1";



        }catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("login()처리에서 예외 발생.....");
        }
        return roomNum;
    }

    //쓰레드는 메세지를 받아서 출력하고 클라이언트에 보내는 역할만 한다.
    public void run(){

        //방정보를 얻은 이후 채팅저장 리스트를 선언해서 사용한다.
        synchronized (hash) {

            System.out.println(roomNum+"");

            //chatters =  new ArrayList<ServerChatter>(hash.get(roomNum));
            chatItems = hashChat.get(roomNum);
            chatters = hash.get(roomNum);


        }


        try{
            String message = "";
            while(true){
                System.out.println(id +" 클라이언트가 메세지를 기다립니다.");
                message = br.readLine();
                //System.out.println("받은 메세지 ==>" + id + ":" + message);
                String check =   message.substring(0,6);
                System.out.println("메시지 내용"+check);

                //채팅에 좋아요를 누른 경우
                // if(message.contains("like//")){
                if(check.equals("like//")){

                    //좋아요가 눌러진 채팅의 정보를 받아서
                    //전체사이즈와 인덱스를 비교해 서버에 저장된 채팅의 좋아요를 올려준다.
                    // System.out.println("like조건");
                    System.out.println("메시지 내용 "+message);

                    String size = message.substring(6,message.lastIndexOf("."));
                    String idx = message.substring(message.lastIndexOf(".")+1);
                    System.out.println("사이:"  + size);
                    System.out.println("인덱스:"  + idx);

                    //전체 채팅내용 크기숫자를 저장한다.
                    int i  = chatItems.size() - Integer.parseInt(size);

                    //인덱스에 전체 채팅내용크기차이만큼 더해준다.
                    index = Integer.parseInt(idx) + i;

                    //해당 인덱스의 채팅아이템을 업데이트 해준다.
                    int like =  chatItems.get(index).getLike();
                    String content = chatItems.get(index).getContent();
                    chatItems.get(index).setLike(like+1);

                    //좋아요가 눌린 채을 해쉬에 저장해준다
                    synchronized (hash) {

                        System.out.println("좋아요 채팅 저장");
                        //chatters =  new ArrayList<ServerChatter>(hash.get(roomNum));
                        hashfavorite.put(roomNum, id+":"+content);

                        System.out.println("저장된 좋아to 내용 :"  + hashfavorite.get(roomNum));



                    }


                    //채팅 내용의 좋아요 표시변화를 다른 유저에게 전달해준다.
                    for(int k =0; k < chatters.size(); k++){



                        System.out.println("모두에게 채팅좋아요 변화 통지");

                        chatters.get(k).sendMessage("like//"+chatItems.size()+"."+index);


                    }



                }
                //메시지를 분류한다.
                //채팅을 채팅저장공간에 저장한다.


                else{

                    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
                    String time;
                    time = sdfNow.format(new Date(System.currentTimeMillis()));

                    chatItems.add(new ChatItem(id, message,time,0,false));
                    System.out.println("채팅사이즈"  + chatItems.size());


                      /*
                synchronized (hash) {
                //chatters =  new ArrayList<ServerChatter>(hash.get(roomNum));
                chatters = hash.get(roomNum);

              }
                */


                    // 자신과 직접 연결된 클라이언트에게 메세지를 다시 전송한다.
                    //this.sendMessage(id + ": " + message);
                    // 1대1채팅을 하도록 연결된 클라이언트에게 메세지를 전송한다.

                    //자신을 제외한 유저들에게 보낸다

                    for(int i =0; i < chatters.size(); i++){

                        if(i!=no){

                            System.out.println("다른 클라이언트에게 전송");




                            chatters.get(i).sendMessage(id + ":" + message);
                        }
                    }

//}


                }



            }
        }catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("메세지를 수신하여 송신중 예외 발생....");
        }finally{
            //채팅방에서 나간 유저를 삭제
            chatters.remove(no);
            close();
            System.out.println("연결을 닫고 쓰레드 종료....");


        }
    }

    /*

     void getPartner()
     {
       System.out.println("파트너 메소드 호출");
       synchronized (hash) {
       //chatters =  new ArrayList<ServerChatter>(hash.get(roomNum));
       chatters = hash.get(roomNum);

     }
       pairNo = ( no % 2 == 0) ? no + 1 : no - 1;
       // 현재 클라이언트와 1대1 채팅을 클라이언트 구하기
       pair = chatters.get(pairNo);

       // 두개의 클라이언트가 동시에 채팅을 시작할 수 있도록 하기위해서
       this.sendMessage("start"); //시작 메세지 전송
       partner = true;
     }


     */
    //메세지 전송을 위한 별도 메소드
    void sendMessage(String message){
        try{
            pw.println(message);
            pw.flush();
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("sendMessage()에서 예외 발생....");
        }
    }

    //저장된 채팅내용을 보내는 메소드
    //메세지 전송을 위한 별도 메소드
    void sendTotal(String message){
        try{
            pw.println(message);
            pw.flush();
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("sendMessage()에서 예외 발생....");
        }




    }



    //베스트 채팅을 다시 들어온 사용자에게 보낸다
    void sendBest(String message){
        System.out.println("best 채팅 존재");

        try{

            synchronized (hash) {


                //chatters =  new ArrayList<ServerChatter>(hash.get(roomNum));
                Bc =  hashfavorite.get(roomNum);
                System.out.println("bestChat"+ Bc);


            }


            pw.println("best//"+Bc);
            pw.flush();
            System.out.println("bestChat 전송");

        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("sendMessage()에서 예외 발생....");
        }



    }





    //close만들 위한 메소드
    public void close(){
        try{
            br.close();
            pw.close();
            socket.close();

            if(chatters.size()-1 != no){

                System.out.println("close()메소드 채팅방 참여자들 인덱스 재정렬");

                int k = chatters.size() - no;
                k = k-1;
                for(int i = 0; i <k;i++){

                    chatters.get(no+i+1).no = chatters.get(pairNo).no -1;
                }


            }
            //pair.close();
        }catch(Exception e){
            System.out.println("close()..도중 예외 발생!");
        }
    }
}


