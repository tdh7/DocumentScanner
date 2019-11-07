package com.tdh7.documentscanner.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ldt.navigation.NavigationFragment;
import com.scanlibrary.ScanConstants;
import com.scanlibrary.ScannerActivity;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.util.Util;

import java.io.File;
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

public class MainFragment extends NavigationFragment {
    public static final String TAG ="MainFragment";

    public static final int REQUEST_CODE_START_SCAN_BY_LIBRARY = 10;
    public final static int REQUEST_CODE_PICK_FROM_GALLERY = 11;
    public final static int REQUEST_CODE_PICK_FROM_DEVICE_CAMERA = 12;

    @BindDimen(R.dimen.dp_unit)
    float mOneDp;

    @BindView(R.id.drawer_parent)
    DrawerLayout mDrawerParent;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_START_SCAN_BY_LIBRARY:
            case REQUEST_CODE_PICK_FROM_DEVICE_CAMERA:
            case REQUEST_CODE_PICK_FROM_GALLERY:
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

        }
    }

    @BindView(R.id.empty_list_view)
    View mEmptyView;

    public void showSavedList() {
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
                    File fo = new File(ScanConstants.PDF_PATH + "/" + (String) mListView.getItemAtPosition(position));
                    if (fo.exists()) {
                        openFile(ScanConstants.PDF_PATH,(String) mListView.getItemAtPosition(position));

                        /*Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(fo), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            Toasty.error(MainActivity.this,R.string.open_pdf_error).show();
                        }*/
                    }
                }
            });
        }
        registerForContextMenu(mListView);
    }

    private void openFile(String directory, String file) {
        try {
            File filePath = new File(directory);
            File fileToWrite = new File(filePath, file);
            final Uri data = FileProvider.getUriForFile(getContext(), "com.scanlibrary.provider", fileToWrite);
            getActivity().grantUriPermission(getActivity().getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String fileExtension = file.substring(file.lastIndexOf("."));
            Log.d(TAG, "onItemClick: extension " + fileExtension);
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            if (fileExtension.contains("apk")) {
                Log.d(TAG, "open as apk");
                intent.setDataAndType(data, "application/vnd.android.package-archive");
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            else
                intent.setData(data);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toasty.error(App.getInstance(),"Not found any app that could open this file").show();
        } catch (Exception e) {
            Toasty.error(App.getInstance(),"Sorry, something went wrong").show();
            Log.d(TAG, "exception when trying to open file: "+e.getMessage());
        }
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

    @OnClick(R.id.pick_photo_icon)
    protected void startScanByLibrary() {
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_START_SCAN_BY_LIBRARY);
    }

    public void openSystemCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile();
        boolean isDirectoryCreated = file.getParentFile().mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri tempFileUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "com.scanlibrary.provider", // As defined in Manifest
                    file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        } else {
            Uri tempFileUri = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        }
        startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE);
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
    private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg");
        fileUri = Uri.fromFile(file);
        return file;
    }

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
}
