package com.scanlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<Image> mImage;
    private Context mContext;

    public ImageAdapter(Context mContext, ArrayList<Image> mImage) {
        this.mContext = mContext;
        this.mImage = mImage;

    }



    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(com.scanlibrary.R.layout.image_layout, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Image image = mImage.get(position);
        Glide.with(mContext)
                .load(image.getPath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(viewHolder.mImg);
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image.isCheck()){
                    viewHolder.mCheck.setVisibility(View.INVISIBLE);
                    image.setCheck(false);
                }
                else {viewHolder.mCheck.setVisibility(View.VISIBLE); image.setCheck(true);}
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mCheck;
        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(com.scanlibrary.R.id.image_view);
            mCheck = view.findViewById(com.scanlibrary.R.id.photoCheck);
            relativeLayout = view.findViewById(com.scanlibrary.R.id.relativelayout);
        }
    }
}