package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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
import nahuy.fithcmus.magiccam.presentation.entities.StickerTextViewWrapper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.views.FitScreenPhotoView;

/**
 * Created by huy on 6/2/2017.
 */

public class EditPhotoStickerTextTopPresentFragment extends Fragment implements EditTopPresentFragmentCallback {

    private Bitmap originBitmap;

    @BindView(R.id.edit_sticker_view)
    StickerView stickerView;

    @BindView(R.id.edit_main_picture)
    FitScreenPhotoView fitScreenPhotoView;

    @BindView(R.id.edit_sticker_text_view)
    EditText stickerEditText;

    private CallingAbstractCommander callingAbstractCommander;

    public static EditPhotoStickerTextTopPresentFragment getInstance(){
        EditPhotoStickerTextTopPresentFragment editPhotoFrameTopPresentFragment = new EditPhotoStickerTextTopPresentFragment();
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

        fitScreenPhotoView.setImageBitmap(this.originBitmap);

        setUpImageSticker();

        stickerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        stickerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showKeyboard();
                }
            }
        });

        stickerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    if(callingAbstractCommander != null && !stickerEditText.getText().toString().equals("")){
                        callingAbstractCommander.process(new StickerTextViewWrapper(stickerView
                                , stickerEditText.getText().toString()));
                        callingAbstractCommander = null;
                    }
                    stickerEditText.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public Fragment getFragment(Bitmap bm) {
        this.originBitmap = bm;
        return this;
    }

    @Override
    public void process(CallingAbstractCommander callingAbstractCommander) {
        stickerEditText.setVisibility(View.VISIBLE);
        stickerEditText.requestFocus();
        this.callingAbstractCommander = callingAbstractCommander;
    }

    @Override
    public Bitmap getProduct() {
        stickerView.setLocked(true);
        return stickerView.createBitmap();
    }

    private void setUpImageSticker(){
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp), BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp), BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        BitmapStickerIcon resizeIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp), BitmapStickerIcon.RIGHT_BOTOM);
        resizeIcon.setIconEvent(new ZoomIconEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, flipIcon, resizeIcon));
        stickerView.setLocked(false);
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(stickerEditText, imm.SHOW_IMPLICIT);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(stickerEditText.getWindowToken(), 0);
    }
}
