package nahuy.fithcmus.magiccam.presentation.uis.fragments.gallery;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery.GalleryGridDisplay;
import nahuy.fithcmus.magiccam.presentation.uis.activities.GalleryActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.SquareItemDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryDisplayStrategy;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryLoadindSubcriber;

/**
 * Created by huy on 2/26/2017.
 */

public class GalleryImageGridFragment extends Fragment implements GalleryLoadindSubcriber {

    private GalleryActivity mainAct;

    private GalleryDisplayStrategy displayStrategy;

    private GalleryClickListener imageClickListener;

    private List<DA_GalleryImageInfo> tmpList;

    @BindView(R.id.gallery_grid_mode_container)
    public RecyclerView imageContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (GalleryActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.gallery_image_grid_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setUpImageContainer();
        updateDisplay(tmpList);
        return rootView;
    }

    private void setUpImageContainer(){
        if(imageContainer == null)
            return;

        int spaceInPixel = 8;

        RecyclerView.LayoutManager gridLayout = new GridLayoutManager(mainAct.getApplicationContext(), 4);
        imageContainer.setLayoutManager(gridLayout);
        imageContainer.addItemDecoration(new SquareItemDecoration(spaceInPixel));

        imageContainer.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void updateDisplay(List<DA_GalleryImageInfo> items) {
        tmpList = items;
        if(displayStrategy == null){
            displayStrategy = new GalleryGridDisplay();
        }
        if(mainAct != null && imageContainer != null)
            displayStrategy.setUpDisplay(getContext(), imageContainer, tmpList, imageClickListener);
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
        return R.drawable.btn_gallery_grid_mode;
    }
}
