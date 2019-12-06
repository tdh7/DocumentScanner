package nahuy.fithcmus.magiccam.presentation.uis.fragments.store;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.presenters.StorePresenter;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.store.StoreFilterAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.StoreItemFragmentCalling;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreIntentFragment extends Fragment implements StorePresenter.StoreFilterViewSurface, StoreItemFragmentCalling{

    private Context context;

    @BindView(R.id.store_list_filter)
    RecyclerView storeListFilter;

    @BindView(R.id.store_list_frame)
    RecyclerView storeListFrame;

    @BindView(R.id.store_list_sticker)
    RecyclerView storeListSticker;

    @BindView(R.id.store_list_sticker_text)
    RecyclerView storeListStickerText;

    @BindView(R.id.store_list_filter_holder)
    TextView storeListFilterHolder;

    @BindView(R.id.store_list_frame_holder)
    TextView storeListFrameHolder;

    @BindView(R.id.store_list_sticker_holder)
    TextView storeListStickerHolder;

    @BindView(R.id.store_list_sticker_text_holder)
    TextView storeListStickerTextHolder;

    public static StoreIntentFragment getInstance(){
        StoreIntentFragment storeIntentFragment = new StoreIntentFragment();
        return storeIntentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_store_intent, container, false);

        ButterKnife.bind(this, rootView);

        queryAllItems();

        return rootView;

    }

    private void queryAllItems() {
        StorePresenter.queryFilter(this);
    }

    @Override
    public void onFilterResult(ArrayList<MyGLEffectShader> shaders) {
        StoreFilterAdapter storeFilterAdapter = new StoreFilterAdapter(context, shaders, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        storeListFilter.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(storeListFilter, storeFilterAdapter, AnimationMaker.MyAnimation.SCALE_IN);
    }

    @Override
    public void onFilterFail() {
        storeListFilterHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void queryFilter(int id) {

    }

    @Override
    public void querySticker(int id) {

    }

    @Override
    public void queryStickerText(int id) {

    }

    @Override
    public void queryFrame(int id) {

    }
}
