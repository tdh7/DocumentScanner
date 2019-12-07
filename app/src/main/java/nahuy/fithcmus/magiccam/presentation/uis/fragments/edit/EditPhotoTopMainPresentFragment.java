package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.uis.activities.SaveAndShareItemActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.BitmapUtils;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.views.FitScreenPhotoView;

/**
 * Created by huy on 5/25/2017.
 */

public class EditPhotoTopMainPresentFragment extends Fragment implements EditTopPresentFragmentCallback {

    private Bitmap originBitmap;

    @BindView(R.id.edit_main_picture)
    FitScreenPhotoView photoView;

    @OnClick(R.id.edit_back)
    void backToCam(){
        getActivity().finish();
    }

    @OnClick(R.id.edit_save)
    void saveAndShare(){
        String imgPath = BitmapUtils.savePicture(getContext(), originBitmap);
        Intent shareIntent = new Intent(getActivity(), SaveAndShareItemActivity.class);
        shareIntent.putExtra(Constants.SHARE_TYPE, true);
        shareIntent.putExtra(Constants.SAVED_IMG_PATH, imgPath);
        startActivity(shareIntent);
    }

    public static EditPhotoTopMainPresentFragment getInstance(){
        EditPhotoTopMainPresentFragment editPhotoTopMainPresentFragment = new EditPhotoTopMainPresentFragment();
        return editPhotoTopMainPresentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_edit_defaul_top, container, false);

        ButterKnife.bind(this, rootView);

        photoView.setImageBitmap(this.originBitmap);
        photoView.setSetMargin(true);

        return rootView;
    }

    @Override
    public Fragment getFragment(Bitmap bm) {
        this.originBitmap = bm;
        return this;
    }

    @Override
    public void process(CallingAbstractCommander callingAbstractCommander) {

    }

    @Override
    public Bitmap getProduct() {
        return this.originBitmap;
    }
}
