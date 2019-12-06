package nahuy.fithcmus.magiccam.data.clients.database.sticker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;

/**
 * Created by huy on 6/20/2017.
 */

public class StickerAccessObject {
    protected boolean isActive = false;

    private static StickerAccessObject instance = new StickerAccessObject();

    private StickerAccessObject(){}

    public static StickerAccessObject getInstance(){
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

    public boolean addSticker(DA_Sticker objectToAdd){

        synchronized (instance) {

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null) {
                // Will show some text when connecting to view layer.
                return false;
            }

            // Content Value is a class that hold <key, value> pair
            // Instead writing the whole query, we do this for simple purpose

            ContentValues insertValues = new ContentValues();

            insertValues.put("STICKER_IMG_PATH", objectToAdd.getFilePath());
            insertValues.putNull("STICKER_COLOR");
            insertValues.put("IS_TEXT", 0);

            currentWork.beginTransaction();

            try {
                long id = currentWork.insertOrThrow("STICKERS", null, insertValues);

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

    public ArrayList<DA_Sticker> getAllStickers(){

        synchronized (instance) {
            ArrayList<DA_Sticker> result = new ArrayList<DA_Sticker>(0);

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            Cursor tracing = currentWork.rawQuery("select STICKER_IMG_PATH from STICKERS where IS_TEXT = 0", null);

            while (tracing.moveToNext()) {
                DA_Sticker se = new DA_Sticker("",
                        tracing.getString(tracing.getColumnIndex("STICKER_IMG_PATH")));
                result.add(se);
            }

            currentWork.close();
            instance.notify();
            return result;
        }
    }
}
