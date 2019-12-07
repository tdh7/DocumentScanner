package nahuy.fithcmus.magiccam.presentation.uis.fragments.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery.GalleryDetailDisplay;
import nahuy.fithcmus.magiccam.presentation.uis.activities.GalleryActivity;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryDisplayStrategy;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryLoadindSubcriber;

/**
 * Created by huy on 2/28/2017.
 */

public class GalleryDetailFragment extends Fragment implements GalleryLoadindSubcriber {

    private GalleryActivity mainAct;

    private GalleryDisplayStrategy displayStrategy;

    private GalleryClickListener imageClickListener;

    private List<DA_GalleryImageInfo> tmpList;

    @BindView(R.id.gallery_to_camera)
    public ImageView navigateCameraButton;

    @BindView(R.id.gallery_detail_mode_container)
    public RecyclerView detailContainer;

    @OnClick(R.id.gallery_to_camera)
    public void changeToCamera(){
        Intent intentToCamera = new Intent(mainAct, MainActivity.class);
        mainAct.startActivity(intentToCamera);
    }

    public static GalleryDetailFragment getInstance(){
        GalleryDetailFragment instance = new GalleryDetailFragment();
        Bundle bundle = new Bundle();
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (GalleryActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.gallery_detail_fragment, container, false);
        ButterKnife.bind(this, rootView);

        // Set image for camera button
        navigateCameraButton.setImageDrawable(ContextCompat.getDrawable(mainAct, R.drawable.btn_gallery_camera));

        setUpDetailContainer();

        // Waiting list
        updateDisplay(tmpList);

        return rootView;
    }

    private void setUpDetailContainer() {
        if (detailContainer == null) {
            return;
        }

        // Set layout for recycler view
        RecyclerView.LayoutManager verticalLayout = new LinearLayoutManager(mainAct, LinearLayoutManager.VERTICAL, false);
        detailContainer.setLayoutManager(verticalLayout);

        detailContainer.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void updateDisplay(List<DA_GalleryImageInfo> items) {
        tmpList = items;
        setUpDetailContainer();
        if(displayStrategy == null){
            displayStrategy = new GalleryDetailDisplay();
        }
        if(mainAct != null && detailContainer != null)
            displayStrategy.setUpDisplay(getContext(), detailContainer, tmpList, imageClickListener);
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public int getFragmentLabel() {
        return R.drawable.btn_gallery_detail_mode;
    }
}
