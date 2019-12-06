package nahuy.fithcmus.magiccam.presentation.uis.fragments.camera;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.BackCamCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.FrontCamCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GLToolClearCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.MakeProductCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.SwitchModeCaptureCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.SwitchModeRecordCommander;
import nahuy.fithcmus.magiccam.presentation.entities.enums.CameraModeAction;
import nahuy.fithcmus.magiccam.presentation.entities.enums.RecordStateMode;
import nahuy.fithcmus.magiccam.presentation.entities.enums.SwitchCamAction;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.ExternalToolCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragMainInteract;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by huy on 5/19/2017.
 */

public class MainBottomFragment extends Fragment {

    public static final String SCA_STATE = "SCA_STATE";
    public static final String FCA_STATE = "FCA_STATE";
    public static final String CMA_STATE = "CMA_STATE";

    private Context context;
    private FragMainInteract fragMainInteract;
    private ExternalToolCallback toolCallback;
    private Boolean isShowTool = null;

    SwitchCamAction sca;
    CameraModeAction cma;
    RecordStateMode rsm;

    @BindView(R.id.ic_capture_record)
    ImageView switchModeBtn;

    @BindView(R.id.btnAction)
    ImageView actionBtn;

    @BindView(R.id.show_hide_tool_box)
    TextView showHideToolBox;

    @BindView(R.id.clear_tool_box)
    TextView clearToolBox;

    @OnClick(R.id.ic_switch)
    void switchCam(){
        changeCameraFacingState();
    }

    @OnClick(R.id.ic_capture_record)
    void changeMode(){
        changeCameraModeState();
    }

    @OnClick(R.id.ic_filter)
    void popFilter(){
        ((MainActivity)getActivity()).popFilterList();
    }

    @OnClick(R.id.ic_camera_tool)
    void popTool(){
        ((MainActivity)getActivity()).popToolList();
    }


    @OnClick(R.id.btnAction)
    void actionClick(){
        fragMainInteract.interactRender(new MakeProductCommander());
        if(cma == CameraModeAction.RECORD) {
            if (rsm == RecordStateMode.STOP) {
                actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.record_button_selected));
                rsm = RecordStateMode.RECORDING;
            }
            else{
                actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.record_button_trap));
                rsm = RecordStateMode.STOP;
            }
        }
    }

    public static MainBottomFragment getInstance(){
        MainBottomFragment mainBottomFragment = new MainBottomFragment();
        return mainBottomFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragMainInteract = (MainActivity)getActivity();
        toolCallback = (MainActivity)getActivity();
        initState();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_bottom_layout, null);

        ButterKnife.bind(this, v);

        showHideToolBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    showHideToolBox.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_shape_rectangle_pressed));
                    showHideToolBox.setTextColor(Color.WHITE);
                    if(isShowTool){
                        toolCallback.popDownColorBar();
                        toolCallback.popDownQuantityBar();
                        showHideToolBox.setText(context.getResources().getString(R.string.show));
                        isShowTool = false;
                    }
                    else{
                        toolCallback.popColorBar();
                        toolCallback.popQuantityBar();
                        showHideToolBox.setText(context.getResources().getString(R.string.hide));
                        isShowTool = true;
                    }
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    showHideToolBox.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_shape_container));
                    showHideToolBox.setTextColor(Color.BLACK);
                    return true;
                }
                return false;
            }
        });

        clearToolBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    clearToolBox.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_shape_rectangle_pressed));
                    clearToolBox.setTextColor(Color.WHITE);
                    fragMainInteract.interactRender(new GLToolClearCommander());
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    clearToolBox.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_shape_container));
                    clearToolBox.setTextColor(Color.BLACK);
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Restore state
        setDefaultFacingState();
        setDefaultCameraMode();
    }

    @Override
    public void onPause() {
        if(cma == CameraModeAction.RECORD) {
            if (rsm == RecordStateMode.RECORDING) {
                fragMainInteract.interactRender(new MakeProductCommander());
                rsm = RecordStateMode.STOP;
            }
        }
        super.onPause();
    }

    private void setDefaultFacingState(){
        if(sca == SwitchCamAction.FRONT){
            fragMainInteract.interactCamera(new FrontCamCommander());
        }
        else{
            fragMainInteract.interactCamera(new BackCamCommander());
        }
    }

    private void setDefaultCameraMode(){
        if(cma == CameraModeAction.CAPTURE){
            fragMainInteract.interactRender(new SwitchModeCaptureCommander());
            switchModeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_capture_trap));
            actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.capture_button_trap));
        }
        else{
            fragMainInteract.interactRender(new SwitchModeRecordCommander());
            switchModeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_record_trap));
            actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.record_button_trap));
        }
    }

    private void changeCameraFacingState() {
        if(sca == SwitchCamAction.FRONT){
            fragMainInteract.interactCamera(new BackCamCommander());
            sca = SwitchCamAction.BACK;
        }
        else{
            fragMainInteract.interactCamera(new FrontCamCommander());
            sca = SwitchCamAction.FRONT;
        }
    }

    private void changeCameraModeState() {
        if(cma == CameraModeAction.CAPTURE){
            fragMainInteract.interactRender(new SwitchModeRecordCommander());
            switchModeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_record_trap));
            actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.record_button_trap));
            cma = CameraModeAction.RECORD;
        }
        else{
            fragMainInteract.interactRender(new MakeProductCommander());
            fragMainInteract.interactRender(new SwitchModeCaptureCommander());
            switchModeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_capture_trap));
            actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.capture_button_trap));
            cma = CameraModeAction.CAPTURE;
        }
    }

    private void initState(){
        // Set to the opposite
        sca = SwitchCamAction.FRONT; // Become front
        cma = CameraModeAction.CAPTURE; // Become record
        rsm = RecordStateMode.STOP;
    }

    public SwitchCamAction getSca() {
        return sca;
    }

    public CameraModeAction getCma() {
        return cma;
    }

    public void setSca(SwitchCamAction sca) {
        this.sca = sca;
    }

    public void setCma(CameraModeAction cma) {
        this.cma = cma;
    }

    public void showShowHideBox(){
        showHideToolBox.setText(context.getResources().getString(R.string.hide));
        showHideToolBox.setVisibility(View.VISIBLE);
        isShowTool = true;
    }

    public void hideShowHideBox(){
        showHideToolBox.setVisibility(View.GONE);
        isShowTool = null;
    }

    public void showClearBox(){
        clearToolBox.setVisibility(View.VISIBLE);
    }

    public void hideClearBox(){
        clearToolBox.setVisibility(View.GONE);
    }
}
