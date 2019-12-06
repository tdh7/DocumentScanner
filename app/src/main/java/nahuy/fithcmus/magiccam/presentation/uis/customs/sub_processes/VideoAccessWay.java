package nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes;

import android.content.Context;
import android.net.Uri;

import java.net.URI;

/**
 * Created by huy on 5/20/2017.
 */

public class VideoAccessWay extends MediaAccessWay {

    public VideoAccessWay(Context context, MediaReceiverContract mediaReceiverContract) {
        super(context, mediaReceiverContract);
    }

    @Override
    public void processMedia(Object media) {
        mediaReceiverContract.receiveMediaUri(Uri.parse(((URI)media).toString()));
    }

}
