package com.tdh7.documentscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import com.tdh7.documentscanner.ui.page.BlankFragment;
import com.tdh7.documentscanner.ui.page.SearchFragment;
import com.tdh7.documentscanner.ui.permission.PermissionActivity;

public class MainActivity extends PermissionActivity {
    public static final String TAG ="MainActivity";

    private static final int REQUEST_CODE = 99;
    public static final String ACTION_PERMISSION_START_UP = "permission_start_up";
    //private ImageView scannedImageView;
    private ListView list;
    private ArrayList<String> alist;
    private ArrayAdapter<String>adap;

    @BindDimen(R.dimen.dp_unit)
    float mOneDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDrawerParent.setScrimColor(0x33000000);
        mDrawerParent.setDrawerElevation(mOneDp * 2f);
        initNavigation(savedInstanceState,R.id.container, BlankFragment.class);

        list = findViewById(R.id.list);
        //executeWriteStorageAction(new Intent(ACTION_PERMISSION_START_UP));
        executePermissionAction(new Intent(ACTION_PERMISSION_START_UP),PermissionActivity.PERMISSION_ALL);
        //scannedImageView = (ImageView) findViewById(R.id.scannedImage);
        mAppBar.postDelayed(this::hideLogo,3000);
    }

    @BindView(R.id.drawer_parent)
    DrawerLayout mDrawerParent;

    @BindView(R.id.app_bar) View mAppBar;
    @BindView(R.id.app_icon) View mAppLogoIcon;
    @BindView(R.id.app_title) View mAppTitle;
    @BindView(R.id.search_hint) View mSearchHint;
    @BindView(R.id.camera_icon) View mCameraIcon;
    @BindView(R.id.add_image_icon) View mAddImageIcon;

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
    public void onRequestPermissionsResult(Intent intent, int permissionType, boolean granted) {
        if(intent!=null) {
            String action = intent.getAction();
            if(action!=null)
            switch (action) {
                case ACTION_PERMISSION_START_UP:
                    if(permissionType==PermissionActivity.PERMISSION_STORAGE && granted)
                    showSavedList();
                    break;
            }
        }
    }

    private void showSavedList() {
        list.setEmptyView(findViewById(R.id.empty_list_view));
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
            adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alist);
            list.setAdapter(adap);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    File fo = new File(ScanConstants.PDF_PATH + "/" + (String) list.getItemAtPosition(position));
                    if (fo.exists()) {
                        openFile(ScanConstants.PDF_PATH,(String) list.getItemAtPosition(position));

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
        registerForContextMenu(list);
    }

    private void openFile(String directory, String file) {
        try {
            File filePath = new File(directory);
            File fileToWrite = new File(filePath, file);
            final Uri data = FileProvider.getUriForFile(this, "com.scanlibrary.provider", fileToWrite);
            grantUriPermission(getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
            Toasty.error(this,"Not found any app that could open this file").show();
        } catch (Exception e) {
            Toasty.error(this,"Sorry, something went wrong").show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Bạn chắc chắn xóa?");
                    builder.setPositiveButton("Chấp nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (fndel != null && fidel >= 0) {
                                fndel.delete();
                                alist.remove(fidel);
                                adap.notifyDataSetChanged();
                                if (alist.size() <= 0) {
                                    list.setAdapter(null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            //Bitmap bitmap = null;
            try {
                //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //getContentResolver().delete(uri, null, null);
                //scannedImageView.setImageBitmap(bitmap);
                String fstr = data.getStringExtra(ScanConstants.SCANNED_RESULT);
                alist.add(fstr);
                if (alist.size() == 1) {
                    adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alist);
                    list.setAdapter(adap);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            File fo = new File(ScanConstants.PDF_PATH + "/" + (String) list.getItemAtPosition(position));
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
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @OnClick(R.id.menu_icon)
    void menuClick() {
        //presentFragment(new AboutFragment());
        mDrawerParent.openDrawer(GravityCompat.START);
    }

    /*
    @OnClick(R.id.fab)
    */
    @OnClick(R.id.camera_icon)
    protected void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.search_hint)
    void appBarClick() {
        presentFragment(new SearchFragment());
    }

    @OnClick(R.id.add_image_icon)
    void addPhoto() {

    }

    @OnClick(R.id.camera_icon)
    void openCamera() {

    }
}
