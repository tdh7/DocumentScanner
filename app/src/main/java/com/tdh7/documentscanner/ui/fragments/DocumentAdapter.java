package com.tdh7.documentscanner.ui.fragments;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.model.CountSectionItem;
import com.tdh7.documentscanner.model.DocumentInfo;
import com.tdh7.documentscanner.ui.dialog.OptionBottomSheet;
import com.tdh7.documentscanner.util.PreferenceUtil;
import com.tdh7.documentscanner.util.ScanConstants;
import com.tdh7.documentscanner.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DocumentAdapter";
    public static final int TYPE_COUNT_SECTION_ITEM = 1;
    public static final int TYPE_SAVED_DOCUMENT_ONE_ROW = 2;
    public static final int TYPE_SAVED_DOCUMENT_TWO_ROW = 3;
    private int mCurrentListType;

    public DocumentAdapter() {
        mCurrentListType = PreferenceUtil.getInstance().getSavedListType();
    }

    public void destroy() {
    }

    private ArrayList<DocumentObject> mData = new ArrayList<>();

    public void setData(List<DocumentObject> list) {
        mData.clear();
        if(list!=null) mData.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_COUNT_SECTION_ITEM:
                return new SectionItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false));
            case TYPE_SAVED_DOCUMENT_TWO_ROW:
                return  new TwoRowDocumentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_document_two_row, parent, false));
                case TYPE_SAVED_DOCUMENT_ONE_ROW:
            default:
                return new DocumentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_document, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mData.get(position) instanceof CountSectionItem)
            return TYPE_COUNT_SECTION_ITEM;
        else if(mCurrentListType==PreferenceUtil.TYPE_LIST_ONE_ROW) return TYPE_SAVED_DOCUMENT_ONE_ROW;
        else return TYPE_SAVED_DOCUMENT_TWO_ROW;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TwoRowDocumentHolder)
            ((TwoRowDocumentHolder)holder).bind((DocumentInfo) mData.get(position));
        else if(holder instanceof DocumentHolder)
            ((DocumentHolder)holder).bind((DocumentInfo) mData.get(position));
        else if (holder instanceof SectionItemHolder) {
            ((SectionItemHolder)holder).bind((CountSectionItem)mData.get(position));
        }
    }

    public NavigationFragment getParentFragment() {
        return mParentFragment;
    }

    public void setParentFragment(NavigationFragment parentFragment) {
        mParentFragment = parentFragment;
    }

    NavigationFragment mParentFragment;
    public void switchListType() {
        mCurrentListType = (mCurrentListType==PreferenceUtil.TYPE_LIST_ONE_ROW) ? PreferenceUtil.TYPE_GRID_TWO_ROW : PreferenceUtil.TYPE_LIST_ONE_ROW;
        PreferenceUtil.getInstance().setSavedListType(mCurrentListType);
        notifyItemRangeChanged(0,mData.size());
    }

    @Override
    public int getItemCount() {
        if(mCurrentListType==PreferenceUtil.TYPE_LIST_ONE_ROW) {

        }
        return mData.size();
    }

    private int[] mOptionIDs = new int[] {
            R.string.open,
            R.string.share,
            R.string.dangerous_divider,
            R.string.delete
    };

    private OptionDocumentCallBack mOptionDocumentCallBack = new OptionDocumentCallBack();

    public int getSpanSize(int spanCount, int position) {
        if(position==0) return  spanCount;
        if(mCurrentListType==PreferenceUtil.TYPE_LIST_ONE_ROW) {
            return spanCount;
        } else return spanCount/2;
    }

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
                    Util.requestOtherAppToOpenThisFile(mAdapter.mParentFragment.getContext(),ScanConstants.PDF_PATH,((DocumentInfo)mAdapter.mData.get(mPosition)).mFileTitle);
                    detach();
                    break;
                case R.string.share:
                 Util.shareThisFile(mAdapter.mParentFragment.getContext(),ScanConstants.PDF_PATH,((DocumentInfo)mAdapter.mData.get(mPosition)).mFileTitle);
                    detach();
                    break;
                case R.string.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mAdapter.mParentFragment.getActivity());
                    builder.setTitle("Delete this file, are you sure?");
                    File deleteFile =  new File(ScanConstants.PDF_PATH,((DocumentInfo)mAdapter.mData.get(mPosition)).mFileTitle);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                            if (deleteFile.delete()) {
                                Log.d(TAG, "deleted file");
                                mAdapter.mData.remove(mPosition);
                                mAdapter.notifyItemRemoved(mPosition);
                                mAdapter.notifyItemChanged(0);
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

    public interface DocumentObject {}

    public class SectionItemHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mCount;
        ImageView mListTypeIcon;

        SectionItemHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mCount = itemView.findViewById(R.id.number);
            mListTypeIcon = itemView.findViewById(R.id.list_view_type);
            mListTypeIcon.setOnClickListener((v) ->DocumentAdapter.this.switchListType());
        }
        public void bind(CountSectionItem object) {
                mTitle.setText(((CountSectionItem) object).getTitle());
                mCount.setText(String.valueOf(mData.size()-1));
                if(mCurrentListType==PreferenceUtil.TYPE_LIST_ONE_ROW) mListTypeIcon.setImageResource(R.drawable.list);
                else mListTypeIcon.setImageResource(R.drawable.grid);
        }
    }

    public class DocumentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.description)
        TextView mDescription;

        @BindView(R.id.state_text_view)
        TextView mStateTextView;

        @OnClick(R.id.quick_button_one)
        void onButtonOneClick() {
            Util.requestOtherAppToOpenThisFile(mStateTextView.getContext(), ScanConstants.PDF_PATH,((DocumentInfo)mData.get(getAdapterPosition())).mFileTitle);
        }

        @OnClick(R.id.quick_button_two)
        void onButtonTwoClick() {
            Util.shareThisFile(mStateTextView.getContext(),ScanConstants.PDF_PATH,((DocumentInfo)mData.get(getAdapterPosition())).mFileTitle);
        }

        @OnClick(R.id.root)
        void onRootClick() {

            itemView.animate().scaleX(0.86f).scaleY(0.86f).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(
                    () -> {
                        itemView.animate().scaleX(1).setDuration(200).setInterpolator(new DecelerateInterpolator()).scaleY(1).withEndAction(
                                this::onButtonOneClick)
                                .start();
                    }
            ).start();

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


        public DocumentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(DocumentInfo info) {
            mTitle.setText(info.mFileTitle);
            mDescription.setText(info.mDescription);
        }
    }

    public class TwoRowDocumentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView mTitle;

        void onButtonOneClick() {
            Util.requestOtherAppToOpenThisFile(mTitle.getContext(), ScanConstants.PDF_PATH,((DocumentInfo)mData.get(getAdapterPosition())).mFileTitle);
        }

        @OnClick(R.id.root)
        void onRootClick() {

            itemView.animate().scaleX(0.86f).scaleY(0.86f).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(
                    () -> {
                        itemView.animate().scaleX(1).setDuration(200).setInterpolator(new DecelerateInterpolator()).scaleY(1).withEndAction(
                                this::onButtonOneClick)
                                .start();
                    }
            ).start();

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


        public TwoRowDocumentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(DocumentInfo info) {
            mTitle.setText(info.mFileTitle);

        }
    }
}
