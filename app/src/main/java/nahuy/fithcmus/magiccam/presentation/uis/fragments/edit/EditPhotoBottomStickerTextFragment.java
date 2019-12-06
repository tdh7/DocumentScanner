package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.content.Context;
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
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;
import nahuy.fithcmus.magiccam.presentation.entities.enums.EditTopType;
import nahuy.fithcmus.magiccam.presentation.presenters.edit_photo.StickerTextAccessPresenter;
import nahuy.fithcmus.magiccam.presentation.uis.activities.EditPhotoActivity;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.edit_photo.EditMainStickerTextAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.RightHorizontalListDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;

/**
 * Created by huy on 6/2/2017.
 */

public class EditPhotoBottomStickerTextFragment extends Fragment implements BottomNavigateFragmentInterface {

    private Context context;
    private FragEditInteract fragEditInteract;
    private EditMainContainerInterface mainContainerInterface;
    private ArrayList<StickerText> stickers;
    private StickerTextAccessPresenter stickerTextAccessPresenter;

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

    public static EditPhotoBottomStickerTextFragment getInstance(){
        EditPhotoBottomStickerTextFragment editPhotoBottomFilterFragment = new EditPhotoBottomStickerTextFragment();
        return editPhotoBottomFilterFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragEditInteract = (EditPhotoActivity)getActivity();
        mainContainerInterface = (EditPhotoActivity)getActivity();
        stickerTextAccessPresenter = new StickerTextAccessPresenter();
        stickers = stickerTextAccessPresenter.getAllStickers(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_edit_bottom_frame, null);

        ButterKnife.bind(this, v);

        EditMainStickerTextAdapter mfa = new EditMainStickerTextAdapter(context, fragEditInteract, stickers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mainFilterList.setAdapter(mfa);
        mainFilterList.addItemDecoration(new RightHorizontalListDecoration(20));
        mainFilterList.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(mainFilterList, mfa, AnimationMaker.MyAnimation.SLIDE_IN);

        return v;
    }

    @Override
    public Fragment getBottomFragment() {
        return EditPhotoBottomStickerTextFragment.getInstance();
    }

    @Override
    public EditTopPresentFragmentCallback getTopFragment() {
        return EditPhotoStickerTextTopPresentFragment.getInstance();
    }
}
