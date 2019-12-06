package nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.Constants;

/**
 * Created by huy on 6/19/2017.
 */

public class MyToolGlHeader {

    private String toolName;
    private int headerImg = R.mipmap.ic_launcher;

    private ArrayList<MyToolGLShader> lstOfTool = new ArrayList<>();

    public MyToolGlHeader(String toolName) {
        this.toolName = toolName;
    }

    public MyToolGlHeader(String toolName, int headerImg) {
        this.toolName = toolName;
        this.headerImg = headerImg;
    }

    public MyToolGlHeader(String toolName, int headerImg, ArrayList<MyToolGLShader> lstOfTool) {
        this.toolName = toolName;
        this.headerImg = headerImg;
        this.lstOfTool = lstOfTool;
    }

    public void setImageViewHeader(Context context, ImageView imageView) {
        Glide.with(context).load(headerImg).into(imageView);
    }

    public ArrayList<MyToolGLShader> getLstOfTool() {
        return lstOfTool;
    }

    public void addTool(MyToolGLShader se) {
        lstOfTool.add(se);
    }

    public boolean isSingleTool(){
        return this.lstOfTool.size() == 1;
    }

    public void setResId(int resId){
        this.headerImg = resId;
    }

}
