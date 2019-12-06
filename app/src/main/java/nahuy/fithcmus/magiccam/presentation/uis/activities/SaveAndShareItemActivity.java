package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.presenters.ShareItemAccessObject;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.RightHorizontalListDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.ToastHelper;

import static nahuy.fithcmus.magiccam.R.string.share;

/**
 * Created by huy on 3/12/2017.
 */

public class SaveAndShareItemActivity extends AppCompatActivity implements View.OnTouchListener{

    private String shareItem = "";

    // false is video and true is image
    private boolean shareType;

    @OnClick(R.id.gallery_back)
    public void back(){
        this.finish();
    }

    @OnClick(R.id.share_goto_home)
    public void backToHome(){
        Intent intent = new Intent(this, NavigateActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.share_sharing)
    public void doSharing(){
        startAShare();
    }

    @BindView(R.id.share_goto_home)
    LinearLayout shareBackHomeButton;

    @BindView(R.id.share_sharing)
    LinearLayout shareSharingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        shareType = intent.getBooleanExtra(Constants.SHARE_TYPE, false);

        if(shareType == true) {
            shareItem = intent.getStringExtra(Constants.SAVED_IMG_PATH);
        }
        else{
            shareItem = intent.getStringExtra(Constants.SHARE_VIDEO);
        }
    }

    public void startAShare(){
        if(!shareItem.equals("")) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Text");

            if (shareType == false) {
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse(shareItem));
                shareIntent.setType("video/mp4");
            } else {
                try {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(
                            MediaStore.Images.Media.insertImage(getContentResolver(),
                                    shareItem, "I am Happy", "Share happy !")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                shareIntent.setType("image/jpeg");
            }
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "send"));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.rounded_share_shape);

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            drawable = ContextCompat.getDrawable(this, R.drawable.rounded_share_shape_pressed);
        }

        if(v == shareBackHomeButton){
            shareBackHomeButton.setBackground(drawable);
            return true;
        }
        else if(v == shareSharingButton){
            shareBackHomeButton.setBackground(drawable);
            return true;
        }

        return false;
    }
}
