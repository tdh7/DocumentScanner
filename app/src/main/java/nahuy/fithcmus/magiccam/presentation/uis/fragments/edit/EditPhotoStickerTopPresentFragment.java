package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.views.FitScreenPhotoView;

/**
 * Created by huy on 6/2/2017.
 */

public class EditPhotoStickerTopPresentFragment extends Fragment implements EditTopPresentFragmentCallback {

    private Bitmap originBitmap;

    @BindView(R.id.edit_sticker_view)
    StickerView stickerView;

    @BindView(R.id.edit_main_picture)
    FitScreenPhotoView fitScreenPhotoView;

    public static EditPhotoStickerTopPresentFragment getInstance(){
        EditPhotoStickerTopPresentFragment editPhotoFrameTopPresentFragment = new EditPhotoStickerTopPresentFragment();
        return editPhotoFrameTopPresentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_top_sticker, null);

        ButterKnife.bind(this, v);

        setUpImageSticker();

        fitScreenPhotoView.setImageBitmap(this.originBitmap);

        return v;
    }

    @Override
    public Fragment getFragment(Bitmap bm) {
        this.originBitmap = bm;
        return this;
    }

    @Override
    public void process(CallingAbstractCommander callingAbstractCommander) {
        callingAbstractCommander.process(stickerView);
    }

    @Override
    public Bitmap getProduct() {
        stickerView.setLocked(true);
        return stickerView.createBitmap();
    }

    private void setUpImageSticker(){
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
        stickerView.configDefaultIcons();
        stickerView.setLocked(false);
        stickerView.setConstrained(true);
    }
}

