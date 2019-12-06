package nahuy.fithcmus.magiccam.presentation.presenters.edit_photo;

import android.content.Context;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.domain.use_cases.GettingFrameUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;

/**
 * Created by huy on 6/20/2017.
 */

public class FrameAccessPresenter {

    public ArrayList<Frame> getAllFrames(Context context) {
        GettingFrameUseCase gettingFrameUseCase = new GettingFrameUseCase();
        ArrayList<Frame> frames = gettingFrameUseCase.getAllFrames();
        return frames;
    }
}
