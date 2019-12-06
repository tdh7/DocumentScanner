package nahuy.fithcmus.magiccam.data.clients.local;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import nahuy.fithcmus.magiccam.data.Constants;

import static nahuy.fithcmus.magiccam.data.Constants.LOCAL_FSH_NAME;

/**
 * Created by huy on 6/18/2017.
 */

public class ReadLocalFSHFile {
    public static String readLocalFSHTextFileSync(Context context, String fileName){
        StringBuilder sb = new StringBuilder("");
        BufferedReader reader = null;
        try {
            File f = new File(context.getFilesDir(), LOCAL_FSH_NAME + "/" + fileName);

            if(f.exists()) {
                reader = new BufferedReader(
                        new FileReader(f));

                // do reading, usually loop until end of file reading
                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    //process line
                    sb.append(mLine).append("\n");
                }
            }
            else{
                throw new Exception("File not exist");
            }
        } catch (IOException e) {
            //log the exception
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    return "";
                }
            }
            return sb.toString();
        }
    }
}
