package com.tdh7.documentscanner.ui.editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.session.DocumentSession;
import com.tdh7.documentscanner.model.BitmapDocument;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.dialog.LoadingScreenDialog;
import com.tdh7.documentscanner.ui.fragments.BaseFragment;
import com.tdh7.documentscanner.ui.permissionscreen.AskingDialog;
import com.tdh7.documentscanner.util.ScanConstants;
import com.tdh7.documentscanner.util.ScanUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class DocumentEditorFragment extends BaseFragment implements FunctionMenuAdapter.MenuItemClickListener, PhotoViewAdapter.PhotoClickListener {

    @BindView(R.id.status_bar)
    View mStatusBar;

    @BindView(R.id.menu_recycler_view)
    RecyclerView mMenuRecyclerView;

    @BindView(R.id.view_pager)
    ViewPager2 mViewPager;

    @BindView(R.id.root)
    View mRoot;

    @BindView(R.id.page_note)
    TextView mPageNote;

    @OnClick(R.id.back_button)
    void back() {
        if(getActivity()!=null)
            getActivity().onBackPressed();
    }

    private PhotoViewAdapter mPhotoViewAdapter;
    private  FunctionMenuAdapter mFunctionMenuAdapter;

    public static DocumentEditorFragment newInstance() {
        return new DocumentEditorFragment();
    }

    public DocumentEditorFragment add(RawBitmapDocument bitmapDocument) {
        mRawBitmapDocuments.add(bitmapDocument);
        return this;
    }

    public DocumentEditorFragment addAll(List<RawBitmapDocument> bitmapDocuments) {
        mRawBitmapDocuments.addAll(bitmapDocuments);
        return this;
    }
    AskingDialog mAskingDialog = null;

    @Override
    public boolean onBackPressed() {
        if(mAskingDialog!=null) {
            mAskingDialog.dismiss();
        } else {
            mAskingDialog = AskingDialog.newInstance().setCallback(result -> {
                switch (result) {
                    case AskingDialog.RESULT_ONE:
                        mDocumentSession.clear();
                        dismiss();
                        break;
                    case AskingDialog.RESULT_TWO:
                        dismiss();
                        break;
                }
            });
            if(DocumentEditorFragment.this.getFragmentManager()!=null)
            mAskingDialog.show(DocumentEditorFragment.this.getFragmentManager(),"ASKING_DIALOG");
            else {
                dismiss();
                mDocumentSession.clear();
            }
            mAskingDialog = null;
        }
        return false;
    }

    private DocumentSession mDocumentSession = DocumentSession.getInstance();
    private ArrayList<RawBitmapDocument> mRawBitmapDocuments = new ArrayList<>();
    public DocumentEditorFragment() {
    }

    @Override
    public int contentLayout() {
        return R.layout.multipage_editor_layout;
    }

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        mFunctionMenuAdapter = new FunctionMenuAdapter();
        mFunctionMenuAdapter.attachToRecyclerView(mMenuRecyclerView);
        mFunctionMenuAdapter.setMenuItemClickListener(this);

        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.add_book,R.string.add_pages,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.rescan,R.string.retake,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_rotate_left_black_24dp,R.string.rotate,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_photo_filter_black_24dp,R.string.filter,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.crop,R.string.cropper,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.rearrange,R.string.rearrange,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.aspect,R.string.ratio_aspect,R.string.rotate_note));
        //mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.trash,R.string.delete,R.string.rotate_note));

        mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_photo_black_24dp,R.string.original,R.string.original_description));
        mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_photo_filter_black_24dp,R.string.magic_color,R.string.magic_color_description));
        mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_color_lens_black_24dp,R.string.gray_scale,R.string.gray_scale_description));
        mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_wb_auto_black_24dp,R.string.b_and_w,R.string.b_and_w_description));
        mFunctionList.add(new FunctionMenuAdapter.MenuItem(R.drawable.ic_rotate_right_black_24dp,R.string.rotate,R.string.rotate_right_description));
        mFunctionMenuAdapter.setData(mFunctionList);

        mPhotoViewAdapter = new PhotoViewAdapter();
        mViewPager.setAdapter(mPhotoViewAdapter);
        mPhotoViewAdapter.setPhotoClickListener(this);
        mViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationY(Math.abs(position) * 50*mDpUnit);
            }
        });
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
                mPageNote.setText(getResources().getString(R.string.page_x_of_n,position+1,mPhotoViewAdapter.getItemCount()));
            }
        });

        addAllRawDocumentsToSession(true);
    }

    private void refreshData() {
        mPhotoViewAdapter.setPages(mDocumentSession.getDocuments());
    }

    private final ArrayList<FunctionMenuAdapter.MenuItem> mFunctionList = new ArrayList<>();
    private void saveToFile(String filePath, String fileName, final boolean shouldDismiss) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                postShowLoading();
                String message = mDocumentSession.saveToFile(filePath,fileName);
                if(message.isEmpty()) {
                    mRoot.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.success(App.getInstance(),"Save successfully!").show();
                            if(getActivity() instanceof MainActivity)
                                ((MainActivity)getActivity()).reloadSavedList();
                            if(shouldDismiss) {
                                mDocumentSession.clear();
                                dismiss();
                            }
                        }
                    },350);
                } else {
                    mRoot.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(App.getInstance(),message+"!").show();
                            if(shouldDismiss) {
                                mDocumentSession.clear();
                                dismiss();
                            }
                        }
                    },350);
                }
                postHideLoading();
            }
        });
    }

    private void saveToFile(final boolean shouldDismiss) {
        if(mDocumentSession.getDocuments().isEmpty()) return;

        String foname = new SimpleDateFormat("dd MM yyyy (HH:mm:ss)").format(new Date());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nhập tên file:");
        final EditText iname = new EditText(getActivity());
        iname.setText(foname);
        iname.setSelectAllOnFocus(true);
        float oneDp = getResources().getDimension(R.dimen.dp_unit);
        int _8dp = (int) (oneDp*16);
        iname.setPadding(_8dp,_8dp,_8dp,_8dp);
        builder.setView(iname);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = iname.getText().toString();
                saveToFile(ScanConstants.PDF_PATH,fileName+".pdf",shouldDismiss);
            }
        });
        builder.show();
    }

    @OnClick(R.id.action_button_top)
    void saveToFile() {
        saveToFile(true);
    }

    private void addAllRawDocumentsToSession(final boolean shouldDismiss) {
        if(mRawBitmapDocuments.isEmpty()) return;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                postShowLoading();
                ArrayList<BitmapDocument> list = new ArrayList<>();
                boolean isFailed = false;
                String message = "";
                BitmapDocument bitmapDocument;
                RawBitmapDocument raw;
                int size = mRawBitmapDocuments.size();
                for (int i = 0; i < size; i++) {
                    raw = mRawBitmapDocuments.get(i);
                    try {
                        bitmapDocument = ScanUtils.buildBitmapDocument(raw);
                    } catch (Exception e) {
                        bitmapDocument = null;
                        isFailed = true;
                        message = "Failure to crop the document";
                    }

                    if(isFailed) break;
                    list.add(bitmapDocument);
                }

                if(isFailed) {
                    final String m = message;
                    mRoot.postDelayed(() -> {
                        Toasty.error(App.getInstance(), m).show();
                        if(shouldDismiss)
                        dismiss();
                    }, 350);
                } else {
                    // successfull
                    mRoot.postDelayed(() -> {
                        mDocumentSession.addAll(list);
                        refreshData();
                        if(mPhotoViewAdapter.getItemCount()!=0) {
                            mCurrentItem = mPhotoViewAdapter.getItemCount() - 1;
                            mPageNote.setText(getResources().getString(R.string.page_x_of_n,mCurrentItem+1,mPhotoViewAdapter.getItemCount()));
                            mViewPager.setCurrentItem(mPhotoViewAdapter.getItemCount() - 1);
                        }
                    },300);
                }
                mRawBitmapDocuments.clear();
                postHideLoading();
            }
        });
    }

    LoadingScreenDialog mLoadingDialog = null;

    private void addScannedImage(BitmapDocument document) {
        mPhotoViewAdapter.addPage(document);
    }

    private void setScannedDocument(RawBitmapDocument document) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                postShowLoading();
                    BitmapDocument bitmapDocument;
                    try {
                        bitmapDocument = ScanUtils.buildBitmapDocument(document);
                        mRoot.post(() -> addScannedImage(bitmapDocument));
                    } catch (Exception e) {
                        mRoot.postDelayed(() -> {
                            Toasty.error(App.getInstance(),"Error when trying to crop the document").show();
                            dismiss();
                        },350);

                    }
                    postHideLoading();
            }
        });
    }

    private void postHideLoading() {
        mRoot.post(()-> {

            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        });
    }

    private void postShowLoading() {
        mRoot.post(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog = LoadingScreenDialog.newInstance(getContext());
                mLoadingDialog.show(getChildFragmentManager(),"LoadingScreenDialog");
            }
        });
    }

    private int mCurrentItem = 0;

    @Override
    public boolean isWhiteTheme(boolean current) {
        return true;
    }


    protected synchronized void showProgressDialog(String message) {
        postShowLoading();
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        mStatusBar.getLayoutParams().height = value;
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.SLIDE_LEFT;
    }

    @Override
    public void onMenuItemClick(FunctionMenuAdapter.MenuItem item, int position) {
        int current = mCurrentItem;
        if(current<0||current>mPhotoViewAdapter.getItemCount()) return;
        BitmapDocument document = mDocumentSession.get(current);
        if(document==null) return;
        switch (item.mTitleRes) {
            case R.string.add_pages:
                break;
            case R.string.retake:
                break;
            case R.string.rotate:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        postShowLoading();
                        document.applyTempFilterOrRotate(BitmapDocument.ACTION_ROTATE);
                        postHideLoading();
                        mRoot.postDelayed(() ->
                        mPhotoViewAdapter.notifyItemChanged(current),125);
                    }
                });

                break;
            case R.string.filter:
                break;
            case R.string.cropper:
                break;
            case R.string.original:
                document.applyTempFilterOrRotate(BitmapDocument.FILTER_NONE);
                mPhotoViewAdapter.notifyItemChanged(current);
                break;
            case R.string.magic_color:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        postShowLoading();
                        document.applyTempFilterOrRotate(BitmapDocument.FILTER_MAGIC);

                        postHideLoading();
                        mRoot.postDelayed(() ->
                                mPhotoViewAdapter.notifyItemChanged(current),125);
                    }
                });
                break;
            case R.string.gray_scale:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        postShowLoading();
                        document.applyTempFilterOrRotate(BitmapDocument.FILTER_GRAY_SCALE);
                        postHideLoading();
                        mRoot.postDelayed(() ->
                                mPhotoViewAdapter.notifyItemChanged(current),125);
                    }
                });
                break;
            case R.string.b_and_w:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        postShowLoading();
                        document.applyTempFilterOrRotate(BitmapDocument.FILTER_BnW);
                        postHideLoading();
                        mRoot.postDelayed(() ->
                                mPhotoViewAdapter.notifyItemChanged(current),125);
                    }
                });
                mPhotoViewAdapter.notifyItemChanged(current);
                break;
        }
    }

    @Override
    public void onPhotoClick(int position) {
        if(mPageNote.getVisibility()==View.GONE) mPageNote.setVisibility(View.VISIBLE);
        else mPageNote.setVisibility(View.GONE);
    }
}