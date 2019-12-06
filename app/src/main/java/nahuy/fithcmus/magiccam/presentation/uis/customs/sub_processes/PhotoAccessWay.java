package nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.BitmapUtils;

/**
 * Created by huy on 5/20/2017.
 */

public class PhotoAccessWay extends MediaAccessWay {

    public PhotoAccessWay(Context context, MediaReceiverContract mediaReceiverContract) {
        super(context, mediaReceiverContract);
    }

    @Override
    public void processMedia(Object media) {
        Uri mediaUri = null;
        if (media instanceof Bitmap) {
            mediaUri = BitmapUtils.saveTempBitmap((Bitmap) media);
        }
        mediaReceiverContract.receiveMediaUri(mediaUri);
    }

}
