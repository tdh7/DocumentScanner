package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;

/**
 * Created by huy on 6/20/2017.
 */

public class EditVideoActivity extends AppCompatActivity implements OnTrimVideoListener{

    @BindView(R.id.video_trimmer)
    K4LVideoTrimmer mVideoTrimmer;

    @BindView(R.id.process_video)
    GeometricProgressView geometricProgressView;

    Uri videoUri;
    boolean shouldDeleted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_video);
        ButterKnife.bind(this);
// intent.getStringExtra(Constants.SENDING_VIDEO_PATH)
        // "/storage/9016-4EF8/DCIM/Camera/20160406_122610.mp4"
        // Get video uri from main
        Intent intent = getIntent();
        videoUri = Uri.parse(intent.getStringExtra(Constants.SENDING_VIDEO_PATH));
        shouldDeleted = intent.getBooleanExtra(Constants.SHOULD_DELETE_VIDEO, false);

        mVideoTrimmer.setMaxDuration(100);
        mVideoTrimmer.setOnTrimVideoListener(this);
        long time = System.currentTimeMillis();
        mVideoTrimmer.setDestinationPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/");
        mVideoTrimmer.setVideoURI(videoUri);
    }

    @Override
    public void getResult(Uri uri) {
        Intent shareIntent = new Intent(this, SaveAndShareItemActivity.class);
        shareIntent.putExtra(Constants.SHARE_VIDEO, uri.toString());
        shareIntent.putExtra(Constants.SHARE_TYPE, false);
        startActivity(shareIntent);
    }

    @Override
    public void cancelAction() {
        finish();
    }
}
