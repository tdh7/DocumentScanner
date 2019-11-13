package com.tdh7.documentscanner.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdh7.documentscanner.R;

import java.util.ArrayList;
import java.util.List;

public class SavedDocumentAdapter extends RecyclerView.Adapter<SavedDocumentAdapter.ItemHolder> {
    private static final String TAG = "SavedDocumentAdapter";

    public SavedDocumentAdapter() {
    }

    public void destroy() {
    }

    private ArrayList<String> mData = new ArrayList<>();

    public void setData(List<String> list) {
        mData.clear();
        if(list!=null) mData.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_document,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String name) {

        }
    }
}
