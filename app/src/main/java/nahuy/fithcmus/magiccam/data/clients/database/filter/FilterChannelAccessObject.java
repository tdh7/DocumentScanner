package nahuy.fithcmus.magiccam.data.clients.database.filter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;

/**
 * Created by huy on 6/3/2017.
 */

public class FilterChannelAccessObject {
    protected boolean isActive = false;

    private static FilterChannelAccessObject instance = new FilterChannelAccessObject();

    private FilterChannelAccessObject(){}

    public static FilterChannelAccessObject getInstance(){
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

    // Return latest kernel id
    public boolean addFilterChannel(String filterName, DA_FilterChannel filterChannel){

        synchronized (instance) {
            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null) {
                // Will show some text when connecting to view layer.
                return false;
            }

            // Content Value is a class that hold <key, value> pair
            // Instead writing the whole query, we do this for simple purpose

            ContentValues insertValues = new ContentValues();

            insertValues.put("FILTER_NAME", filterName);
            insertValues.put("PATH", filterChannel.getPath());
            insertValues.put("CHANNEL_W", filterChannel.getW());
            insertValues.put("CHANNEL_H", filterChannel.getH());

            currentWork.beginTransaction();

            try {
                long id = currentWork.insertOrThrow("FILTER_CHANNEL", null, insertValues);

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

    // Get all filter channels
    public ArrayList<DA_FilterChannel> getFilterChannels(String filterName){

        synchronized (instance) {

            ArrayList<DA_FilterChannel> result = new ArrayList<>();

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            String[] selectArgs = {"" + filterName};

            Cursor tracing = currentWork.rawQuery("select * from FILTER_CHANNEL where FILTER_NAME=?", selectArgs);

            while (tracing.moveToNext()) {
                DA_FilterChannel filterChannel = new DA_FilterChannel(tracing.getString(tracing.getColumnIndex("PATH"))
                                                    , tracing.getString(tracing.getColumnIndex("FILTER_NAME"))
                                                    , tracing.getInt(tracing.getColumnIndex("CHANNEL_W"))
                                                    , tracing.getInt(tracing.getColumnIndex("CHANNEL_H")));
                result.add(filterChannel);
            }

            currentWork.close();

            instance.notify();

            return result;
        }
    }

}
