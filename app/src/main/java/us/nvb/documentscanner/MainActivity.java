package us.nvb.documentscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99;
    //private ImageView scannedImageView;
    private ListView list;
    private ArrayList<String> alist;
    private ArrayAdapter<String>adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        list = findViewById(R.id.list);
        list.setEmptyView(findViewById(R.id.empty_list));
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
        //scannedImageView = (ImageView) findViewById(R.id.scannedImage);
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

    protected void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_exit:
                finish();
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
