package com.example.youtube;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatDataAdapter extends RecyclerView.Adapter<ChatDataAdapter.ItemViewHolder> {

private ArrayList<ChatItem> chatItems;
private OnItemClickListener mListener = null;
        boolean check;
    WatchingActivity watchingActivity;

        Context context;
        String id;


//테마데이터 어댑터 생성자
public ChatDataAdapter(Context context, ArrayList<ChatItem> chatItems,String id) {

        this.context = context;
        this.chatItems = chatItems;
        this.id = id;

        }

//
@Override
public ChatDataAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        ChatDataAdapter.ItemViewHolder viewHolder = new ChatDataAdapter.ItemViewHolder(view); // 뷰객체를 파라미터로 받아 뷰 홀더객체를 생성

        return viewHolder;
        }

@Override
public void onBindViewHolder(final ChatDataAdapter.ItemViewHolder holder, int position) {


        //상대방이 나에게 말한거면
        //if(!chatItems.get(position).getId().equals("me")) {
        // Glide.with(context).load(Uri.parse(chatItems.get(position).getThumbnail())).into(holder.thumbnailView);
        if(chatItems.get(position).getId().equals(id)){
            holder.content_tv.setText(chatItems.get(position).getContent());
            holder.content_tv.setTextColor(Color.YELLOW);
        }else{
            holder.content_tv.setText(chatItems.get(position).getContent());

        }


        holder.id_tv.setText(chatItems.get(position).getId());
        /*holder.like_btn.setText("♡");
        holder.like_btn.setTextColor(Color.RED);*/
        //holder.content_tv.requestFocus(View.FOCUS_DOWN);

       /* //내가 상대방에게 말한거면
        }else{

            holder.id_tv.setText(chatItems.get(position).getId());
            holder.id_tv.setTextColor(Integer.parseInt("#000080"));
            holder.content_tv.setText(chatItems.get(position).getContent());
            holder.content_tv.setTextColor(Integer.parseInt("#0000CD"));



        }*/

        }


@Override
public int getItemCount() {//어댑터에서 관리하는 리스트에 저장된 아이템의 갯수를 확인 = 리스트의 크기를 확인
        return chatItems.size();
        }


public void addItem(ChatItem item) {
        chatItems.add(item);
        }

public void setItems(ArrayList<ChatItem> Item) {
        this.chatItems = Item;
        }

public ChatItem getItem(int position) {
        return chatItems.get(position);
        }

public void setItem(int position, ChatItem item) {
        chatItems.set(position, item);
        }

public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
        }


//테마 아이템뷰 정의 부분
public class ItemViewHolder extends RecyclerView.ViewHolder {


    private TextView id_tv, content_tv,like_btn;


    public ItemViewHolder(final View itemView) {
        super(itemView);


        //상대방의 대화내용과 상대방의 정보를 담을 아이템
        like_btn = itemView.findViewById(R.id.like_btn);
        id_tv = itemView.findViewById(R.id.id_tv);
        content_tv = itemView.findViewById(R.id.content_tv);




        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(context,pos,Toast.LENGTH_LONG).show();

                int pos = getAdapterPosition() ;
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, pos) ;
                    }
                }

                //Toast.makeText(context, "내용이 클릭됨", Toast.LENGTH_LONG).show();

            }


        });






    }
}

// 커스텀 리스너 인터페이스
public interface OnItemClickListener
{
    void onItemClick(View v, int pos);
}

}
