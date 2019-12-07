package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.CameraCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GLCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GlColorCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GlToolSizeCommander;
import nahuy.fithcmus.magiccam.presentation.entities.enums.CameraModeAction;
import nahuy.fithcmus.magiccam.presentation.entities.enums.SwitchCamAction;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.ExternalToolCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragMainInteract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes.MediaReceiverContract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.FileNameHelper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.FragmentHelper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.ToastHelper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.SurfaceInteract;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.camera.MainBottomFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.camera.MainChoiceListFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.camera.MainToolChoiceListFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.camera.MainTopFragment;

/**
 * Order of declare variable
 * 1. Constants
 * 2. State
 * 3. Local created variables
 * 4. [Sample data]
 * 5. Reference variables
 */

public class MainActivity extends AppCompatActivity implements FragMainInteract,SurfaceInteract.RecordSI,
        SurfaceInteract.CaptureSI, MediaReceiverContract, ExternalToolCallback {

	private static final String TAG = MainActivity.class.getName();
	private static final String FILTER_SELECT = "FILTER_SELECT";

	private boolean filterSelected = false;
	private boolean toolSelected = false;
    private int makeProductCount = 0;

	private MainBottomFragment mainBottomFragment;
	private MainTopFragment mainTopFragment;
	private MainChoiceListFragment mainChoiceListFragment;
	private MainToolChoiceListFragment mainToolBoxFragment;
	private FragMainInteract fragMainInteract;

    @BindView(R.id.quantity_seek)
    SeekBar quantitySeek;

    @BindView(R.id.quantity_text)
    TextView quantityText;

    @BindView(R.id.color_seek)
    ColorSeekBar colorSeekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.FILE_DIRS = getFilesDir().getAbsolutePath();

		ButterKnife.bind(this);

		// Init fragments.
		initFragment();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onRestoreInstanceState(savedInstanceState, persistentState);

		filterSelected = savedInstanceState.getBoolean(FILTER_SELECT);
		mainBottomFragment.setSca((SwitchCamAction)savedInstanceState.getSerializable(MainBottomFragment.SCA_STATE));
		mainBottomFragment.setCma((CameraModeAction)savedInstanceState.getSerializable(MainBottomFragment.CMA_STATE));
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume -- acquiring camera");
		super.onResume();
		defaultSetUpFragment();
        Log.d(TAG, "onResume complete: " + this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause -- releasing camera");
        super.onPause();
        Log.d(TAG, "onPause complete");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		outState.putBoolean(FILTER_SELECT, filterSelected);

		// Save bottom fragment state
		outState.putSerializable(MainBottomFragment.SCA_STATE, mainBottomFragment.getSca());
		outState.putSerializable(MainBottomFragment.CMA_STATE, mainBottomFragment.getCma());
    }

	private void initFragment(){
		if(mainTopFragment == null){
			mainTopFragment = MainTopFragment.getInstance();
			fragMainInteract = mainTopFragment;
		}
		if(mainBottomFragment == null){
			mainBottomFragment = MainBottomFragment.getInstance();
		}
		if(mainChoiceListFragment == null){
			mainChoiceListFragment = MainChoiceListFragment.getInstance();
		}
        if(mainToolBoxFragment == null){
            mainToolBoxFragment = MainToolChoiceListFragment.getInstance();
        }
	}

	private void defaultSetUpFragment(){
		setUpInvidualFragment(R.id.camera_top_fragment, mainTopFragment);
		setUpInvidualFragment(R.id.camera_bottom_fragment, mainBottomFragment);
	    setToolFragment();
        setFilterFragment();
	}

    private void setUpInvidualFragment(int id, Fragment fragment){
        FragmentHelper.setUpInvidualFragment(this, id, fragment);
    }

	private void setFilterFragment(){
        setUpInvidualFragment(R.id.filter_list_fragment, mainChoiceListFragment);
		filterSelected = !filterSelected;
		// If on -> off, off -> on
        popFilterList();
	}

	public void popFilterList() {
		if (filterSelected) {
			// Hide it.
			hideFilterFragment();
		} else {
			// Show it.
			showFilterFragment();
		}
	}

    private void setToolFragment(){
        setUpInvidualFragment(R.id.tool_fragment, mainToolBoxFragment);
        toolSelected = !toolSelected;
        // If on -> off, off -> on
        popToolList();
    }


    public void popToolList() {
        if (toolSelected) {
            // Hide it.
            hideToolFragment();
        } else {
            // Show it.
            showToolFragment();
        }
    }

    @Override
    public void popColorBar() {
        colorSeekBar.setVisibility(View.VISIBLE);
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int i2) {
                int intColor = colorSeekBar.getColor();
                int r = Color.red(intColor);
                int g = Color.green(intColor);
                int b = Color.blue(intColor);
                fragMainInteract.interactRender(new GlColorCommander(r, g, b));
            }
        });
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                colorSeekBar.setColorBarPosition(0);
                int intColor = colorSeekBar.getColor();
                int r = Color.red(intColor);
                int g = Color.green(intColor);
                int b = Color.blue(intColor);
                fragMainInteract.interactRender(new GlColorCommander(r, g, b));
            }
        }, 300);
    }

    private boolean setProgress = false;

    @Override
    public void popQuantityBar() {
        quantitySeek.setVisibility(View.VISIBLE);
        quantityText.setVisibility(View.VISIBLE);
        if(setProgress == false) {
            quantitySeek.setProgress(18);
            setProgress = true;
        }
        quantityText.setText("" + quantitySeek.getProgress());
        quantitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fragMainInteract.interactRender(new GlToolSizeCommander(progress));
                quantityText.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragMainInteract.interactRender(new GlToolSizeCommander(quantitySeek.getProgress()));
            }
        }, 300);
    }

    @Override
    public void popDownColorBar() {
        colorSeekBar.setVisibility(View.GONE);
    }

    @Override
    public void popDownQuantityBar() {
        quantitySeek.setVisibility(View.GONE);
        quantityText.setVisibility(View.GONE);
    }

	@Override
	public void recvRecord(URI path) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "File save as: " + path.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void recvBm(Bitmap bm) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
							"Huy" + (new Date()).toString() + ".jpg");
		
		try{
			bm.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(output));
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		    Uri contentUri = Uri.fromFile(output);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
			Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show();
		}
		catch(IOException ioe){
			Log.e("ERROR BM", ioe.getMessage());
			Toast.makeText(this, "Save fail", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void interactCamera(CameraCommander camAct) {
		fragMainInteract.interactCamera(camAct);
	}

	@Override
	public void interactRender(GLCommander renderAct) {
        fragMainInteract.interactRender(renderAct);
	}

    @Override
    public void receiveMediaUri(final Uri mediaUri) {

        if(mediaUri == null){
            ToastHelper.shortNoti(this, "NULL TYPE");
            return;
        }
        String mimeType = null;

        String fileExtension = FileNameHelper.getExtensionFromFileName(mediaUri.toString());

        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase());

        if(mimeType.contains("image")){
            // Navigate to edit image activity
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, EditPhotoActivity.class);
                    intent.putExtra(Constants.EDIT_IMG_PATH_INTENT_KEY, mediaUri.toString());
                    intent.putExtra(Constants.EDIT_IMG_SHOULD_DELETE, true);
                    startActivity(intent);
                }
            });
        }
        else if(mimeType.contains("video")){
            // Navigate to edit video activity
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, EditVideoActivity.class);
                    intent.putExtra(Constants.SENDING_VIDEO_PATH, mediaUri.toString());
                    intent.putExtra(Constants.SHOULD_DELETE_VIDEO, true);
                    startActivity(intent);
                }
            });
        }

    }

    private void hideToolFragment(){
        FragmentHelper.hideFilterFragment(this, mainToolBoxFragment);
        toolSelected = false;
    }

    private void showToolFragment(){
        FragmentHelper.showFilterFragment(this, mainToolBoxFragment);
		// Close filter fragment if any
		FragmentHelper.hideFilterFragment(this, mainChoiceListFragment);
		filterSelected = false;
		toolSelected = true;
    }

    private void hideFilterFragment(){
        FragmentHelper.hideFilterFragment(this, mainChoiceListFragment);
        filterSelected = false;
    }

    private void showFilterFragment(){
        FragmentHelper.showFilterFragment(this, mainChoiceListFragment);
		// Close filter fragment if any
		FragmentHelper.hideFilterFragment(this, mainToolBoxFragment);
		toolSelected = false;
		filterSelected = true;
    }

    @Override
    public void popClearButton() {
        mainBottomFragment.showClearBox();
    }

    @Override
    public void popDownClearButton() {
        mainBottomFragment.hideClearBox();
    }

    @Override
    public void popShowHideButton() {
        mainBottomFragment.showShowHideBox();
    }

    @Override
    public void popDownShowHideButton() {
        mainBottomFragment.hideShowHideBox();
    }
}
