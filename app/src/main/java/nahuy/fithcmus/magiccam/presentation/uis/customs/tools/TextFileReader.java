package nahuy.fithcmus.magiccam.presentation.uis.customs.tools;

import android.content.Context;

import nahuy.fithcmus.magiccam.presentation.presenters.ReadingFSHFilePresenter;

/**
 * Created by huy on 5/17/2017.
 */

public class TextFileReader {
    public static String readTextFromLocalFile(Context context, String fileName){
        return ReadingFSHFilePresenter.readingFSHTextFile(context, fileName);
    }
}
