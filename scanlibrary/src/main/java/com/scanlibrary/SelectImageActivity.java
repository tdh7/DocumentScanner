package com.scanlibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectImageActivity extends AppCompatActivity {

    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private ImageView mImageView;
    private Button mSelectButton;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    public static ArrayList<Image> mImageArrayList;
    public int mCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(SelectImageActivity.this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);


        setContentView(R.layout.activity_select_image);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_recyclerview);
        mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mImageArrayList = new ArrayList<>();
        getImagesFromAlbum();
        ImageAdapter mImageAdapter;
        mImageAdapter = new ImageAdapter(SelectImageActivity.this,  mImageArrayList);
        mRecyclerView.setAdapter(mImageAdapter);
        mSelectButton = (Button)findViewById(R.id.btn_selectimages);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i<mImageArrayList.size();i++)
                {
                    if(mImageArrayList.get(i).isCheck())
                    {
                        mCount++;
                    }
                }
                if(mCount < 2) Toast.makeText(SelectImageActivity.this, mCount + " image selected", Toast.LENGTH_SHORT).show();
                if(mCount > 1) Toast.makeText(SelectImageActivity.this, mCount + " images selected", Toast.LENGTH_SHORT).show();
                mCount = 0;
            }
        });

    }


    private void getImagesFromAlbum()
    {

        String  [] projectionBucket = {MediaStore.MediaColumns.DATA};


        Cursor cursor = getContentResolver().query(mImageUri, projectionBucket, null, null, null);
        while (cursor.moveToNext())
        {
            mImageArrayList.add(new Image(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))));
        }

    }
}
