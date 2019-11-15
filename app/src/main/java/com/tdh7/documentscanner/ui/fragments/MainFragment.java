package com.tdh7.documentscanner.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.picker.CropEdgeQuickView;
import com.tdh7.documentscanner.model.CountSectionItem;
import com.tdh7.documentscanner.model.DocumentInfo;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.dialog.LoadingScreenDialog;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.util.ScanConstants;
import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Util;
import com.tdh7.documentscanner.util.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.tdh7.documentscanner.ui.MainActivity.ACTION_OPEN_MEDIA_PICKER;
import static com.tdh7.documentscanner.ui.MainActivity.ACTION_PERMISSION_START_UP;

public class MainFragment extends NavigationFragment implements CropEdgeQuickView.QuickViewActionCallback {
    public static final String TAG ="MainFragment";

    public static final int REQUEST_CODE_START_SCAN_BY_LIBRARY = 10;
    public final static int REQUEST_CODE_PICK_FROM_GALLERY = 11;
    public final static int REQUEST_CODE_PICK_FROM_DEVICE_CAMERA = 12;
    public static final int REQUEST_CODE_SHARE_FILE = 13;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    DocumentAdapter mAdapter;

    @BindDimen(R.dimen.dp_unit)
    float mOneDp;

    @BindView(R.id.drawer_parent)
    DrawerLayout mDrawerParent;

    @Override
    public boolean onBackPressed() {
        if(mCropEdgeQuickView!=null&&mCropEdgeQuickView.isAttached()) {
            mCropEdgeQuickView.detach();
            return false;
        } else return true;
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        ((ViewGroup.MarginLayoutParams)mAppBar.getLayoutParams()).topMargin = value;
    }

    @BindView(R.id.constraint_parent)
    ConstraintLayout mConstraintParent;

    @BindView(R.id.app_bar) View mAppBar;
    @BindView(R.id.app_icon) View mAppLogoIcon;
    @BindView(R.id.app_title) View mAppTitle;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void closeDrawerIfOpened() {
        mDrawerParent.closeDrawer(GravityCompat.START);
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.main_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mDrawerParent.setScrimColor(0x33000000);
        mDrawerParent.setDrawerElevation(mOneDp * 2f);
        mAdapter = new DocumentAdapter();
        mAdapter.setParentFragment(this);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager glm = new GridLayoutManager(getContext(),4);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getSpanSize(glm.getSpanCount(),position);
            }
        });
        mRecyclerView.setLayoutManager(glm);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshData);
        mAppBar.postDelayed(this::hideLogo,3000);
    }

    @BindView(R.id.search_hint) View mSearchHint;
    @BindView(R.id.pick_camera_icon) View mCameraIcon;
    @BindView(R.id.pick_photo_icon) View mAddImageIcon;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private void hideLogo() {

        mAppTitle.animate().alpha(0).scaleX(0.75f).scaleY(0.75f).setDuration(350).start();
        mAppLogoIcon.animate().alpha(0).scaleX(0.75f).scaleY(0.75f).setDuration(350)
                .withEndAction(() -> {
                    mAddImageIcon.animate().alpha(1).scaleX(1).scaleY(1).setDuration(350).start();
                    mCameraIcon.animate().alpha(1).scaleX(1).scaleY(1).setDuration(350).start();
                    mSearchHint.animate().alpha(1).setDuration(350).start();
                }).start();

    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_START_SCAN_BY_LIBRARY) {
            if(resultCode == Activity.RESULT_OK) {
                if(null != data && null != data.getExtras()) {
                    String filePath = data.getExtras().getString(ScanConstants.SCANNED_RESULT);
                    Bitmap baseBitmap = ScanUtils.decodeBitmapFromFile(filePath, ScanConstants.IMAGE_NAME);
                    mScannedImage.setVisibility(View.VISIBLE);
                    mScannedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mScannedImage.setImageBitmap(baseBitmap);
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                mScannedImage.setVisibility(View.GONE);
            }
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_FROM_DEVICE_CAMERA:
                detectEdgeAndShowQuickView(fileUri);
                break;
            case REQUEST_CODE_PICK_FROM_GALLERY:
                detectEdgeAndShowQuickView(data.getData());
        }
    }

    private void detectEdgeAndShowQuickView(Uri uri) {
        if(uri==null) {
            Toasty.error(App.getInstance(),"Sorry, something went wrong when trying to get the image").show();
            return;
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mConstraintParent.post(()-> showLoading());
                Bitmap bitmap = null;
                try {
                    if(getContext()!=null)
                        bitmap = Utils.getBitmapWithUri(getContext(),uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int rotate = ScanUtils.getCameraPhotoOrientation(getContext(),uri);
                Log.d(TAG, "image is rotatd by "+rotate);
                float[] point = ScanUtils.detectEdge(bitmap, rotate);
                Util.logPoints("ScanUtils",point);
                RawBitmapDocument document = new RawBitmapDocument(
                        bitmap,
                        rotate,
                        new float[] {
                                (rotate%180==0) ? bitmap.getWidth() : bitmap.getHeight(),
                                (rotate%180==0) ? bitmap.getHeight() : bitmap.getWidth(),

                        },
                        point);
                mConstraintParent.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCropEdgeQuickView==null) mCropEdgeQuickView = new CropEdgeQuickView();
                        mCropEdgeQuickView.attachAndPresent(MainFragment.this,document);
                        hideLoading();
                    }
                });

            }
        });
    }

    public void refreshData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<DocumentAdapter.DocumentObject> list = new ArrayList<>();
                try {
                    File file = new File(ScanConstants.PDF_PATH);
                    if (file.exists()) {
                        //File[] fileArray = file.listFiles();
                        File[] fileArray = file.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".pdf");
                            }
                        });
                        if(fileArray!= null)
                        for (File f : fileArray) {
                            if (f.isFile() && f.getName().endsWith(".pdf")) {
                                list.add(new DocumentInfo(f.getName(), f.getAbsolutePath(),
                                        Util.humanReadableByteCount(f.length()) + " • " +
                                                DateUtils.formatDateTime(getContext(), f.lastModified(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                                                        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY)));
                            }
                        }
                    }
                } catch (Exception ignored) {}
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        list.add(0, new CountSectionItem("Scanned Documents",list.size()));
                        mAdapter.setData(list);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
    }

    @OnClick(R.id.menu_icon)
    void menuClick() {
        mDrawerParent.openDrawer(GravityCompat.START);
    }

    private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg");
        fileUri = Uri.fromFile(file);
        return file;
    }

    public void openSystemCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile();
        boolean isDirectoryCreated = file.getParentFile().mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri tempFileUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "com.tdh7.documentscanner.provider", // As defined in Manifest
                    file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        } else {
            Uri tempFileUri = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        }
        startActivityForResult(cameraIntent, REQUEST_CODE_PICK_FROM_DEVICE_CAMERA);
    }


    @OnClick(R.id.search_hint)
    void appBarClick() {
        presentFragment(new SearchFragment());
    }

    private void clearTempImages() {
        try {
            File tempFolder = new File(ScanConstants.IMAGE_PATH);
            for (File f : tempFolder.listFiles())
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri fileUri;
    /*private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg");
        fileUri = Uri.fromFile(file);
        return file;
    }*/

    @OnClick(R.id.pick_photo_icon)
    void addPhotoFromGallery() {
        requestOpenMediaGallery();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).executeWriteStorageAction(new Intent(ACTION_PERMISSION_START_UP));
        }
    }

    @OnClick(R.id.pick_camera_icon)
    void openCamera() {
        presentFragment(new CameraPickerFragment());
    }

    public void requestOpenMediaGallery() {
        if(getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).executeWriteStorageAction(new Intent(ACTION_OPEN_MEDIA_PICKER));
    }

    public void openMediaGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_FROM_GALLERY);
    }

    private CropEdgeQuickView mCropEdgeQuickView;

    @Override
    public ViewGroup getParentLayout() {
        return mConstraintParent;
    }

    @Override
    public void onQuickViewAttach() {
        mCropEdgeQuickView.showActionButton();
        if(getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setTheme(false);
    }

    @Override
    public void onQuickViewDetach(float[] points) {
        mCropEdgeQuickView = null;
        if(getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setTheme(true);
    }

    @Override
    public void onActionClick() {
        presentFragment(EditorFragment.newInstance(mCropEdgeQuickView.getBitmapDocument()));
        mCropEdgeQuickView.detach();
    }
    LoadingScreenDialog mLoadingDialog = null;


    private void hideLoading() {
        mConstraintParent.post(()-> {
            if(mLoadingDialog!=null)
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        });
    }

    private void showLoading() {

        mLoadingDialog = LoadingScreenDialog.newInstance(getContext());
        mLoadingDialog.show(getChildFragmentManager(),"LoadingScreenDialog");
    }
}
