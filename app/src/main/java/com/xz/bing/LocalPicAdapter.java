package com.xz.bing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * LocalPicture活动的recycler列表的适配器
 */
public class LocalPicAdapter extends RecyclerView.Adapter<LocalPicAdapter.ViewHolder> {
    private Context mContext;
    private List<LocalPic> mlocalImageList;
    static  class ViewHolder extends  RecyclerView.ViewHolder{
        CardView cardView;
        ImageView localPicImage;
        TextView localPicEnddate;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            localPicImage = view.findViewById(R.id.local_pic_image);
            localPicEnddate = view.findViewById(R.id.local_pic_enddate);
        }
    }
    public LocalPicAdapter(List<LocalPic> localPicList){
        mlocalImageList =localPicList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {

            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.local_pic_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        //列表点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                LocalPic localPic = mlocalImageList.get(position);
                Intent intent = new Intent(mContext,LocalPictureDetails.class);
                intent.putExtra("enddate",localPic.getEnddate());
                intent.putExtra("uri",localPic.getUri());
                intent.putExtra("copyright",localPic.getCopyright());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalPic localPic = mlocalImageList.get(position);
        holder.localPicEnddate.setText(localPic.getEnddate());
        Glide.with(mContext).load(localPic.getUri()).into(holder.localPicImage);

    }

    @Override
    public int getItemCount() {
        return mlocalImageList.size();
    }

}
