package nahuy.fithcmus.magiccam.data.clients.database.frame;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;

/**
 * Created by huy on 6/20/2017.
 */

public class FrameAccessObject {
    protected boolean isActive = false;

    private static FrameAccessObject instance = new FrameAccessObject();

    private FrameAccessObject(){}

    public static FrameAccessObject getInstance(){
        return instance;
    }

    // This method call each time we exec a query
    private SQLiteDatabase openDatabase(String name){

        SQLiteDatabase result = null;

        try{
            result = SQLiteDatabase.openDatabase(name, null, 0);
            this.isActive = true;
        }
        catch(Exception e){
            // result = this.createDatabase(name);
        }

        return result;

    }

    public boolean addFrame(DA_Frame objectToAdd){

        synchronized (instance) {

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null) {
                // Will show some text when connecting to view layer.
                return false;
            }

            // Content Value is a class that hold <key, value> pair
            // Instead writing the whole query, we do this for simple purpose

            ContentValues insertValues = new ContentValues();

            insertValues.put("FRAME_IMG_PATH", objectToAdd.getFilePath());
            insertValues.put("FRAME_WIDTH", objectToAdd.getWidth());
            insertValues.put("FRAME_HEIGHT", objectToAdd.getHeight());

            currentWork.beginTransaction();

            try {
                long id = currentWork.insertOrThrow("FRAME", null, insertValues);

                if (id == -1)
                    throw new Exception("Can't insert");
                // if id different from -1 then we create a folder in internal storage that stand for the
                // album.

                currentWork.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("ERROR INSERT", e.getMessage());
                return false;
            } finally {
                currentWork.endTransaction();
                currentWork.close();
            }

            instance.notify();
            return true;
        }
    }

    public ArrayList<DA_Frame> getAllFrames(){

        synchronized (instance) {
            ArrayList<DA_Frame> result = new ArrayList<DA_Frame>(0);

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            Cursor tracing = currentWork.rawQuery("select * from FRAME", null);

            while (tracing.moveToNext()) {
                DA_Frame se = new DA_Frame(tracing.getString(tracing.getColumnIndex("FRAME_IMG_PATH")),
                        tracing.getInt(tracing.getColumnIndex("FRAME_WIDTH")),
                        tracing.getInt(tracing.getColumnIndex("FRAME_HEIGHT")));
                result.add(se);
            }

            currentWork.close();
            instance.notify();
            return result;
        }
    }
}
