package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.EditGetProductCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.filter.EditFilterCommander;
import nahuy.fithcmus.magiccam.presentation.entities.enums.EditTopType;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.presenters.ReadingFSHFilePresenter;
import nahuy.fithcmus.magiccam.presentation.uis.activities.EditPhotoActivity;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.SubFilterAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.RightHorizontalListDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.SubContainerInterface;

/**
 * Created by huy on 5/24/2017.
 */

public class EditPhotoBottomFilterFragment extends Fragment implements BottomNavigateFragmentInterface, SubContainerInterface {

    private Context context;
    private FragEditInteract fragEditInteract;
    private EditMainContainerInterface mainContainerInterface;
    private ArrayList<MyGLEffectShader> filters;

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

    public static EditPhotoBottomFilterFragment getInstance(){
        EditPhotoBottomFilterFragment editPhotoBottomFilterFragment = new EditPhotoBottomFilterFragment();
        return editPhotoBottomFilterFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragEditInteract = (EditPhotoActivity)getActivity();
        mainContainerInterface = (EditPhotoActivity)getActivity();
        ReadingFSHFilePresenter readingFSHFilePresenter = new ReadingFSHFilePresenter();
        filters = readingFSHFilePresenter.getAllFilters(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_edit_bottom_filter, null);

        ButterKnife.bind(this, v);

        SubFilterAdapter subFilterAdapter = new SubFilterAdapter(getContext(), this, filters);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        subFilterAdapter.setColor(Color.DKGRAY);
        mainFilterList.setAdapter(subFilterAdapter);
        mainFilterList.addItemDecoration(new RightHorizontalListDecoration(20));
        mainFilterList.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(mainFilterList, subFilterAdapter, AnimationMaker.MyAnimation.SLIDE_IN);

        return v;
    }

    @Override
    public Fragment getBottomFragment() {
        return EditPhotoBottomFilterFragment.getInstance();
    }

    @Override
    public EditTopPresentFragmentCallback getTopFragment() {
        return EditPhotoGLTopPresentFragment.getInstance();
    }

    @Override
    public void subContainerProcess(MyGLEffectShader se) {
        fragEditInteract.process(new EditFilterCommander(se));
    }
}
