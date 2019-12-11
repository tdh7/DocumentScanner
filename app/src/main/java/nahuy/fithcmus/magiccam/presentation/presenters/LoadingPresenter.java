package nahuy.fithcmus.magiccam.presentation.presenters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.MainNavItem;
import nahuy.fithcmus.magiccam.presentation.uis.activities.GalleryActivity;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.ToastHelper;

import static nahuy.fithcmus.magiccam.presentation.Constants.DEBUG_MODE;

/**
 * Created by huy on 6/17/2017.
 */

public class LoadingPresenter {

    private boolean isPermissionOk = false;
    private final int APP_PERMISSION = 10001;
    private LoadingViewSurface loadingViewSurface;

    public LoadingPresenter(LoadingViewSurface loadingViewSurface) {
        this.loadingViewSurface = loadingViewSurface;
    }

    public int getAppPermission() {
        return APP_PERMISSION;
    }

    public boolean isPermissionOk() {
        return isPermissionOk;
    }

    public void setPermissionOk(boolean permissionOk){
        this.isPermissionOk = permissionOk;
    }

    private void startAsyncCreateTask(Context context){
        (new AsyncTask<Context, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Context... params) {
                if(params[0] != null) {
                    boolean result1 = prepareDatabaseFile(params[0]);
                    boolean result2 = prepareDatabaseData(params[0]);
                    return result1 & result2;
                }
                else{
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    loadingViewSurface.loadDatabaseSuccess();
                }
                else{
                    loadingViewSurface.loadDatabaseFail();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            private boolean prepareDatabaseData(Context context) {
                boolean result1 = LocalSyncLoadingPresenter.getInstance().copyAssets(context);
                boolean result2 = LocalSyncLoadingPresenter.getInstance().loadDataInDatabase(context);
                return result1 & result2;
            }

            private boolean prepareDatabaseFile(Context context) {
                return LocalSyncLoadingPresenter.getInstance().copyDatabase(context);
            }

        }).execute(context);
    }

    public void checkForPermission(Activity activity){

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ToastHelper.longNoti(activity, activity.getResources().getString(R.string.notify_permission_mess));

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.INTERNET},
                        APP_PERMISSION);

            } else {

                ActivityCompat.requestPermissions(activity,
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

    public void doLoading(Context context){
        boolean isCreated = LocalSyncLoadingPresenter.getInstance().checkIfAlreadyInit(context);
        if(isCreated){
            if(!DEBUG_MODE) {
                return;
            }
            else {
                startAsyncCreateTask(context);
            }
        }
        else{
            // Start async created task
            startAsyncCreateTask(context);
        }
    }

    public ArrayList<MainNavItem> getMainNavItems() {
        ArrayList<MainNavItem> items = new ArrayList<>();

        items.add(new MainNavItem("Camera", R.drawable.ic_linked_camera_black_24dp, MainActivity.class));
        items.add(new MainNavItem("Gallery", R.drawable.ic_nav_gallery_trap, GalleryActivity.class));

        return items;
    }

    public interface LoadingViewSurface{
        void loadDatabaseSuccess();
        void loadDatabaseFail();
    }
}
