package com.tdh7.documentscanner.ui.fragments;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.model.DocumentInfo;
import com.tdh7.documentscanner.ui.dialog.OptionBottomSheet;
import com.tdh7.documentscanner.util.ScanConstants;
import com.tdh7.documentscanner.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ItemHolder> {
    private static final String TAG = "DocumentAdapter";

    public DocumentAdapter() {
    }

    public void destroy() {
    }

    private ArrayList<DocumentInfo> mData = new ArrayList<>();

    public void setData(List<DocumentInfo> list) {
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

    public NavigationFragment getParentFragment() {
        return mParentFragment;
    }

    public void setParentFragment(NavigationFragment parentFragment) {
        mParentFragment = parentFragment;
    }

    NavigationFragment mParentFragment;

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private int[] mOptionIDs = new int[] {
            R.string.open,
            R.string.share,
            R.string.dangerous_divider,
            R.string.delete
    };
    private OptionDocumentCallBack mOptionDocumentCallBack = new OptionDocumentCallBack();

    private static class OptionDocumentCallBack implements OptionBottomSheet.CallBack {
        private DocumentAdapter mAdapter;
        private int mPosition;
        OptionBottomSheet.CallBack attach(DocumentAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
            return this;
        }

        void detach() {
            mAdapter = null;
        }

        @Override
        public boolean onOptionClicked(int optionID) {
            if(mAdapter!=null&&mAdapter.mParentFragment!=null&&mAdapter.mParentFragment.getActivity()!=null&&mPosition<mAdapter.getItemCount())
            switch (optionID) {
                case R.string.open:
                    Util.requestOtherAppToOpenThisFile(mAdapter.mParentFragment.getContext(),ScanConstants.PDF_PATH,mAdapter.mData.get(mPosition).mFileTitle);
                    detach();
                    break;
                case R.string.share:
                 Util.shareThisFile(mAdapter.mParentFragment.getContext(),ScanConstants.PDF_PATH,mAdapter.mData.get(mPosition).mFileTitle);
                    detach();
                    break;
                case R.string.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mAdapter.mParentFragment.getActivity());
                    builder.setTitle("Delete this file, are you sure?");
                    File deleteFile =  new File(ScanConstants.PDF_PATH,mAdapter.mData.get(mPosition).mFileTitle);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                            if (deleteFile.delete()) {
                                Log.d(TAG, "deleted file");
                                mAdapter.notifyItemRemoved(mPosition);
                        }
                        dialog.dismiss();
                        detach();
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            detach();
                        }
                    });
                    builder.show();
                    break;
            }
            return true;
        }
    }

    private void showOption(int position) {
        if(mParentFragment!=null&&mParentFragment.getActivity()!=null)
        OptionBottomSheet.newInstance(mOptionIDs, mOptionDocumentCallBack.attach(this,position))
                .show(mParentFragment.getActivity().getSupportFragmentManager(),OptionBottomSheet.TAG);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.description)
        TextView mDescription;

        @BindView(R.id.state_text_view)
        TextView mStateTextView;

        @OnClick(R.id.quick_button_one)
        void onButtonOneClick() {
            onRootClick();
        }

        @OnClick(R.id.quick_button_two)
        void onButtonTwoClick() {
            Util.shareThisFile(mStateTextView.getContext(),ScanConstants.PDF_PATH,mData.get(getAdapterPosition()).mFileTitle);
        }

        @OnClick(R.id.root)
        void onRootClick() {
            Util.requestOtherAppToOpenThisFile(mStateTextView.getContext(), ScanConstants.PDF_PATH,mData.get(getAdapterPosition()).mFileTitle);
        }

        @OnClick(R.id.menu_button)
        void onMenuClick() {
            showOption(getAdapterPosition());
        }

        @OnLongClick(R.id.root)
        void onRootLongClick() {
            Util.vibrate(App.getInstance());
            showOption(getAdapterPosition());
        }


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(DocumentInfo info) {
            mTitle.setText(info.mFileTitle);
            mDescription.setText(info.mDescription);
        }
    }
}
