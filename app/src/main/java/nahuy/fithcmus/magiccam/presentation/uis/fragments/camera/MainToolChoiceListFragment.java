package nahuy.fithcmus.magiccam.presentation.uis.fragments.camera;

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
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GLToolCommander;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGlHeader;
import nahuy.fithcmus.magiccam.presentation.presenters.ToolGLHeaderPresenter;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.MainToolAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.SubToolAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.decorations.RightHorizontalListDecoration;
import nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations.AnimationMaker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.ExternalToolCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragMainInteract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.MainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.SubToolContainerInterface;

/**
 * Created by huy on 6/19/2017.
 */

public class MainToolChoiceListFragment extends Fragment implements MainContainerInterface, SubToolContainerInterface {

    private FragMainInteract fragMainInteract;
    private ExternalToolCallback externalToolCallback;
    private ArrayList<MyToolGlHeader> myToolGlHeaders;
    protected Context context;

    @BindView(R.id.filter_list)
    public RecyclerView mainFilterList;

    public static MainToolChoiceListFragment getInstance(){
        MainToolChoiceListFragment mainChoiceListFragment = new MainToolChoiceListFragment();
        return mainChoiceListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragMainInteract = (MainActivity) getActivity();
        externalToolCallback = (MainActivity)getActivity();
        initToolHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_camera_choice_list_layout, null);

        ButterKnife.bind(this, v);

        MainToolAdapter mainToolAdapter = new MainToolAdapter(context, this, myToolGlHeaders);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mainFilterList.setAdapter(mainToolAdapter);
        mainFilterList.addItemDecoration(new RightHorizontalListDecoration(20));
        mainFilterList.setLayoutManager(linearLayoutManager);
        AnimationMaker.setAnimation(mainFilterList, mainToolAdapter, AnimationMaker.MyAnimation.SCALE_IN);

        return v;
    }

    @Override
    public void mainContainerId(int id) {
        MyToolGlHeader selectedTool = myToolGlHeaders.get(id);
        ArrayList<MyToolGLShader> subTools = selectedTool.getLstOfTool();
        popDownExternalBar();
        if(selectedTool.isSingleTool()) {
            // Just need to change commander, show color and quantity bar if needed
            startToolCommand(subTools.get(0));
            popUpExternalBar(subTools.get(0));
        }
        else{
            SubToolAdapter subToolAdapter = new SubToolAdapter(context, this, subTools);
            mainFilterList.setAdapter(subToolAdapter);
        }
    }
    
    @Override
    public void subContainerProcess(MyToolGLShader se) {
        popDownExternalBar();
        startToolCommand(se);
        popUpExternalBar(se);
    }

    @Override
    public void subCotainerClose() {
        MainToolAdapter mainToolAdapter = new MainToolAdapter(context, this, myToolGlHeaders);
        mainFilterList.setAdapter(mainToolAdapter);
        // popDownExternalBar();
    }

    private void startToolCommand(MyToolGLShader subTool) {
        // Change tool using in texture2d
        fragMainInteract.interactRender(new GLToolCommander(subTool));
    }

    private void initToolHeader(){
        myToolGlHeaders = new ArrayList<>();

        ToolGLHeaderPresenter toolGLHeaderPresenter = new ToolGLHeaderPresenter();

        myToolGlHeaders = toolGLHeaderPresenter.loadTool();
    }

    private void popUpExternalBar(MyToolGLShader subTool) {
        // Pop up external tools from main activity
        int numberOfOpenTools = 0;

        if (subTool.isUsingColor()) {
            numberOfOpenTools++;
            externalToolCallback.popColorBar();
        }
        if (subTool.isUsingQuantity()) {
            numberOfOpenTools++;
            externalToolCallback.popQuantityBar();
        }
        if (subTool.isUsingClear()){
            numberOfOpenTools++;
            externalToolCallback.popClearButton();
        }

        // Only open show hide button if number of opened tool greater than 0
        if(numberOfOpenTools > 0){
            externalToolCallback.popShowHideButton();
        }
    }

    private void popDownExternalBar() {
        externalToolCallback.popDownShowHideButton();
        externalToolCallback.popDownColorBar();
        externalToolCallback.popDownQuantityBar();
        externalToolCallback.popDownClearButton();
    }
}
