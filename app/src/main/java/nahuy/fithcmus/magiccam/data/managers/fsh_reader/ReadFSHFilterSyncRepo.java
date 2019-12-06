package nahuy.fithcmus.magiccam.data.managers.fsh_reader;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.clients.database.filter.ShaderAccessObject;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;

/**
 * Created by huy on 6/18/2017.
 */

public class ReadFSHFilterSyncRepo {
    public ArrayList<DA_MyGLShader> readAllFilters(){
        return ShaderAccessObject.getInstance().getAllShaders();
    }
    public DA_MyGLShader readFilterByName(String filterName){
        return ShaderAccessObject.getInstance().getFilterByName(filterName);
    }
}
