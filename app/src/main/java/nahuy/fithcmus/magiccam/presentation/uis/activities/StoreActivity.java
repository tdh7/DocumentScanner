package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.presenters.StorePresenter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.FragmentHelper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.ToastHelper;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.store.StoreIntentFragment;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreActivity extends AppCompatActivity implements StorePresenter.StoreViewSurface{

    StoreIntentFragment storeIntentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StorePresenter.queryKey(this, this);
    }

    @Override
    public void onQueryKeySuccess() {
        ToastHelper.shortNoti(this, "Query key success!");
        storeIntentFragment = StoreIntentFragment.getInstance();
        FragmentHelper.setUpInvidualFragment(this, R.id.store_intent, storeIntentFragment);
    }

    @Override
    public void onQueryKeyFail() {
        ToastHelper.shortNoti(this, "Query key fail!");
        finish();
    }
}
