package com.tdh7.documentscanner.ui.editor;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.util.Tool;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FunctionMenuAdapter  extends RecyclerView.Adapter<FunctionMenuAdapter.MenuHolder> {
    private static final String TAG = "FunctionMenuAdapter";
    private ArrayList<MenuItem> mData = new ArrayList<>();

    private int mRecyclerViewWidth;

    public int getVerticalCount() {
        return mVerticalCount;
    }

    public void setVerticalCount(int verticalCount) {
        mVerticalCount = verticalCount;
    }

    private int mVerticalCount = 5;
    private float mDpUnit = 1;
    private int mIconWidth = 50;
    private int mItemWidth = 0;
    private int mItemMargin = 0;
    private int mRecyclerLeftRightMargin = 0;
    GridLayoutManager gridLayoutManager;
    private int mSpanSize = 2;
    public FunctionMenuAdapter() {
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        mRecyclerViewWidth = Tool.getScreenSize(recyclerView.getContext())[0];
        mDpUnit = recyclerView.getResources().getDimension(R.dimen.dp_unit);
        mIconWidth = (int) (50f * mDpUnit);
        mItemMargin = (mRecyclerViewWidth - mVerticalCount*mIconWidth)/(mVerticalCount+1);
        mItemWidth = mItemMargin + mIconWidth;
        mRecyclerLeftRightMargin = mItemMargin/2;

        recyclerView.setAdapter(this);
        gridLayoutManager = new GridLayoutManager(recyclerView.getContext(),2,GridLayoutManager.HORIZONTAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mSpanSize;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        SnapHelper menuSnapHelper = new MultiSnapHelper(SnapGravity.CENTER, 5, 100);
        menuSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
           @Override
           public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
               int position = parent.getChildLayoutPosition(view);
               if(position==0)  outRect.left = mRecyclerLeftRightMargin;
               else if (position + 1 == getItemCount()) outRect.right = mRecyclerLeftRightMargin;
           }
       });
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_document_editor,parent,false);
        return new MenuHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    public int getItemWidth(int position) {
        return mItemWidth;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<MenuItem> functionList) {
        mData.clear();
        if(functionList!=null) mData.addAll(functionList);
        notifyDataSetChanged();
    }

    public int getSpanSize() {
        return mSpanSize;
    }

    public void setSpanSize(int spanSize) {
        mSpanSize = spanSize;
    }

    public void addPreData(ArrayList<MenuItem> functionList) {
        if(functionList!=null) mData.addAll(0,functionList);
    }

    public ArrayList<MenuItem> getData() {
        return mData;
    }

    public static class MenuItem {
        public final @DrawableRes int mDrawableRes;
        public final @StringRes int mTitleRes;
        public final @StringRes int mDescriptionRes;

        public MenuItem(final @DrawableRes int drawableRes,final @StringRes int titleRes,final  @StringRes int descriptionRes) {
            mDrawableRes = drawableRes;
            mTitleRes = titleRes;
            mDescriptionRes = descriptionRes;
        }
    }

    public interface MenuItemClickListener {
        void onMenuItemClick(MenuItem item, int position);
    }

    public MenuItemClickListener getMenuItemClickListener() {
        return mMenuItemClickListener;
    }

    public void setMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        mMenuItemClickListener = menuItemClickListener;
    }

    MenuItemClickListener mMenuItemClickListener;

    public class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mTitle;
        ImageView mIcon;
        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mIcon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(MenuItem item) {
            itemView.getLayoutParams().width = getItemWidth(getAdapterPosition());
            mTitle.setText(item.mTitleRes);
            mIcon.setImageResource(item.mDrawableRes);
        }

        @Override
        public void onClick(View v) {
            if(mMenuItemClickListener!=null) mMenuItemClickListener.onMenuItemClick(mData.get(getAdapterPosition()),getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Toasty.normal(App.getInstance(),mData.get(getAdapterPosition()).mDescriptionRes).show();
            return true;
        }
    }
}
