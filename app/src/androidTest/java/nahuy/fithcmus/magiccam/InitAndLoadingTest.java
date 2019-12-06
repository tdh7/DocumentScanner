package nahuy.fithcmus.magiccam;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.domain.use_cases.GettingFrameUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.GettingStickerTextUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.GettingStickerUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.GettingFSHFilterUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.SyncLoadingInitUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

import static org.junit.Assert.assertTrue;

/**
 * Created by huy on 6/21/2017.
 */

@RunWith(AndroidJUnit4.class)
public class InitAndLoadingTest {

    @Test
    public void testIfInitDatabaseSuccess(){
        // Context of the app under test.
        loadingDatabase();
    }

    @Test
    public void testIfLoadingAssetsSuccess(){
        // Context of the app under test.
        loadingAsset();
    }

    @Test
    public void testIfAlreadyInitCheckSuccessWhenAlreadyInitDatabase(){
        loadingDatabase();
        loadingAsset();
        Context appContext = InstrumentationRegistry.getTargetContext();
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        syncLoadingInitUseCase.checkIfAlreadyInit(appContext);
    }

    @Test
    public void testIfNumberOfFilterIsLoadingCorrectly(){
        loadingDatabase();
        GettingFSHFilterUseCase readFSHFilterUseCase = new GettingFSHFilterUseCase();
        ArrayList<MyGLEffectShader> filters = readFSHFilterUseCase.getAllFilters();
        assertTrue(filters.size() > 0);
    }

    @Test
    public void testIfNumberOfFrameIsLoadingCorrectly(){
        loadingDatabase();
        GettingFrameUseCase frameUseCase = new GettingFrameUseCase();
        ArrayList<Frame> frames = frameUseCase.getAllFrames();
        assertTrue(frames.size() > 0);
    }

    @Test
    public void testIfNumberOfStickerIsLoadingCorrectly(){
        loadingDatabase();
        GettingStickerUseCase gettingStickerUseCase = new GettingStickerUseCase();
        ArrayList<Sticker> stickers = gettingStickerUseCase.getAllStickers();
        assertTrue(stickers.size() > 0);
    }

    @Test
    public void testIfNumberOfStickerTextIsLoadingCorrectly(){
        loadingDatabase();
        GettingStickerTextUseCase gettingStickerTextUseCase = new GettingStickerTextUseCase();
        ArrayList<StickerText> stickerTexts = gettingStickerTextUseCase.getAllStickerTexts();
        assertTrue(stickerTexts.size() > 0);
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
