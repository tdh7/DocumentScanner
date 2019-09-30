package us.nvb.documentscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.nvb.documentscanner.ui.page.AboutFragment;
import us.nvb.documentscanner.ui.page.BlankFragment;
import us.nvb.documentscanner.ui.permission.PermissionActivity;

public class MainActivity extends PermissionActivity {

    private static final int REQUEST_CODE = 99;
    public static final String ACTION_PERMISSION_START_UP = "permission_start_up";
    //private ImageView scannedImageView;
    private ListView list;
    private ArrayList<String> alist;
    private ArrayAdapter<String>adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initNavigation(savedInstanceState,R.id.container, BlankFragment.class);

        list = findViewById(R.id.list);
        //executeWriteStorageAction(new Intent(ACTION_PERMISSION_START_UP));
        executePermissionAction(new Intent(ACTION_PERMISSION_START_UP),PermissionActivity.PERMISSION_ALL);
        //scannedImageView = (ImageView) findViewById(R.id.scannedImage);
        mAppBar.postDelayed(this::switchToSearchLabel,3000);
    }

    @BindView(R.id.app_bar)
    View mAppBar;

    @BindView(R.id.app_icon)
    View mAppIconView;

    @BindView(R.id.app_title)
    View mAppTitleView;



    private void switchToSearchLabel() {

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
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(fo), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                }
            });
        }
        registerForContextMenu(list);
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

    @OnClick(R.id.fab)
    protected void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.app_bar)
    void appBarClick() {
        presentFragment(new AboutFragment());
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
        presentFragment(new AboutFragment());
    }
}
