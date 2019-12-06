package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.InvokeActivityCommander;
import nahuy.fithcmus.magiccam.presentation.entities.MainNavItem;
import nahuy.fithcmus.magiccam.presentation.presenters.LoadingPresenter;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.MainNavItemAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.MainNavigateCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.ToastHelper;

/**
 * Created by huy on 6/2/2017.
 */

public class NavigateActivity extends AppCompatActivity implements MainNavigateCallback, LoadingPresenter.LoadingViewSurface {

    private LoadingPresenter loadingPresenter;

    @BindView(R.id.navigate_app_recycle)
    RecyclerView navigateItems;

    @BindView(R.id.navigate_app_store)
    ImageView navigateToStore;

    @BindView(R.id.navigate_app_setting)
    ImageView navigateToSetting;

    @OnClick(R.id.navigate_app_store)
    public void storeClick(){
        ToastHelper.shortNoti(this, "Not supported yet");
        /*Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);*/
    }

    @OnClick(R.id.navigate_app_setting)
    public void settingClick(){
        ToastHelper.shortNoti(this, "Not supported yet");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Constants.DEBUG_MODE = true;
        super.onCreate(savedInstanceState);
        loadingPresenter = new LoadingPresenter(this);
        loadingPresenter.checkForPermission(this);
        if(loadingPresenter.isPermissionOk()){
            loadingPresenter.doLoading(this);
        }
        setContentView(R.layout.activity_navigate);
        ButterKnife.bind(this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        // Prepare data.
        ArrayList<MainNavItem> lstOfItems = loadingPresenter.getMainNavItems();

        MainNavItemAdapter mainNavItemAdapter = new MainNavItemAdapter(this, this, lstOfItems);

        navigateItems.setAdapter(mainNavItemAdapter);

        // Prepare layout.
        GridLayoutManager gridLayout = new GridLayoutManager(this, 2);
        navigateItems.setLayoutManager(gridLayout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == loadingPresenter.getAppPermission()){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadingPresenter.setPermissionOk(true);
                loadingPresenter.doLoading(this);
            }
            else{
                // Notice or something
            }
        }
    }

    @Override
    public void processNavRequest(InvokeActivityCommander invokeActivityCommander) {
        if(loadingPresenter.isPermissionOk()) {
            invokeActivityCommander.process(this);
        }
        else{
            loadingPresenter.checkForPermission(this);
        }
    }

    @Override
    public void loadDatabaseSuccess() {
        ToastHelper.shortNoti(this, "Load success");
    }

    @Override
    public void loadDatabaseFail() {
        ToastHelper.shortNoti(this, "Load fail");
    }
}