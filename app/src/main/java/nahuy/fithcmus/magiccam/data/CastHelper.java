package nahuy.fithcmus.magiccam.data;

import java.util.Arrays;

/**
 * Created by huy on 5/23/2017.
 */

public class CastHelper {

    public static float[] getFloatArrayFromStr(String arr){
        String[] splittedArr = arr.split(",");
        float[] result = new float[splittedArr.length];
        splittedArr[0] = splittedArr[0].substring(1);
        splittedArr[splittedArr.length - 1] = splittedArr[splittedArr.length - 1]
                .substring(0, splittedArr[splittedArr.length - 1].length() - 1);
        for(int i = 0; i < splittedArr.length; i++){
            result[i] = Float.parseFloat(splittedArr[i]);
        }
        return result;
    }

    public static String getStringArrayFromFloat(float[] arr){
        return Arrays.toString(arr);
    }
}
