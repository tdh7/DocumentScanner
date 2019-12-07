package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.EditNavItem;
import nahuy.fithcmus.magiccam.presentation.presenters.EditItemAccessObject;
import nahuy.fithcmus.magiccam.presentation.uis.activities.EditPhotoActivity;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.PhotoEditNavAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.InvidualSpaceDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;

/**
 * Created by huy on 5/24/2017.
 */

public class EditPhotoBottomNavFragment extends Fragment implements BottomNavigateFragmentInterface {

    @BindView(R.id.bottom_navigate)
    RecyclerView navList;

    private EditMainContainerInterface bottomNavigateFragmentInterface;
    public static EditPhotoBottomNavFragment getInstance(){
        EditPhotoBottomNavFragment editPhotoBottomNavFragment = new EditPhotoBottomNavFragment();
        return editPhotoBottomNavFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bottomNavigateFragmentInterface = (EditPhotoActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_bottom_navigate, null);

        ButterKnife.bind(this, v);

        ArrayList<EditNavItem> items = EditItemAccessObject.getLstOfItems();

        PhotoEditNavAdapter photoEditNavAdapter = new PhotoEditNavAdapter(bottomNavigateFragmentInterface, items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(bottomNavigateFragmentInterface.takeContext(),
                LinearLayoutManager.HORIZONTAL, false);
        navList.setAdapter(photoEditNavAdapter);
        navList.addItemDecoration(new InvidualSpaceDecoration(30,50,45,30));
        navList.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(navList, photoEditNavAdapter, AnimationMaker.MyAnimation.SCALE_IN);

        return v;
    }

    @Override
    public Fragment getBottomFragment() {
        return this;
    }

    @Override
    public EditTopPresentFragmentCallback getTopFragment() {
        return EditPhotoTopMainPresentFragment.getInstance();
    }
}
