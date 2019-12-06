package nahuy.fithcmus.magiccam.presentation.uis.fragments.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.FilterCommander;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShaderHeader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.presenters.ReadingFSHFilePresenter;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.SubFilterAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.InvidualSpaceDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.RightHorizontalListDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragMainInteract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.SubContainerInterface;

/**
 * Created by huy on 5/20/2017.
 */

public class MainChoiceListFragment extends Fragment implements SubContainerInterface {

    protected Context context;
    private FragMainInteract fragMainInteract;

    @BindView(R.id.filter_list)
    public RecyclerView mainFilterList;

    public static MainChoiceListFragment getInstance(){
        MainChoiceListFragment mainChoiceListFragment = new MainChoiceListFragment();
        return mainChoiceListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragMainInteract = (MainActivity) getActivity();
        initShaderHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_camera_choice_list_layout, null);

        ButterKnife.bind(this, v);

        ReadingFSHFilePresenter readingFSHFilePresenter = new ReadingFSHFilePresenter();
        ArrayList<MyGLEffectShader> filters = readingFSHFilePresenter.getAllFilters(getContext());
        SubFilterAdapter subFilterAdapter = new SubFilterAdapter(getContext(), this, filters);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        // mainFilterList.addItemDecoration(new InvidualSpaceDecoration(-50,10,-50,10));
        mainFilterList.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(mainFilterList, subFilterAdapter, AnimationMaker.MyAnimation.SCALE_IN);

        return v;
    }

    @Override
    public void subContainerProcess(MyGLEffectShader se) {
        fragMainInteract.interactRender(new FilterCommander(se));
    }

    protected void initShaderHeader(){
        // lstOfMainHeader = ShaderHeaderAccessObject.getInstance().getAllShaderHeader();
    }

}
