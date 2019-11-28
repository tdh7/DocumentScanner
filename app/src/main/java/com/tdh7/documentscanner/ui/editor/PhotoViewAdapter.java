package com.tdh7.documentscanner.ui.editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.model.BitmapDocument;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewAdapter.PhotoHolder> {
    private ArrayList<BitmapDocument> mData = new ArrayList<>();
    public void setPages(List<BitmapDocument> list) {
        mData.clear();
        if(list!=null) mData.addAll(list);
        notifyDataSetChanged();
    }

    public void addPage(BitmapDocument document) {
        mData.add(document);
        notifyItemInserted(mData.size()-1);
    }

    public void addPage(int index, BitmapDocument document) {
        mData.add(index,document);
        notifyItemInserted(index);
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    public interface PhotoClickListener {
        void onPhotoClick(int position);
    }

    public PhotoClickListener getPhotoClickListener() {
        return mPhotoClickListener;
    }

    public void setPhotoClickListener(PhotoClickListener photoClickListener) {
        mPhotoClickListener = photoClickListener;
    }

    private PhotoClickListener mPhotoClickListener;

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        PhotoView mPhotoView;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mPhotoView = (PhotoView) itemView;
            mPhotoView.setMinimumScale(0.85f);
            mPhotoView.setMediumScale(1f);
            mPhotoView.setScale(0.85f);
            mPhotoView.setOnClickListener(this);
        }
        public void bind(BitmapDocument document) {

            if(document.mTempBitmap!=null)
                mPhotoView.setImageBitmap(document.mTempBitmap);
            else mPhotoView.setImageBitmap(document.mDocumentBitmap);

            mPhotoView.setScale(0.85f,false);
        }

        @Override
        public void onClick(View v) {
            if(mPhotoClickListener!=null) mPhotoClickListener.onPhotoClick(getAdapterPosition());
        }
    }
}
