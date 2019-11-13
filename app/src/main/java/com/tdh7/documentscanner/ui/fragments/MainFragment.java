package com.tdh7.documentscanner.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.picker.CropEdgeQuickView;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.util.ScanConstants;
import com.tdh7.documentscanner.util.Util;
import com.tdh7.documentscanner.util.Utils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tdh7.documentscanner.ui.MainActivity.ACTION_OPEN_MEDIA_PICKER;
import static com.tdh7.documentscanner.ui.MainActivity.ACTION_PERMISSION_START_UP;

public class MainFragment extends NavigationFragment implements CropEdgeQuickView.QuickViewActionCallback {
    public static final String TAG ="MainFragment";

    public static final int REQUEST_CODE_START_SCAN_BY_LIBRARY = 10;
    public final static int REQUEST_CODE_PICK_FROM_GALLERY = 11;
    public final static int REQUEST_CODE_PICK_FROM_DEVICE_CAMERA = 12;


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    SavedDocumentAdapter mAdapter = new SavedDocumentAdapter();

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

    private ArrayList<String> alist;
    private ArrayAdapter<String>adap;

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
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //executeWriteStorageAction(new Intent(ACTION_PERMISSION_START_UP));
      //  executePermissionAction(new Intent(ACTION_PERMISSION_START_UP), PermissionActivity.PERMISSION_ALL);
        mAppBar.postDelayed(this::hideLogo,3000);
    }

    @BindView(R.id.list) ListView mListView;
    @BindView(R.id.search_hint) View mSearchHint;
    @BindView(R.id.pick_camera_icon) View mCameraIcon;
    @BindView(R.id.pick_photo_icon) View mAddImageIcon;

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
            case REQUEST_CODE_START_SCAN_BY_LIBRARY:
                if(resultCode==Activity.RESULT_OK) {
                    //Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    //Bitmap bitmap = null;
                    try {
                        //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        //getContentResolver().delete(uri, null, null);
                        //scannedImageView.setImageBitmap(bitmap);
                        String fstr = data.getStringExtra(ScanConstants.SCANNED_RESULT);
                        alist.add(fstr);
                        if (alist.size() == 1) {
                            adap = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, alist);
                            mListView.setAdapter(adap);
                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    File fo = new File(ScanConstants.PDF_PATH + "/" + (String) mListView.getItemAtPosition(position));
                                    if (fo.exists()) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(fo), "application/pdf");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            adap.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_PICK_FROM_DEVICE_CAMERA:
            case REQUEST_CODE_PICK_FROM_GALLERY:
                Bitmap bitmap = null;
                try {
                    bitmap = Utils.getBitmapWithUri(getContext(), data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    showQuickView(bitmap);
                }
        }
    }

    private void showQuickView(Bitmap bitmap) {
        float[] point = new float[8];
        Util.getDefaultValue(point);
        RawBitmapDocument document = new RawBitmapDocument(bitmap,0,new float[]{bitmap.getWidth(),bitmap.getHeight()}, point);
        if(mCropEdgeQuickView==null) mCropEdgeQuickView = new CropEdgeQuickView();
        mCropEdgeQuickView.attachAndPresent(this,document);
    }

    @BindView(R.id.empty_list_view)
    View mEmptyView;

    public void showSavedList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        mAdapter.setData(list);


        if(true) return;
        mListView.setEmptyView(mEmptyView);
        File file = new File(ScanConstants.PDF_PATH);
        alist = new ArrayList<>();
        if (file.exists()) {
            File[] cfile = file.listFiles();
            for (File f : cfile) {
                if (f.isFile() && f.getName().endsWith(".pdf")) {
                    alist.add(f.getName());
                }
            }
        }

        if (alist.size() > 0) {
            adap = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, alist);
            mListView.setAdapter(adap);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Util.requestOtherAppToOpenThisFile(getContext(), ScanConstants.PDF_PATH,(String) mListView.getItemAtPosition(position));
                }
            });
        }
        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(alist.get(info.position));
            menu.add(Menu.NONE, 0, 0, "Chia sẻ tài liệu");
            menu.add(Menu.NONE, 1, 1, "Xóa tài liệu");
        }
    }

    File fndel;
    int fidel = -1;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        File fl = new File(ScanConstants.PDF_PATH + "/" + alist.get(info.position));

        fndel = null;
        fidel = -1;

        if (fl.exists()) {
            switch (menuItemIndex) {
                case 0:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fl));
                    sendIntent.setType("application/pdf");
                    startActivity(sendIntent);
                    break;
                case 1:
                    fndel = fl;
                    fidel = info.position;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Bạn chắc chắn xóa?");
                    builder.setPositiveButton("Chấp nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (fndel != null && fidel >= 0) {
                                fndel.delete();
                                alist.remove(fidel);
                                adap.notifyDataSetChanged();
                                if (alist.size() <= 0) {
                                    mListView.setAdapter(null);
                                }
                            }
                            fndel = null;
                            fidel = -1;
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    break;
                case 2:
                    break;
            }
        }
        return true;
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @OnClick(R.id.menu_icon)
    void menuClick() {
        //presentFragment(new AboutFragment());
        mDrawerParent.openDrawer(GravityCompat.START);
    }


    protected void startScanByLibrary() {
       /* Intent intent = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_START_SCAN_BY_LIBRARY);*/
       // Intent intent = new Intent(getActivity(), ScanActivity.class);
       // startActivityForResult(intent, REQUEST_CODE_START_SCAN_BY_LIBRARY);
    }

    /*@BindView(R.id.scanned_image)
    TouchImageView mScannedImage;*/

    public void openSystemCamera() {
   /*     Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
        startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE);*/
    }

    @OnClick(R.id.search_hint)
    void appBarClick() {
        presentFragment(new SearchFragment());
    }

    private void clearTempImages() {
      /*  try {
            File tempFolder = new File(ScanConstants.IMAGE_PATH);
            for (File f : tempFolder.listFiles())
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
}
