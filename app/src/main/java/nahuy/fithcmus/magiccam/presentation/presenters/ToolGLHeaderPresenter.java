package nahuy.fithcmus.magiccam.presentation.presenters;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGlHeader;

/**
 * Created by huy on 6/19/2017.
 */

public class ToolGLHeaderPresenter {

    public ArrayList<MyToolGlHeader> loadTool(){

        ArrayList<MyToolGlHeader> myToolGlHeaders = new ArrayList<>();

        myToolGlHeaders.add(initDrawTool());
        myToolGlHeaders.add(initDrawTool2());
        myToolGlHeaders.add(initDrawTool3());
        myToolGlHeaders.add(initDrawTool4());
        myToolGlHeaders.add(initDrawTool5());

        return myToolGlHeaders;

    }

    private MyToolGlHeader initDrawTool(){
        //B2|B2|B3,B1,B2|"draw_buf_a.fsh","draw_buf_b.fsh",
        MyToolGlHeader draw = new MyToolGlHeader("Free Draw");
        MyToolGLShader drawItem = new MyToolGLShader("Free Draw",
                "draw.fsh", "B1,B2|B2|B3,B1,B2|B3,M1", "", new String[]{"draw_buf_a.fsh","draw_buf_b.fsh","draw_buf_c.fsh"});
        draw.setResId(R.drawable.tool_draw);
        drawItem.setResId(R.drawable.tool_draw);
        drawItem.setUsingColor(true);
        drawItem.setUsingQuantity(true);
        drawItem.setUsingClear(true);
        draw.addTool(drawItem);
        return draw;
    }

    private MyToolGlHeader initDrawTool2(){
        MyToolGlHeader draw = new MyToolGlHeader("Vignette");
        MyToolGLShader drawItem = new MyToolGLShader("Circle Vignette",
                "vignette_circle.fsh", "M1", "", new String[]{});
        MyToolGLShader drawItem2 = new MyToolGLShader("Rectangle Vignette",
                "vignette_rectangle.fsh", "M1", "", new String[]{});
        draw.setResId(R.drawable.tool_vignette);
        drawItem.setResId(R.drawable.tool_vig_cir);
        drawItem2.setResId(R.drawable.tool_vig_rec);
        drawItem.setUsingColor(true);
        drawItem.setUsingQuantity(true);
        drawItem2.setUsingColor(true);
        drawItem2.setUsingQuantity(true);
        draw.addTool(drawItem);
        draw.addTool(drawItem2);
        return draw;
    }

    private MyToolGlHeader initDrawTool3(){
        MyToolGlHeader draw = new MyToolGlHeader("Mirror");
        MyToolGLShader drawItem = new MyToolGLShader("Mirror_V",
                "mirror_v.fsh", "M1", "", new String[]{});
        MyToolGLShader drawItem2 = new MyToolGLShader("Mirror_V_R",
                "mirror_v_r.fsh", "M1", "", new String[]{});
        MyToolGLShader drawItem3 = new MyToolGLShader("Mirror_H",
                "mirror_h.fsh", "M1", "", new String[]{});
        MyToolGLShader drawItem4 = new MyToolGLShader("Mirror_H_R",
                "mirror_h_r.fsh", "M1", "", new String[]{});
        MyToolGLShader drawItem5 = new MyToolGLShader("Mirror_4",
                "mirror_4.fsh", "M1", "", new String[]{});
        MyToolGLShader drawItem6 = new MyToolGLShader("Mirror_4_R",
                "mirror_4_r.fsh", "M1", "", new String[]{});
        draw.setResId(R.drawable.tool_mirror);
        drawItem.setResId(R.drawable.tool_mirror_ver);
        drawItem2.setResId(R.drawable.tool_mirror_ver_rev);
        drawItem3.setResId(R.drawable.tool_mirror_hor);
        drawItem4.setResId(R.drawable.tool_mirror_hor_rev);
        drawItem5.setResId(R.drawable.tool_mirror_4);
        drawItem6.setResId(R.drawable.tool_mirror_4_rev);
        draw.addTool(drawItem);
        draw.addTool(drawItem2);
        draw.addTool(drawItem3);
        draw.addTool(drawItem4);
        draw.addTool(drawItem5);
        draw.addTool(drawItem6);
        return draw;
    }

    private MyToolGlHeader initDrawTool4(){
        MyToolGlHeader draw = new MyToolGlHeader("Fisheye");
        MyToolGLShader drawItem = new MyToolGLShader("Fisheye",
                "fisheye.fsh", "M1", "", new String[]{});

        MyToolGLShader drawItem2 = new MyToolGLShader("Anti Fisheye",
                "anti_fisheye_single.fsh", "M1", "", new String[]{});

        MyToolGLShader drawItem3 = new MyToolGLShader("Fisheye Double",
                "fisheye_double.fsh", "M1|B1", "", new String[]{"fisheye_double_buf.fsh"});

        MyToolGLShader drawItem4 = new MyToolGLShader("Anti Fisheye Double",
                "anti_fisheye_double.fsh", "M1|B1", "", new String[]{"anti_fisheye_double_buf.fsh"});

        draw.setResId(R.drawable.tool_fisheye);
        drawItem.setResId(R.drawable.tool_fisheye);
        drawItem2.setResId(R.drawable.tool_anti_fisheye);
        drawItem3.setResId(R.drawable.tool_fisheye_2);
        drawItem4.setResId(R.drawable.tool_anti_fisheye_2);
        drawItem.setUsingQuantity(true);
        drawItem2.setUsingQuantity(true);
        drawItem3.setUsingQuantity(true);
        drawItem4.setUsingQuantity(true);

        draw.addTool(drawItem);
        draw.addTool(drawItem2);
        draw.addTool(drawItem3);
        draw.addTool(drawItem4);

        return draw;
    }


    private MyToolGlHeader initDrawTool5() {
        MyToolGlHeader draw = new MyToolGlHeader("Cinema");
        MyToolGLShader drawItem = new MyToolGLShader("Motion Blur",
                "motion_blur.fsh", "B1,M1|B1", "", new String[]{"motion_blur_buf.fsh"});

        MyToolGLShader drawItem2 = new MyToolGLShader("Chromatic",
                "chromatic.fsh", "M1", "", new String[]{});

        MyToolGLShader drawItem3 = new MyToolGLShader("Video On Fire",
                "video_on_fire.fsh", "M1,B2|B2,B1|B1,B2", "", new String[]{"video_on_fire_buf_a.fsh", "video_on_fire_buf_b.fsh"});

        MyToolGLShader drawItem4 = new MyToolGLShader("Edge Glow",
                "edge_glow.fsh", "M1", "", new String[]{});

        draw.setResId(R.drawable.tool_cinema);
        drawItem.setResId(R.drawable.tool_motion_blur);
        drawItem2.setResId(R.drawable.tool_3d);
        drawItem3.setResId(R.drawable.tool_fire);
        drawItem4.setResId(R.drawable.tool_glow);
        drawItem.setUsingQuantity(true);
        drawItem4.setUsingColor(true);

        draw.addTool(drawItem);
        draw.addTool(drawItem2);
        draw.addTool(drawItem3);
        draw.addTool(drawItem4);

        return draw;
    }
}
