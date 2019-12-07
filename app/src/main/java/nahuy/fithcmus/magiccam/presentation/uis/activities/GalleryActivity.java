package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery.GalleryImageCommander;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryAlbumInfo;
import nahuy.fithcmus.magiccam.presentation.presenters.gallery.GalleryImageLoadingPresenter;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.gallery.GalleryAlbumAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.LineDividerDecorator;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.SquareItemDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.VerticalSpaceItemDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryFragCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryLoadindSubcriber;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.gallery.GalleryDetailFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.gallery.GalleryImageGridFragment;

/**
 * Created by huy on 3/26/2017.
 */

public class GalleryActivity extends AppCompatActivity implements GalleryFragCallback {

    private static final String DEFAULT_GALLERY_NAME = "Albums";
    private static final String CURRENT_SUBCRIBER_SAVE = "current_subcriber";
    private Toolbar galleryToolBar;

    private BroadcastReceiver changeAlbumReceiver;
    private BroadcastReceiver editImageReceiver;

    private GalleryImageLoadingPresenter galleryImageLoadingPresenter = new GalleryImageLoadingPresenter();
    private List<GalleryLoadindSubcriber> subcribers = new ArrayList<>();

    private int currentSubcriber = 0;
    private long currentAlbumId = -1;

    @BindView(R.id.gallery_layout)
    DrawerLayout galleryAlbumListLayout;

    @BindView(R.id.gallery_album_list)
    RecyclerView imageAlbums;

    private ActionBarDrawerToggle mDrawerToggle;

    @BindView(R.id.gallery_intent)
    FrameLayout galleryIntentFragment;

    @BindView(R.id.gallery_album_name)
    TextView currentGalleryName;

    @BindView(R.id.gallery_back_button)
    ImageView backBtn;

    @BindView(R.id.gallery_switch_mode)
    ImageView gallerySwitchMode;

    @BindView(R.id.gallery_choose_album)
    ImageView galleryChooseAlbum;

    private GalleryImageGridFragment galleryImageGridFragment = null;
    private GalleryDetailFragment galleryDetailFragment = null;

    @OnClick(R.id.gallery_back_button)
    public void backAction(){
        this.finish();
    }

    // Switch between grid and detail mode
    @OnClick(R.id.gallery_switch_mode)
    public void switchDisplayMode(){
        detachIntentFragment();
        if(currentSubcriber == subcribers.size() - 1){
            currentSubcriber = 0;
        }
        else{
            currentSubcriber += 1;
        }
        attachNewIntentFragment();
        galleryImageLoadingPresenter.requestUpdateByAlbumName(this, currentAlbumId);
        setModeLabel();
    }

    // Open album list
    @OnClick(R.id.gallery_choose_album)
    public void chooseAlbum(){
        if(galleryAlbumListLayout.isDrawerOpen(Gravity.RIGHT)) {
            galleryAlbumListLayout.closeDrawer(Gravity.RIGHT, true);
        }
        else{
            galleryAlbumListLayout.openDrawer(Gravity.RIGHT, true);
        }
    }

    @SuppressWarnings("getColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstTimeForSecondMode = true;
        // ActivityUtils.setFullScreen(this);
        checkForPermission();

        setContentView(R.layout.activity_gallery);
        galleryToolBar = (Toolbar)findViewById(R.id.gallery_toolbar);

        setSupportActionBar(galleryToolBar);

        ButterKnife.bind(this);

        galleryAlbumListLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.gallery_toolbar_background));
        // GalleryPresenter.getmInstance(this).setCurrentAlbumNameForTextView(currentGalleryName);

        currentGalleryName.setText(DEFAULT_GALLERY_NAME);

        List<DA_GalleryAlbumInfo> albumInfos = galleryImageLoadingPresenter.getListOfAlbums(this);
        constructAlbumList(albumInfos);

        // Reload save state
        if(savedInstanceState != null){
            currentSubcriber = savedInstanceState.getInt(CURRENT_SUBCRIBER_SAVE);
        }

        initSubcriber();
        // Set up fragment
        setUpImg();
        subcribesChannels();

    }



    private void constructAlbumList(List<DA_GalleryAlbumInfo> albumInfos){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        GalleryAlbumAdapter adapter = new GalleryAlbumAdapter(this, R.layout.gallery_album_list_item, albumInfos);
        adapter.setClickListener(new GalleryClickListener() {
            @Override
            public void onClick(Object item) {
                if (item instanceof DA_GalleryAlbumInfo) {
                    DA_GalleryAlbumInfo albumInfo = (DA_GalleryAlbumInfo) item;
                    albumInfo.setAlbumNameForTextView(currentGalleryName);
                    currentAlbumId = albumInfo.getAlbumId();
                    galleryImageLoadingPresenter.requestUpdateByAlbumName(GalleryActivity.this,
                            currentAlbumId);
                }
            }
        });

        imageAlbums.setAdapter(adapter);
        imageAlbums.setLayoutManager(linearLayoutManager);
        imageAlbums.addItemDecoration(new SquareItemDecoration(4));
        imageAlbums.addItemDecoration(new LineDividerDecorator(this, 12));
        imageAlbums.addItemDecoration(new VerticalSpaceItemDecoration(12));
    }

    private void initSubcriber(){
        subcribers.add(new GalleryDetailFragment());
        subcribers.add(new GalleryImageGridFragment());
    }

    private void setUpImg(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.gallery_intent, subcribers.get(currentSubcriber).getFragment());
        ft.commit();
    }

    private void detachIntentFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(subcribers.get(currentSubcriber).getFragment());
        ft.commit();
    }

    private static boolean isFirstTimeForSecondMode;

    private void attachNewIntentFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(!isFirstTimeForSecondMode)
            ft.attach(subcribers.get(currentSubcriber).getFragment());
        else {
            ft.replace(R.id.gallery_intent, subcribers.get(currentSubcriber).getFragment());
            isFirstTimeForSecondMode = false;
        }
        ft.commit();
    }

    private void subcribesChannels(){
        galleryImageLoadingPresenter.subcribeAll(subcribers);
    }

    private void setModeLabel(){
        // Set last display mode icon
        gallerySwitchMode.setImageDrawable(ContextCompat.getDrawable(this, subcribers.get(currentSubcriber).getFragmentLabel()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == APP_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionOk = true;
                // PrepareFirstTimePresenter.prepareAllWork(this);
            }
            else{
                System.exit(0);
            }
        }
    }

    private boolean isPermissionOk = false;
    private final static int APP_PERMISSION = 10001;

    private void checkForPermission(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.INTERNET},
                        APP_PERMISSION);
            }
        }
        else{
            isPermissionOk = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start first time request
        galleryImageLoadingPresenter.requestUpdate(this);
        // Regis change album receiver.
        /*IntentFilter changeAlbumFilter = new IntentFilter(GALLERY_CHANGE_ALBUM_RECEIVER);
        changeAlbumReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GALLERY_CHANGE_ALBUM_RECEIVER)){
                    if(galleryImageGridFragment != null){
                        if(viewMode == GALLERY_VIEW_MODE.GRID_MODE) {
                            getSupportFragmentManager().beginTransaction()
                                    .detach(galleryImageGridFragment)
                                    .attach(galleryImageGridFragment)
                                    .commit();
                        }
                        else{
                            getSupportFragmentManager().beginTransaction()
                                    .detach(galleryDetailFragment)
                                    .attach(galleryDetailFragment)
                                    .commit();
                        }
                    }
                }
            }
        };
        registerReceiver(changeAlbumReceiver, changeAlbumFilter);

        // Regis edit image receiver.
        IntentFilter editImageFilter = new IntentFilter(GALLERY_EDIT_IMAGE_RECEIVER);
        editImageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GALLERY_EDIT_IMAGE_RECEIVER)){
                    String imgPath = intent.getStringExtra(Constants.SAVED_IMG_PATH);
                    Intent editNavigateIntent = new Intent(GalleryActivity.this, EditPhotoActivity.class);
                    editNavigateIntent.putExtra(Constants.SAVED_IMG_PATH, imgPath);
                    startActivity(editNavigateIntent);
                }
            }
        };
        registerReceiver(editImageReceiver, editImageFilter);*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SUBCRIBER_SAVE, currentSubcriber);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void invokeEdit(GalleryImageCommander galleryImageCommander) {
        galleryImageCommander.process(this);
    }
}