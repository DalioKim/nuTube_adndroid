package com.example.youtube;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentDataAdapter extends RecyclerView.Adapter<CommentDataAdapter.ItemViewHolder> {

    private ArrayList<CommentItem> commentItems;
    OnCommentItemClickListener listener = null;    // 리스너 객체 참조를 저장하는 변수

    Context context ;   //콘텍스트 선언
    int position;
    int start;

    MainActivity mainActivity;

    //테마데이터 어댑터 생성자
    public CommentDataAdapter(Context context,ArrayList<CommentItem> commentItems) {
        this.context=context;
        this.commentItems = commentItems;
    }

    //
    @Override
    public CommentDataAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        CommentDataAdapter.ItemViewHolder viewHolder = new CommentDataAdapter.ItemViewHolder(view); // 뷰객체를 파라미터로 받아 뷰 홀더객체를 생성
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentDataAdapter.ItemViewHolder holder, final int position) {


        /*holder.thumbnailView.setBackground(new ShapeDrawable(new OvalShape()));
        holder.thumbnailView.setClipToOutline(true);
*/
        holder.user_info.setText(commentItems.get(position).getId()+"               게시시간:"+commentItems.get(position).getDate());
        holder.comment.setText(commentItems.get(position).content);
        /*Glide.with(context).load(Uri.parse(commentItems.get(position).getThumbnail())).into(holder.thumbnail_view);
        holder.name_view.setText("테마 : "+commentItems.get(position).getName());

        Spannable span = (Spannable) holder.comment.getText();
        //Pattern p = Pattern.matches ("^01(?:0|1|[6-9]) - (?:\\d{3}|\\d{4}) - \\d{4}$",span);
        //Pattern p = Pattern.compile( "^([01][0-9]|2[0-3]):([0-5][0-9])");
        Pattern p = Pattern.compile("(^[0-9]*$)");


        if(Pattern.matches("(^[0-9]*$)",holder.comment.getText())){
            Log.i("어댑터","정규식통과");
            int start = p.matcher(holder.comment.getText()).start();
            span.setSpan(new ForegroundColorSpan(Color.BLUE), start, start + 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        }
        */
       /* Spannable span = (Spannable) holder.comment.getText();
        String text = span.toString();

        if(text.contains("hi")) {
            int start = text.indexOf("hi");
            int end = start + "hi".length();

            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {




                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.linkColor = 0xff000000;
                    super.updateDrawState(ds);
                }
            }, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            holder.comment.setText(span);
            holder.comment.setHighlightColor(Color.TRANSPARENT);
            holder.comment.setMovementMethod(LinkMovementMethod.getInstance());
        }*/




       /* Spannable span = (Spannable) holder.comment.getText();
        String text = span.toString();

        if(text.contains("hi")) {
            int start = text.indexOf("hi");
            int end = start + "hi".length();
            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {


                    //int pos = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (listener != null) {
                            listener.onItemClick(widget, position);
                        }
                    }

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.linkColor = 0xff000000;
                    super.updateDrawState(ds);
                }
            }, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            holder.comment.setText(span);
            holder.comment.setHighlightColor(Color.TRANSPARENT);
            holder.comment.setMovementMethod(LinkMovementMethod.getInstance());

        }*/

        final Spannable span = (Spannable) holder.comment.getText();
        String text = span.toString();

       // Pattern p = Pattern.compile("^[0-9][0-9]+:[0-9][0-9]$");
        //Pattern.matches("^[0-9]+[0-9]+:[0-9][0-9]$", text);
        //Boolean bo = Pattern.matches("(^[0-9][0-9]+:[0-9][0-9]$)",text);
        //if(Pattern.matches("^[0-9]+[0-9]+:[0-9][0-9]$", text)) {
            //if(Pattern.matches("^[0-9]$", text)) {
                //if(Pattern.matches("^.*([01][0-9]|2[0-3]):([0-5][0-9]).*$", text)) {

                    //Matcher matcher = p.matcher(text);
            //Pattern p = Pattern.compile("^([01][0-9]|2[0-3]):([0-5][0-9])$");
        Pattern p = Pattern.compile("([01][0-9]|2[0-3]):([0-5][0-9])");

        Matcher matcher = p.matcher(text);
            //int start = matcher.start();*/
        int i = 0;
            while (matcher.find()) {
                i++;
                Log.i("패턴검색","found: " + i + " : "
                        + matcher.start() + " - " + matcher.end());

                start = matcher.start();
                final String link = span.toString().substring(start, start+5);

                //Log.i("어댑터클래", "찾"+end)
                span.setSpan(new ClickableSpan() {

                 /*   public String Click(View widget) {

                        String string = span.toString();
                        if (position != RecyclerView.NO_POSITION) {
                            // 리스너 객체의 메서드 호출.
                            if (listener != null) {
                                listener.onItemClick(widget, position);
                            }
                        }

                    return string;

                    }*/



                    @Override
                    public void onClick(View widget) {

                        if (position != RecyclerView.NO_POSITION) {
                            // 리스너 객체의 메서드 호출.
                            if (listener != null) {
                                listener.onItemClick(widget, position,link);
                            }
                        }



                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(context.getResources().getColor(R.color.colorPrimary));
                        //ds.linkColor = 0xff000000;
                        super.updateDrawState(ds);
                    }
                }, start, start+5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                holder.comment.setText(span);
                holder.comment.setHighlightColor(Color.TRANSPARENT);
                holder.comment.setMovementMethod(LinkMovementMethod.getInstance());





            }
        //}
/*스
        if(bo) {
            int start = p.matcher(text).start();
            int end = start + 4;
            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {


                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (listener != null) {
                            listener.onItemClick(widget, position);
                        }
                    }

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.linkColor = 0xff000000;
                    super.updateDrawState(ds);
                }
            }, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            holder.comment.setText(span);
            holder.comment.setHighlightColor(Color.TRANSPARENT);
            holder.comment.setMovementMethod(LinkMovementMethod.getInstance());

        }*/


    }

    @Override
    public int getItemCount() {//어댑터에서 관리하는 리스트에 저장된 아이템의 갯수를 확인 = 리스트의 크기를 확인
        return commentItems.size();
    }


    public void addItem(CommentItem item) {
        commentItems.add(item);
    }

    public void setItems(ArrayList<CommentItem> Item) {
        this.commentItems = Item;
    }

    public CommentItem getItem(int position) {
        return commentItems.get(position);
    }

    public void setItem(int position, CommentItem item) {
        commentItems.set(position, item);
    }

    public void setOnItemClickListener(OnCommentItemClickListener listener) {
        this.listener = listener;
    }



    //테마 아이템뷰 정의 부분
    public class ItemViewHolder extends RecyclerView.ViewHolder {


        private ImageView thumbnailView;
        private TextView user_info, comment,comment_info;


        public ItemViewHolder(View itemView) {
            super(itemView);

            thumbnailView = itemView.findViewById(R.id.thumbnailView);
            user_info = itemView.findViewById(R.id.user_info);
            comment = itemView.findViewById(R.id.comment);
            //comment_info = itemView.findViewById(R.id.comment_info);





/*
            // 아이템 클릭 이벤트 처리.
            user_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context,pos,Toast.LENGTH_LONG).show();

                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (listener != null) {
                            listener.onItemClick(v, pos) ;
                        }
                    }

                    //Toast.makeText(context, "내용이 클릭됨", Toast.LENGTH_LONG).show();

                }








            });*/


            //span.onClick(comment);




/*
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
//                    Toast.makeText(context,pos,Toast.LENGTH_LONG).show();

                    //리스너 객체의 메서도 호출
                    if (listener != null) {
                        listener.onItemClick(CommentDataAdapter.ItemViewHolder.this,v, position) ;
                    }



                }








            });*/








            //span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            /*String text = span.toString();

            int start = text.indexOf("hi");
            int end = start + "hi".length();
            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.linkColor = 0xff000000;
                    super.updateDrawState(ds);
                }
            }, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            comment.setText(span);
            comment.setHighlightColor(Color.TRANSPARENT);
            comment.setMovementMethod(LinkMovementMethod.getInstance());*/






        }
    }

    //커스텀 리스너 인터페이스 정의
    public interface OnCommentItemClickListener {
        void onItemClick( View view, int position, String span) ;
    }


    /*class MyClickableSpan extends ClickableSpan {
        View.OnClickListener clickListener;
        String content;

        public MyClickableSpan(String content, View.OnClickListener clickListener) {
            super();
            this.content = content;
            this.clickListener= clickListener;
        }

        public void onClick(View tv) {
            tv.setTag(content);
            clickListener.onClick(tv);
        }

        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.parseColor("#689899"));
            ds.setUnderlineText(true); // set to false to remove underline
        }
    }*/


}
