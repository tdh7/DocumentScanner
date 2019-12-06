package nahuy.fithcmus.magiccam.data.clients.database.filter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterKernel;


/**
 * Created by huy on 5/23/2017.
 */

public class FilterKernelAccessObject {
    protected boolean isActive = false;

    private static FilterKernelAccessObject instance = new FilterKernelAccessObject();

    private FilterKernelAccessObject(){}

    public static FilterKernelAccessObject getInstance(){
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
    public boolean addFilterKernel(String filterName, int kernelId){

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
            insertValues.put("KERNEL_ID", kernelId);

            currentWork.beginTransaction();

            try {
                long id = currentWork.insertOrThrow("FILTER_KERNEL", null, insertValues);

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

    public ArrayList<DA_FilterKernel> getKernel(String filterName){

        synchronized (instance) {
            ArrayList<DA_FilterKernel> result = new ArrayList<>();

            ArrayList<Integer> kernelIds = getKernelId(filterName);

            for(Integer kernelId : kernelIds) {
                result.add(new DA_FilterKernel(kernelId, KernelAccessObject.getInstance().getKernel(kernelId)));
            }

            instance.notify();

            return result;
        }
    }

    private ArrayList<Integer> getKernelId(String filterName){
        synchronized (instance) {
            ArrayList<Integer> lstIndex = new ArrayList<>();

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return null;

            String[] selectArgs = {filterName};
            Cursor tracing = currentWork.rawQuery("select * from FILTER_KERNEL where FILTER_NAME=?", selectArgs);

            while (tracing.moveToNext()) {
                lstIndex.add(tracing.getInt(tracing.getColumnIndex("KERNEL_ID")));
            }

            currentWork.close();
            instance.notify();
            return lstIndex;
        }
    }
}
