package nahuy.fithcmus.magiccam.presentation.uis.customs.tools;

/**
 * Created by huy on 5/24/2017.
 */

public class FileNameHelper {
    public static String getExtensionFromFileName(String fileName){
        int firstDot = fileName.lastIndexOf('.') + 1;
        int length = fileName.length();
        return fileName.substring(firstDot, length);
    }
}
