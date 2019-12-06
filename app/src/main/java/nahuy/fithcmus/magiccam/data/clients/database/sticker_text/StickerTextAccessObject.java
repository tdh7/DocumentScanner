package nahuy.fithcmus.magiccam.data.clients.database.sticker_text;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;
import nahuy.fithcmus.magiccam.data.entities.DA_StickerText;

/**
 * Created by huy on 6/20/2017.
 */

public class StickerTextAccessObject {

    protected boolean isActive = false;

    private static StickerTextAccessObject instance = new StickerTextAccessObject();

    private StickerTextAccessObject(){}

    public static StickerTextAccessObject getInstance(){
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

    public boolean addSticker(DA_StickerText objectToAdd){

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
            insertValues.put("STICKER_COLOR", objectToAdd.getColor());
            insertValues.put("IS_TEXT", 1);

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

    public ArrayList<DA_StickerText> getAllStickers(){

        synchronized (instance) {
            ArrayList<DA_StickerText> result = new ArrayList<DA_StickerText>(0);

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            Cursor tracing = currentWork.rawQuery("select * from STICKERS", null);

            while (tracing.moveToNext()) {
                int isText = tracing.getInt(tracing.getColumnIndex("IS_TEXT"));
                if(isText == 1) {
                    DA_StickerText se = new DA_StickerText("",
                            tracing.getString(tracing.getColumnIndex("STICKER_IMG_PATH")),
                            tracing.getString(tracing.getColumnIndex("STICKER_COLOR")));
                    result.add(se);
                }
            }

            currentWork.close();
            instance.notify();
            return result;
        }
    }
}
