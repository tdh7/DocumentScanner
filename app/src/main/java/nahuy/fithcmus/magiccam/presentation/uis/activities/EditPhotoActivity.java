package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;

import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.EditGetProductCommander;
import nahuy.fithcmus.magiccam.presentation.entities.enums.EditTopType;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.BitmapUtils;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.FragmentHelper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoBottomNavFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoTopMainPresentFragment;

/**
 * Created by nahuy on 5/24/2017.
 */

public class EditPhotoActivity extends AppCompatActivity implements FragEditInteract, EditMainContainerInterface {

    private EditTopPresentFragmentCallback topFragment;
    private EditTopPresentFragmentCallback tmpTopFragment;
    private BottomNavigateFragmentInterface bottomFragment;

    // Current Bitmap
    private Bitmap currentBitmap;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_main);

        ButterKnife.bind(this);

        // Get image file.
        Intent fromMainIntent = getIntent();
        // DA_GalleryImageInfo imgInfo = (DA_GalleryImageInfo)fromMainIntent.getSerializableExtra(Constants.GALLERY_IMG_PATH_INTENT_KEY);
        String imagePath = fromMainIntent.getStringExtra(Constants.EDIT_IMG_PATH_INTENT_KEY);
        boolean shouldDeleted = fromMainIntent.getBooleanExtra(Constants.EDIT_IMG_SHOULD_DELETE, false);

        Display mDisplay = getWindowManager().getDefaultDisplay();
        final int width  = mDisplay.getWidth();
        final int height = mDisplay.getHeight();

        currentBitmap = BitmapUtils.getBitmapFromTmpPath(EditPhotoActivity.this, imagePath);

        if(currentBitmap == null){
            // Can't load bitmap
            finish();
        }

        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initFragment() {
        if (topFragment == null) {
            topFragment = EditPhotoTopMainPresentFragment.getInstance();
            tmpTopFragment = topFragment;
        }
        if(bottomFragment == null) {
            bottomFragment = EditPhotoBottomNavFragment.getInstance();
        }
    }

    private void setDefaultFragment(){
        // Top fragment
        FragmentHelper.setUpInvidualFragment(this, R.id.photo_edit_top_fragment, topFragment.getFragment(currentBitmap));
        FragmentHelper.setUpInvidualFragment(this, R.id.photo_edit_bottom_fragment, bottomFragment.getBottomFragment());
    }

    @Override
    public void process(CallingAbstractCommander commander) {
        if (commander instanceof EditGetProductCommander) {
            currentBitmap = tmpTopFragment.getProduct();
            FragmentHelper.setUpInvidualFragment(this, R.id.photo_edit_bottom_fragment, bottomFragment.getBottomFragment());
            FragmentHelper.setUpInvidualFragment(EditPhotoActivity.this
                    , R.id.photo_edit_top_fragment
                    , topFragment.getFragment(currentBitmap));
        } else {
            // Transfer commander to top present
            tmpTopFragment.process(commander);
        }
    }

    @Override
    public void mainContainerNavigate(BottomNavigateFragmentInterface bottomNavigateFragmentInterface) {
        // Calling from navigate bottom fragment item to change edit item
        tmpTopFragment = bottomNavigateFragmentInterface.getTopFragment();
        FragmentHelper.setUpInvidualFragment(EditPhotoActivity.this
                , R.id.photo_edit_top_fragment
                , tmpTopFragment.getFragment(currentBitmap));
        FragmentHelper.setUpInvidualFragment(this
                , R.id.photo_edit_bottom_fragment
                , bottomNavigateFragmentInterface.getBottomFragment());
    }

    @Override
    public void onMyBackPressed() {
        FragmentHelper.setUpInvidualFragment(this, R.id.photo_edit_bottom_fragment, bottomFragment.getBottomFragment());
        FragmentHelper.setUpInvidualFragment(this, R.id.photo_edit_top_fragment, topFragment.getFragment(currentBitmap));
    }

    @Override
    public Context takeContext() {
        return this;
    }

}
