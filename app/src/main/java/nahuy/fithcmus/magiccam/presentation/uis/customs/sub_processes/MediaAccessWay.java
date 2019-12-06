package nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes;

import android.content.Context;

/**
 * Created by huy on 5/20/2017.
 */

public abstract class MediaAccessWay {
    protected Context context;
    protected MediaReceiverContract mediaReceiverContract;

    public MediaAccessWay(Context context, MediaReceiverContract mediaReceiverContract){
        this.context = context;
        this.mediaReceiverContract = mediaReceiverContract;
    }

    public abstract void processMedia(Object media);
}
