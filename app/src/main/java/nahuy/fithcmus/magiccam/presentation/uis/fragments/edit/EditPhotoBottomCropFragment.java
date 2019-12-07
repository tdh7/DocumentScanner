package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.EditGetProductCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.Crop16_9Commander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.Crop3_4Commander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.Crop4_3Commander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.Crop9_16Commander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.CropCircleCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.CropCircleSquare;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.CropFitCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.CropFreeCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.CropSquareCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.RotateLCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.crop.RotateRCommander;
import nahuy.fithcmus.magiccam.presentation.uis.activities.EditPhotoActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;

/**
 * Created by huy on 6/1/2017.
 */

public class EditPhotoBottomCropFragment extends Fragment implements BottomNavigateFragmentInterface {
    private Context context;
    private FragEditInteract fragEditInteract;
    private EditMainContainerInterface mainContainerInterface;

    @OnClick(R.id.buttonFitImage)
    void fitImage(){
        fragEditInteract.process(new CropFitCommander());
    }

    @OnClick(R.id.button1_1)
    void squareImage(){
        fragEditInteract.process(new CropSquareCommander());
    }

    @OnClick(R.id.button3_4)
    void _3_4_Image(){
        fragEditInteract.process(new Crop3_4Commander());
    }

    @OnClick(R.id.button4_3)
    void _4_3_fitImage(){
        fragEditInteract.process(new Crop4_3Commander());
    }

    @OnClick(R.id.button9_16)
    void _9_16_fitImage(){
        fragEditInteract.process(new Crop9_16Commander());
    }

    @OnClick(R.id.button16_9)
    void _16_9_fitImage(){
        fragEditInteract.process(new Crop16_9Commander());
    }

    @OnClick(R.id.buttonCircle)
    void circleImage(){
        fragEditInteract.process(new CropCircleCommander());
    }

    @OnClick(R.id.buttonFree)
    void freeImage(){
        fragEditInteract.process(new CropFreeCommander());
    }

    @OnClick(R.id.buttonShowCircleButCropAsSquare)
    void squareCircleImage(){
        fragEditInteract.process(new CropCircleSquare());
    }

    @OnClick(R.id.buttonRotateLeft)
    void rotateLeft(){
        fragEditInteract.process(new RotateLCommander());
    }

    @OnClick(R.id.buttonRotateRight)
    void rotateRight(){
        fragEditInteract.process(new RotateRCommander());
    }

    @OnClick(R.id.buttonCropBack)
    void back(){
        mainContainerInterface.onMyBackPressed();
    }

    @OnClick(R.id.buttonDone)
    void done(){
        fragEditInteract.process(new EditGetProductCommander());
    }

    public static EditPhotoBottomCropFragment getInstance(){
        EditPhotoBottomCropFragment editPhotoBottomFilterFragment = new EditPhotoBottomCropFragment();
        return editPhotoBottomFilterFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        fragEditInteract = (EditPhotoActivity)getActivity();
        mainContainerInterface = (EditPhotoActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_edit_bottom_crop, null);

        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public Fragment getBottomFragment() {
        return EditPhotoBottomCropFragment.getInstance();
    }

    @Override
    public EditTopPresentFragmentCallback getTopFragment() {
        return EditPhotoCropTopPresentFragment.getInstance();
    }
}
