package nahuy.fithcmus.magiccam.data.clients.database.filter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterKernel;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.FilterKernel;

/**
 * Created by huy on 5/23/2017.
 */

public class ShaderAccessObject {

    protected boolean isActive = false;

    private static ShaderAccessObject instance = new ShaderAccessObject();

    private ShaderAccessObject(){}

    public static ShaderAccessObject getInstance(){
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

    public boolean addFilter(DA_MyGLShader objectToAdd){

        synchronized (instance) {

            // -2 is mean not using kernel
            ArrayList<Integer> latestKernelIds = new ArrayList<>();

            // Add kernel if any
            if(objectToAdd.isUsingKernel()) {
                ArrayList<DA_FilterKernel> lstOfKernel = objectToAdd.getKernel();
                for (DA_FilterKernel kernel : lstOfKernel) {
                    latestKernelIds.add(KernelAccessObject.getInstance().addKernel(kernel.getKernel()));
                }
            }

            // Add filter channel if any
            if(objectToAdd.isUsingEChannel()){
                ArrayList<DA_FilterChannel> filterChannels = objectToAdd.getFilterChannels();
                for(DA_FilterChannel filterChannel : filterChannels){
                    FilterChannelAccessObject.getInstance().addFilterChannel(objectToAdd.getFilterName(), filterChannel);
                }
            }

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null) {
                // Will show some text when connecting to view layer.
                return false;
            }

            // Content Value is a class that hold <key, value> pair
            // Instead writing the whole query, we do this for simple purpose

            ContentValues insertValues = new ContentValues();

            insertValues.put("FILTER_NAME", objectToAdd.getFilterName());
            insertValues.put("FILTER_SHADER", objectToAdd.getFilterShader());
            insertValues.put("FILTER_IMG", objectToAdd.getFilterImg());
            insertValues.put("USING_CHANNEL", objectToAdd.isUsingEChannel() ? 1 : 0);
            insertValues.put("USING_KERNEL", objectToAdd.isUsingKernel() ? 1 : 0);
            insertValues.put("ORDER_CODE", objectToAdd.getOrderCode());

            currentWork.beginTransaction();

            try {
                long id = currentWork.insertOrThrow("FILTER", null, insertValues);

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

            if(latestKernelIds.size() > 0) {
                for (Integer integer : latestKernelIds) {
                    FilterKernelAccessObject.getInstance().addFilterKernel(objectToAdd.getFilterName(), integer);
                }
            }

            instance.notify();
            return true;
        }
    }

    public ArrayList<DA_MyGLShader> getAllShaders(){

        synchronized (instance) {
            ArrayList<DA_MyGLShader> result = new ArrayList<DA_MyGLShader>(0);

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            Cursor tracing = currentWork.rawQuery("select * from FILTER", null);

            while (tracing.moveToNext()) {
                DA_MyGLShader se = new DA_MyGLShader(tracing.getString(tracing.getColumnIndex("FILTER_NAME")),
                        tracing.getString(tracing.getColumnIndex("FILTER_SHADER")),
                        tracing.getString(tracing.getColumnIndex("FILTER_IMG")),
                        tracing.getString(tracing.getColumnIndex("ORDER_CODE")));
                se.setUsingChannel(tracing.getInt(tracing.getColumnIndex("USING_CHANNEL")) == 1 ? true : false);
                se.setUsingKernel(tracing.getInt(tracing.getColumnIndex("USING_KERNEL")) == 1 ? true : false);
                se.getFilterChannels();
                se.getKernel();
                result.add(se);
            }

            currentWork.close();
            instance.notify();
            return result;
        }
    }

    public float[] getKernel(){

        return null;
    }

    public DA_MyGLShader getFilterByName(String filterName) {
        synchronized (instance) {
            DA_MyGLShader result = null;

            SQLiteDatabase currentWork = this.openDatabase(Constants.DATABASE_PATH);

            if (currentWork == null)
                return result;

            String[] selectionArgs = {filterName};
            Cursor tracing = currentWork.rawQuery("select * from FILTER where FILTER_NAME=?", selectionArgs);

            while (tracing.moveToNext()) {
                DA_MyGLShader se = new DA_MyGLShader(tracing.getString(tracing.getColumnIndex("FILTER_NAME")),
                        tracing.getString(tracing.getColumnIndex("FILTER_SHADER")),
                        tracing.getString(tracing.getColumnIndex("FILTER_IMG")),
                        tracing.getString(tracing.getColumnIndex("ORDER_CODE")));
                se.setUsingChannel(tracing.getInt(tracing.getColumnIndex("USING_CHANNEL")) == 1 ? true : false);
                se.setUsingKernel(tracing.getInt(tracing.getColumnIndex("USING_KERNEL")) == 1 ? true : false);
                se.getFilterChannels();
                se.getKernel();
                result = se;
            }

            currentWork.close();
            instance.notify();
            return result;
        }
    }
}
