package nahuy.fithcmus.magiccam.data.clients.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by huy on 6/20/2017.
 */

public abstract class AbsDAO {

    protected boolean isActive = false;

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

}
