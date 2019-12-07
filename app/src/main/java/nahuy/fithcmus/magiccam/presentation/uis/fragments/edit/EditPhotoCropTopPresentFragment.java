package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isseiaoki.simplecropview.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;

/**
 * Created by huy on 6/1/2017.
 */

public class EditPhotoCropTopPresentFragment extends Fragment implements EditTopPresentFragmentCallback {

    private Bitmap originBitmap;

    @BindView(R.id.cropImageView)
    CropImageView cropImageView;

    public static EditPhotoCropTopPresentFragment getInstance(){
        EditPhotoCropTopPresentFragment editPhotoCropTopPresentFragment = new EditPhotoCropTopPresentFragment();
        return editPhotoCropTopPresentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_top_crop, null);

        ButterKnife.bind(this, v);
        cropImageView.setImageBitmap(originBitmap);

        return v;
    }

    @Override
    public Fragment getFragment(Bitmap bm) {
        this.originBitmap = bm;
        return this;
    }

    @Override
    public void process(CallingAbstractCommander callingAbstractCommander) {
        callingAbstractCommander.process(cropImageView);
    }

    @Override
    public Bitmap getProduct() {
        return cropImageView.getCroppedBitmap();
    }
}
