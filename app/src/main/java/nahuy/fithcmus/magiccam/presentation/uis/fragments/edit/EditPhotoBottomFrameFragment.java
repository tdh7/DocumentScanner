package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.content.Context;
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
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.EditGetProductCommander;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.presenters.edit_photo.FrameAccessPresenter;
import nahuy.fithcmus.magiccam.presentation.uis.activities.EditPhotoActivity;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.edit_photo.EditMainFrameAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.InvidualSpaceDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;

/**
 * Created by huy on 6/1/2017.
 */

public class EditPhotoBottomFrameFragment extends Fragment implements BottomNavigateFragmentInterface {

    private Context context;
    private FragEditInteract fragEditInteract;
    private EditMainContainerInterface mainContainerInterface;
    private ArrayList<Frame> frameShaders;
    FrameAccessPresenter frameAccessPresenter = new FrameAccessPresenter();

    @BindView(R.id.filter_list)
    RecyclerView mainFilterList;

    @OnClick(R.id.edit_cancel)
    void cancelEdit(){
        mainContainerInterface.onMyBackPressed();
    }

    @OnClick(R.id.edit_save)
    void saveEdit(){
        fragEditInteract.process(new EditGetProductCommander());
    }

    public static EditPhotoBottomFrameFragment getInstance(){
        EditPhotoBottomFrameFragment editPhotoBottomFilterFragment = new EditPhotoBottomFrameFragment();
        return editPhotoBottomFilterFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragEditInteract = (EditPhotoActivity)getActivity();
        mainContainerInterface = (EditPhotoActivity)getActivity();
        frameShaders = frameAccessPresenter.getAllFrames(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_edit_bottom_frame, null);

        ButterKnife.bind(this, v);

        EditMainFrameAdapter mfa = new EditMainFrameAdapter(context, fragEditInteract, frameShaders);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mainFilterList.setAdapter(mfa);
        mainFilterList.addItemDecoration(new InvidualSpaceDecoration(20,10,20,10));
        mainFilterList.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(mainFilterList, mfa, AnimationMaker.MyAnimation.SLIDE_IN);

        return v;
    }

    @Override
    public Fragment getBottomFragment() {
        return EditPhotoBottomFrameFragment.getInstance();
    }

    @Override
    public EditTopPresentFragmentCallback getTopFragment() {
        return EditPhotoFrameTopPresentFragment.getInstance();
    }
}
