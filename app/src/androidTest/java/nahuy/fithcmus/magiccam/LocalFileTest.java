package nahuy.fithcmus.magiccam;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import nahuy.fithcmus.magiccam.domain.use_cases.ReadFSHFileUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.SyncLoadingInitUseCase;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by huy on 6/23/2017.
 */

@RunWith(AndroidJUnit4.class)
public class LocalFileTest {

    @Test
    public void checkIfGettingFilterIsCorrect(){
        loadingDatabase();
        loadingAsset();
        Context appContext = InstrumentationRegistry.getTargetContext();
        String[] filterName = {"anti_fisheye_double.fsh","anti_fisheye_double_buf.fsh",
                "anti_fisheye_single.fsh","b_w.fsh",
                "chromatic.fsh","cross_hatch.fsh",
                "draw.fsh","draw_buf_a.fsh",
                "draw_buf_b.fsh","draw_buf_c.fsh",
                "edge_glow.fsh","emboss.fsh",
                "fisheye.fsh","fisheye_double.fsh",
                "fisheye_double_buf.fsh","frame_function.fsh",
                "lut.fsh","mirror_4.fsh","mirror_4_r.fsh",
                "mirror_h.fsh","mirror_h_r.fsh",
                "mirror_v.fsh","mirror_v_r.fsh",
                "motion_blur.fsh","motion_blur_buf.fsh",
                "navigate.fsh","normal.fsh","normal_origin_ext.fsh",
                "pixelize.fsh","sepia.fsh","sketch.fsh",
                "swirl.fsh","toon.fsh","video_on_fire.fsh",
                "video_on_fire_buf_a.fsh","video_on_fire_buf_b.fsh",
                "vignette_circle.fsh","vignette_rectangle.fsh","vintage.fsh"};
        ReadFSHFileUseCase readFSHFileUseCase = new ReadFSHFileUseCase();
        for(String f : filterName){
            String filter = readFSHFileUseCase.readFshTextFile(appContext, f);
            assertNotEquals(filter, "");
        }
    }

    private void loadingDatabase(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        assertTrue(syncLoadingInitUseCase.initLoadDatabase(appContext));
        assertTrue(syncLoadingInitUseCase.initDataInDatabase(appContext));
    }

    private void loadingAsset(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        assertTrue(syncLoadingInitUseCase.initLoadAssets(appContext));
    }
}
