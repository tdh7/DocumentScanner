package nahuy.fithcmus.magiccam.data.clients.database.filter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import nahuy.fithcmus.magiccam.data.CastHelper;
import nahuy.fithcmus.magiccam.data.Constants;

/**
 * Created by huy on 5/23/2017.
 */

public class KernelAccessObject {
    protected boolean isActive = false;

    private static KernelAccessObject instance = new KernelAccessObject();

    private KernelAccessObject(){}

    public static KernelAccessObject getInstance(){
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
    public int addKernel(float[] kernel){

        synchronized (instance) {
            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null) {
                // Will show some text when connecting to view layer.
                return -1;
            }

            // Content Value is a class that hold <key, value> pair
            // Instead writing the whole query, we do this for simple purpose

            ContentValues insertValues = new ContentValues();

            String kernelValue = CastHelper.getStringArrayFromFloat(kernel);
            insertValues.putNull("KERNEL_ID");
            insertValues.put("KERNEL_VALUE", kernelValue);

            currentWork.beginTransaction();

            try {
                long id = currentWork.insertOrThrow("KERNEL", null, insertValues);

                if (id == -1)
                    throw new Exception("Can't insert");
                // if id different from -1 then we create a folder in internal storage that stand for the
                // album.

                currentWork.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("ERROR INSERT", e.getMessage());
                return -1;
            } finally {
                currentWork.endTransaction();
                currentWork.close();
            }

            instance.notify();
            return getKernelLatestId();
        }
    }

    public int getKernelLatestId(){
        synchronized (instance) {
            int result = -1;

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            Cursor tracing = currentWork.rawQuery("SELECT * FROM KERNEL ORDER BY KERNEL.KERNEL_ID DESC LIMIT 1", null);

            while (tracing.moveToNext()) {
                result = tracing.getInt(tracing.getColumnIndex("KERNEL_ID"));
            }

            currentWork.close();
            instance.notify();
            return result;
        }
    }

    public float[] getKernel(int kernelId){

        synchronized (instance) {
            float[] result = null;

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            String[] selectArgs = {"" + kernelId};
            Cursor tracing = currentWork.rawQuery("select * from KERNEL where KERNEL_ID=?", selectArgs);

            String kernel = "";

            while (tracing.moveToNext()) {
                kernel = tracing.getString(tracing.getColumnIndex("KERNEL_VALUE"));
            }

            currentWork.close();
            instance.notify();
            return CastHelper.getFloatArrayFromStr(kernel);
        }
    }

}
